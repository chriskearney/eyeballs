package com.comandante.eyeballs.camera;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;



public class MotionDetectedEvent {

    private final MotionDetector motionDetector;
    private final BufferedImage previousOriginal;
    private final BufferedImage currentOriginal;
    private final double area;
    private final Point cog;
    private final ArrayList<Point> points;

    public MotionDetectedEvent(MotionDetector motionDetector, BufferedImage previousOriginal, BufferedImage currentOriginal, double area, Point cog, ArrayList<Point> points) {
        this.motionDetector = motionDetector;
        this.previousOriginal = previousOriginal;
        this.currentOriginal = currentOriginal;
        this.area = area;
        this.cog = cog;
        this.points = points;
    }

    public MotionDetector getMotionDetector() {
        return motionDetector;
    }

    public BufferedImage getPreviousOriginal() {
        return previousOriginal;
    }

    public BufferedImage getCurrentOriginal() {
        return currentOriginal;
    }

    public double getArea() {
        return area;
    }

    public Point getCog() {
        return cog;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }
}
