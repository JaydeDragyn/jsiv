package jsiv;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class Viewport extends JPanel {

    public enum FocusMode {
        WINDOW_CENTER, POINTER
    };

    public static final int PAN_FINE = 1;
    public static final int PAN_COARSE = 10;

    private final ViewportListener viewportListener;
    private boolean navigationAvailable;

    private BufferedImage image;
    private BufferedImage nextButton;
    private BufferedImage previousButton;
    private Rectangle nextButtonArea = new Rectangle(0,0,0,0);
    private Rectangle previousButtonArea = new Rectangle(0,0,0,0);
    private static final int NAVIGATION_BUTTON_OFFSET = 10;
    private boolean inButtonAreas;
    private Dimension imageSize = new Dimension(0,0);
    private Dimension imageScaledSize = new Dimension(0,0);
    private int imageOffsetX;
    private int imageOffsetY;
    private Point maxSmallOffset = new Point(0,0);
    private Point minLargeOffset = new Point(0,0);
    private Dimension viewportSize = new Dimension(0,0);
    private Point viewportCenter = new Point(0,0);
    private double zoomLevel = 1.0;
    private static final double MIN_PIXELS_ON_ZOOM = 8.0;
    private Point mouseLocation = new Point(0,0);
    private Point mouseLastLocation = new Point(0,0);

    private Point pressPoint = new Point(0,0);
    private boolean leftButtonPressed;
    private boolean leftButtonDragged;
    private boolean rightButtonPressed;
    private static final int LMB = MouseEvent.BUTTON1;
    private static final int RMB = MouseEvent.BUTTON3;
    private static final int MOUSE_DRAG_THRESHOLD = 3;


    public Viewport(ViewportListener viewportListener) {
        this.viewportListener = viewportListener;
        navigationAvailable = false;

        initResizeListener();
        initMouseListeners();

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 600));
        createNavigationButtons();
        setImage(JSIVSplash.getSplashImage());

        // setImage() will call resetZoom() to ensure the splash image we just
        // gave it is centered and zoomed appropriately, but right now the
        // Viewport has a size of 0,0 which will make resetZoom() abort, so
        // we will manually set the image position.
        imageOffsetX = 250;
        imageOffsetY = 172;
    }

    public void setImage(BufferedImage newImage) {
        image = newImage;
        if (image == null) {
            imageSize.setSize(0,0);
        } else {
            imageSize.setSize(image.getWidth(), image.getHeight());
        }

        imageScaledSize.setSize(imageSize);
        viewportListener.imageSizeChanged(new Dimension(imageSize));
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
            // Since we're here, the next potential zoomLevel will be < 1x
            // Check the current size (not the potential new size)
            // if the current size would fit, then we don't want to zoom out
            // further, we abort and leave the zoomLevel as-is
            if ((viewportSize.width >= imageScaledSize.width) &&
                (viewportSize.height >= imageScaledSize.height)) {
                return;
            }
        }

        setZoom(zoomLevelQuery, focusMode);
    }
    
    public void resetZoom() {
        // If viewport has not been sized yet, abort
        if (viewportSize.width == 0 || viewportSize.height == 0) { return; }

        // Find the actual zoom level that makes both axis fit within
        // the viewport at the largest size possible.
        double fitZoom = Math.min(
            (double) viewportSize.width / imageSize.width,
            (double) viewportSize.height / imageSize.height
        );

        // Now convert down to the nearest power of 2
        int n = (int) Math.floor(Math.log(fitZoom) / Math.log(2));
        zoomLevel = Math.pow(2, n);

        // Now finish the reset by calculating the scaled image size,
        // clamp limits, image position (to be centered) and finally
        // report the new zoom level to the listener
        imageScaledSize.setSize((int)(imageSize.width * zoomLevel),
                                (int)(imageSize.height * zoomLevel));
        updateClampLimits();
        centerImage(FocusMode.WINDOW_CENTER);
        repaint();
        viewportListener.zoomChanged(zoomLevel);
    }
    
    private void setZoom(double newZoomLevel, FocusMode focusMode) {
        Point focusPoint = switch (focusMode) {
            case WINDOW_CENTER -> new Point(viewportCenter);
            case POINTER       -> new Point(mouseLocation);
        };

        int focusPixelX = (int)Math.floor((focusPoint.x - imageOffsetX) / zoomLevel);
        int focusPixelY = (int)Math.floor((focusPoint.y - imageOffsetY) / zoomLevel);

        zoomLevel = newZoomLevel;
        imageScaledSize.setSize((int)(imageSize.width * zoomLevel),
                                (int)(imageSize.height * zoomLevel));

        // The end term "-(int)(zoomLevel/2)" is to line up the center of the
        // focused pixel under the mouse, so when zoomed in and the pixels
        // are large enough, the mouse will always remain in the center of
        // the pixel that was focused on
        imageOffsetX = focusPoint.x -(int)(focusPixelX * zoomLevel) -(int)(zoomLevel /2);
        imageOffsetY = focusPoint.y -(int)(focusPixelY * zoomLevel) -(int)(zoomLevel /2);

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
        }
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

        // draw the image at zoomed scale where user last put it
        Graphics2D pen = (Graphics2D) g.create();
        pen.translate(imageOffsetX, imageOffsetY);
        pen.scale(zoomLevel, zoomLevel);
        pen.drawImage(image, 0,0, null);
        pen.dispose();

        // draw navigation buttons if mouse is over one of them
        if (inButtonAreas) {
            pen = (Graphics2D) g.create();
            pen.drawImage(previousButton,
                            previousButtonArea.x,
                            previousButtonArea.y,
                            null);
            pen.drawImage(nextButton,
                            nextButtonArea.x,
                            nextButtonArea.y,
                            null);
            pen.dispose();
        }
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
        viewportSize.setSize(getSize());
        viewportCenter.setLocation(viewportSize.width / 2,
                                   viewportSize.height / 2);
        nextButtonArea.setBounds(
                viewportSize.width - nextButton.getWidth() - NAVIGATION_BUTTON_OFFSET,
                (viewportSize.height / 2) - (nextButton.getHeight() / 2),
                nextButton.getWidth(),
                nextButton.getHeight());
        previousButtonArea.setBounds(
                NAVIGATION_BUTTON_OFFSET,
                (viewportSize.height / 2) - (previousButton.getHeight() / 2),
                previousButton.getWidth(),
                previousButton.getHeight());
        viewportListener.viewportSizeChanged(new Dimension(viewportSize));
    }

    private void updateClampLimits() {
        // minSmallOffset is always going to be 0,0
        maxSmallOffset.setLocation(viewportSize.width - imageScaledSize.width,
                                    viewportSize.height - imageScaledSize.height);
        minLargeOffset.setLocation(-(imageScaledSize.width - viewportSize.width),
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

    private boolean inPreviousButtonArea() {
        return previousButtonArea.contains(mouseLocation);
    }

    private boolean inNextButtonArea() {
        return nextButtonArea.contains(mouseLocation);
    }

    private boolean inNavigationButtonAreas() {
        return (inPreviousButtonArea() || inNextButtonArea());
    }

    private Color getColorUnderPointer(int pointerX, int pointerY) {
        Color color;
        // if pointer is not over the image, or if there is no image,
        // report Background color pixel
        if ((image == null)
                || (pointerX < 0) || (pointerY < 0)
                || (pointerX >= imageSize.width)
                || (pointerY >= imageSize.height)) {
            color = getBackground();
        } else {
            // otherwise report color of pixel under pointer
            color = new Color(image.getRGB(pointerX,pointerY), true);
        }
        return color;
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
                // abort if a button is already pressed
                // only one button should be pressed at a time,
                if (leftButtonPressed || rightButtonPressed) { return; }
                
                // record the position the user pressed the button
                pressPoint.setLocation(mouseLocation);
                
                // Left button?
                if (e.getButton() == LMB) {
                    leftButtonPressed = true;
                    leftButtonDragged = false;
                }
                
                // Right button?
                if (e.getButton() == RMB) {
                    rightButtonPressed = true;
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // If LMB then this is either navigation, a recenter or a drag
                if (e.getButton() == LMB) {
                    // Verify if LMB is the button we are even processing
                    if (!leftButtonPressed) { return; }

                    // If it was a drag, then clean up and done
                    if (leftButtonDragged) {
                        leftButtonPressed = false;
                        leftButtonDragged = false;
                        return;
                    }

                    // If it was navigation, then navigate
                    if (inPreviousButtonArea()) {
                        if (navigationAvailable) {
                            viewportListener.requestOpenPrevious();
                            repaint();
                        }
                    } else if (inNextButtonArea()) {
                        if (navigationAvailable) {
                            viewportListener.requestOpenNext();
                            repaint();
                        }
                    } else {
                        // It wasn't a drag or navigation, so recenter on mouse
                        centerImage(FocusMode.POINTER);
                    }

                    // Finally, clear LMB to wait for new press, and done
                    leftButtonPressed = false;
                    return;
                }
                
                // If RMB then it is a copy-color request
                if (e.getButton() == RMB) {
                    // Verify if RMB is the button we are even processing
                    if (!rightButtonPressed) { return; }
                    
                    viewportListener.requestCopyColorToClipboard(
                        getColorUnderPointer(
                            (int)((pressPoint.x - imageOffsetX) / zoomLevel),
                            (int)((pressPoint.y - imageOffsetY) / zoomLevel)));

                    // Finally, clear RMB to wait for new press, and done
                    rightButtonPressed = false;
                    return;
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseLastLocation.setLocation(mouseLocation);
                mouseLocation = e.getPoint();

                // This is when mouse moves without any buttons pressed, so
                if (navigationAvailable && inNavigationButtonAreas()) {
                    // Show navigation buttons if pointer just moved over one
                    if (!inButtonAreas) {
                        inButtonAreas = true;
                        repaint();
                    }
                } else {
                    // Hide navigation buttons if pointer moved away from them
                    if (inButtonAreas) {
                        inButtonAreas = false;
                        repaint();
                    }
                }

                // Now report color under pointer
                viewportListener.newColorUnderPointer(getColorUnderPointer(
                    (int)((mouseLocation.x - imageOffsetX) / zoomLevel),
                    (int)((mouseLocation.y - imageOffsetY) / zoomLevel)));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseLastLocation.setLocation(mouseLocation);
                mouseLocation = e.getPoint();

                // This application only drags with LMB
                if (!leftButtonPressed) { return; }
                
                // Check if we are already dragging
                if (!leftButtonDragged) {
                    int dx = mouseLocation.x - pressPoint.x;
                    int dy = mouseLocation.y - pressPoint.y;

                    // Not yet, so make sure mouse moved beyond drag threshold
                    if (Math.abs(dx) > MOUSE_DRAG_THRESHOLD
                     || Math.abs(dy) > MOUSE_DRAG_THRESHOLD) {
                        // It did, so mark as a drag now and pan from
                        // pressPoint to keep image properly with mouse
                        leftButtonDragged = true;
                        panImage(dx, dy);
                    }
                } else {
                    // We are already panning, so move from last mouse location
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
