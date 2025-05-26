/* verilator lint_off UNUSED */
/* verilator lint_off MULTIDRIVEN */
/* verilator lint_off BLKSEQ */
/* verilator lint_off WIDTH */

/**/
module gpioemu (
    input clk,
    input n_reset,
    input [16:0] saddress,
    input srd,
    input swr,
    input [31:0] sdata_in,
    output [31:0] sdata_out,
    input [31:0] gpio_in,
    input gpio_latch,
    output [31:0] gpio_out,
    output [31:0] gpio_in_s_insp
);


    // Adresy
    localparam IN_ADDR     = 16'h680;
    localparam STATE_ADDR  = 16'h688;
    localparam RESULT_ADDR = 16'h690;
    localparam CTRL_ADDR   = 16'h698;
	

	
	// Dane wyjściowe
    reg [31:0] sdata_out_s;
    assign sdata_out = sdata_out_s;
    // Bufor
    reg [7:0] buffer [0:249];
    reg [7:0] write_ptr;
    reg [7:0] read_ptr;
    reg [15:0] write_counter;
	reg [7:0] bit_num;


    // CRC
    reg [31:0] crc;
    reg [31:0] result;
    reg [31:0] crc_temp;
    reg crc_ready;
	localparam POLY = 32'h814141AB;
    // Rejestry
    reg [7:0] ctrl;
    reg [7:0] state;
	reg [7:0] in;
	//flagi
    reg calc_done;
	reg full;
	reg empty;
	reg force_fill;

    // FSM
    reg [2:0] state_fsm;
    localparam IDLE = 3'd0;
    localparam PROCESS = 3'd1;
    localparam DONE = 3'd2;
	localparam READ = 3'd3;

    // GPIO
    reg [31:0] gpio_in_s;
    reg [31:0] gpio_out_s;
    assign gpio_out = gpio_out_s;
    assign gpio_in_s_insp = gpio_in_s;
	
    assign gpio_out = gpio_out_s;
    assign gpio_in_s_insp = gpio_in_s;

    // Latch GPIO input
    always @(posedge gpio_latch) begin
        gpio_in_s <= gpio_in;
    end
    // RESET
	always @(negedge n_reset) begin
        gpio_in_s <= 0;
        gpio_out_s <= 0;
        sdata_out_s <= 0;
        write_ptr <= 0;
        read_ptr <= 0;
        write_counter <= 0;
        crc <= 32'h00000000;
        result <= 32'h00000000;
		bit_num <= 0;
        crc_ready <= 0;
        ctrl <= 0;
        calc_done <= 0;
        state_fsm <= IDLE;
		force_fill <= 0;
		full <=0;
		empty <=1;
    end
	

    // FSM
    always @(posedge clk) begin
		case (state_fsm)
			IDLE: begin
				if (ctrl == 8'hA) begin //czyszczenie
					write_ptr <= 0;
					read_ptr <= 0;
					crc <= 32'h0;
					result <= 0;
					calc_done <= 0;
					crc_ready <= 0;
					ctrl <= 0;
					if (write_counter > 0) begin
						buffer[write_ptr] = 8'h0;
						write_ptr <= write_ptr - 1;
						write_counter <= write_counter-1;
					end
					empty <= 1;
					full <= 0;
					state_fsm <= IDLE;
				end else if (ctrl == 8'hB) begin // wykonaj obliczenia
					read_ptr <= 0;
					ctrl <= 0;
					state_fsm <= PROCESS;
				end else if (ctrl == 8'hC) begin //nadpis 
					write_ptr <= 0;
					write_counter <= 0; //(nowe)
					result <= 0;
					crc<=0;
					read_ptr <= 0;
					ctrl <= 0;
					state_fsm <= IDLE;
				end else if (ctrl == 8'hD) begin //zapełnij sztucznie bufor danymi - tylko do testu
					if (write_counter<250) begin
						buffer[write_ptr] = 8'h1;
						write_ptr <= write_ptr + 1;
						write_counter <= write_counter + 1;
						force_fill <= 1;
						state_fsm <= IDLE;
					end else begin
					force_fill <= 0;
					state_fsm <= IDLE;
					end
				end
			end
			PROCESS: begin
				if (write_counter > 0) begin
					crc_temp = crc ^ (buffer[read_ptr] << 24);
					crc_temp = (crc_temp[31]) ? (crc_temp << 1) ^ 32'h814141AB : (crc_temp << 1);
					crc_temp = (crc_temp[31]) ? (crc_temp << 1) ^ 32'h814141AB : (crc_temp << 1);
					crc_temp = (crc_temp[31]) ? (crc_temp << 1) ^ 32'h814141AB : (crc_temp << 1);
					crc_temp = (crc_temp[31]) ? (crc_temp << 1) ^ 32'h814141AB : (crc_temp << 1);
					crc_temp = (crc_temp[31]) ? (crc_temp << 1) ^ 32'h814141AB : (crc_temp << 1);
					crc_temp = (crc_temp[31]) ? (crc_temp << 1) ^ 32'h814141AB : (crc_temp << 1);
					crc_temp = (crc_temp[31]) ? (crc_temp << 1) ^ 32'h814141AB : (crc_temp << 1);
					crc_temp = (crc_temp[31]) ? (crc_temp << 1) ^ 32'h814141AB : (crc_temp << 1);
					crc <= crc_temp;
					read_ptr <= read_ptr + 1;
					write_counter <= write_counter - 1;
					end else begin
					result <= crc;
					crc_ready <= 1;
					calc_done <= 1;
					state_fsm <= DONE;
					end
			end
			DONE: begin
				state_fsm <= IDLE;
				calc_done <= 0;
			end
			default: begin
				state_fsm <= IDLE;
			end
		endcase
    end

    // Zapis danych
    always @(posedge swr) begin
            case (saddress)
				IN_ADDR: begin
					if (write_counter < 250) begin
						if (!force_fill) begin
							in <= sdata_in[7:0];
							buffer[write_ptr] <= sdata_in[7:0];
							write_ptr <= write_ptr + 1;
							write_counter <= write_counter + 1;	
							end
						end else begin
							full <= 1;
						end
						empty <= 0;
				end
                CTRL_ADDR: begin
                    ctrl <= sdata_in[7:0];
                end
			default: begin
				
			end
            endcase
        end

    // Stan
    always @(*) begin
        state = 8'd0;
        if (full == 1)      state[0] = 1; 
		if (state_fsm == IDLE)      state[1] = 1; 
		if (state_fsm == DONE)      state[2] = 1; 
        if (state_fsm == PROCESS)      state[3] = 1; 
        if (empty)        state[4] = 1; 
		if (ctrl != 8'h00)     state[5] = 1; 
    end

    // Odczyt
    always @(posedge srd) begin
        case (saddress)
            STATE_ADDR: sdata_out_s <= {24'd0, state};
            RESULT_ADDR: begin
                if (crc_ready) begin
                    sdata_out_s <= result;
				end
			end
			default: begin
				sdata_out_s <= 32'h0;
			end
        endcase
    end
endmodule