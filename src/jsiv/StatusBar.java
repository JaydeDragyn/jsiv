package jsiv;

import java.awt.*;
import javax.swing.*;

public class StatusBar extends JPanel {

    private JLabel indexCountLabel;
    private JLabel imageSizeLabel;
    private JLabel viewportSizeLabel;
    private JLabel zoomLevelLabel;
    private JLabel colorLabel;
    private JLabel separator1, separator2, separator3, separator4;

    public StatusBar() {
        super(new FlowLayout(FlowLayout.LEFT, 10, 0));
        
        setFocusable(false);
        setRequestFocusEnabled(false);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0,
                UIManager.getColor("Separator.foreground")),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));

        indexCountLabel = new JLabel();
        imageSizeLabel = new JLabel();
        viewportSizeLabel = new JLabel();
        zoomLevelLabel = new JLabel();
        colorLabel = new JLabel();
        
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
        add(colorLabel);
        
        updateIndexCount(0, 0);
        updateImageSize(new Dimension(0, 0));
        updateViewportSize(new Dimension(0, 0));
        updateZoomLevel(1.0);
        updateColor("R:- G:- B:-");
        
        imageSizeLabel.setToolTipText("Size of this image in pixels.");
        viewportSizeLabel.setToolTipText("Size of the viewport in pixels.");
        zoomLevelLabel.setToolTipText("Image magnification level.");
        colorLabel.setToolTipText("Color of pixel under the pointer "
                                + "(Right click to copy to clipboard).");
    }

    public void updateIndexCount(int index, int imageCount) {
        indexCountLabel.setText("File: " + index + " of " + imageCount);
        indexCountLabel.setToolTipText("Showing file " +
            index + " of " + imageCount + " from current image folder.");
    }
    
    public void updateImageSize(Dimension imageSize) {
        String labelText;

        if (imageSize == null) {
            labelText = "No Image Size";
        } else {
            labelText = "Image Size: " +
                        imageSize.width + "x" + imageSize.height;
        }
        imageSizeLabel.setText(labelText);
    }
    
    public void updateViewportSize(Dimension viewportSize) {
        String labelText;

        if (viewportSize == null) {
            labelText = "No Viewport Size";
        } else {
            labelText = "Viewport: " +
                    viewportSize.width + "x" + viewportSize.height;
        }

        viewportSizeLabel.setText(labelText);
    }
    
    public void updateZoomLevel(double zoomLevel) {
        int adjZoomLevel = (int)(zoomLevel * 100);
        zoomLevelLabel.setText("Zoom: " + adjZoomLevel + "%");
    }
    
    public void updateColor(String colorText) {
        if (colorText == null) {
            colorLabel.setText("No Color Info");
        } else {
            colorLabel.setText(colorText);
        }
    }
}
