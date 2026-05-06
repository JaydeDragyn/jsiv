package usermanual;

import java.io.*;
import java.nio.file.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.event.HyperlinkEvent.EventType;

public class UserManualWindow implements HyperlinkListener {

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
    private Action exitUserManualAction;

    private Action reloadUserManualAction;

    // Window components
    private static UserManualWindow userManualWindow;
    private static String windowTitle = "User Manual";
    private JFrame userManualFrame;
    private JPanel mainPanel;
    private JPanel navContainerPanel;
    private JEditorPane sectionNavigation;
    private JButton toggleNavigationButton;
    private boolean navigationVisible = true;
    private JEditorPane content;
    private JScrollPane contentScrollPane;
    private static String document = "";
    private static String userManual = "";

    // Link navigation

    public static void setTitle(String title) {
        windowTitle = title;
        if (userManualWindow != null) {
            userManualWindow.setWindowTitle(title);
        }
    }

    public static void setDocument(String filename) {
        document = filename;
        if (userManualWindow != null) {
            userManualWindow.loadDocument(document);
        }
    }

    public static void show() {
        if (userManualWindow == null) {
            userManualWindow = new UserManualWindow();
        }
        userManualWindow.showUserManual();
    }

    private UserManualWindow() {
        userManualFrame = new JFrame(windowTitle);

        initActionItems();
        initMainPanel();
        initMenu();
        initInputActionMaps();

        userManualFrame.pack();

        loadDocument(document);
    }

    public void showUserManual() {
        userManualFrame.setVisible(true);
    }

    public void setWindowTitle(String title) {
        userManualFrame.setTitle(title);
    }

    private void initMainPanel() {
        // Make the main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(800, 600));

        // Add empty borders to the main panel
        mainPanel.add(makeBorderPanel(new Dimension(0, borderSize)), BorderLayout.NORTH);
        mainPanel.add(makeBorderPanel(new Dimension(0, borderSize)), BorderLayout.SOUTH);
        mainPanel.add(makeBorderPanel(new Dimension(borderSize, 0)), BorderLayout.WEST);
        mainPanel.add(makeBorderPanel(new Dimension(borderSize, 0)), BorderLayout.EAST);

        // Section Navigation html display
        sectionNavigation = new JEditorPane("text/html", "<h1>Section Navigation</h1>");
        sectionNavigation.setBorder(new EtchedBorder());
        sectionNavigation.setEditable(false);
        sectionNavigation.addHyperlinkListener(this);

        // Toggle Navigation Button
        toggleNavigationButton = new JButton(toggleNavigationAction);
        toggleNavigationButton.setPreferredSize(new Dimension(borderSize, 0));
        toggleNavigationButton.setText(collapseButtonText);
        toggleNavigationButton.setMargin(new Insets(0,0,0,0));

        // Content html display
        content = new JEditorPane("text/html", "<h1>Content</h1>");
        content.setBorder(new EtchedBorder());
        content.setEditable(false);
        content.addHyperlinkListener(this);

        // create and fill the toggle button/border between nav and content
        JPanel borderPanel = new JPanel(new GridLayout(1, 2));
        borderPanel.add(toggleNavigationButton);
        borderPanel.add(makeBorderPanel(new Dimension(borderSize, 0)));

        // Put the Section Navigation display into a Scroll pane
        JScrollPane sectionNavigationScrollPane = new JScrollPane(sectionNavigation);

        // create and fill the Section Navigation panel
        navContainerPanel = new JPanel(new BorderLayout());
        navContainerPanel.setPreferredSize(new Dimension(navDefaultSize, 0));
        navContainerPanel.add(sectionNavigationScrollPane, BorderLayout.CENTER);
        navContainerPanel.add(borderPanel, BorderLayout.EAST);

        // Put the Content display into a Scroll pane
        contentScrollPane = new JScrollPane(content);

        // create and fill the center part of the main panel
        JPanel centerMainPanel = new JPanel(new BorderLayout());
        centerMainPanel.add(navContainerPanel, BorderLayout.WEST);
        centerMainPanel.add(contentScrollPane, BorderLayout.CENTER);

        // add the center part of the main panel to the main panel
        mainPanel.add(centerMainPanel, BorderLayout.CENTER);

        // finally, put the main panel in the center of the frame
        userManualFrame.add(mainPanel, BorderLayout.CENTER);
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

        exitUserManualAction = new AbstractAction("Exit User Manual") {
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

        reloadUserManualAction = new AbstractAction("Reload User Manual") {
            {
                putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
                putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                setDocument(document);
            }
        };
    }


    private void initInputActionMaps() {
        InputMap inputMap = mainPanel.getRootPane()
                                     .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = mainPanel.getRootPane().getActionMap();


    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu navigationMenu = new JMenu("Navigation");
        navigationMenu.setMnemonic(KeyEvent.VK_N);
        navigationMenu.add(new JMenuItem(reloadUserManualAction));
        navigationMenu.add(new JMenuItem(toggleNavigationAction));
        navigationMenu.add(new JMenuItem(exitUserManualAction));
        menuBar.add(navigationMenu);

        userManualFrame.setJMenuBar(menuBar);
    }

    private void toggleSectionNavigation() {
        if (navigationVisible) {
            sectionNavigation.setVisible(false);
            toggleNavigationButton.setText(expandButtonText);
            navContainerPanel.setPreferredSize(new Dimension(borderSize * 2, 0));
            navigationVisible = false;
        } else {
            sectionNavigation.setVisible(true);
            toggleNavigationButton.setText(collapseButtonText);
            navContainerPanel.setPreferredSize(new Dimension(navDefaultSize, 0));
            navigationVisible = true;
        }

        mainPanel.revalidate();
    }

    private void showSectionNavigation(boolean setVisible) {
        if (navigationVisible != setVisible) { toggleSectionNavigation(); }
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            String bookmark = e.getDescription();
            if (bookmark != null && bookmark.startsWith("#")) {
                bookmark = bookmark.substring(1);
            }
            content.scrollToReference(bookmark);
        }
    }

    private void loadDocument(String filename) {
        // If no filename has been specified
        if (filename.isEmpty()) {
            setContent("<h1>No User Manual specified, nothing to display.</h1>");
            sectionNavigation.setText("");
            showSectionNavigation(false);
            return;
        }

        // A filename was specified, try to read the file and set
        // the content to that file content
        try {
            String c = Files.readString(new File(filename).toPath());
            setContent(c);
        }
        // Could not set the content page to the filename
        catch (IOException e) {
            setContent("<h1>Unable to load " + filename + "</h1>");
            sectionNavigation.setText("");
            showSectionNavigation(false);
            return;
        }

        // content has been set and displayed, gather index links

    }

    private void setContent(String newContent) {
        userManual = newContent;
        content.setText(userManual);
        content.setCaretPosition(0);
    }
}
