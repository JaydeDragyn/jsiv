package jsiv;

import java.util.Optional;
import java.awt.image.BufferedImage;

public interface ImageNavigatorListener {

    public void allowNavigationChanged(boolean allowNavigation);
    public void newImageAvailable(Optional<BufferedImage> image);
    
}
