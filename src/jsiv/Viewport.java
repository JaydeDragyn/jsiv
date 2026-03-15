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
    private Dimension imageSize = new Dimension(0,0);
    private Dimension imageScaledSize = new Dimension(0,0);
    private int imageOffsetX;
    private int imageOffsetY;
    private Point maxSmallOffset;
    private Point minLargeOffset;
    private Dimension viewportSize = new Dimension(0,0);
    private Point viewportCenter;
    private double zoomLevel = 1.0;
    private int openPreviousBorder;
    private int openNextBorder;
    private Point mouseLocation = new Point(0,0);
    private Point mouseLastLocation = new Point(0,0);

    private Point pressPoint;
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
        updateViewportSize();
        setSplashImage();
    }

    public void setImage(BufferedImage newImage) {
        image = newImage;
        imageSize = new Dimension(image.getWidth(), image.getHeight());
        viewportListener.imageSizeChanged(imageSize);
        imageScaledSize = new Dimension(imageSize);
        imageOffsetX = (viewportSize.width - imageScaledSize.width) / 2;
        imageOffsetY = (viewportSize.height - imageScaledSize.height) / 2;
        updateClampLimits();
        resetZoom();
    }
    
    public void zoomIn(FocusMode focusMode) {
        System.out.println("Viewport.zoomIn() with Focus Mode: " + focusMode);
        viewportListener.zoomChanged(1.0f);
    }
    
    public void zoomOut(FocusMode focusMode) {
        System.out.println("Viewport.zoomOut() with Focus Mode: " + focusMode);
        viewportListener.zoomChanged(1.0f);
    }
    
    public void resetZoom() {
        System.out.println("Viewport.resetZoom() ");
        centerImage(FocusMode.WINDOW_CENTER);
        viewportListener.zoomChanged(1.0f);
    }
    
    public void centerImage(FocusMode focusMode) {
        switch (focusMode) {
            case WINDOW_CENTER :
                // pan the image by the different between the center of the
                // viewport and the image offset (less half image size)
                panImage(viewportCenter.x - imageOffsetX - (imageScaledSize.width / 2),
                         viewportCenter.y - imageOffsetY - (imageScaledSize.height / 2));
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
        System.out.println("Viewport.setNavigationAvailable(" + available + ")");
        navigationAvailable = available;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) { return; }

        Graphics2D pen = (Graphics2D) g;
        pen.translate(imageOffsetX, imageOffsetY);
        pen.scale(zoomLevel, zoomLevel);
        pen.drawImage(image, 0,0, null);
        pen.dispose();

    }

    private void setSplashImage() {
        BufferedImage splashImage = new BufferedImage(300, 255, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pen = splashImage.createGraphics();
        
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

        setImage(splashImage);

        // manually set the image offset, assuming we get an 800x600 viewport
        // because this will be called in the Viewport constructor, and the
        // viewport size will not yet be available, so these will end up as
        // 0,0 otherwise, putting the splash image in the top-left corner.
        imageOffsetX = 250;
        imageOffsetY = 172;
    }
    
    private void updateViewportSize() {
        viewportSize = new Dimension(getSize());
        viewportCenter = new Point(viewportSize.width / 2,
                                   viewportSize.height / 2);
        openPreviousBorder = viewportCenter.x / 2;
        openNextBorder = viewportCenter.x + openPreviousBorder;
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

    private void handleLeftClick() {
        Dimension distanceFromPress = new Dimension(
                    Math.abs(mouseLocation.x - pressPoint.x),
                    Math.abs(mouseLocation.y - pressPoint.y));
        System.out.println("Left Click at " + mouseLocation.x +
                "," + mouseLocation.y +
                " - " + distanceFromPress.width +
                "," + distanceFromPress.height +
                " pixels from starting location of " + pressPoint.x +
                "," + pressPoint.y);
        if (distanceFromPress.width > POINTER_DRIFT_THRESHOLD ||
            distanceFromPress.height > POINTER_DRIFT_THRESHOLD) {
                System.out.println("Pointer drifted too far, aborting click.");
                return;
        }
        if (pressPoint.x < openPreviousBorder) {
            openPreviousRequested();
        }
        if (pressPoint.x > openNextBorder) {
            openNextRequested();
        }
    }

    private void openNextRequested() {
        System.out.println("Viewport.openNextRequested()");
        if (!navigationAvailable) { return; }
        viewportListener.requestOpenNext();
        repaint();
    }
    
    private void openPreviousRequested() {
        System.out.println("Viewport.openPreviousRequested()");
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
                pressPoint = new Point(mouseLocation);
                
                // Left button?
                if (e.getButton() == MouseEvent.BUTTON1) {
                    leftButtonPressed = true;
                    System.out.println("LMB pressed at " + pressPoint.x +
                                        "," + pressPoint.y);
                }
                
                // Right button?
                if (e.getButton() == MouseEvent.BUTTON3) {
                    rightButtonPressed = true;
                    rightButtonDragged = false;
                    System.out.println("RMB pressed at " + pressPoint.x +
                                        "," + pressPoint.y);
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
                mouseLastLocation = new Point(mouseLocation);
                mouseLocation = e.getPoint();
                
                int focusPixelX = (mouseLocation.x - imageOffsetX) / (int)zoomLevel;
                int focusPixelY = (mouseLocation.y - imageOffsetY) / (int)zoomLevel;
                
                // if pointer is not over the image, report Black pixel
                if ((focusPixelX < 0) || (focusPixelY < 0) ||
                     (focusPixelX >= imageSize.width) ||
                     (focusPixelY >= imageSize.height)) {
                     viewportListener.newColorUnderPointer(0, 0, 0);
                     return;
                }

                // otherwise report color of pixel under pointer
                Color c = new Color(image.getRGB(focusPixelX,focusPixelY), true);
                viewportListener.newColorUnderPointer(
                    c.getRed(),
                    c.getGreen(),
                    c.getBlue()
                );
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseLastLocation = new Point(mouseLocation);
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
