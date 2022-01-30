/*
 * Copyright (C) 2022 <mark@makr.zone>
 * 
 * This file is part of OpenPnP.
 * 
 * OpenPnP is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * OpenPnP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with OpenPnP. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * For more information about OpenPnP visit http://openpnp.org
 */

package org.openpnp.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class HsvIndicator extends JComponent {

    public HsvIndicator() {
        super();
    }

    private int minHue;
    private int maxHue;
    private int minSaturation;
    private int maxSaturation;
    private int minValue;
    private int maxValue;

    @Override
    public Dimension getPreferredSize() {
        Dimension superDim = super.getPreferredSize();
        int width = (int)Math.max(superDim.getWidth(), 192);
        int height = (int)Math.max(superDim.getHeight(), 128); 
        return new Dimension(width, height);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        int diameter = Math.min(width, height);
        int unit = diameter/5;
        double radius = diameter/2.0; 
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int [] data = new int[width*height];
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                double dx = x - radius;
                double dy = y - radius;
                double r = Math.sqrt(dx*dx + dy*dy);
                if (r < radius) {
                    double saturation = 255.0*r/radius;
                    double hue = 255.0*((1.0 + Math.atan2(-dy, dx)/Math.PI/2.0) % 1.0);
                    double alpha = Math.min(1.0, radius - r);
                    double dHue = r/64;
                    double dSat = radius/255;
                    double included = Math.max(0, Math.min(1.0, Math.min(
                            dHue*(minHue <= maxHue ? Math.min(hue - minHue, maxHue - hue) : Math.max(hue - minHue, maxHue - hue)),
                            dSat*Math.min(saturation - minSaturation, maxSaturation - saturation))));
                    alpha = alpha*(included*0.8 + 0.2);
                    Color color = makeHsvColor((int)hue, (isEnabled() ? (int)saturation : 0), maxValue, (int) (255*alpha));
                    data[y*width + x] = color.getRGB();
                }
            }
        }
        
        for (int y = 0; y < diameter; y++) {
            int value = 255 - 255*y/diameter; 
            int hue = (minHue < maxHue ? (minHue + maxHue)/2 : ((maxHue - minHue)/2) & 0xFF);
            double dVal = diameter/255.0;
            double included = Math.max(0, Math.min(1.0, 
                    dVal*(Math.min(value - minValue, maxValue - value))));
            double alpha = included*0.8 + 0.2;
            int x0 = diameter + unit;
            int x1 = Math.min(diameter + unit*2, width);
            for (int x = x0; x < x1; x++) {
                double r = (double)(x - x0)/(x1 - x0);
                int saturation = isEnabled() && included > 0 ? 
                        ((int) (r*(maxSaturation - minSaturation) + minSaturation)) : 0;
                Color color = makeHsvColor(hue, 
                        saturation, 
                                value, (int) (255*alpha));
                data[y*width + x] = color.getRGB();
            }
        }
        image.setRGB(0, 0, width, height, data, 0, width);
        g2d.drawImage(image, 0, 0, width, height, 0, 0, width, height, null);
    }

    public int getMinHue() {
        return minHue;
    }

    public void setMinHue(int minHue) {
        this.minHue = minHue;
        repaint();
    }

    public int getMaxHue() {
        return maxHue;
    }

    public void setMaxHue(int maxHue) {
        this.maxHue = maxHue;
        repaint();
    }

    public int getMinSaturation() {
        return minSaturation;
    }

    public void setMinSaturation(int minSaturation) {
        this.minSaturation = minSaturation;
        repaint();
    }

    public int getMaxSaturation() {
        return maxSaturation;
    }

    public void setMaxSaturation(int maxSaturation) {
        this.maxSaturation = maxSaturation;
        repaint();
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
        repaint();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        repaint();
    }

    private Color makeHsvColor(int hue, int saturation, int value, int alpha){
        double s = saturation/255.0;
        double v = value/255.0;
        double h = 360.0*hue/255;
        double C = s*v;
        double X = C*(1 - Math.abs(((h/60.0) % 2) - 1.0));
        double m = v - C;
        double r, g, b;
        if (h >= 0 && h < 60){
            r = C; g = X; b = 0;
        }
        else if (h >= 60 && h < 120){
            r = X; g = C; b = 0;
        }
        else if (h >= 120 && h < 180){
            r = 0; g = C; b = X;
        }
        else if (h >= 180 && h < 240){
            r = 0; g = X; b = C;
        }
        else if (h >= 240 && h < 300){
            r = X; g = 0; b = C;
        }
        else{
            r = C; g = 0; b = X;
        }
        int red = (int) ((r+m)*255);
        int green = (int) ((g+m)*255);
        int blue = (int) ((b+m)*255);
        return new Color(red, green, blue, alpha);
    }
}
