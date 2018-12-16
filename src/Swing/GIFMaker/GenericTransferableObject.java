package colorpixelreader.Swing.GIFMaker;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author nikalsh
 */
class GenericTransferableObject<T> implements Transferable {

    List<T> list;

    public GenericTransferableObject(T objectToMakeTransferable) {
        list = new LinkedList<>();
        list.add(objectToMakeTransferable);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.javaFileListFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.javaFileListFlavor.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return list;
    }
}
