package pl.edu.pw.elka.prm2t;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Ta klasa generuje planszę z rozmieszczonymi wieżowcami
 */
public class LatinSquareGenerator {

    /**
     * Generuje szablonową macierz kwadratową o rozmiarze n.
     * Kwadrat łaciński to n x n tablica wypełniona n różnymi symbolami,
     * z których każdy występuje dokładnie raz w każdym wierszu i dokładnie raz w każdej kolumnie.
     *
     * @param n rozmiar kwadratowej macierzy
     * @return 2D tablica reprezentująca szablon kwadratu łacińskiego
     */
    public static int[][] generateTemplate(int n) {
        int[][] board = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = (i + j) % n + 1;
            }
        }
        return board;
    }

    /**
     * Generuje losowy kwadrat łaciński o rozmiarze n.
     *
     * @param n rozmiar kwadratu łacińskiego
     * @return 2D tablica reprezentująca losowo przemieszany kwadrat łaciński
     */
    public static int[][] generate(int n) {
        int[][] initialBoard = generateTemplate(n);
        List<int[]> board = Arrays.asList(initialBoard);
        Collections.shuffle(board);
        List<int[]> flippedBoard = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            flippedBoard.add(new int[n]);
            for (int j = 0; j < n; j++) {
                flippedBoard.get(i)[j] = board.get(j)[i];
            }
        }
        Collections.shuffle(flippedBoard);
        return flippedBoard.toArray(new int[0][]);
    }

    /**
     * Drukuje podany kwadrat łaciński.
     *
     * @param board 2D tablica reprezentująca kwadrat łaciński do wydrukowania
     */
    public static void printBoard(int[][] board) {
        int n = board.length;
        for (int[] ints : board) {
            for (int j = 0; j < n; j++) {
                System.out.printf("%2d ", ints[j]);
            }
            System.out.println();
        }
    }

    /**
     * Metoda główna do wykonania generowania i drukowania kwadratu łacińskiego.
     *
     * @param args argumenty wiersza poleceń (nieużywane)
     */
    public static void main(String[] args) {
        int n = 25;

        int[][] board = generate(n);

        printBoard(board);
    }
}
