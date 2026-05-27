/*
 * Copyright (c) 2026 Jayde Dragyn
 * Licensed under the MIT License. 
 * See LICENSE.MD file in the project root for full license information.
 */

package jsiv;

import java.awt.image.BufferedImage;

public interface ImageNavigatorListener {

    void navigationAvailabilityChanged(boolean available);
    void newImageLoaded(String imageName,
                        BufferedImage image,
                        int index, int total);
    
}
