package jsiv;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Viewport extends JPanel {

    public enum FocusMode {
        WINDOW_CENTER, POINTER
    };

    private Dimension viewportSize;
    private Point viewportCenter;
    private Point mouseLocation;
    
    private Point pressPoint;
    private boolean leftButtonPressed;
    private boolean rightButtonPressed;
    private boolean rightButtonDragged;

    public Viewport() {
        setBackground(Color.BLACK);
        updateViewportSize();
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateViewportSize();
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseLocation = e.getPoint();
                System.out.print("\b\b\b\b\b\b\b\b" + mouseLocation.x +
                                    "," + mouseLocation.y);
            }
        });
        
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

    public void setImage(BufferedImage newImage) {
        System.out.println("viewport.setImage() invoked");
    }
    
    public void zoomIn(FocusMode focusMode) {
        System.out.println("viewport.zoomIn() invoked with Focus Mode: " + focusMode);
    }
    
    public void zoomOut(FocusMode focusMode) {
        System.out.println("viewport.zoomOut() invoked with Focus Mode: " + focusMode);
    }
    
    public void resetZoom() {
        System.out.println("viewport.resetZoom() invoked");
    }
    
    public void centerImage(FocusMode focusMode) {
        System.out.println("viewport.centerImage() invoked with Focus Mode: " + focusMode);
    }
    
    private void updateViewportSize() {
        viewportSize = getSize();
        viewportCenter = new Point(viewportSize.width / 2,
                                   viewportSize.height / 2);
                                   
        System.out.print("viewport size changed to " + viewportSize.width +
                "," + viewportSize.height);
        System.out.println(" - viewport center point is " + viewportCenter.x +
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
    
}
