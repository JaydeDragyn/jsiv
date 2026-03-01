/*
    JSIV - Jayde's Simple Image/Icon Viewer
    
*/

import javax.swing.SwingUtilities;
import JSIV.ProgramWindow;

public class JSIV {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProgramWindow::new);
    }
    
}
