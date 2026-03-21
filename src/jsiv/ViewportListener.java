package jsiv;

import java.awt.*;

public interface ViewportListener {
    void requestOpenNext();
    void requestOpenPrevious();
    void imageSizeChanged(Dimension newImageSize);
    void viewportSizeChanged(Dimension newViewportSize);
    void zoomChanged(double newZoomLevel);
    void newColorUnderPointer(Color color);
    void requestCopyColorToClipboard(Color color);
}
