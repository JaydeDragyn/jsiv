package jsiv;

import java.awt.image.BufferedImage;

public interface ImageNavigatorListener {

    void navigationAvailabilityChanged(boolean available);
    void newImageLoaded(String imageName,
                        BufferedImage image,
                        int index, int total);
    
}
