`timescale 1ns/1ps

module gpioemu_tb;
    reg n_reset = 1;
    reg [16:0] saddress = 0;
    reg srd = 0;
    reg swr = 0; 
    reg [31:0] sdata_in = 0;
    reg [31:0] gpio_in = 0;  // GPIO Input
    reg gpio_latch = 0;       // Sygnał latchowania
    reg clk = 0;
    
    wire [31:0] gpio_out;
    wire [31:0] sdata_out;
    wire [31:0] gpio_in_s_insp;  // Odczyt z latcha
    
    // Addresses
    parameter IN_ADDR     = 16'h680;
    parameter STATE_ADDR  = 16'h688;
    parameter RESULT_ADDR = 16'h690;
    parameter CTRL_ADDR   = 16'h698;
    
    reg [31:0] read_data;

    gpioemu dut (
        .clk(clk),
        .n_reset(n_reset),
        .swr(swr),
        .srd(srd),
        .saddress(saddress),
        .sdata_in(sdata_in),
        .sdata_out(sdata_out),
        .gpio_in(gpio_in),
        .gpio_latch(gpio_latch),
        .gpio_out(gpio_out),
        .gpio_in_s_insp(gpio_in_s_insp)
    );
    
    // 100MHz clock
    always #5 clk = ~clk;
    
    
    initial begin
        $dumpfile("gpioemu.vcd");
        $dumpvars(0, gpioemu_tb);
        
        $display("[%0tns] === TEST START ===", $time);
        
        // Reset sequence
        n_reset = 1;
        #10 n_reset = 0;
        #20 n_reset = 1;
        #50; // Wait after reset
		//swr = 1;
		saddress = IN_ADDR;
		swr = 1;
		sdata_in = {8'hEE};
		#10;
		swr = 0;
		#10;
		saddress = IN_ADDR;
		swr = 1;
		sdata_in = {8'hBB};
		#10;
		swr = 0;
		#10;
		sdata_in = {8'hA};
		saddress = CTRL_ADDR; //poleenie - wyczysc bufor
		#10
		swr = 1;
		#10
		swr = 0;
		sdata_in = {8'hC};
		saddress = CTRL_ADDR; //
		#10
		swr = 1;
		#10
		swr = 0;
		#10;
		sdata_in = {8'hEE};
		saddress = IN_ADDR;
		#10;
		swr = 1;
		#10;
		swr = 0;
		#10;
		swr = 1;
		saddress = CTRL_ADDR;
		sdata_in = {8'hB};
		#10;
		swr = 0;
		#10 

		saddress = RESULT_ADDR;
		#10;
		srd = 1;
		#10;
		srd = 0; 
		#10 
		saddress = STATE_ADDR;
		srd = 1;
		#10;
		srd = 0; 
		#10 
		#10
		swr = 1;
		saddress = CTRL_ADDR;
		sdata_in = {8'hC};
		#10;
		swr = 0;
		#10;
		swr = 1;
		saddress = CTRL_ADDR;
		sdata_in = {8'hD};
		#10;
		swr = 0;
		#3000;
		saddress = IN_ADDR;
		swr = 1;
		sdata_in = {8'hBB};
		#10;
		swr = 1;
		saddress = CTRL_ADDR;
		sdata_in = {8'hC};
		#10;
		swr = 0;
		#10 
		swr = 0;
		#10 
		saddress = IN_ADDR;
		swr = 1;
		sdata_in = {8'hAA};
		#10;
		swr = 0;
		#10 
		saddress = IN_ADDR;
		swr = 1;
		sdata_in = {8'hCC};
		#10;
		swr = 0;
		#10 
		swr = 1;
		saddress = CTRL_ADDR;
		sdata_in = {8'hB};
		#10;
		swr = 0;
		#10 
		saddress = STATE_ADDR; //odczyt stanu 
		srd = 1;
		#10;
		srd = 0;
		#10;
		saddress = RESULT_ADDR; //odczyt stanu 
		#10;
		srd = 1;
		#10;
		srd = 0; 
		sdata_in = {8'hA};
		saddress = CTRL_ADDR; //poleenie - wyczysc bufor
		#10
		swr = 1;
		#10
		swr = 0;
		#10;
		sdata_in = {8'hEE};
		saddress = IN_ADDR;
		#10;
		swr = 1;
		#10;
		swr = 0;
		sdata_in = {8'hBB};
		saddress = IN_ADDR;
		#10;
		swr = 1;
		#10;
		swr = 0;
		saddress = STATE_ADDR; //odczyt stanu
		srd = 1;
		#10;
		srd = 0; 
		#10 
		saddress = CTRL_ADDR; //rozpocznij obliczenia
		sdata_in = 8'hB; 
		swr = 1;
		#10
		swr = 0;
		saddress = STATE_ADDR; //odczyt stanu
		srd = 1;
		#10;
		srd = 0; 
		#10 
		saddress = RESULT_ADDR; //odczyt wyniku
		srd = 1;
		#10;
		srd = 0;
		#10;

		$finish;
        
    end
endmodule
