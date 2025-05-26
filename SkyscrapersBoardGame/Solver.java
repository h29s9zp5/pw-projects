package pl.edu.pw.elka.prm2t;

public class Solver {
    public static void solve(SkyBoard board) {
        int size = board.getSize() + 2; // pełny rozmiar planszy z podpowiedziami
        for (int i = 1; i < size - 1; i++) { // pętla przez wiersze wewnętrznej planszy
            for (int j = 1; j < size - 1; j++) { // pętla przez kolumny wewnętrznej planszy
                int correctValue = board.getCorrect(i, j);
                board.set(i, j, correctValue);
            }
        }
    }
}