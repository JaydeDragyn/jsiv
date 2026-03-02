package jsiv;

import java.util.Optional;
import java.awt.image.BufferedImage;

public class ImageNavigator {

    private BufferedImage currentImage;

    public ImageNavigator() {

    }

    public Optional<BufferedImage> openFile() {
        System.out.println("ImageNavigator.openFile()");
        return Optional.empty();
    }
    
    public Optional<BufferedImage> openNext() {
        System.out.println("ImageNavigator.openNext()");
        return Optional.empty();
    }

    public Optional<BufferedImage> openPrevious() {
        System.out.println("ImageNavigator.openPrevious()");        
        return Optional.empty();
    }

    public boolean canNavigate() {
        return false;
    }
}

