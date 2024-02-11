// Eric Li, Charlie Zhao, ICS4U, Finished 6/17/2022
// displays dialogue, animates dialogue, and renders box that contains dialogue as well as the portraits of the characters
// that speak to the players

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class DialogueMenu extends TextBox implements Serializable {
    public static final int PORTRAIT_WIDTH = 200;
    public static final int PADDING = 20;
    public static final int TOP_PADDING = 10;
    public static final double LINE_SPACING = 1.5;
    public static final int FREQUENCY = 2;
    public BufferedImageWrapper portrait;
    public int currentFrame = 0;
    public int frameCounter = 0;
    public boolean isNarrator;
    public SoundWrapper currentSound;

    public DialogueMenu(int y, int yHeight, Font font, BufferedImageWrapper portrait, boolean isNarrator) {
        super(y, GamePanel.GAME_WIDTH - PORTRAIT_WIDTH - PADDING*3, yHeight, 0, font, null, null);
        this.portrait = portrait;
        checkForNarrator();
        this.isNarrator = isNarrator;
    }

    // check if the person speaking is currently the narrator, and adjust the newX value accordingly
    public void checkForNarrator() {
        if (isNarrator) {
            newX = PORTRAIT_WIDTH + PADDING*2;
        } else {
            newX = PADDING;
        }
    }

    // draw a centered text box
    public void drawCenteredTextBox(Graphics g, String text, Color backgroundColor, Color textColor) {
        // only draw currentFrame number of text characters; this animates the process of drawing the text
        text = text.substring(0, currentFrame);
        // only set the background color if it is not null
        if (backgroundColor != null) {
            g.setColor(textColor);
            // if the person speaking is the narrator, draw the textbox to the right of the portrait
            if (isNarrator) {
                g.drawImage(portrait.image, newX - PORTRAIT_WIDTH - PADDING, newY, PORTRAIT_WIDTH, yHeight, null);
            } else { // otherwise, draw the textbox to the left of the portrait
                g.drawImage(portrait.image, GamePanel.GAME_WIDTH - PORTRAIT_WIDTH - PADDING, newY, PORTRAIT_WIDTH, yHeight, null);
            }
            // create border and set it to a width of 4.0
            ((Graphics2D)g).setStroke(new BasicStroke(4f));
            // draw the border
            g.drawRect(newX, newY, xWidth, yHeight - 4);
            // set color of the rectangle inside the border, and draw it
            g.setColor(backgroundColor);
            g.fillRect(newX, newY, xWidth, yHeight - 4);
        }
        // set text color and draw it
        g.setColor(textColor);
        drawCenteredString(g, newY, newX, text);
    }

    // draw a centered string
    public static void drawCenteredString(Graphics g, int y, int x, String text) {
        // split text by spaces (into individual words)
        String[] newText = text.split(" ");
        // create new ArrayList that will compose of every line in the new text
        ArrayList<String> lines = new ArrayList<>();
        // add an empty line; this line will eventually have more text added to it
        lines.add("");
        // get font size
        FontMetrics metrics = g.getFontMetrics();
        // declare temporary variables used in the for loop, and initialize them
        int currentLineWidth = 0, lastLineIndex = 0;
        for (String s: newText) {
            // add width of new word to current line width
            currentLineWidth += metrics.stringWidth(s + " ");
            // if the newLineWidth still fits in the dialogue box, add it to the current line
            if (currentLineWidth - metrics.stringWidth(" ") < (GamePanel.GAME_WIDTH - PORTRAIT_WIDTH - PADDING*5)) {
                lines.set(lastLineIndex, lines.get(lastLineIndex) + s + " ");
            } else { // otherwise, create a new line, set the current line width to the current width, and increment the current line index
                currentLineWidth = metrics.stringWidth(s);
                lines.add(s + " ");
                lastLineIndex ++;
            }
        }
        // leave TOP_PADDING + (metrics.getAscent() - metrics.getDescent()) * LINE_SPACING)
        // space between the top of the dialogue box and the text
        y += TOP_PADDING;
        for (String s: lines) {
            // add spacing since last line
            y += (metrics.getAscent() - metrics.getDescent()) * LINE_SPACING;
            // draw actual string
            g.drawString(s + "\n", x + PADDING, y);
        }
    }

    public boolean draw(Graphics g, String text, Color backgroundColor, Color textColor) {
        // before every draw, check if the status of the current person speaking changed
        checkForNarrator();
        // if more than FREQUENCY ticks has passed since the last text animation, animate by drawing one more char
        if (frameCounter >= FREQUENCY) {
            frameCounter -= FREQUENCY;
            currentFrame += 1;
            // play dialogue sound if it is not already playing
            if (currentSound == null || !currentSound.sound.clip.isOpen()) {
                try {
                    currentSound = UtilityFunction.playSound("sound/pen-2.wav");
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // set font of string to be drawn
        g.setFont(font);
        drawCenteredTextBox(g, text, backgroundColor, textColor);
        // increment the frame counter
        frameCounter++;
        // if the text has been completely drawn (nothing left to animate), return true
        if (currentFrame >= text.length()) {
            currentFrame = 0;
            // if the text is not being animated, there is no reason to play the sound either, so it is closed and then dereferenced
            if (currentSound != null) {
                currentSound.sound.close();
                currentSound = null;
            }
            return true;
        } else { // otherwise, return false
            return false;
        }
    }
}
