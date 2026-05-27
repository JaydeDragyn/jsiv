/*
 * Copyright (c) 2026 Jayde Dragyn
 * Licensed under the MIT License. 
 * See LICENSE.MD file in the project root for full license information.
 */

import javax.swing.SwingUtilities;
import jsiv.ProgramWindow;

public class jsiv {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProgramWindow::new);
    }
    
}
