package jsiv;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.*;
import javax.swing.*;

public class Viewport extends JPanel {

    public enum FocusMode {
        WINDOW_CENTER, POINTER
    };

    private Point mouseLocation;

    public Viewport() {
        setBackground(Color.BLACK);
        
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
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseLocation = e.getPoint();
                System.out.print("\b\b\b\b\b\b\b\b" + mouseLocation.x +
                                    "," + mouseLocation.y);
            }
        });
        
    }
        
    public void open() {
        System.out.println("viewport.open() invoked");
    }
    
    public void openNext() {
        System.out.println("viewport.openNext() invoked");
    }
    
    public void openPrevious() {
        System.out.println("viewport.openPrevious() invoked");
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
    
}
