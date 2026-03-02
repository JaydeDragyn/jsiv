package jsiv;

import java.awt.event.*;
import javax.swing.*;

public class ProgramWindow {

    // Actions
    private Action openFileAction;
    private Action openNextAction;
    private Action openPreviousAction;
    private Action quitAction;
    private Action panAction;
    private Action moveImageAction;
    private Action zoomInAction;
    private Action zoomOutAction;
    private Action resetZoomAction;
    private Action centerImageAction;
    private Action userManualAction;
    private Action aboutAction;

    // Window components
    private JFrame frame;
    private ImageNavigator imageNavigator;
    private Viewport viewport;
    
    public ProgramWindow() {
        initActionItems();
        initFrame();
        initMenu();
        initViewport();
        initStatusBar();
        imageNavigator = new ImageNavigator();
        
        openNextAction.setEnabled(imageNavigator.canNavigate());
        openPreviousAction.setEnabled(imageNavigator.canNavigate());

        frame.setVisible(true);
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
                System.out.print("Open File option invoked -> ");
                imageNavigator.openFile().ifPresent(viewport::setImage);
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
                System.out.print("Open Next option invoked -> ");
                imageNavigator.openNext().ifPresent(viewport::setImage);
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
                System.out.print("Open Previous option invoked -> ");
                imageNavigator.openPrevious().ifPresent(viewport::setImage);
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
        
        panAction = new AbstractAction("Pan") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pan action invoked");
            }
        };
        
        moveImageAction = new AbstractAction("Move Image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Move Image action invoked");
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
                System.out.print("Zoom In action invoked -> ");
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
                System.out.print("Zoom Out action invoked -> ");
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
                System.out.print("Reset Zoom action invoked -> ");
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
                System.out.print("Center Image action invoked -> ");
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
                System.out.println("User Manual action invoked");
            }
        };
        
        aboutAction = new AbstractAction("About") {
            {
                putValue(MNEMONIC_KEY, KeyEvent.VK_A);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("About action invoked");
            }
        };
    }
    
    private void initFrame() {
        frame = new JFrame("JSIV");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
    }
        
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(new JMenuItem(openFileAction));
        fileMenu.add(new JMenuItem(openNextAction));
        fileMenu.add(new JMenuItem(openPreviousAction));
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
        
    private void initViewport() {
        viewport = new Viewport();
        frame.add(viewport);
    }
        
    private void initStatusBar() {
        
    }
    
    
}
