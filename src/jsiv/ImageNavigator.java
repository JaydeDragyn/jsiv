package jsiv;

import java.util.Optional;
import java.awt.image.BufferedImage;

public class ImageNavigator {

    private ImageNavigatorListener imageNavigatorListener;
    private BufferedImage currentImage;

    public ImageNavigator(ImageNavigatorListener listener) {
        this.imageNavigatorListener = listener;
    }

    public void openSplashFile() {
        System.out.println("ImageNavigator.openSplashFile()");
        imageNavigatorListener.newImageAvailable(Optional.empty());
        imageNavigatorListener.allowNavigationChanged(false);
    }

    public void openFile() {
        System.out.println("ImageNavigator.openFile()");
        imageNavigatorListener.newImageAvailable(Optional.empty());
        imageNavigatorListener.allowNavigationChanged(true);
    }
    
    public void openNext() {
        System.out.println("ImageNavigator.openNext()");
        imageNavigatorListener.newImageAvailable(Optional.empty());
    }

    public void openPrevious() {
        System.out.println("ImageNavigator.openPrevious()");        
        imageNavigatorListener.newImageAvailable(Optional.empty());
    }
}

