package JSIV;

import java.awt.event.*;
import javax.swing.*;

public class ProgramWindow {

    // Actions
    private Action openAction;
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
    private JMenuBar menuBar;
    
    public ProgramWindow() {
        initActionItems();
        initFrame();
        initMenu();
        initViewport();
        initStatusBar();

        frame.setJMenuBar(menuBar);
        
        frame.setVisible(true);
    }

    private void initActionItems() {
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
    }
    
    private void initFrame() {
        frame = new JFrame("JSIV");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
    }
    
    private void initMenu() {
        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(new JMenuItem(quitAction));
        
        menuBar.add(fileMenu);
    }
    
    private void initViewport() {
        
    }
    
    private void initStatusBar() {
        
    }
    
    
}
