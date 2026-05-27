/*
 * Copyright (c) 2026 Jayde Dragyn
 * Licensed under the MIT License. 
 * See LICENSE.MD file in the project root for full license information.
 */

 package jsiv;

 import java.awt.*;
 import java.awt.image.BufferedImage;

public class JSIVSplash {

    private JSIVSplash() { } // private constructor to prevent instantiation

    private static BufferedImage splashImage;

    public static BufferedImage getSplashImage() {
        if (splashImage == null) { createSplashImage(); }
        return splashImage;
    }

    private static void createSplashImage() {
        splashImage = new BufferedImage(300, 255, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pen = splashImage.createGraphics();

        pen.setColor(new Color(255, 0, 0, 255));
        pen.fillRect(0,0, 200,200);

        pen.setColor(new Color(0, 255, 0, 250));
        pen.fillRect(150,30, 150,200);

        pen.setColor(new Color(0, 0, 255, 245));
        pen.fillRect(75,105, 200,150);

        pen.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        pen.setColor(Color.WHITE);
        pen.setFont(new Font("SansSerif", Font.BOLD, 48));
        pen.drawString("JSIV", 25,80);

        pen.setColor(new Color(168, 255, 192, 255));
        pen.setFont(new Font("SansSerif", Font.BOLD, 24));
        pen.drawString("Jayde's", 180,80);

        pen.setColor(new Color(168, 192, 255, 255));
        pen.setFont(new Font("SansSerif", Font.PLAIN, 24));
        pen.drawString("Simple", 115,160);
        pen.drawString("Image",  130,185);
        pen.drawString("Viewer", 145,210);

        pen.dispose();
    }

}
