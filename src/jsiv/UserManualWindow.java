package jsiv;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class UserManualWindow {

    // Settings
    private static final int borderSize = 15;
    private static final int navDefaultSize = 250;
    private static final String collapseButtonText =
                "<html><center>&lt;<br><br>&lt;<br><br>&lt;</center></html>";
    private static final String expandButtonText =
                "<html><center>&gt;<br><br>&gt;<br><br>&gt;</center></html>";

    // Actions
    private Action toggleNavigationAction;
    private Action navigateNextLinkAction;
    private Action navigatePreviousLinkAction;
    private Action closeUserManualAction;

    // Window components
    private static UserManualWindow userManualWindow;
    private JFrame userManualFrame;
    private JPanel mainPanel;
    private JPanel navContainerPanel;
    private JEditorPane sectionNavigation;
    private JButton toggleNavigationButton;
    private JEditorPane content;

    // Link navigation

    public static void show() {
        if (userManualWindow == null) {
            userManualWindow = new UserManualWindow();
        }
        userManualWindow.showUserManual();
    }

    public UserManualWindow() {
        userManualFrame = new JFrame("JSIV User Manual");

        initActionItems();
        initMainPanel();
        initMenu();

        userManualFrame.add(mainPanel, BorderLayout.CENTER);

        initInputActionMaps();

        userManualFrame.pack();
    }

    public void showUserManual() {
        userManualFrame.setVisible(true);
    }

    private void initMainPanel() {
        // Make the main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(800, 600));
        mainPanel.setBackground(Color.BLACK);

        // Add empty borders to the main panel
        mainPanel.add(makeBorderPanel(new Dimension(0, borderSize)), BorderLayout.NORTH);
        mainPanel.add(makeBorderPanel(new Dimension(0, borderSize)), BorderLayout.SOUTH);
        mainPanel.add(makeBorderPanel(new Dimension(borderSize, 0)), BorderLayout.WEST);
        mainPanel.add(makeBorderPanel(new Dimension(borderSize, 0)), BorderLayout.EAST);

        // Section Navigation html display
        sectionNavigation = new JEditorPane("text/html", "<h1>Section Navigation</h1>");
        sectionNavigation.setBorder(new EtchedBorder());
        sectionNavigation.setEditable(false);

        // Toggle Navigation Button
        toggleNavigationButton = new JButton(toggleNavigationAction);
        toggleNavigationButton.setPreferredSize(new Dimension(borderSize, 0));
        toggleNavigationButton.setText(collapseButtonText);
        toggleNavigationButton.setMargin(new Insets(0,0,0,0));

        // Content html display
        content = new JEditorPane("text/html", "<h1>Content</h1>");
        content.setBorder(new EtchedBorder());
        content.setEditable(false);

        // create and fill the toggle button/border between nav and content
        JPanel borderPanel = new JPanel(new GridLayout(1, 2));
        borderPanel.add(toggleNavigationButton);
        borderPanel.add(makeBorderPanel(new Dimension(borderSize, 0)));

        // create and fill the Section Navigation panel
        navContainerPanel = new JPanel(new BorderLayout());
        navContainerPanel.setPreferredSize(new Dimension(navDefaultSize, 0));
        navContainerPanel.add(sectionNavigation, BorderLayout.CENTER);
        navContainerPanel.add(borderPanel, BorderLayout.EAST);

        // create and fill the center part of the main panel
        JPanel centerMainPanel = new JPanel(new BorderLayout());
        centerMainPanel.add(navContainerPanel, BorderLayout.WEST);
        centerMainPanel.add(content, BorderLayout.CENTER);

        // finally add the center part of the main panel to the main panel
        mainPanel.add(centerMainPanel, BorderLayout.CENTER);
    }

    private JPanel makeBorderPanel(Dimension size) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(size);
        return panel;
    }

    private void initActionItems() {
        toggleNavigationAction = new AbstractAction("Toggle Navigation") {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSectionNavigation();
            }
        };

        navigateNextLinkAction = new AbstractAction("Next Link") {
            {

            }

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        navigatePreviousLinkAction = new AbstractAction("Previous Link") {
            {

            }

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        closeUserManualAction = new AbstractAction("Exit User Manual") {
            {
                /*
                 * Using Ctrl-E for this instead of Ctrl-C or Ctrl-Q.
                 *
                 * Ctrl-C conflicts with Windows' Copy
                 * Ctrl-Q would be too easy to accidentally press multiple
                 *      times and end up closing the program unintentionally
                 *
                 * Ctrl-E does not conflict with any other accelerator keys
                 */
                putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
                putValue(MNEMONIC_KEY, KeyEvent.VK_E);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                userManualFrame.setVisible(false);
            }
        };
    }


    private void initInputActionMaps() {
        InputMap inputMap = mainPanel.getRootPane()
                                     .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = mainPanel.getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T,
                        InputEvent.CTRL_DOWN_MASK), "toggleNavigation");
        actionMap.put("toggleNavigation", toggleNavigationAction);
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu navigationMenu = new JMenu("Navigation");
        navigationMenu.setMnemonic(KeyEvent.VK_N);
        navigationMenu.add(new JMenuItem(toggleNavigationAction));
        navigationMenu.add(new JMenuItem(closeUserManualAction));
        menuBar.add(navigationMenu);

        userManualFrame.setJMenuBar(menuBar);
    }

    private void toggleSectionNavigation() {
        if (sectionNavigation.isVisible()) {
            sectionNavigation.setVisible(false);
            toggleNavigationButton.setText(expandButtonText);
            navContainerPanel.setPreferredSize(new Dimension(borderSize * 2, 0));
        } else {
            sectionNavigation.setVisible(true);
            toggleNavigationButton.setText(collapseButtonText);
            navContainerPanel.setPreferredSize(new Dimension(navDefaultSize, 0));
        }

        mainPanel.revalidate();
    }
}
