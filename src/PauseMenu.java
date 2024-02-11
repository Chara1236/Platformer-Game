// Eric Li, Charlie Zhao, ICS4U, Finished 6/17/2022
// Render the pause menu and offset text

import java.awt.*;
import java.io.Serializable;

public class PauseMenu extends TextBox implements Serializable {
    public boolean hasBorder;
    public PauseMenu(int y, int textYOffset, int xWidth, int yHeight, int totalWidth, Font font, String text, boolean hasBorder) {
        super(y, xWidth, yHeight, totalWidth, font, text, null);
        // shift y up by the offset given
        this.y -= textYOffset;
        this.hasBorder = hasBorder;
    }

    public void drawCenteredTextBox(Graphics g, String text, Color backgroundColor, Color textColor) {
        // draw background if backgroundColor is not null
        // please note that later uses of this class (and TextBox) pass a fully transparent color to make debugging easier
        if (backgroundColor != null) {
            // set color of border
            g.setColor(textColor);
            if (hasBorder) {
                ((Graphics2D) g).setStroke(new BasicStroke(4f));
            }
            // draw border
            g.drawRect(newX, newY, xWidth, yHeight);
            // set color of rectangle and draw rectangle in border
            g.setColor(backgroundColor);
            g.fillRect(newX, newY, xWidth, yHeight);
        }
        // set text color and draw centered string
        g.setColor(textColor);
        drawCenteredString(g, y, newX, xWidth, text);
    }
}
