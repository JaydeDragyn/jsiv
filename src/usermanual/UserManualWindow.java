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
    private static String userManual = "";
    private ArrayList<JMenuItem> navigationMenuItems;
    private int navigationIndex = 0;

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
        sectionNavigation = new JEditorPane("text/html", "Section Navigation");
        sectionNavigation.setBorder(new EtchedBorder());
        sectionNavigation.setEditable(false);
        sectionNavigation.addHyperlinkListener(this);

        // Toggle Navigation Button
        toggleNavigationButton = new JButton(toggleNavigationAction);
        toggleNavigationButton.setPreferredSize(new Dimension(borderSize, 0));
        toggleNavigationButton.setText(collapseButtonText);
        toggleNavigationButton.setMargin(new Insets(0,0,0,0));

        // Content html display
        content = new JEditorPane("text/html", "Content");
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

            }

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        helpInstructionsAction = new AbstractAction("User Manual Instructions") {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        helpAboutAction = new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {

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

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK),
                        "reload");
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

        JMenu navigationMenu = new JMenu("Navigation");
        navigationMenu.add(navigateBackAction);
        navigationMenu.addSeparator();
        navigationMenu.add(new JMenuItem("3.2.2.5 Section about something"));
        navigationMenu.add(new JMenuItem("1 Quick Reference"));
        navigationMenu.add(new JMenuItem("3.5 Section about something else"));
        navigationMenu.add(new JMenuItem("3.2 Section about widgets"));
        navigationMenu.add(new JMenuItem("5 Section about Menus"));
        navigationMenu.add(new JMenuItem("3.1 Section about using things"));
        navigationMenu.add(new JMenuItem("3.2.2.2 Section about zooming"));
        navigationMenu.add(new JMenuItem("3.2.4.1 Section about panning"));
        navigationMenu.add(new JMenuItem("6 Section about StatusBar"));
        navigationMenu.add(new JMenuItem("4 Section about abstraction"));
        menuBar.add(navigationMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.add(helpInstructionsAction);
        helpMenu.add(helpAboutAction);
        menuBar.add(helpMenu);

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

    private void loadDocument(String documentPath) {
        // If no filename has been specified
        if (documentPath.isEmpty()) {
            setContentErrorMessage("<h1>No User Manual specified, nothing to display.</h1>");
            return;
        }

        // A path was provided for the document, convert to URL if we can
        URL url = getClass().getResource(documentPath);
        if (url == null) {
            setContentErrorMessage("<h1>Unable to find " + documentPath + "</h2>");
            return;
        }

        try {
            // Now try to read the document
            String document = Files.readString(Path.of(url.toURI()));

            // We have the document, set the base URL so any image links
            // work properly, and then put the document into the content Pane
            content.setContentType("text/html");
            HTMLDocument doc = (HTMLDocument) content.getDocument();
            doc.setBase(url);
            content.setText(document);
            content.setCaretPosition(0);

            // Finally, populate the navigationSection
            // with links from the document
            populateNavigationSection(document);
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
                           + "\n  <h2>Section Navigation</h2>"
                           + "\n";

        // Now parse the document for those links
        // looking for <a name="XXX">YYY</a> labels
        // Display YYY as the body of an anchor that points to #XXX
        Pattern p = Pattern.compile("<a name=\"(.*?)\">(.*?)</a>", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(document);

        while (m.find()) {
            String link = m.group(1);
            String label = m.group(2);

            navDocument += "<a href=\"#" + link + "\">" + label + "<br>\n";
        }

        // Now close the body and the document
        navDocument += "</body></html>";

        // Finally, set this to be the sectionNavigation's content
        sectionNavigation.setText(navDocument);
        showSectionNavigation(true);
    }
}
