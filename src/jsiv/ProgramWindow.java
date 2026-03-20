package jsiv;

import java.util.Optional;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class ProgramWindow implements ViewportListener, ImageNavigatorListener {

    // Actions
    private Action openFileAction;
    private Action openNextAction;
    private Action openPreviousAction;
    private Action refreshAction;
    private Action quitAction;
    private Action zoomInAction;
    private Action zoomOutAction;
    private Action resetZoomAction;
    private Action centerImageAction;
    private Action changeBackgroundColorAction;
    private Action useRGBColorModeAction;
    private Action useHexColorModeAction;
    private Action userManualAction;
    private Action aboutAction;

    // Window components
    private JFrame frame;
    private ImageNavigator imageNavigator;
    private Viewport viewport;
    private StatusBar statusBar;

    // Color reporting
    private enum ColorMode { RGB, HEX };
    private ColorMode colorMode = ColorMode.RGB;
    private Color backgroundColor = Color.BLACK;

    public ProgramWindow() {
        frame = new JFrame("JSIV");
        frame.setIconImage(JSIVSplash.getSplashImage());
        imageNavigator = new ImageNavigator(this, frame);
        statusBar = new StatusBar();
        viewport = new Viewport(this);
        initActionItems();
        initMenu();
        initInputActionMaps();
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        frame.add(viewport, BorderLayout.CENTER);
        frame.add(statusBar, BorderLayout.SOUTH);

        setNavigationAvailability(false);
        useRGBColorModeAction.setEnabled(false);
        useHexColorModeAction.setEnabled(true);

        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void navigationAvailabilityChanged(boolean available) {
        setNavigationAvailability(available);
    }
    
    @Override
    public void newImageLoaded(String imageName,
                                BufferedImage image,
                                int index, int total) {
        frame.setTitle("JSIV - " + imageName);
        viewport.setImage(image);
        statusBar.updateIndexCount(index, total);
    }

    @Override
    public void requestOpenNext() {
        imageNavigator.openNext();
    }
    
    @Override
    public void requestOpenPrevious() {
        imageNavigator.openPrevious();
    }
    
    @Override
    public void imageSizeChanged(Dimension newImageSize) {
        statusBar.updateImageSize(newImageSize);
    }
    
    @Override
    public void viewportSizeChanged(Dimension newViewportSize) {
        statusBar.updateViewportSize(newViewportSize);
    }
    
    @Override
    public void zoomChanged(double newZoomLevel) {
        statusBar.updateZoomLevel(newZoomLevel);
    }
    
    @Override
    public void newColorUnderPointer(Color color) {
        if (colorMode == ColorMode.RGB) {
            statusBar.updateRGB(color);
        } else {
            statusBar.updateHex(color);
        }
    }
    
    private void setNavigationAvailability(boolean available) {
        openNextAction.setEnabled(available);
        openPreviousAction.setEnabled(available);
        refreshAction.setEnabled(available);
        viewport.setNavigationAvailability(available);
    }
    
    private void showHelpAboutDialog() {
        JOptionPane.showMessageDialog(frame,
            "JSIV - Jayde's Simple Image Viewer\n" +
            "Version 1.0.0\n\n" +
            "Developed by: Jayde Dragyn\n" +
            "Contact: JaydeDragyn@outlook.com\n" +
            "Source code: https://github.com/JaydeDragyn/jsiv\n\n" +
            "Built with Java 17.0.4.1 2022-08-18 LTS\n\n" +
            "Technical guidance assisted by:\n" +
            "        ChatGPT (OpenAI) and Claude (Anthropic)",
            "JSIV - About",
            JOptionPane.INFORMATION_MESSAGE,
            new ImageIcon(JSIVSplash.getSplashImage())
        );
    }

    private void initActionItems() {
        openFileAction = new AbstractAction("Open File") {
            {
                putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
                putValue(MNEMONIC_KEY, KeyEvent.VK_O);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                imageNavigator.openFile();
            }
        };
        
        openNextAction = new AbstractAction("Open Next") {
            {
                putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
                putValue(MNEMONIC_KEY, KeyEvent.VK_N);
                putValue(DISPLAYED_MNEMONIC_INDEX_KEY, 5);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                imageNavigator.openNext();
            }
        };
        
        openPreviousAction = new AbstractAction("Open Previous") {
            {
                putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
                putValue(MNEMONIC_KEY, KeyEvent.VK_P);
                putValue(DISPLAYED_MNEMONIC_INDEX_KEY, 5);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                imageNavigator.openPrevious();
            }
        };

        refreshAction = new AbstractAction("Refresh") {
            {
                putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK |
                                                          InputEvent.SHIFT_DOWN_MASK));
                putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                imageNavigator.refresh();
            }
        };
        
        quitAction = new AbstractAction("Quit") {
            {
                putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
                putValue(MNEMONIC_KEY, KeyEvent.VK_Q);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
              
        zoomInAction = new AbstractAction("Zoom In") {
            {
                putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK));
                putValue(MNEMONIC_KEY, KeyEvent.VK_I);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                viewport.zoomIn(Viewport.FocusMode.WINDOW_CENTER);
            }
        };
        
        zoomOutAction = new AbstractAction("Zoom Out") {
            {
                putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
                putValue(MNEMONIC_KEY, KeyEvent.VK_O);
                putValue(DISPLAYED_MNEMONIC_INDEX_KEY, 5);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                viewport.zoomOut(Viewport.FocusMode.WINDOW_CENTER);
            }
        }; 
        
        resetZoomAction = new AbstractAction("Reset Zoom") {
            {
                putValue(ACCELERATOR_KEY,
                        KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK));
                putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                viewport.resetZoom();
            }
        };
        
        centerImageAction = new AbstractAction("Center Image") {
            {
                putValue(ACCELERATOR_KEY,
                        KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
                putValue(MNEMONIC_KEY, KeyEvent.VK_C);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                viewport.centerImage(Viewport.FocusMode.WINDOW_CENTER);
            }
        };

        changeBackgroundColorAction = new AbstractAction("Change Background Color") {
            {
                putValue(ACCELERATOR_KEY,
                        KeyStroke.getKeyStroke(KeyEvent.VK_B,
                        InputEvent.CTRL_DOWN_MASK));
                putValue(MNEMONIC_KEY, KeyEvent.VK_B);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                    frame,
                    "Choose a new Background color",
                    backgroundColor
                );
                if (newColor == null) { return; } // user cancelled
                backgroundColor = newColor;
                viewport.changeBackgroundColor(newColor);
            }
        };

        useRGBColorModeAction = new AbstractAction("Use RGB Mode") {
            {
                putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                colorMode = ColorMode.RGB;
                statusBar.updateRGB(backgroundColor);
                useRGBColorModeAction.setEnabled(false);
                useHexColorModeAction.setEnabled(true);
            }
        };

        useHexColorModeAction = new AbstractAction("Use HEX Mode") {
            {
                putValue(MNEMONIC_KEY, KeyEvent.VK_H);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                colorMode = ColorMode.HEX;
                statusBar.updateHex(backgroundColor);
                useRGBColorModeAction.setEnabled(true);
                useHexColorModeAction.setEnabled(false);
            }
        };

        userManualAction = new AbstractAction("User Manual") {
            {
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
                putValue(MNEMONIC_KEY, KeyEvent.VK_U);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("User Manual action -> ");
            }
        };
        
        aboutAction = new AbstractAction("About") {
            {
                putValue(MNEMONIC_KEY, KeyEvent.VK_A);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelpAboutDialog();
            }
        };
    }

    private void initInputActionMaps() {
        InputMap inputMap = viewport.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = viewport.getActionMap();

        inputMap.put(panNavKey(KeyEvent.VK_UP, true, false), "panUpFine");
        inputMap.put(panNavKey(KeyEvent.VK_UP, true, true), "panUpCoarse");
        inputMap.put(panNavKey(KeyEvent.VK_DOWN, true, false), "panDownFine");
        inputMap.put(panNavKey(KeyEvent.VK_DOWN, true, true), "panDownCoarse");
        inputMap.put(panNavKey(KeyEvent.VK_LEFT, true, false), "panLeftFine");
        inputMap.put(panNavKey(KeyEvent.VK_LEFT, true, true), "panLeftCoarse");
        inputMap.put(panNavKey(KeyEvent.VK_RIGHT, true, false), "panRightFine");
        inputMap.put(panNavKey(KeyEvent.VK_RIGHT, true, true), "panRightCoarse");

        actionMap.put("panUpFine", panAction(0,-Viewport.PAN_FINE));
        actionMap.put("panUpCoarse", panAction(0,-Viewport.PAN_COARSE));
        actionMap.put("panDownFine", panAction(0, Viewport.PAN_FINE));
        actionMap.put("panDownCoarse", panAction(0, Viewport.PAN_COARSE));
        actionMap.put("panLeftFine", panAction(-Viewport.PAN_FINE,0));
        actionMap.put("panLeftCoarse", panAction(-Viewport.PAN_COARSE,0));
        actionMap.put("panRightFine", panAction(Viewport.PAN_FINE,0));
        actionMap.put("panRightCoarse", panAction(Viewport.PAN_COARSE,0));

        inputMap.put(panNavKey(KeyEvent.VK_LEFT, false, false), "navigatePrevious");
        inputMap.put(panNavKey(KeyEvent.VK_RIGHT, false, false), "navigateNext");

        actionMap.put("navigatePrevious", openPreviousAction);
        actionMap.put("navigateNext", openNextAction);
        }

    private KeyStroke panNavKey(int key, boolean ctrl, boolean shift) {
        return KeyStroke.getKeyStroke(key,
                            ((ctrl)?InputEvent.CTRL_DOWN_MASK:0) |
                            ((shift)?InputEvent.SHIFT_DOWN_MASK:0));
    }

    private Action panAction(int dx, int dy) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewport.panImage(dx, dy);
            }
        };
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(new JMenuItem(openFileAction));
        fileMenu.add(new JMenuItem(openNextAction));
        fileMenu.add(new JMenuItem(openPreviousAction));
        fileMenu.add(new JMenuItem(refreshAction));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(quitAction));
        menuBar.add(fileMenu);

        JMenu imageMenu = new JMenu("Image");
        imageMenu.setMnemonic(KeyEvent.VK_I);
        imageMenu.add(new JMenuItem(zoomInAction));
        imageMenu.add(new JMenuItem(zoomOutAction));
        imageMenu.add(new JMenuItem(resetZoomAction));
        imageMenu.add(new JMenuItem(centerImageAction));
        menuBar.add(imageMenu);

        JMenu colorMenu = new JMenu("Color");
        colorMenu.setMnemonic(KeyEvent.VK_C);
        colorMenu.add(new JMenuItem(changeBackgroundColorAction));
        colorMenu.addSeparator();
        colorMenu.add(new JMenuItem(useRGBColorModeAction));
        colorMenu.add(new JMenuItem(useHexColorModeAction));
        menuBar.add(colorMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.add(new JMenuItem(userManualAction));
        helpMenu.add(new JMenuItem(aboutAction));
        menuBar.add(helpMenu);

        frame.setJMenuBar(menuBar);
    }
    
}
