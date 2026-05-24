package usermanual;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.HTMLDocument;

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
    private Action closeUserManualAction;
    private Action navigateBackAction;
    private Action helpInstructionsAction;
    private Action helpAboutAction;

    private Action reloadUserManualAction;  // Hidden option.
                                            // Useful for writing/editing
                                            // the user manual without
                                            // having to re-compile or
                                            // re-run the program over
                                            // and over.

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
    private JMenu navigationMenu;
    private ArrayList<JCheckBoxMenuItem> navigationList;
    private ArrayList<String> navigationListText;
    private int navigationListIndex = 0;
    private HashMap<String, String> navigationMap;
    private static final int NAVIGATION_LIST_INDEX_MAX = 10;

    // Link navigation

    public static void setDocument(String filename) {
        document = filename;
        if (userManualWindow != null) {
            userManualWindow.loadDocument(document);
        }
    }

    public static void setTitle(String title) {
        windowTitle = title;
        if (userManualWindow != null) {
            userManualWindow.setWindowTitle(title);
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
        initNavigation();

        userManualFrame.pack();

        loadDocument(document);
    }

    public void setWindowTitle(String title) {
        userManualFrame.setTitle(title);
    }

    public void showUserManual() {
        userManualFrame.setVisible(true);
    }

    private void initMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(800, 600));

        mainPanel.add(makeBorderPanel(new Dimension(0, borderSize)), BorderLayout.NORTH);
        mainPanel.add(makeBorderPanel(new Dimension(0, borderSize)), BorderLayout.SOUTH);
        mainPanel.add(makeBorderPanel(new Dimension(borderSize, 0)), BorderLayout.WEST);
        mainPanel.add(makeBorderPanel(new Dimension(borderSize, 0)), BorderLayout.EAST);

        sectionNavigation = new JEditorPane("text/html", "Section Navigation");
        sectionNavigation.setBorder(new EtchedBorder());
        sectionNavigation.setEditable(false);
        sectionNavigation.addHyperlinkListener(this);

        toggleNavigationButton = new JButton(toggleNavigationAction);
        toggleNavigationButton.setPreferredSize(new Dimension(borderSize, 0));
        toggleNavigationButton.setText(collapseButtonText);
        toggleNavigationButton.setMargin(new Insets(0,0,0,0));

        content = new JEditorPane("text/html", "Content");
        content.setBorder(new EtchedBorder());
        content.setEditable(false);
        content.addHyperlinkListener(this);

        JPanel borderPanel = new JPanel(new GridLayout(1, 2));
        borderPanel.add(toggleNavigationButton);
        borderPanel.add(makeBorderPanel(new Dimension(borderSize, 0)));

        JScrollPane sectionNavigationScrollPane = new JScrollPane(sectionNavigation);

        navContainerPanel = new JPanel(new BorderLayout());
        navContainerPanel.setPreferredSize(new Dimension(navDefaultSize, 0));
        navContainerPanel.add(sectionNavigationScrollPane, BorderLayout.CENTER);
        navContainerPanel.add(borderPanel, BorderLayout.EAST);

        contentScrollPane = new JScrollPane(content);

        JPanel centerMainPanel = new JPanel(new BorderLayout());
        centerMainPanel.add(navContainerPanel, BorderLayout.WEST);
        centerMainPanel.add(contentScrollPane, BorderLayout.CENTER);

        mainPanel.add(centerMainPanel, BorderLayout.CENTER);

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

        closeUserManualAction = new AbstractAction("Close User Manual") {
            {
                // No Accelerator key for this action
                putValue(MNEMONIC_KEY, KeyEvent.VK_C);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                userManualFrame.setVisible(false);
            }
        };

        navigateBackAction = new AbstractAction("Back") {
            {
                putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
                putValue(MNEMONIC_KEY, KeyEvent.VK_B);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                navigationListIndex = Math.max(-1, navigationListIndex -1);

                if (navigationListIndex >= 0) {
                    // We can still go "Back"
                    String link = navigationListText.get(navigationListIndex);
                    content.scrollToReference(link);
                } else {
                    // End of the list, go to the top.
                    content.setCaretPosition(0);
                }
                updateNavigationMenu();
            }
        };

        helpInstructionsAction = new AbstractAction("User Manual Instructions") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserManualInstructionsDialog();
            }
        };

        helpAboutAction = new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutHelpDialog();
            }
        };

        // Hidden option for use in editing a User Manual
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
        // Keys we will use
        KeyStroke ctrlR = KeyStroke.getKeyStroke(KeyEvent.VK_R,
                                                InputEvent.CTRL_DOWN_MASK);
        KeyStroke backspace = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);

        // First, unbind Backspace so we can use it for navigate Back
        // (we will not add it here, it will be added in initMenu()
        //  but we unbind it here since this method handles keybinds)
        sectionNavigation.getInputMap().put(backspace, "none");
        content.getInputMap().put(backspace, "none");

        // Now add our key binds
        JRootPane rootPane = mainPanel.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(ctrlR, "reload");

        actionMap.put("reload", reloadUserManualAction);


    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        viewMenu.add(new JMenuItem(toggleNavigationAction));
        viewMenu.addSeparator();
        viewMenu.add(new JMenuItem(closeUserManualAction));
        menuBar.add(viewMenu);

        navigationMenu = new JMenu("Navigation");
        navigationMenu.setMnemonic(KeyEvent.VK_N);
        navigationMenu.add(navigateBackAction);
        navigationMenu.addSeparator();
        menuBar.add(navigationMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.add(helpInstructionsAction);
        helpMenu.add(helpAboutAction);
        menuBar.add(helpMenu);

        userManualFrame.setJMenuBar(menuBar);
    }

    private void initNavigation() {
        navigationList = new ArrayList<>(NAVIGATION_LIST_INDEX_MAX);
        navigationListText = new ArrayList<>(NAVIGATION_LIST_INDEX_MAX);

        for (int i = 0; i < NAVIGATION_LIST_INDEX_MAX; i++) {
            final int link = i;
            navigationList.add(new JCheckBoxMenuItem(new AbstractAction("") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    navigateToLink(link);
                }
            }));
        }

        navigationList.get(0).setText("<Empty>");
        navigationMenu.add(navigationList.get(0));
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
            String link = e.getDescription();
            if (link != null && link.startsWith("#")) {
                link = link.substring(1);
            }
            content.scrollToReference(link);
            addLink(link);
            updateNavigationMenu();
        }
    }

    private void navigateToLink(int linkNumber) {
        navigationListIndex = linkNumber;
        String link = navigationListText.get(navigationListIndex);
        content.scrollToReference(link);
        addLink(link);
        updateNavigationMenu();
    }

    private void addLink(String link) {
        // Check to see if the link is already in the list
        int index = navigationListText.indexOf(link);

        if (index>= 0) {
            // If link is in the list already, move it to the top
            shiftNavigationListTextLinksDown(index);
            navigationListText.set(navigationListText.size() - 1, link);
        } else {
            // Otherwise put this new link on top.
            if (navigationListText.size() == NAVIGATION_LIST_INDEX_MAX) {
                // If the list already has NAVIGATION_LIST_INDEX_MAX entries
                // then shift everything down so the oldest falls off
                shiftNavigationListTextLinksDown(0);
                navigationListText.set(navigationListText.size() - 1, link);
            } else {
                // Otherwise just add this new link to the end
                navigationListText.add(link);
            }
        }

        // Now mark this link as the current
        navigationListIndex = navigationListText.size() - 1;
    }

    private void shiftNavigationListTextLinksDown(int startIndex) {
        // Starting at index given, shift all links after it down one.
        // The last two links will be the same - assumption is caller
        // will change that last link themselves
        for (int i = startIndex + 1; i < navigationListText.size(); i++) {
            navigationListText.set(i - 1, navigationListText.get(i));
        }
    }

    private void updateNavigationMenu() {
        // First, update the navigationList with the navigationListStrings
        for (int i = 0; i < navigationListText.size(); i++) {
            navigationList.get(i).setText(navigationMap.get(navigationListText.get(i)));
            navigationList.get(i).setState(i == navigationListIndex);
        }

        // Then check to see if all 10 navigation menu items are there
        if (navigationMenu.getMenuComponentCount() < NAVIGATION_LIST_INDEX_MAX + 2) {
            // User has not clicked 10 links yet so clear and reset the menu
            // with the active navigation list menu items.
            navigationMenu.removeAll();

            // Start with the Back and separator that should always be present
            navigationMenu.add(navigateBackAction);
            navigationMenu.addSeparator();

            // Now loop through the JMenuItems backwards (so the lowest index
            // is the lowest in the list) and add each that is not ""
            for (int i = NAVIGATION_LIST_INDEX_MAX - 1; i >= 0; i--) {
                if (!navigationList.get(i).getText().isEmpty()) {
                    navigationMenu.add(navigationList.get(i));
                }
            }
        }
    }

    private void loadDocument(String documentPath) {
        // If no filename has been specified
        if (documentPath.isEmpty()) {
            setContentErrorMessage(
                    "<h1>No User Manual specified, nothing to display.</h1>");
            return;
        }

        // A path was provided for the document, convert to URL if we can
        URL url = getClass().getResource(documentPath);
        if (url == null) {
            setContentErrorMessage("<h1>Unable to find " + documentPath + "</h1>");
            return;
        }

        try {
            // Now try to read the document
            String htmlContent = Files.readString(Path.of(url.toURI()));

            // We have the document, set the base URL so any image links work
            // properly, and then put the htmlContent into the content Pane
            content.setContentType("text/html");
            HTMLDocument doc = (HTMLDocument) content.getDocument();
            doc.setBase(url);
            content.setText(htmlContent);
            content.setCaretPosition(0);

            // Finally, populate the navigationSection
            // with links from the htmlContent
            populateNavigationSection(htmlContent);
        }
        // Could not set the content page to the filename
        catch (Exception e) {
            setContentErrorMessage("<h1>Error reading " + documentPath + "</h1>");
            return;
        }
    }

    private void setContentErrorMessage(String errorMessage) {
        content.setText(errorMessage);
        content.setCaretPosition(0);
        sectionNavigation.setText("");
        showSectionNavigation(false);
    }

    private void populateNavigationSection(String document) {
        // This will build an .html document for the navigation pane
        // consisting of links and labels taken in order from the
        // <a name=""> tags of the given document (which we assume
        // is the document that the content pane is displaying).

        // Start with the header and open the body
        String navDocument = "<html>"
                           + "\n<head>"
                           + "\n  <style>"
                           + "\n    body { font-family: sans-serif;"
                           + "\n           font-size: 12pt;"
                           + "\n           padding: 5px;"
                           + "\n    }"
                           + "\n    a { text-decoration: none;"
                           + "\n        color: blue;"
                           + "\n    }"
                           + "\n  </style>"
                           + "\n</head>"
                           + "\n<body>"
                           + "\n  <h2>Jump To...</h2>"
                           + "\n";

        // Now parse the document for those links
        // looking for <a name="XXX">YYY</a> labels
        // Display YYY as the body of an anchor that points to #XXX
        Pattern p = Pattern.compile("<a name=\"(.*?)\">(.*?)</a>",
                                        Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(document);

        // Also set up the navigationMap so that when the user
        // clicks on a link, we can match it to the Section title
        // to display in the Navigation menu
        navigationMap = new HashMap<>();


        while (m.find()) {
            String link = m.group(1);
            String label = m.group(2);

            navDocument += "<a href=\"#" + link + "\">" + label + "<br>\n";
            navigationMap.put(link, label);
            navigationMap.put(label, link);
        }

        // Now close the body and the document
        navDocument += "</body></html>";

        // Finally, set this to be the sectionNavigation's content
        sectionNavigation.setText(navDocument);
        showSectionNavigation(true);
    }

    private void showUserManualInstructionsDialog() {
        JOptionPane.showMessageDialog(userManualFrame,
            "User Manual Instructions:\n\n" +
            "Scroll the panel on the right to read the document.\n\n" +
            "Click on a link to jump to the relevant section.\n\n" +
            "Backspace or Menu->Navigation->Back to return to the last\n" +
            "     link you clicked on, or back to the top if there are\n" +
            "     no more links to return to.\n\n" +
            "Navigation menu shows the last 10 links you clicked.\n" +
            "     You can select any of those links to navigate to.\n" +
            "     The checkmark shows the last link visited so you can\n" +
            "     see where Backspace will take you.\n" +
            "     If there are no checkmarks, Backspace will return to\n" +
            "     beginning of the user manual.\n\n" +
            "Click on the button between panels with < to collapse the\n" +
            "     Jump to list.\n" +
            "     If already collapsed, click on the button to the left\n" +
            "     with > to expand the Jump to list.\n",
            "User Manual Instructions",
            JOptionPane.INFORMATION_MESSAGE,
            null
        );
    }

    private void showAboutHelpDialog() {
        JOptionPane.showMessageDialog(userManualFrame,
            "User Manual Viewer\n" +
            "Version 1.0.0\n\n" +
            "Developed by: Jayde Dragyn\n" +
            "Contact: JaydeDragyn@outlook.com\n" +
            "Source code:\n" +
            "     Part of https://github.com/JaydeDragyn/jsiv\n\n" +
            "Built with Java 17.0.4.1 2022-08-18 LTS\n\n" +
            "Technical guidance assisted by:\n" +
            "     Claude Sonnet 4.6 (Anthropic)",
            "User Manual Viewer - About",
            JOptionPane.INFORMATION_MESSAGE,
            null
        );
    }
}
