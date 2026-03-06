package jsiv;

import java.util.Optional;
import java.awt.image.BufferedImage;

public class ImageNavigator {

    private ImageNavigatorListener imageNavigatorListener;
    private BufferedImage currentImage;

    public ImageNavigator(ImageNavigatorListener listener) {
        this.imageNavigatorListener = listener;
    }

    public void openFile() {
        System.out.println("ImageNavigator.openFile()");
        imageNavigatorListener.newImageAvailable(Optional.empty());
        imageNavigatorListener.navigationAvailabilityChanged(true);
        imageNavigatorListener.navigationIndexChanged(3, 42);
    }
    
    public void openNext() {
        System.out.println("ImageNavigator.openNext()");
        imageNavigatorListener.newImageAvailable(Optional.empty());
        imageNavigatorListener.navigationIndexChanged(4, 42);
    }

    public void openPrevious() {
        System.out.println("ImageNavigator.openPrevious()");        
        imageNavigatorListener.newImageAvailable(Optional.empty());
        imageNavigatorListener.navigationIndexChanged(2, 42);
    }
}

