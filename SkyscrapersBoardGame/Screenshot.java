package pl.edu.pw.elka.prm2t;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Screenshot {
    /**
     * Robi screena JFrame i zapisuje jako obrazek.
     *
     * @param frame JFrame do zapisu.
     */
    public static void takeScreenshot(JFrame frame) {
        String path = System.getProperty("user.home");
        File file = new File(path, "plansza_skyscrapers.png");

        try {
            Rectangle captureRect = new Rectangle(frame.getBounds());
            BufferedImage screenshot = new BufferedImage(captureRect.width, captureRect.height, BufferedImage.TYPE_INT_ARGB);

            frame.paint(screenshot.getGraphics());

            ImageIO.write(screenshot, "png", file);
            JOptionPane.showMessageDialog(frame, "Screenshot saved successfully!");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to save screenshot.");
        }
    }
}
