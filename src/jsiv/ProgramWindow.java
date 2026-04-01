package jsiv;

import java.util.HexFormat;
import java.util.Optional;
import java.awt.*;
import java.awt.datatransfer.*;
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
    private Color lastColor = Color.BLACK;
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
        frame.setTransferHandler(new ImageDropHandler(imageNavigator));
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
        lastColor = color;
        statusBar.updateColor(colorToString(color));
    }

    @Override
    public void requestCopyColorToClipboard(Color color) {
        try {
            String colorString = colorToString(color);
            StringSelection stringSelection = new StringSelection(colorString);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            statusBar.updateColor(colorString + " Copied!");
        } catch (java.awt.HeadlessException e) {
            System.err.println("Cannot perform CopyToClipboard function: "
                            + "Clipboard operations require a graphical environment.");
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(frame,
                                      "Could not copy color to system clipboard",
                                      "Clipboard is unavailable or busy",
                                      JOptionPane.WARNING_MESSAGE);
        }
    }

    private String colorToString(Color color) {
        return (colorMode==ColorMode.RGB)?colorToRGB(color):colorToHex(color);
    }

    private String colorToRGB(Color color) {
        return "R:" + color.getRed()
            + " G:" + color.getGreen()
            + " B:" + color.getBlue();
    }

    private String colorToHex(Color color) {
        HexFormat hex = HexFormat.of().withUpperCase();
        return "0x"
            + hex.toHexDigits((byte)(color.getRed()   & 0xFF))
            + hex.toHexDigits((byte)(color.getGreen() & 0xFF))
            + hex.toHexDigits((byte)(color.getBlue()  & 0xFF));
    }

    private void setColorMode(ColorMode mode) {
        colorMode = mode;

        statusBar.updateColor(colorToString(lastColor));

        useRGBColorModeAction.setEnabled(mode != ColorMode.RGB);
        useHexColorModeAction.setEnabled(mode != ColorMode.HEX);
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
                    KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS,
                    InputEvent.CTRL_DOWN_MASK));
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
                    KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,
                    InputEvent.CTRL_DOWN_MASK));
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
                        KeyStroke.getKeyStroke(KeyEvent.VK_0,
                        InputEvent.CTRL_DOWN_MASK));
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
                        KeyStroke.getKeyStroke(KeyEvent.VK_C,
                        InputEvent.CTRL_DOWN_MASK));
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
                viewport.setBackground(newColor);
            }
        };

        useRGBColorModeAction = new AbstractAction("Use RGB Mode") {
            {
                putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                setColorMode(ColorMode.RGB);
            }
        };

        useHexColorModeAction = new AbstractAction("Use HEX Mode") {
            {
                putValue(MNEMONIC_KEY, KeyEvent.VK_H);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                setColorMode(ColorMode.HEX);
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

        inputMap.put(moveNavKey(KeyEvent.VK_UP, true, false), "moveUpFine");
        inputMap.put(moveNavKey(KeyEvent.VK_UP, true, true), "moveUpCoarse");
        inputMap.put(moveNavKey(KeyEvent.VK_DOWN, true, false), "moveDownFine");
        inputMap.put(moveNavKey(KeyEvent.VK_DOWN, true, true), "moveDownCoarse");
        inputMap.put(moveNavKey(KeyEvent.VK_LEFT, true, false), "moveLeftFine");
        inputMap.put(moveNavKey(KeyEvent.VK_LEFT, true, true), "moveLeftCoarse");
        inputMap.put(moveNavKey(KeyEvent.VK_RIGHT, true, false), "moveRightFine");
        inputMap.put(moveNavKey(KeyEvent.VK_RIGHT, true, true), "moveRightCoarse");

        actionMap.put("moveUpFine", moveAction(0,-Viewport.MOVE_FINE));
        actionMap.put("moveUpCoarse", moveAction(0,-Viewport.MOVE_COARSE));
        actionMap.put("moveDownFine", moveAction(0, Viewport.MOVE_FINE));
        actionMap.put("moveDownCoarse", moveAction(0, Viewport.MOVE_COARSE));
        actionMap.put("moveLeftFine", moveAction(-Viewport.MOVE_FINE,0));
        actionMap.put("moveLeftCoarse", moveAction(-Viewport.MOVE_COARSE,0));
        actionMap.put("moveRightFine", moveAction(Viewport.MOVE_FINE,0));
        actionMap.put("moveRightCoarse", moveAction(Viewport.MOVE_COARSE,0));

        inputMap.put(moveNavKey(KeyEvent.VK_LEFT, false, false), "navigatePrevious");
        inputMap.put(moveNavKey(KeyEvent.VK_RIGHT, false, false), "navigateNext");

        actionMap.put("navigatePrevious", openPreviousAction);
        actionMap.put("navigateNext", openNextAction);
        }

    private KeyStroke moveNavKey(int key, boolean ctrl, boolean shift) {
        return KeyStroke.getKeyStroke(key,
                            ((ctrl)?InputEvent.CTRL_DOWN_MASK:0) |
                            ((shift)?InputEvent.SHIFT_DOWN_MASK:0));
    }

    private Action moveAction(int dx, int dy) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewport.moveImage(dx, dy);
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
