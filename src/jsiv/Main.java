/*
 * Copyright (c) 2026 Jayde Dragyn
 * Licensed under the MIT License.
 * See LICENSE.MD file in the project root for full license information.
 */

package jsiv;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            SwingUtilities.invokeLater(() -> new ProgramWindow(args[0]));
        } else {
            SwingUtilities.invokeLater(ProgramWindow::new);
        }
    }

}
