package pl.edu.pw.elka.prm2t;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Stack;
//stos, ktory sklada sie z plansz - board, ktore maja rozne stany
// undo - petla, ktora nadaje planszy wartosci planszy stos.pop PO zrobieniu pop (najpierw musi usunac ten obecny stan ze stosu)
//zapis stanu planszy - jesli update board, to zostaje ona zapisana wczesniej na stos
/**
 * Klasa MatrixDisplay tworzy okno z wyświetloną macierzą n (kwadratem łacińskim) w interfejsie graficznym.
 */
public class MatrixDisplay extends JFrame {
    public SkyBoard board;
    private final JTextField[][] textFields;
    private final Stack<SkyBoard> stack;
    private boolean pencilMode = false;
    private final Pencil[][] pencilMarks;


    public MatrixDisplay(int boardSize) {
        this(new SkyBoard(boardSize));
    }

    public MatrixDisplay(SkyBoard boardWithClues) {
        stack = new Stack<>();
        board = boardWithClues;
        setTitle("Plansza gry");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int boardSize = board.getSize();
        int fullBoardSize = boardSize + 2;

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JPanel matrixPanel = new JPanel(new GridLayout(fullBoardSize, fullBoardSize, 5, 5));

        textFields = new JTextField[fullBoardSize][fullBoardSize];
        pencilMarks = new Pencil[fullBoardSize][fullBoardSize];


        for (int i = 0; i < fullBoardSize; i++) {
            for (int j = 0; j < fullBoardSize; j++) {
                pencilMarks[i][j] = new Pencil();
                JTextField textField = new JTextField(String.valueOf(board.get(i, j) > 0 ? board.get(i, j) : ""));
                textField.setHorizontalAlignment(SwingConstants.CENTER);
                textField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                textField.setBackground(Color.WHITE);
                textField.setPreferredSize(new Dimension(50, 50));
                textFields[i][j] = textField;

                if (i == 0 || i == fullBoardSize - 1 || j == 0 || j == fullBoardSize - 1) {
                    textField.setFocusable(false);
                    textField.setBackground(Color.LIGHT_GRAY);
                } else {
                    ((AbstractDocument) textField.getDocument()).setDocumentFilter(new SingleDigitFilter(boardSize));

                    int finalI = i;
                    int finalJ = j;
                    textField.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {

                            switch (e.getKeyCode()) {
                                case KeyEvent.VK_UP -> {
                                    if (finalI > 1) textFields[finalI - 1][finalJ].requestFocus();
                                }
                                case KeyEvent.VK_DOWN -> {
                                    if (finalI < fullBoardSize - 2) textFields[finalI + 1][finalJ].requestFocus();
                                }
                                case KeyEvent.VK_LEFT -> {
                                    if (finalJ > 1) textFields[finalI][finalJ - 1].requestFocus();
                                }
                                case KeyEvent.VK_RIGHT -> {
                                    if (finalJ < fullBoardSize - 2) textFields[finalI][finalJ + 1].requestFocus();
                                }
                            }
                        }
                    });

                    textField.getDocument().addDocumentListener(new DocumentListener() {
                        @Override
                        public void insertUpdate(DocumentEvent e) {
                            updateBoard();
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                            updateBoard();
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                            updateBoard();
                        }

                        public void updateBoard() {
                            board.set(finalI, finalJ, !Objects.equals(textField.getText(), "") ? Integer.parseInt(textField.getText()) : 0);
                            //if (board!=null) {
                            stack.push(board);
                            System.out.println("TEST");
                            // }
                        }
                    });
                }

                matrixPanel.add(textField);
            }
        }



        //Guzik do sprawdzania
        JButton sprawdzanieGuzik = new JButton("Check");
        sprawdzanieGuzik.addActionListener(e -> {
            if (board.isBoardCorrect()) {
                JOptionPane.showMessageDialog(MatrixDisplay.this, "Congratulations! The board is correct!");
            } else {
                JOptionPane.showMessageDialog(MatrixDisplay.this, "The board is incorrect. Please try again.");
            }
        });
        //Guzik do rozwiązywania
        JButton solverGuzik = new JButton("Solve");
        solverGuzik.addActionListener(e -> {
            Solver.solve(board);
            updateGUI();
        });



        // Guzik do zapisywania planszy jako obraz
        JButton screenshotGuzik = new JButton("Screenshot");
        screenshotGuzik.addActionListener(e -> Screenshot.takeScreenshot(MatrixDisplay.this));
        JButton undoGuzik = new JButton("Undo");
        undoGuzik.addActionListener(e-> undo());

        // Pencil Mode Toggle Button
        JToggleButton pencilToggleButton = new JToggleButton("Pencil Mode");
        pencilToggleButton.addActionListener(e -> {
            pencilMode = pencilToggleButton.isSelected();
            for (int i = 1; i < fullBoardSize - 1; i++) {
                for (int j = 1; j < fullBoardSize - 1; j++) {
                    if (pencilMode) {
                        textFields[i][j].setText(pencilMarks[i][j].getMarksAsString());
                    } else {
                        textFields[i][j].setText(String.valueOf(board.get(i, j)));
                    }
                }
            }

        });


        JButton clearPencilMarksButton = new JButton("Clear Pencil Marks");
        clearPencilMarksButton.addActionListener(e -> {
            for (int i = 1; i < textFields.length - 1; i++) {
                for (int j = 1; j < textFields[i].length - 1; j++) {
                    pencilMarks[i][j].clearMarks();
                    updatePencilMarks(i, j);
                }
            }
        });


        //Guzik do zapisywania planszy do pliku
        JButton zapisywanieGuzik = new JButton("Save");
        JFrame frame = new JFrame("Save File");
        zapisywanieGuzik.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory().getAbsoluteFile());
            fileChooser.setSelectedFile(new File("board.skyscrapers"));
            if (fileChooser.showSaveDialog(mainPanel) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(board.getSize() + 2 + "\n");
                    for (int i = 0; i < fullBoardSize; i++) {
                        for (int j = 0; j < fullBoardSize; j++) {
                            writer.write(board.get(i, j) + " ");
                        }
                        writer.newLine();
                    }
                    writer.newLine();
                    for (int i = 0; i < fullBoardSize; i++) {
                        for (int j = 0; j < fullBoardSize; j++) {
                            writer.write(board.getCorrect(i, j) + " ");
                        }
                        writer.newLine();
                    }
                    JOptionPane.showMessageDialog(frame, "File saved successfully!");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Failed to save the file.");
                }
            }
        });

        JPanel Guziki = new JPanel(new GridLayout(3,2,5,5));
        Guziki.add(sprawdzanieGuzik);
        Guziki.add(solverGuzik);
        Guziki.add(zapisywanieGuzik);
        Guziki.add(screenshotGuzik);
        Guziki.add(undoGuzik);
        Guziki.add(pencilToggleButton);
        Guziki.add(new JLabel(""));
        Guziki.add(clearPencilMarksButton);


        mainPanel.add(matrixPanel, BorderLayout.CENTER);
        mainPanel.add(Guziki, BorderLayout.SOUTH);

        MatrixDisplay.this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int size = Math.min(matrixPanel.getWidth() / fullBoardSize, matrixPanel.getHeight() / fullBoardSize);
                Dimension newSize = new Dimension(size, size);
                for (int i = 0; i < fullBoardSize; i++) {
                    for (int j = 0; j < fullBoardSize; j++) {
                        textFields[i][j].setPreferredSize(newSize);
                    }
                }
                matrixPanel.revalidate();
            }
        });

        add(mainPanel);
        setLocationRelativeTo(null);

        pack();
        setVisible(true);
    }


    // Metoda aktualizująca GUI
    private void updateGUI() {
        int fullBoardSize = board.getSize() + 2;
        for (int i = 1; i < fullBoardSize - 1; i++) {
            for (int j = 1; j < fullBoardSize - 1; j++) {
                textFields[i][j].setText(String.valueOf(board.get(i, j)));
            }
        }
    }
    public void undo() {
        stack.pop(); //usuwa biezacy stan planszy ze stosu
        SkyBoard b = stack.pop(); //b - poprzedni stan planszy, usuwany ze stosu
        System.out.println(stack.size());
        board = b;//aktualizacja planszy board
        stack.push(board);

    }
    public boolean isPencilMode() {
        return pencilMode;
    }

    private static class SingleDigitFilter extends DocumentFilter {
        private final int max;

        private SingleDigitFilter(int max) {
            this.max = max;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && string.matches("[1-%s]".formatted(max)) && fb.getDocument().getLength() == 0) {
                fb.insertString(offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && text.matches("[1-%s]".formatted(max)) && fb.getDocument().getLength() - length + text.length() <= 1) {
                fb.replace(offset, length, text, attrs);
            }
        }
    }
    private void updatePencilMarks(int i, int j) {
        textFields[i][j].setText(pencilMarks[i][j].getMarksAsString());
    }
}
