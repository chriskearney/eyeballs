package com.comandante.eyeballs.camera.webcam;

import com.github.sarxos.webcam.util.jh.JHBlurFilter;
import com.github.sarxos.webcam.util.jh.JHGrayFilter;
import com.google.common.collect.EvictingQueue;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MotionDetectorDefaultAlgorithm implements MotionDetectorAlgorithm {

    public final EvictingQueue<BufferedImage> imageQueue = EvictingQueue.create(1);
    public static final int DEFAULT_PIXEL_THREASHOLD = 25;
    public static final double DEFAULT_AREA_THREASHOLD = 0.2;
    private volatile int pixelThreshold = DEFAULT_PIXEL_THREASHOLD;
    private volatile double areaThreshold = DEFAULT_AREA_THREASHOLD;
    private double area = 0;
    private Point cog = null;
    private final JHBlurFilter blur = new JHBlurFilter(6, 6, 1);
    private final JHGrayFilter gray = new JHGrayFilter();
    public MotionDetectorDefaultAlgorithm(int pixelThreshold, double areaThreshold) {
        setPixelThreshold(pixelThreshold);
        setAreaThreshold(areaThreshold);
    }

    @Override
    public BufferedImage prepareImage(BufferedImage original) {
        BufferedImage modified = blur.filter(original, null);
        modified = gray.filter(modified, null);
        return modified;
    }

    @Override
    public boolean detect(BufferedImage previousModified, BufferedImage currentModified) {
        imageQueue.add(currentModified);
        points.clear();
        int p = 0;

        int cogX = 0;
        int cogY = 0;

        int w = currentModified.getWidth();
        int h = currentModified.getHeight();

        int j = 0;
        if (previousModified != null) {
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {

                    int cpx = currentModified.getRGB(x, y);
                    int ppx = previousModified.getRGB(x, y);
                    int pid = combinePixels(cpx, ppx) & 0x000000ff;

                    if (pid >= pixelThreshold) {
                        Point pp = new Point(x, y);
                        boolean keep = j < maxPoints;

                        if (keep) {
                            for (Point g : points) {
                                if (g.x != pp.x || g.y != pp.y) {
                                    if (pp.distance(g) <= range) {
                                        keep = false;
                                        break;
                                    }
                                }
                            }
                        }

                        if (keep) {
                            points.add(new Point(x, y));
                            j += 1;
                        }

                        cogX += x;
                        cogY += y;
                        p += 1;
                    }
                }
            }
        }

        area = p * 100d / (w * h);

        if (area >= areaThreshold) {
            cog = new Point(cogX / p, cogY / p);
            return true;
        } else {
            cog = new Point(w / 2, h / 2);
            return false;
        }
    }

    @Override
    public Point getCog() {
        return this.cog;
    }

    @Override
    public double getArea() {
        return this.area;
    }

    public void setPixelThreshold(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Pixel intensity threshold cannot be negative!");
        }
        if (threshold > 255) {
            throw new IllegalArgumentException("Pixel intensity threshold cannot be higher than 255!");
        }
        this.pixelThreshold = threshold;
    }

    public void setAreaThreshold(double threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Area fraction threshold cannot be negative!");
        }
        if (threshold > 100) {
            throw new IllegalArgumentException("Area fraction threshold cannot be higher than 100!");
        }
        this.areaThreshold = threshold;
    }

    private static int combinePixels(int rgb1, int rgb2) {

        // first ARGB

        int a1 = (rgb1 >> 24) & 0xff;
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;

        // second ARGB

        int a2 = (rgb2 >> 24) & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;

        r1 = clamp(Math.abs(r1 - r2));
        g1 = clamp(Math.abs(g1 - g2));
        b1 = clamp(Math.abs(b1 - b2));

        // in case if alpha is enabled (translucent image)

        if (a1 != 0xff) {
            a1 = a1 * 0xff / 255;
            int a3 = (255 - a1) * a2 / 255;
            r1 = clamp((r1 * a1 + r2 * a3) / 255);
            g1 = clamp((g1 * a1 + g2 * a3) / 255);
            b1 = clamp((b1 * a1 + b2 * a3) / 255);
            a1 = clamp(a1 + a3);
        }

        return (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
    }

    private static int clamp(int c) {
        if (c < 0) {
            return 0;
        }
        if (c > 255) {
            return 255;
        }
        return c;
    }

    ArrayList<Point> points = new ArrayList<Point>();

    public static final int DEFAULT_RANGE = 50;

    public static final int DEFAULT_MAX_POINTS = 100;

    private int range = DEFAULT_RANGE;

    private int maxPoints = DEFAULT_MAX_POINTS;

    public void setPointRange(int i){
        range = i;
    }

    public int getPointRange(){
        return range;
    }

    public void setMaxPoints(int i){
        maxPoints = i;
    }

    public int getMaxPoints(){
        return maxPoints;
    }

    public ArrayList<Point> getPoints(){
        return points;
    }

    public BufferedImage getLastImage() {
        return imageQueue.element();
    }
}