package com.comandante.eyeballs.common;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Date;

public class ImageFormatting {

    public static void writeDate(BufferedImage image, Date date) {
        Graphics g = image.getGraphics();
        g.setFont(g.getFont().deriveFont(24f));
        g.setColor(Color.GREEN);
        g.drawString(date.toString(), 10, image.getHeight() - 10);
        g.dispose();
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
