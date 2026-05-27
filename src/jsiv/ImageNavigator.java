/*
 * Copyright (c) 2026 Jayde Dragyn
 * Licensed under the MIT License. 
 * See LICENSE.MD file in the project root for full license information.
 */

package jsiv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.awt.FileDialog;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageNavigator {

    private ImageNavigatorListener imageNavigatorListener;
    private FileDialog fileDialog;
    private JFrame dialogParent;
    private File currentImageFile;
    private BufferedImage currentImage;
    private ArrayList<File> imageFileList;
    private int imageFileIndex;
    private boolean navigationAvailable;

    public ImageNavigator(ImageNavigatorListener listener, JFrame dialogParent) {
        this.imageNavigatorListener = listener;
        this.dialogParent = dialogParent;
        fileDialog = new FileDialog(dialogParent, "Load an image", FileDialog.LOAD);
        fileDialog.setMultipleMode(false);
        fileDialog.setDirectory(System.getProperty("user.home"));
        imageFileList = new ArrayList<>();
        navigationAvailable = false;
    }

    public void openFile() {
        fileDialog.setVisible(true);

        // if user cancelled, abort
        if (fileDialog.getFile() == null) {
            return;
        }

        openFile(fileDialog.getDirectory(), fileDialog.getFile());
    }


    public void openFile(String path, String name) {
        // user selected a file, try to open the file
        File file = new File(path, name);
        String fullPathName = getFullPathName(file);

        Optional<BufferedImage> tempImage = loadFile(file);

        // if file is not successfully opened, present an alert
        // dialog that lets the user know we could not open the file.
        // then abort.
        if (tempImage.isEmpty()) {
            if (!isImageFile(file)) {
                showError("Unable to open file",
                    fullPathName + "\n" +
                    "File type not supported.");
            } else {
                showError("Unable to open file",
                        "Could not open file: " + fullPathName);
            }
            return;
        }

        // make a list of the filenames in the directory
        createImageFileList(path);

        if (imageFileList.isEmpty()) {
            // How did we get here?  User picked a file in this
            // directory and we successfully opened it, why are
            // there now no image files?  We'll report at least the
            // one file we were able to open and disable navigation.
            changeNavigationAvailability(false);
            imageNavigatorListener.newImageLoaded(file.getName(),
                                                  tempImage.get(), 1, 1);
            return;
        }

        // determine which index the cached file is in the sorted list
        int tempIndex = imageFileList.indexOf(file);

        // if -1 is returned, the file was not found in the index.
        // potential causes are beyond the scope of this program, so
        // we will simply declare an error and abort
        if (tempIndex == -1) {
            showError("File error", fullPathName +
                        " appears to have been moved or deleted.");
            return;
        }

        // if we get here, file loaded successfully,
        // it is in our file list, and we know its index
        // so cache the image and record the index
        currentImageFile = file;
        currentImage = tempImage.get();
        imageFileIndex = tempIndex;

        // emit messages for other components to update
        imageNavigatorListener.newImageLoaded(currentImageFile.getName(),
                                                currentImage,
                                                imageFileIndex +1,
                                                imageFileList.size());
        changeNavigationAvailability(imageFileList.size() > 1);
    }

    public void refresh() {
        // make sure we have an image and a directory to refresh with
        // abort if we do not
        if (currentImageFile == null) { return; }

        // build the new file list
        createImageFileList(currentImageFile.getParent());

        if (imageFileList.isEmpty()) {
            // There are no files in the directory now.  Since we have
            // one file cached, we will pretend that file is still there,
            // and report 1 of 1 with no navigation available.
            // This will keep something in the display and allow the
            // user to open a new file in another directory if they want.
            changeNavigationAvailability(false);
            imageNavigatorListener.newImageLoaded(currentImageFile.getName(),
                                                    currentImage, 1, 1);
            return;
        }

        // determine which index the cached file is in the sorted list
        int tempIndex = imageFileList.indexOf(currentImageFile);

        // if -1 is returned, the current image file was not found in the list.
        // This means the current image file was removed from the directory
        // at some point after it was loaded.  In that case, we will let the
        // index remain as -1.  An openNext() or openPrevious() call will
        // increment or decrement this index and then make sure it is in-bounds
        // of the imageFileList, so leaving it at -1 will pretend the currently
        // displayed file is still there, but as file index 0 of X.  If the user
        // navigates, it will disappear.  This may confuse, but it is a problem
        // with the file system and we are not going to try and fix their system
        // Instead, we'll just emit the new info to reset the image and update
        // the status bar.  Allow navigation if there is at least one image in
        // the list so they can navigate to the image that actually is there
        if (tempIndex == -1) {
            imageNavigatorListener.newImageLoaded(currentImageFile.getName(),
                                                    currentImage,
                                                    imageFileIndex +1,
                                                    imageFileList.size());
            changeNavigationAvailability(imageFileList.size() > 0);
            return;
        }

        // if we get here, the current image was found in the list
        // and we know the index, so record the index
        imageFileIndex = tempIndex;

        // emit messages for other components to update
        imageNavigatorListener.newImageLoaded(currentImageFile.getName(),
                                                currentImage,
                                                imageFileIndex +1,
                                                imageFileList.size());
        changeNavigationAvailability(imageFileList.size() > 1);
    }

    // openNext() and openPrevious() call navigate() with a direction:
    // +1 to go forward/next
    // -1 to go backward/previous
    public void openNext() {
        navigate(+1);
    }

    public void openPrevious() {
        navigate(-1);
    }

    public void showError(String title, String error) {
        JOptionPane.showMessageDialog(dialogParent,
                                      error,
                                      title,
                                      JOptionPane.WARNING_MESSAGE);
    }

    private boolean isImageFile(File file) {
        if (file == null) { return false; }
        if (!file.isFile()) { return false; }

        String name = file.getName().toLowerCase();

        return name.endsWith(".png")
            || name.endsWith(".gif")
            || name.endsWith(".jpg")
            || name.endsWith(".jpeg")
            || name.endsWith(".bmp")
            || name.endsWith(".tif")
            || name.endsWith(".tiff")
            || name.endsWith(".wbmp");
    }

    private void navigate(int direction) {
        // first, make sure navigation is available and that we have a list of
        // more than 1 file to navigate through.  If either is false, emit
        // a message to reinforce that navigation is not available, and then
        // abort since we should not have reached this method call.
        if (!navigationAvailable || imageFileList.size() < 2) {
            changeNavigationAvailability(false);
            return;
        }

        // set aside a temp buffer to try and load the next image(s)
        Optional<BufferedImage> temp;

        // now we loop, attempting to load the image in the list that is
        // adjacent to the current index, in direction (+1 for next, -1 for
        // previous).  If we are unable to load an image, we will remove it
        // from the list.  If the loop ends, we successfully loaded a new
        // image.  Otherwise, the loop will abort with a return after
        // emitting new state information to the other components.
        do {

            // first, check to see if there is more than one image in the list
            // if not, then either we removed all of the other images and are
            // back at the starting image, or we got here incorrectly.
            // Either way, emit messages to reset the current image, update
            // the file index and disable navigation, then we're done
            if (imageFileList.size() < 2) {
                changeNavigationAvailability(false);
                imageNavigatorListener.newImageLoaded(currentImageFile.getName(),
                                                        currentImage, 1, 1);
                return;
            }

            // now move the index in direction.  Using modulo so direction
            // is irrelevant to bounds checking.  Adding the list size
            // ensures the index never goes negative for the modulo.
            imageFileIndex = (imageFileIndex + direction + imageFileList.size())
                             % imageFileList.size();

            // now attempt to load the image
            temp = loadFile(imageFileList.get(imageFileIndex));

            if (temp.isEmpty()) {
                // we did not load anything, so remove the image from the list
                imageFileList.remove(imageFileIndex);
            }

        } while (temp.isEmpty());

        // Now that we're out of the loop, temp holds the new image we just
        // loaded.  Cache that image and emit the new Image and file index
        // No need to change navigation availability - if that was needed,
        // the first check in the loop would have done that.
        currentImageFile = imageFileList.get(imageFileIndex);
        currentImage = temp.get();
        imageNavigatorListener.newImageLoaded(currentImageFile.getName(),
                                                currentImage,
                                                imageFileIndex +1,
                                                imageFileList.size());
    }

    private Optional<BufferedImage> loadFile(File file) {
        BufferedImage tempImage;
        try {
            tempImage = ImageIO.read(file);
            if (tempImage == null) { return Optional.empty(); }
        } catch (IOException e) {
            return Optional.empty();
        }

        return Optional.of(tempImage);
    }

    private void createImageFileList(String path) {
        // clear the list
        imageFileList.clear();

        // gather the filenames
        File directory = new File(path);
        File[] files = directory.listFiles();

        // abort if we didn't get anything'
        if (files == null) { return; }

        // add only image files
        for (File file : files) {
            if (isImageFile(file)) {
                imageFileList.add(file);
            }
        }
        Collections.sort(imageFileList);
    }

    private void changeNavigationAvailability(boolean available) {
        navigationAvailable = available;
        imageNavigatorListener.navigationAvailabilityChanged(available);
    }

    private String getFullPathName(File file) {
        String fullPathName;
        try {
            fullPathName = file.getCanonicalPath();
        } catch (Exception e) {
            fullPathName = file.getAbsolutePath();
        }
        return fullPathName;
    }
}

