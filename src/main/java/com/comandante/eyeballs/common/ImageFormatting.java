package com.comandante.eyeballs.common;


import com.sun.tools.doclint.Entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageFilter;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Date;

import static com.sun.tools.doclint.Entity.image;

public class ImageFormatting {

    public static BufferedImage writeDate(BufferedImage bi, Date date) {
        BufferedImage image = createLargerImage(bi);
        Graphics g = image.getGraphics();
        g.setFont(g.getFont().deriveFont(18f));
        g.setColor(Color.GRAY);
        g.drawString(date.toString(), 5, image.getHeight() - 5);
        g.dispose();
        return image;
    }

    public static BufferedImage createLargerImage(BufferedImage bi) {
        BufferedImage tile = new BufferedImage(bi.getWidth(), bi.getHeight() + 22, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = tile.getGraphics();
        g.drawImage(bi,0,0, null);
        return tile;
    }
}
