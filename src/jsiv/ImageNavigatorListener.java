package jsiv;

import java.util.Optional;
import java.awt.image.BufferedImage;

public interface ImageNavigatorListener {

    public void navigationAvailabilityChanged(boolean available);
    public void newImageLoaded(String imageName,
                                BufferedImage image,
                                int index, int total);
    
}
