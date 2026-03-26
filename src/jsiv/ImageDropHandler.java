package jsiv;

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
                String path = files.get(0).getParent();
                String name = files.get(0).getName();
                imageNavigator.openFile(path, name);
                return true;
            }
        } catch (Exception e) {
            imageNavigator.showError("Dropped file error",
                                    "Could not open the dropped file.");
        }

        return false;
    }
}
