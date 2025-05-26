package pl.edu.pw.elka.prm2t;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Pencil {
    private final Set<Integer> marks;

    public Pencil() {
        marks = new HashSet<>();
    }

    public void addMark(int mark, SkyBoard board) {
        if (marks.size() < board.getSize()) {
            marks.add(mark);
        }
    }

    public void removeMark(int mark) {
        marks.remove(mark);
    }

    public Set<Integer> getMarks() {
        return marks;
    }

    public String getMarksAsString() {
        return marks.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    public void clearMarks() {
        marks.clear();
    }
}
