// Eric Li, Charlie Zhao, ICS4U, Finished 6/17/2022
// Creates a centered textbox with centered text
// The textbox can also have a filled background

import java.awt.*;
import java.io.Serializable;

public class TextBox extends Rectangle implements Serializable {
    int y;
    int xWidth, yHeight;
    int newX, newY;
    Font font;
    public String text;
    public final String id;

    public TextBox(int y, int xWidth, int yHeight, int totalWidth, Font font, String text, String id) {
        newX = (totalWidth - xWidth)/2;
        newY = y - yHeight/2;
        this.yHeight = yHeight;
        this.xWidth = xWidth;
        this.y = y;
        this.font = font;
        this.text = text;
        this.id = id;
    }

    // checks if a mouse is hovering over the textbox
    public boolean isHover(int x, int y) {
        if (x >= newX && x <= newX + xWidth) {
            return (y >= newY && y <= newY + yHeight);
        }
        return false;
    }

    // draws the centered textbox
    public void drawCenteredTextBox(Graphics g, String text, Color backgroundColor, Color textColor) {
        // if the backgroundColor is not null, draw a rectangle filled with that color
        if (backgroundColor != null) {
            g.setColor(backgroundColor);
            g.fillRect(newX, newY, xWidth, yHeight);
        }
        // set text color and draw string
        g.setColor(textColor);
        drawCenteredString(g, y, newX, xWidth, text);
    }

    // draws the centered string
    public static void drawCenteredString(Graphics g, int y, int x, int xWidth, String text) {
        int newX, newY;
        // get font size
        FontMetrics metrics = g.getFontMetrics();
        // determine x for the text
        newX = x + (xWidth - metrics.stringWidth(text)) / 2;
        // center y (half is above y value, half is below y value)
        newY = y + (metrics.getAscent() - metrics.getDescent())/2;
        // draw centered string
        g.drawString(text, newX, newY);
    }

    public void draw(Graphics g, Color backgroundColor, Color textColor) {
        g.setFont(font);
        drawCenteredTextBox(g, text, backgroundColor, textColor);
    }

    @Override
    public boolean equals(Object o) {
        try {
            TextBox t = (TextBox)o;
            // duck typing equals check
            // if it shows the same id, assume that it is equal; this is because ids should be unique
            // also makes some corner cases easier
            return this.id.equals(t.id);
        } catch (ClassCastException e) {
            return false;
        }
    }
}
