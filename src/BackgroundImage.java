// Eric Li, Charlie Zhao, ICS4U, Finished 2022-06-15
// the purpose of this class is to create a background
// this background can also be parallax and move as the player moves
import java.awt.*;
import java.io.Serializable;

public class BackgroundImage implements Serializable {
    public int x, y;
    public int width, height;
    public int parallaxRatio;
    public BufferedImageWrapper backgroundImage;
    public Camera camera;

    public BackgroundImage(int x, int y, BufferedImageWrapper backgroundImage, int width, int height, int parallaxRatio, Camera camera) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.backgroundImage = backgroundImage;
        this.parallaxRatio = parallaxRatio;

        this.camera = camera;
    }
    public void draw(Graphics g){
        // draw image, image moves one pixel every parallaxRatio pixels the camera moves
        g.drawImage(backgroundImage.image, x-camera.x/parallaxRatio % GamePanel.GAME_WIDTH, y, width, height, null);
        // draws second image so that the background is always present
        g.drawImage(backgroundImage.image, x-camera.x/parallaxRatio % GamePanel.GAME_WIDTH + GamePanel.GAME_WIDTH - 1, y, width, height, null);
    }
}
