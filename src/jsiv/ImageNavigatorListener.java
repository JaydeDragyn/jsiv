package jsiv;

import java.util.Optional;
import java.awt.image.BufferedImage;

public interface ImageNavigatorListener {

    public void navigationAvailabilityChanged(boolean available);
    public void newImageAvailable(Optional<BufferedImage> image);
    public void navigationIndexChanged(int index, int total);
    
}
