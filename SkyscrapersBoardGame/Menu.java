package pl.edu.pw.elka.prm2t;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;

/**
 * Klasa Menu tworzy menu główne aplikacji z przyciskami "Zagraj" i "Zapisz".
 */
public class Menu extends JFrame {
    /**
     * Konstruktor tworzący menu główne z przyciskami "Zagraj" i "Zapisz".
     */
    public Menu() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            throw new RuntimeException("Błąd przy wczytywaniu systemowych stylów: ", ex);
        }
        setTitle("Menu Główne");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);

        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Guzik "Zagraj"
        JButton playButton = new JButton("Zagraj");
        JButton loadButton = new JButton("Wczytaj grę");
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.addActionListener(e -> {
            //JSlider slider = new JSlider(0, 2, 0);
            /*String[] difficulyLevels = {"Łatwy", "Średni", "Trudny"};
            Hashtable<Integer, JComponent> labels = new Hashtable<>();
            for (int i=0; i<difficulyLevels.length; i++) {

                labels.put(0, new JTextField());
            }*/
            Object[] options = {"Łatwy", "Średni", "Trudny"};
            int choice = JOptionPane.showOptionDialog(mainPanel, "Wybierz poziom trudności", "Poziom Trudności", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            switch (choice) {
                case 0 -> new MatrixDisplay(3);
                case 1 -> new MatrixDisplay(4);
                case 2 -> new MatrixDisplay(6);
            }
            dispose();
        });

        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            //JFrame frame = new JFrame("Testing");
            fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory().getAbsoluteFile());
            fileChooser.setSelectedFile(new File("board.skyscrapers"));
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    int fullBoardSize = Integer.parseInt(reader.readLine());
                    int[][] playerBoard = new int[fullBoardSize][fullBoardSize];
                    int[][] board = new int[fullBoardSize][fullBoardSize];

                    for (int i=0; i<fullBoardSize; i++) {
                        String[] line = reader.readLine().split(" ");

                        for (int j=0; j<fullBoardSize; j++) {
                            playerBoard[i][j] = Integer.parseInt(line[j]);
                        }
                    }
                    reader.readLine();
                    for (int i=0; i<fullBoardSize; i++) {
                        String[] line = reader.readLine().split(" ");

                        for (int j=0; j<fullBoardSize; j++) {
                            board[i][j] = Integer.parseInt(line[j]);
                        }
                    }
                    var skyBoard = new SkyBoard(board, playerBoard);
                    new MatrixDisplay(skyBoard);

                    JOptionPane.showMessageDialog(mainPanel, "File loaded successfully!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(mainPanel, "Failed to load the file.");
                }
                dispose();
            }
        });




        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(playButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(loadButton);
        mainPanel.add(Box.createVerticalGlue());
        add(mainPanel);
        setVisible(true);
    }

    /**
     * Metoda główna uruchamiająca aplikację.
     * Inicjalizuje i wyświetla okno Menu.
     *
     * @param args argumenty wiersza poleceń (nieużywane)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Menu::new);
    }
}
