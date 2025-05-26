package pl.edu.pw.elka.prm2t;

import java.util.Arrays;

import static pl.edu.pw.elka.prm2t.LatinSquareGenerator.generate;

/**
 * Klasa SkyBoard reprezentuje planszę gry SkyScrapers z podpowiedziami.
 */
public class SkyBoard {
    private final int[][] board;
    private final int[][] playerBoard;
    private final int size;

    /**
     * Konstruktor klasy SkyBoard.
     *
     * @param size  rozmiar planszy
     */
    public SkyBoard(int size) {
        this.size = size;

        board = new int[size+2][size+2];
        playerBoard = new int[size+2][size+2];

        int[][] game = generate(size);
        generateClues(game);
        for (int i = 0; i < board.length; i++) {
            playerBoard[i] = Arrays.copyOf(board[i], board[i].length);
        }
        placeGameOnBoard(game, board);
    }

    public SkyBoard(int[][] boardWithClues, int[][] playerBoard) {
        board = boardWithClues;
        this.playerBoard = playerBoard;
        size = boardWithClues.length-2;
    }


    public void printBoard() {
        LatinSquareGenerator.printBoard(playerBoard);
        System.out.println();
        LatinSquareGenerator.printBoard(board);
    }

    public int getSize() {
        return size;
    }

    /**
     * Zwraca cyfrę stojącą w konkretnym polu.
     * @param x współrzędna pozioma pola
     * @param y współrzędna pionowa pola
     */
    public int get(int x, int y) {
        return playerBoard[x][y];
    }

    /**
     * Zwraca cyfrę, która powinna stać w konkretnym polu.
     * @param x współrzędna pozioma pola
     * @param y współrzędna pionowa pola
     */
    public int getCorrect(int x, int y) {
        return board[x][y];
    }

    /**
     * Zmienia cyfrę stojącą w konkretnym polu. Służy w celach aktualizacji planszy po wpisaniu liczby
     * @param x współrzędna pozioma pola
     * @param y współrzędna pionowa pola
     */
    public void set(int x, int y, int value) {
        playerBoard[x][y] = value;
    }

    /**
     * Metoda oblicza, które budynki są widoczne z danej strony.
     */
    private void generateClues(int[][] game) {
        for (int i = 0; i < size; i++) {
            int count = 0;
            int maxValue = 0;
            for (int j = 0; j < size; j++) {
                if (game[i][j] > maxValue) {
                    maxValue = game[i][j];
                    count++;
                }
            }
            board[i+1][0] = count;
        }
        for (int j = 0; j < size; j++) {
            int count = 0;
            int maxValue = 0;
            for (int i = 0; i < size; i++) {
                if (game[i][j] > maxValue) {
                    maxValue = game[i][j];
                    count++;
                }
            }
            board[0][j+1] = count;
        }
        for (int i = 0; i < size; i++) {
            int count = 0;
            int maxValue = 0;
            for (int j = size - 1; j >= 0; j--) {
                if (game[i][j] > maxValue) {
                    maxValue = game[i][j];
                    count++;
                }
            }
            board[i+1][size+1] = count;
        }
        for (int j = 0; j < size; j++) {
            int count = 0;
            int maxValue = 0;
            for (int i = size - 1; i >= 0; i--) {
                if (game[i][j] > maxValue) {
                    maxValue = game[i][j];
                    count++;
                }
            }
            board[size+1][j+1] = count;
        }
    }

    /**
     * Umieszcza wygenerowaną grę na planszy z podpowiedziami.
     *
     * @param g macierz gry
     * @param b macierz planszy z podpowiedziami
     */
    public void placeGameOnBoard(int[][] g, int[][] b) {
        for (int i = 0; i < size; i++) {
            System.arraycopy(g[i], 0, b[i + 1], 1, size);
        }
    }
    public boolean isBoardCorrect() {
        for (int i = 1; i <= size; i++) {
            for (int j = 1; j <= size; j++) {
                if (playerBoard[i][j] != board[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
