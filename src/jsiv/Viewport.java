package jsiv;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class Viewport extends JPanel {

    public enum FocusMode {
        WINDOW_CENTER, POINTER
    };

    private final ViewportListener viewportListener;
    private boolean navigationAvailable;

    private BufferedImage image;
    private BufferedImage nextButton;
    private BufferedImage previousButton;
    private Rectangle nextButtonArea;
    private Rectangle previousButtonArea;
    private boolean inButtonAreas;
    private Dimension imageSize = new Dimension(0,0);
    private Dimension imageScaledSize = new Dimension(0,0);
    private int imageOffsetX;
    private int imageOffsetY;
    private Point maxSmallOffset;
    private Point minLargeOffset;
    private Dimension viewportSize = new Dimension(0,0);
    private Point viewportCenter = new Point(0,0);
    private double zoomLevel = 1.0;
    private static final double MIN_PIXELS_ON_ZOOM = 8.0;
    private int openPreviousBorder;
    private int openNextBorder;
    private Point mouseLocation = new Point(0,0);
    private Point mouseLastLocation = new Point(0,0);

    private Point pressPoint = new Point(0,0);
    private boolean leftButtonPressed;
    private boolean rightButtonPressed;
    private boolean rightButtonDragged;
    private static final int MOUSE_DRAG_THRESHOLD = 1;
    private static final int POINTER_DRIFT_THRESHOLD = 4;

    public static final int PAN_FINE = 1;
    public static final int PAN_COARSE = 10;


    public Viewport(ViewportListener viewportListener) {
        this.viewportListener = viewportListener;
        navigationAvailable = false;
        
        initResizeListener();
        initMouseListeners();

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 600));
        setSplashImage();
        createNavigationButtons();
    }

    public void setImage(BufferedImage newImage) {
        image = newImage;
        imageSize = new Dimension(image.getWidth(), image.getHeight());
        viewportListener.imageSizeChanged(imageSize);
        imageScaledSize = new Dimension(imageSize);
        updateClampLimits();
        resetZoom();
    }
    
    public void zoomIn(FocusMode focusMode) {
        // Check if we can zoom in any further.  Limit is MIN_PIXELS_ON_ZOOM
        // visible in the viewport along either axis.  If zooming in would
        // reduce the visible pixels along either axis to less than that (even
        // if only by less than one pixel) then we abort the zoom in action.
        double zoomLevelQuery = zoomLevel * 2.0;
        if (((viewportSize.width / zoomLevelQuery) < MIN_PIXELS_ON_ZOOM) ||
            ((viewportSize.height / zoomLevelQuery) < MIN_PIXELS_ON_ZOOM)) {
            return;
        }

        setZoom(zoomLevelQuery, focusMode);
    }
    
    public void zoomOut(FocusMode focusMode) {
        // Check if we can zoom out any further.  Limit is the largest
        // of 1x or whatever is needed to bring the scaled image size to fit
        // within the viewport.  If zooming out would try to reduce the
        // scaled image below that, then the zoom out action will be ignored.
        double zoomLevelQuery = zoomLevel / 2.0;
        if (zoomLevelQuery < 1.0) {
            // check the current size, not the potential new size.
            if ((viewportSize.width >= imageScaledSize.width) &&
                (viewportSize.height >= imageScaledSize.height)) { return; }
        }

        setZoom(zoomLevelQuery, focusMode);
    }
    
    public void resetZoom() {
        zoomLevel = 1.0;

        while (imageBelowHalfViewportSize()) { zoomIn(FocusMode.WINDOW_CENTER); }
        while (imageExceedsViewportBounds()) { zoomOut(FocusMode.WINDOW_CENTER); }
        centerImage(FocusMode.WINDOW_CENTER);

        repaint();
    }
    
    private boolean imageExceedsViewportBounds() {
        return (imageScaledSize.width > viewportSize.width)
                || (imageScaledSize.height > viewportSize.height);
    }

    private boolean imageBelowHalfViewportSize() {
        return (imageScaledSize.width < (viewportSize.width / 2))
                || (imageScaledSize.height < (viewportSize.height / 2));
    }

    private void setZoom(double newZoomLevel, FocusMode focusMode) {

        Point focusPoint = switch (focusMode) {
            case WINDOW_CENTER -> new Point(viewportCenter);
            case POINTER       -> new Point(mouseLocation);
        };

        int focusPixelX = (int)((focusPoint.x - imageOffsetX) / zoomLevel);
        int focusPixelY = (int)((focusPoint.y - imageOffsetY) / zoomLevel);

        zoomLevel = newZoomLevel;
        imageScaledSize = new Dimension((int)(imageSize.width * zoomLevel),
                                        (int)(imageSize.height * zoomLevel));

        imageOffsetX = focusPoint.x - (int)(focusPixelX * zoomLevel) - (int)(zoomLevel / 2);
        imageOffsetY = focusPoint.y - (int)(focusPixelY * zoomLevel) - (int)(zoomLevel / 2);

        updateClampLimits();
        clampImageToViewport();
        repaint();
        viewportListener.zoomChanged(zoomLevel);
    }

    public void centerImage(FocusMode focusMode) {
        switch (focusMode) {
            case WINDOW_CENTER :
                imageOffsetX = viewportCenter.x - (imageScaledSize.width / 2);
                imageOffsetY = viewportCenter.y - (imageScaledSize.height / 2);
                repaint();
                break;
            case POINTER :
                // pan the image by the difference between the
                // center of the viewport and the mouse pointer
                panImage(viewportCenter.x - pressPoint.x,
                        viewportCenter.y - pressPoint.y);
                break;
        };
    }

    public void panImage(int dx, int dy) {
        imageOffsetX += dx;
        imageOffsetY += dy;
        clampImageToViewport();
        repaint();
    }

    public void setNavigationAvailability(boolean available) {
        navigationAvailable = available;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) { return; }

        Graphics2D pen = (Graphics2D) g.create();

        pen.translate(imageOffsetX, imageOffsetY);
        pen.scale(zoomLevel, zoomLevel);
        pen.drawImage(image, 0,0, null);
        pen.dispose();

        if (inButtonAreas) {
            pen = (Graphics2D) g.create();
            int y = (viewportSize.height / 2) - 38;
            pen.drawImage(previousButton, 0,y, null);
            pen.drawImage(nextButton, viewportSize.width - 50,y, null);
            pen.dispose();
        }
    }

    private void setSplashImage() {
        image = new BufferedImage(300, 255, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pen = image.createGraphics();
        
        pen.setColor(new Color(255, 0, 0, 255));
        pen.fillRect(0,0, 200,200);
        
        pen.setColor(new Color(0, 255, 0, 250));
        pen.fillRect(150,30, 150,200);
        
        pen.setColor(new Color(0, 0, 255, 245));
        pen.fillRect(75,105, 200,150);
        
        pen.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        pen.setColor(Color.WHITE);
        pen.setFont(new Font("SansSerif", Font.BOLD, 48));
        pen.drawString("JSIV", 25,80);
        
        pen.setColor(new Color(168, 255, 192, 255));
        pen.setFont(new Font("SansSerif", Font.BOLD, 24));
        pen.drawString("Jayde's", 180,80);
        
        pen.setColor(new Color(168, 192, 255, 255));
        pen.setFont(new Font("SansSerif", Font.PLAIN, 24));
        pen.drawString("Simple", 115,160);
        pen.drawString("Image",  130,185);
        pen.drawString("Viewer", 145,210);
        
        pen.dispose();

        // manually set Viewport fields because this method is called
        // during the Viewport constructor, and the Viewport will have
        // a size of 0,0 at this time.
        zoomLevel = 1.0;
        imageOffsetX = 250;
        imageOffsetY = 172;
        imageSize = new Dimension(image.getWidth(), image.getHeight());
        viewportListener.imageSizeChanged(imageSize);
        imageScaledSize = new Dimension(imageSize);
    }

    private void createNavigationButtons() {
        Polygon chev = new Polygon();
        chev.addPoint( 5, 18);
        chev.addPoint(23, 36);
        chev.addPoint(23, 37);
        chev.addPoint( 5, 55);
        chev.addPoint( 0, 50);
        chev.addPoint( 0, 49);
        chev.addPoint(12, 37);
        chev.addPoint(12, 36);
        chev.addPoint( 0, 24);
        chev.addPoint( 0, 23);
        chev.addPoint( 5, 18);

        nextButton = new BufferedImage(50,75, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pen = nextButton.createGraphics();
        pen.setColor(new Color( 32, 32, 32,168));
        pen.fillRect(0,0, 50,75);
        pen.setColor(new Color(192,192,192,168));
        pen.drawString("Next", 12,12);
        pen.drawString("Image", 8,70);
        pen.translate(2,0);
        pen.fillPolygon(chev);
        pen.translate(22,0);
        pen.fillPolygon(chev);
        pen.dispose();

        previousButton = new BufferedImage(50,75, BufferedImage.TYPE_INT_ARGB);
        pen = previousButton.createGraphics();
        pen.setColor(new Color( 32, 32, 32,168));
        pen.fillRect(0,0, 50,75);
        pen.setColor(new Color(192,192,192,168));
        pen.drawString("Prev", 12,12);
        pen.drawString("Image", 8,70);
        pen.translate(48,0);
        pen.scale(-1,1);
        pen.fillPolygon(chev);
        pen.translate(22,0);
        pen.fillPolygon(chev);
        pen.dispose();

    }

    private void updateViewportSize() {
        viewportSize = new Dimension(getSize());
        viewportCenter = new Point(viewportSize.width / 2,
                                   viewportSize.height / 2);
        int y = (viewportSize.height / 2) - (nextButton.getHeight() / 2);
        nextButtonArea = new Rectangle(
                        viewportSize.width - nextButton.getWidth(),y,
                        nextButton.getWidth(), nextButton.getHeight());
        previousButtonArea = new Rectangle(
                        0, y,
                        previousButton.getWidth(), previousButton.getHeight());
        viewportListener.viewportSizeChanged(viewportSize);
    }

    private void updateClampLimits() {
        // minSmallOffset is always going to be 0,0
        maxSmallOffset = new Point(viewportSize.width - imageScaledSize.width,
                                       viewportSize.height - imageScaledSize.height);
        minLargeOffset = new Point(-(imageScaledSize.width - viewportSize.width),
                                     -(imageScaledSize.height - viewportSize.height));
        // maxLargeOffset is always going to be 0,0
    }

    private void clampImageToViewport() {
        // for each axis:
        // if the viewport is larger than the image, use maxSmallOffset
        // otherwise use minLargeOffset
        // No minSmallOffset or maxLargeOffset because for this application,
        // both will always be 0,0, so they are hardcoded
        imageOffsetX = (viewportSize.width >= imageScaledSize.width)?
                        Math.max(0, Math.min(maxSmallOffset.x, imageOffsetX)):
                        Math.max(minLargeOffset.x, Math.min(0, imageOffsetX));
        imageOffsetY = (viewportSize.height >= imageScaledSize.height)?
                        Math.max(0, Math.min(maxSmallOffset.y, imageOffsetY)):
                        Math.max(minLargeOffset.y, Math.min(0, imageOffsetY));
    }

    private boolean inNavigationButtonAreas() {
        return (nextButtonArea.contains(mouseLocation)
                || previousButtonArea.contains(mouseLocation));
    }

    private void handleLeftClick() {
        Dimension distanceFromPress = new Dimension(
                    Math.abs(mouseLocation.x - pressPoint.x),
                    Math.abs(mouseLocation.y - pressPoint.y));

        if (distanceFromPress.width > POINTER_DRIFT_THRESHOLD ||
            distanceFromPress.height > POINTER_DRIFT_THRESHOLD) {
                return;
        }
        if (previousButtonArea.contains(pressPoint)) {
            openPreviousRequested();
        }
        if (nextButtonArea.contains(pressPoint)) {
            openNextRequested();
        }
    }

    private void openNextRequested() {
        if (!navigationAvailable) { return; }
        viewportListener.requestOpenNext();
        repaint();
    }
    
    private void openPreviousRequested() {
        if (!navigationAvailable) { return; }
        viewportListener.requestOpenPrevious();
        repaint();
    }

    private void initResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateViewportSize();
                updateClampLimits();
                clampImageToViewport();
                repaint();
            }
        });
    }

    private void initMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ignore if a button is already pressed
                // only one should be pressed at a time,
                // so this will cause us to ignore the other one
                if (leftButtonPressed || rightButtonPressed) { return; }
                
                // record the position the user pressed the button
                pressPoint.setLocation(mouseLocation);
                
                // Left button?
                if (e.getButton() == MouseEvent.BUTTON1) {
                    leftButtonPressed = true;
                }
                
                // Right button?
                if (e.getButton() == MouseEvent.BUTTON3) {
                    rightButtonPressed = true;
                    rightButtonDragged = false;
                }
            }
            
            public void mouseReleased(MouseEvent e) {
                mouseLocation = e.getPoint();

                // LMB
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // verify the correct button was released or don't act
                    if (!leftButtonPressed) { return; }
                    handleLeftClick();
                    leftButtonPressed = false;
                }
                
                // RMB
                if (e.getButton() == MouseEvent.BUTTON3) {
                    // verify the correct button was released or don't act
                    if (!rightButtonPressed) { return; }
                    
                    // if this wasn't a drag, it's a click.
                    if (!rightButtonDragged) {
                        centerImage(FocusMode.POINTER);
                    }
                    rightButtonPressed = false;
                    rightButtonDragged = false;
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseLastLocation.setLocation(mouseLocation);
                mouseLocation = e.getPoint();
                
                int focusPixelX = (int)((mouseLocation.x - imageOffsetX) / zoomLevel);
                int focusPixelY = (int)((mouseLocation.y - imageOffsetY) / zoomLevel);
                
                if (navigationAvailable && inNavigationButtonAreas()) {
                    if (!inButtonAreas) {
                        inButtonAreas = true;
                        repaint();
                    }
                } else {
                    if (inButtonAreas) {
                        inButtonAreas = false;
                        repaint();
                    }
                }

                // if pointer is not over the image, report Black pixel
                if ((focusPixelX < 0) || (focusPixelY < 0) ||
                     (focusPixelX >= imageSize.width) ||
                     (focusPixelY >= imageSize.height)) {
                     viewportListener.newColorUnderPointer(0, 0, 0);
                } else {
                    // otherwise report color of pixel under pointer
                    Color c = new Color(image.getRGB(focusPixelX,focusPixelY), true);
                    viewportListener.newColorUnderPointer(
                        c.getRed(),
                        c.getGreen(),
                        c.getBlue()
                    );
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseLastLocation.setLocation(mouseLocation);
                mouseLocation = e.getPoint();

                // This application only drags with RMB
                if (!rightButtonPressed) { return; }
                
                if (!rightButtonDragged) {
                    int dx = Math.abs(mouseLocation.x - pressPoint.x);
                    int dy = Math.abs(mouseLocation.y - pressPoint.y);
                    if (dx > MOUSE_DRAG_THRESHOLD || dy > MOUSE_DRAG_THRESHOLD) { 
                        rightButtonDragged = true; 
                    }
                } else {
                    panImage(mouseLocation.x - mouseLastLocation.x,
                             mouseLocation.y - mouseLastLocation.y);
                }
            }
        });
        
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    zoomIn(FocusMode.POINTER);
                } else {
                    zoomOut(FocusMode.POINTER);
                }
            }
        });    
    }
}
