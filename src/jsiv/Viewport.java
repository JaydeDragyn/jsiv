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

    private Dimension viewportSize;
    private Point viewportCenter;
    private Point mouseLocation = new Point(0,0);
    private Point mouseLastLocation = new Point(0,0);

    private Point pressPoint;
    private boolean leftButtonPressed;
    private boolean rightButtonPressed;
    private boolean rightButtonDragged;
    private static final int MOUSE_DRAG_THRESHOLD = 1;

    public static final int PAN_FINE = 1;
    public static final int PAN_COARSE = 10;
    private int panStepSize;

    public Viewport() {
        initResizeListener();
        initMouseListeners();

        setBackground(Color.BLACK);
        updateViewportSize();
    }

    public void setImage(BufferedImage newImage) {
        System.out.println("Viewport.setImage()");
    }
    
    public void zoomIn(FocusMode focusMode) {
        System.out.println("Viewport.zoomIn() with Focus Mode: " + focusMode);
    }
    
    public void zoomOut(FocusMode focusMode) {
        System.out.println("Viewport.zoomOut() with Focus Mode: " + focusMode);
    }
    
    public void resetZoom() {
        System.out.println("Viewport.resetZoom() ");
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
                                   
        System.out.print("Viewport size changed to " + viewportSize.width +
                "," + viewportSize.height);
        System.out.println(" - Viewport center point is " + viewportCenter.x +
                "," + viewportCenter.y);
    }
    
    private void handleLeftClick() {
        System.out.println("Left Click at " + mouseLocation.x +
                "," + mouseLocation.y +
                " - " + (mouseLocation.x - pressPoint.x) +
                "," + (mouseLocation.y - pressPoint.y) +
                " pixels from starting location of " + pressPoint.x +
                "," + pressPoint.y);
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
                pressPoint = mouseLocation;
                
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
                    int originDelta = Math.max(mouseLocation.x - pressPoint.x,
                                               mouseLocation.y - pressPoint.y);
                    if (originDelta > MOUSE_DRAG_THRESHOLD) { 
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
