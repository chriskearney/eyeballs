package com.comandante.eyeballs.common;


import com.comandante.eyeballs.camera.webcam.MotionDetectedEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class ImageFormatting {

    public static BufferedImage writeDateAndMotionDetails(MotionDetectedEvent event, Date date) {
        BufferedImage image = createLargerImage(event.getCurrentOriginal());
        final String writeString = "points: " + event.getPoints().size() + "| area: " + round(event.getArea(), 2) + "| cog: " + event.getCog().getX() + "x" + event.getCog().getY();
        return writeImageDetails(image, writeString);
    }

    public static BufferedImage writeDate(BufferedImage bi, Date date) {
        BufferedImage image = createLargerImage(bi);
        final String writeString = date.toString();
        return writeImageDetails(image, writeString);
    }

    public static BufferedImage writeImageDetails(BufferedImage bi, String details) {
        BufferedImage image = createLargerImage(bi);
        Graphics g = image.getGraphics();
        g.setFont(g.getFont().deriveFont(18f));
        g.setColor(Color.GRAY);
        g.drawString(details , 5, image.getHeight() - 5);
        g.dispose();
        return image;
    }

    public static BufferedImage createLargerImage(BufferedImage bi) {
        BufferedImage tile = new BufferedImage(bi.getWidth(), bi.getHeight() + 22, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = tile.getGraphics();
        g.drawImage(bi,0,0, null);
        return tile;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
