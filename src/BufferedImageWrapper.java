// Eric Li, Charlie Zhao, ICS4U, Finished 2022-06-19
// Enables serialization of the otherwise unserializable BufferedImage object

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.Hashtable;

public class BufferedImageWrapper implements Serializable {
    transient public BufferedImage image;
    public String imageString;
    public Boolean flipImage = false;

    // same constructor as BufferedImage
    public BufferedImageWrapper(int width, int height, int imageType) {
        image = new BufferedImage(width, height, imageType);
    }

    // same constructor as BufferedImage
    public BufferedImageWrapper(int width, int height, int imageType, IndexColorModel cm) {
        image = new BufferedImage(width, height, imageType, cm);
    }

    // same constructor as BufferedImage
    public BufferedImageWrapper(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied, Hashtable<?, ?> properties) {
        image = new BufferedImage(cm, raster, isRasterPremultiplied, properties);
    }

    // directly load an image into the object
    public BufferedImageWrapper(BufferedImage image) {
        this.image = image;
    }

    // save a file location to load the image from
    public BufferedImageWrapper(String imageString) throws IOException {
        this(imageString, false);
    }

    // save a file location to load the image from, and flip the image if necessary
    public BufferedImageWrapper(String imageString, boolean flip) throws IOException {
        // flip the image by calling the GamePanel static method if the flip boolean is passed as true
        BufferedImage temporaryImage = GamePanel.getImage(imageString);
        if (flip) {
            image = GamePanel.flipImageHorizontally(temporaryImage);
        } else {
            image = temporaryImage;
        }
        flipImage = flip;
        this.imageString = imageString;
    }

    // empty constructor to allow using the custom serializing method readObject
    public BufferedImageWrapper() {}

    // custom writeObject method that allows writing an otherwise unserializable method to disk
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        // save whether the image is flipped
        out.writeObject(flipImage);
        // if the imageString is present, write that to disk to prevent excessive disk writes
        if (imageString != null) {
            out.writeObject(imageString);
        } else { // otherwise, write the image to disk
            ImageIO.write(image, "png", out); // png is lossless
        }
    }

    // custom readObject method that allows reading an otherwise unserializable method from disk
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object o;
        // read whether the image is flipped or not
        flipImage = (Boolean)in.readObject();
        o = in.readObject();
        // if the loaded object is a string, load the image residing at the path received from Path.of(string)
        if (o instanceof String) {
            BufferedImage temporaryImage;
            this.imageString = (String)o;
            temporaryImage = GamePanel.getImage(imageString);
            // then flip the image if the flipImage boolean is true
            if (flipImage) {
                image = GamePanel.flipImageHorizontally(temporaryImage);
            } else {
                image = temporaryImage;
            }
        } else { // otherwise, load the image directly from the save file
            image = ImageIO.read(in);
        }
    }
}
