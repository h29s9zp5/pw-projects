package pl.edu.pw.elka.prm2t;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class SingleDigitFilter extends DocumentFilter {
    private final MatrixDisplay matrixDisplay;

    public SingleDigitFilter(MatrixDisplay matrixDisplay) {
        this.matrixDisplay = matrixDisplay;
    }
}
//zakom entowane bo nie dziala
//    @Override
//    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
//        if (!matrixDisplay.isPencilMode() && string != null && !string.equals("0") && string.matches("\\d") && fb.getDocument().getLength() == 0) {
//            fb.insertString(offset, string, attr);
//        } else if (matrixDisplay.isPencilMode()) {
//            fb.insertString(offset, string, attr);
//        }
//    }
//
//    @Override
//    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
//        if (!matrixDisplay.isPencilMode() && text != null && !text.equals("0") && text.matches("\\d") && fb.getDocument().getLength() == 0) {
//            fb.replace(offset, length, text, attrs);
//        } else if (matrixDisplay.isPencilMode()) {
//            fb.replace(offset, length, text, attrs);
