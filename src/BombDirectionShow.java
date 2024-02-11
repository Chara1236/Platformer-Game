// Eric Li, Charlie Zhao, ICS4U, Finished 2022-06-17
// this class shows the projected trajectory of a bomb based on the current mouse position

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;

public class BombDirectionShow extends StickyBomb implements Serializable {
    public BombDirectionShow(int x, int y, int xVelocity, int yVelocity) {
        super(x, y, xVelocity, yVelocity, null, null);
        WIDTH = 25;
        HEIGHT = 25;
    }

    public void draw(Graphics g) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        // updates realX (affected by camera.x)
        update();
        isMove = true;
        // renders a maximum of 10 rectangles
        int loopCounter = 0;
        while(isMove&&loopCounter<10) {
            super.move();
            // draws rectangles if they don't intersect a tile
            if(isMove&&canUpdate(0,0)) {
                g.drawRect(x - GameFrame.game.camera.x, y, 2, 2);
            }
            loopCounter++;
        }
    }
}
