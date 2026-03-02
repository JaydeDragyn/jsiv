package jsiv;

import java.util.Optional;
import java.awt.image.BufferedImage;

public class ImageNavigator {

    private BufferedImage currentImage;

    public ImageNavigator() {

    }

    public Optional<BufferedImage> openFile() {
        System.out.println("imageNavigator.openFile() invoked");
        return Optional.empty();
    }
    
    public Optional<BufferedImage> openNext() {
        System.out.println("imageNavigator.openNext() invoked");
        return Optional.empty();
    }

    public Optional<BufferedImage> openPrevious() {
        System.out.println("imageNavigator.openPrevious() invoked");        
        return Optional.empty();
    }

    public boolean canNavigate() {
        return false;
    }
}

