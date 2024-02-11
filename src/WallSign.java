// Eric Li, Charlie Zhao, ICS4U, Finished 6/17/2022
// Draws signs on the walls of levels

import java.awt.*;
import java.io.Serializable;

public class WallSign extends TextBox implements Serializable {

    public WallSign(int x, int y, Font font, String text) {
        super(0, 0, 0, 0, font, text, null);
        this.newX = x;
        this.y = y;
    }
    public void draw(Graphics g, Color textColor) {
        int oldX = this.newX;
        newX -= GameFrame.game.camera.x;
        super.draw(g, new Color(0, 0, 0, 0), textColor);
        newX = oldX;
    }
}
