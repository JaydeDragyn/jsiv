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
    private Action panUpAction;
    private Action panDownAction;
    private Action panLeftAction;
    private Action panRightAction;
    private Action zoomInAction;
    private Action zoomOutAction;
    private Action resetZoomAction;
    private Action centerImageAction;
    private Action userManualAction;
    private Action aboutAction;

    // Input and Action maps
    private InputMap inputMap;
    private ActionMap actionMap;
    
    // Window components
    private JFrame frame;
    private ImageNavigator imageNavigator;
    private Viewport viewport;
    private StatusBar statusBar;
    
    public ProgramWindow() {
        frame = new JFrame("JSIV");
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
        frame.setTitle(imageName);
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
    public void newColorUnderPointer(int red, int green, int blue) {
        statusBar.updateRGB(red, green, blue);
    }
    
    private void setNavigationAvailability(boolean available) {
        openNextAction.setEnabled(available);
        openPreviousAction.setEnabled(available);
        viewport.setNavigationAvailability(available);
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
                System.out.print("Refresh Action ->");
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
                System.out.print("Zoom In action -> ");
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
                System.out.print("Zoom Out action -> ");
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
                System.out.print("Reset Zoom action -> ");
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
                System.out.print("Center Image action -> ");
                viewport.centerImage(Viewport.FocusMode.WINDOW_CENTER);
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
                System.out.println("About action -> ");
            }
        };
    }

    private void initInputActionMaps() {
        inputMap = viewport.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        actionMap = viewport.getActionMap();

        inputMap.put(panKey(KeyEvent.VK_UP, true), "panUpFine");
        inputMap.put(panKey(KeyEvent.VK_DOWN, true), "panDownFine");
        inputMap.put(panKey(KeyEvent.VK_LEFT, true), "panLeftFine");
        inputMap.put(panKey(KeyEvent.VK_RIGHT, true), "panRightFine");

        actionMap.put("panUpFine", panAction(0,-Viewport.PAN_FINE));
        actionMap.put("panDownFine", panAction(0, Viewport.PAN_FINE));
        actionMap.put("panLeftFine", panAction(-Viewport.PAN_FINE,0));
        actionMap.put("panRightFine", panAction(Viewport.PAN_FINE,0));

        inputMap.put(panKey(KeyEvent.VK_LEFT, false), "navigatePrevious");
        inputMap.put(panKey(KeyEvent.VK_RIGHT, false), "navigateNext");

        actionMap.put("navigatePrevious", openPreviousAction);
        actionMap.put("navigateNext", openNextAction);
        }

    private KeyStroke panKey(int key, boolean ctrl) {
        return KeyStroke.getKeyStroke(key, (ctrl)?InputEvent.CTRL_DOWN_MASK:0);
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
        fileMenu.add(new JMenuItem(quitAction));
        
        menuBar.add(fileMenu);
        
        JMenu imageMenu = new JMenu("Image");
        imageMenu.setMnemonic(KeyEvent.VK_I);
        
        imageMenu.add(new JMenuItem(zoomInAction));
        imageMenu.add(new JMenuItem(zoomOutAction));
        imageMenu.add(new JMenuItem(resetZoomAction));
        imageMenu.add(new JMenuItem(centerImageAction));
        menuBar.add(imageMenu);
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.add(new JMenuItem(userManualAction));
        helpMenu.add(new JMenuItem(aboutAction));
        menuBar.add(helpMenu);
        
        frame.setJMenuBar(menuBar);
    }
    
}
