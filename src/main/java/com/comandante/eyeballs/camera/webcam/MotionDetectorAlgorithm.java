package com.comandante.eyeballs.camera.webcam;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public interface MotionDetectorAlgorithm {

    BufferedImage prepareImage(BufferedImage original);

    boolean detect(BufferedImage previousModified, BufferedImage currentModified);

    Point getCog();

    double getArea();

    void setPointRange(int i);

    void setMaxPoints(int i);

    int getPointRange();

    int getMaxPoints();

    ArrayList<Point> getPoints();

    BufferedImage getLastImage();

    }
