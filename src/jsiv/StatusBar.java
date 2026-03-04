package jsiv;

import java.awt.*;
import javax.swing.*;

public class StatusBar extends JPanel {

    private JLabel indexCountLabel;
    private JLabel imageSizeLabel;
    private JLabel viewportSizeLabel;
    private JLabel zoomLevelLabel;
    private JLabel rgbLabel;
    private JLabel separator1, separator2, separator3, separator4;

    public StatusBar() {
        super(new FlowLayout(FlowLayout.LEFT, 10, 0));
        
        indexCountLabel = new JLabel();
        imageSizeLabel = new JLabel();
        viewportSizeLabel = new JLabel();
        zoomLevelLabel = new JLabel();
        rgbLabel = new JLabel();
        
        separator1 = new JLabel(" | ");
        separator2 = new JLabel(" | ");
        separator3 = new JLabel(" | ");
        separator4 = new JLabel(" | ");
       
        add(indexCountLabel);
        add(separator1);
        add(imageSizeLabel);
        add(separator2);
        add(viewportSizeLabel);
        add(separator3);
        add(zoomLevelLabel);
        add(separator4);
        add(rgbLabel);
        
        updateIndexCount(0, 0);
        updateImageSize(new Dimension(0, 0));
        updateViewportSize(new Dimension(0, 0));
        updateZoomLevel(1.0f);
        updateRGB(0, 0, 0);
        
        imageSizeLabel.setToolTipText("Size of this image in pixels.");
        viewportSizeLabel.setToolTipText("Size of the viewport in pixels.");
        zoomLevelLabel.setToolTipText("Image magnification level.");
        rgbLabel.setToolTipText(
            "Red, Green and Blue values of pixel under the pointer.");
    }

    public void updateIndexCount(int index, int imageCount) {
        indexCountLabel.setText(
            "<html><b>File</b>: " + 
            index + " / " + imageCount + 
            "</html>");
        indexCountLabel.setToolTipText("Showing file " +
            index + " of " + imageCount + " from current image folder.");
    }
    
    public void updateImageSize(Dimension imageSize) {
        imageSizeLabel.setText(
            "<html><b>Image Size</b>: " + 
            imageSize.width + "x" + imageSize.height + 
            "</html>");
    }
    
    public void updateViewportSize(Dimension viewportSize) {
        viewportSizeLabel.setText(
            "<html><b>Viewport</b>: " +
            viewportSize.width + "x" + viewportSize.height +
            "</html>");
    }
    
    public void updateZoomLevel(double zoomLevel) {
        zoomLevelLabel.setText(
            "<html><b>Zoom</b>: " + zoomLevel + "</html>");
    }
    
    public void updateRGB(int red, int green, int blue) {
        rgbLabel.setText(
            "<html><b>R</b>:" + red +
            " <b>G</b>:" + green +
            " <b>B</b>:" + blue +
            "</html>");
    }
}
