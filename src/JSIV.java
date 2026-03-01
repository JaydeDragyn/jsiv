/*
    JSIV - Jayde's Simple Image/Icon Viewer
    
*/

import javax.swing.SwingUtilities;
import jsiv.ProgramWindow;

public class jsiv {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProgramWindow::new);
    }
    
}
