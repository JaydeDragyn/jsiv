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
    private ColorModel colorModel;
    private Dimension imageSize = new Dimension(0,0);
    private Dimension imageOffset = new Dimension(0,0);
    private Dimension viewportSize = new Dimension(0,0);
    private Point viewportCenter;
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
        colorModel = image.getColorModel();
        imageSize = new Dimension(image.getWidth(), image.getHeight());
        viewportListener.imageSizeChanged(imageSize);
        
        imageOffset = new Dimension(
                (viewportSize.width - imageSize.width) / 2,
                (viewportSize.height - imageSize.height) / 2);
        
        repaint();
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
        System.out.print("Viewport.centerImage() with Focus Mode: " + 
                    focusMode + " at point ");
        switch (focusMode) {
            case WINDOW_CENTER : System.out.println(viewportCenter.x + 
                                              "," + viewportCenter.y);
                                 break;
            case POINTER : System.out.println(pressPoint.x + 
                                        "," + pressPoint.y);
                           break;
        };



    }

    public void panImage(int dx, int dy) {
        System.out.println("Viewport.panImage() " +
                        dx + " horizontally, " +
                        dy + " vertically");
    }

    public void setNavigationAvailability(boolean available) {
        System.out.println("Viewport.setNavigationAvailable(" + available + ")");
        navigationAvailable = available;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, imageOffset.width,imageOffset.height, null);
        }
    }

    private void setSplashImage() {
        image = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
        colorModel = image.getColorModel();
        Graphics2D pen = image.createGraphics();
        
        pen.setColor(new Color(255, 0, 0, 255));
        pen.fillRect(150,70, 200,200);
        
        pen.setColor(new Color(0, 255, 0, 250));
        pen.fillRect(300,100, 150,200);
        
        pen.setColor(new Color(0, 0, 255, 245));
        pen.fillRect(225,175, 200,150);
        
        pen.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        pen.setColor(Color.WHITE);
        pen.setFont(new Font("SansSerif", Font.BOLD, 48));
        pen.drawString("JSIV", 175,150);
        
        pen.setColor(new Color(168, 255, 192, 255));
        pen.setFont(new Font("SansSerif", Font.BOLD, 24));
        pen.drawString("Jayde's", 330,150);
        
        pen.setColor(new Color(168, 192, 255, 255));
        pen.setFont(new Font("SansSerif", Font.PLAIN, 24));
        pen.drawString("Simple", 265,230);
        pen.drawString("Image",  280,255);
        pen.drawString("Viewer", 295,280);
        
        pen.dispose();
        
        imageSize = new Dimension(600, 400);
        viewportListener.imageSizeChanged(imageSize);
        imageOffset = new Dimension(100, 100);
        repaint();
    }
    
    private void updateViewportSize() {
        viewportSize = getSize();
        viewportCenter = new Point(viewportSize.width / 2,
                                   viewportSize.height / 2);
        openPreviousBorder = viewportCenter.x / 2;
        openNextBorder = viewportCenter.x + openPreviousBorder;
        viewportListener.viewportSizeChanged(viewportSize);
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
                System.out.print(mouseLocation.x + "," + mouseLocation.y +
                                    "\b\b\b\b\b\b\b\b");

                
                int x = mouseLocation.x - imageOffset.width;
                int y = mouseLocation.y - imageOffset.height;
                
                // if pointer is not over the image, report Black pixel
                if ((x < 0) || (y < 0) ||
                    (x >= imageSize.width) || (y >= imageSize.height)) {
                    viewportListener.newColorUnderPointer(0, 0, 0);
                    return;
                } 
                // otherwise report color of pixel under pointer
                int inq = image.getRGB(x,y);
                viewportListener.newColorUnderPointer(
                    colorModel.getRed(inq),
                    colorModel.getGreen(inq),
                    colorModel.getBlue(inq));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseLastLocation = new Point(mouseLocation);
                mouseLocation = e.getPoint();
                System.out.print(mouseLocation.x + "," + mouseLocation.y +
                                    "\b\b\b\b\b\b\b\b");

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
