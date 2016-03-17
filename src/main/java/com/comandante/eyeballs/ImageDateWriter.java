package com.comandante.eyeballs;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;

public class ImageDateWriter {

    public static void writeDate(BufferedImage image) {
        Graphics g = image.getGraphics();
        g.setFont(g.getFont().deriveFont(24f));
        g.setColor(Color.GREEN);
        g.drawString(new Date().toString(), 10, image.getHeight() - 10);
        g.dispose();
    }
}
