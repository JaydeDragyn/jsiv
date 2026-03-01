package jsiv;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;

public class Viewport extends JPanel {

    public enum ZoomFocusMode {
        WINDOW_CENTER, POINTER
    };

    private ZoomFocusMode zoomFocusMode;

    public Viewport() {
        setBackground(Color.BLACK);
        
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                setZoomFocusMode(ZoomFocusMode.POINTER);
                if (e.getWheelRotation() < 0) {
                    zoomIn();
                } else {
                    zoomOut();
                }
            }
        });
    }
    
    public void setZoomFocusMode(ZoomFocusMode newMode) {
        System.out.print("viewport set ZoomFocusMode to " + newMode + " - ");
        zoomFocusMode = newMode;
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
    
    public void zoomIn() {
        System.out.println("viewport.zoomIn() invoked");
    }
    
    public void zoomOut() {
        System.out.println("viewport.zoomOut() invoked");
    }
    
    public void resetZoom() {
        System.out.println("viewport.resetZoom() invoked");
    }
    
    public void centerImage() {
        System.out.println("viewport.centerImage() invoked");
    }
    
}
