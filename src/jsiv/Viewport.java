package jsiv;

import java.awt.Color;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Viewport extends JPanel {

    public enum FocusMode {
        WINDOW_CENTER, POINTER
    };

    private final ViewportListener viewportListener;
    private boolean navigationAllowed;

    private Dimension viewportSize;
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
    private int panStepSize;

    public Viewport(ViewportListener viewportListener) {
        this.viewportListener = viewportListener;
        navigationAllowed = false;
        
        initResizeListener();
        initMouseListeners();

        setBackground(Color.BLACK);
        updateViewportSize();
    }

    public void setImage(BufferedImage newImage) {
        System.out.println("Viewport.setImage()");
    }
    
    public void setFallbackSplash() {
        System.out.println("Viewport.setFallbackSplash()");
    }
    
    public void zoomIn(FocusMode focusMode) {
        System.out.println("Viewport.zoomIn() with Focus Mode: " + focusMode);
    }
    
    public void zoomOut(FocusMode focusMode) {
        System.out.println("Viewport.zoomOut() with Focus Mode: " + focusMode);
    }
    
    public void resetZoom() {
        System.out.println("Viewport.resetZoom() ");
        centerImage(FocusMode.WINDOW_CENTER);
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
    
    private void updateViewportSize() {
        viewportSize = getSize();
        viewportCenter = new Point(viewportSize.width / 2,
                                   viewportSize.height / 2);
        openPreviousBorder = viewportCenter.x / 2;
        openNextBorder = viewportCenter.x + openPreviousBorder;
        System.out.print("Viewport size changed to " + viewportSize.width +
                "," + viewportSize.height);
        System.out.println(" - Viewport center point is " + viewportCenter.x +
                "," + viewportCenter.y);
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

    public void setNavigationAllowed(boolean allowed) {
        System.out.println("Viewport.setNavigationAllowed(" + allowed + ")");
        navigationAllowed = allowed;
    }

    private void openNextRequested() {
        System.out.println("Viewport.openNextRequested()");
        if (!navigationAllowed) { return; }
        viewportListener.requestOpenNext();
        repaint();
    }
    
    private void openPreviousRequested() {
        System.out.println("Viewport.openPreviousRequested()");
        if (!navigationAllowed) { return; }
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
                mouseLastLocation = mouseLocation;
                mouseLocation = e.getPoint();
                System.out.print(mouseLocation.x + "," + mouseLocation.y +
                                    "\b\b\b\b\b\b\b\b");
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseLastLocation = mouseLocation;
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
