// Eric Li, Charlie Zhao, ICS4U, Completed 6/20/2022
// SingleTile class adds sprites to the tile class
import java.awt.*;
import java.io.Serializable;

public class SingleTile extends Tile implements Serializable {
    public BufferedImageWrapper tileImage;

    public SingleTile(int x, int y, BufferedImageWrapper tileImage) throws SpriteException {
        //Creates tile
        super(x, y);
        //Sets sprite for the tile
        if (tileImage.image.getWidth() != tileImage.image.getHeight()) {
            throw new SpriteException();
        }
        this.tileImage = tileImage;
    }

    //Draws the tile on the screen
    public void draw(Graphics g){
        g.drawImage(tileImage.image, x-GameFrame.game.camera.x, y, length, length, null);
    }
}
