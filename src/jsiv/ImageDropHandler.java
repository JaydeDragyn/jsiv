/*
 * Copyright (c) 2026 Jayde Dragyn
 * Licensed under the MIT License.
 * See LICENSE.MD file in the project root for full license information.
 */

package jsiv;

import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import java.awt.datatransfer.DataFlavor;
import java.util.List;
import java.io.File;

public class ImageDropHandler extends TransferHandler {

    private final ImageNavigator imageNavigator;

    public ImageDropHandler(ImageNavigator imageNavigator) {
        this.imageNavigator = imageNavigator;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) { return false; }

        try {
            @SuppressWarnings("unchecked")
            List<File> files = (List<File>) support.getTransferable()
                    .getTransferData(DataFlavor.javaFileListFlavor);

            if (!files.isEmpty()) {
                // We only care about the first file in the list,
                // so if the user dropped multiple files, we ignore
                // the rest.  The order the system reports the files
                // to us determines what we consider the "first file"
                File droppedFile = files.get(0);
                String path = droppedFile.getParent();
                String name = droppedFile.getName();
                imageNavigator.openFile(path, name);
                return true;
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(() ->
                imageNavigator.showError("Dropped file error",
                                    "Could not open the dropped file.")
            );
        }

        return false;
    }
}
