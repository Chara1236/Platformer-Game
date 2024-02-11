// Eric Li, Charlie Zhao, ICS4U, Finished 6/20/2022
/* SettingPanel acts as the JPanel that controls the settings; it mostly creates new Middleware and adds that Middleware to GamePanel

Child of MenuPanel because it shares most of the characteristics, and child of JPanel because JPanel contains methods for drawing to the screen

Implements KeyListener interface to listen for keyboard input

Implements Runnable interface to use "threading" - let the game do two things at once

*/

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

public class SettingPanel extends MenuPanel {

    //dimensions of window
    public static final int GAME_WIDTH = 1225;
    public static final int GAME_HEIGHT = 630;

    public TextBox title;
    public TextBox up, down, left, right, tip;
    public ArrayList<TextBox> textBoxArray = new ArrayList<>();
    public Font standardFont = new Font(Font.MONOSPACED, Font.BOLD, 60);
    public Font smallFont = new Font(Font.MONOSPACED, Font.PLAIN, 40);
    public Font smallerFont = new Font(Font.MONOSPACED, Font.ITALIC + Font.BOLD, 24);
    public boolean waitForKey;
    public int lastKeyCode = -1;
    public int currentBox = 0;
    public PauseMenu pauseMenu;

    public SettingPanel(CameraPanel gameFrame) throws IOException, SpriteException, UnsupportedAudioFileException, LineUnavailableException {
        super(gameFrame);
        // initialize new textboxes and add the textboxes to the selectable textbox array
        title = new TextBox(100, 400, 100, GAME_WIDTH, standardFont, "Settings", null);
        up = new TextBox(300, 600, 50, GAME_WIDTH, smallFont, "Up", Integer.toString(KeyEvent.VK_W));
        down = new TextBox(400, 600, 50, GAME_WIDTH, smallFont, "Down", Integer.toString(KeyEvent.VK_S));
        left = new TextBox(350, 600, 50, GAME_WIDTH, smallFont, "Left", Integer.toString(KeyEvent.VK_A));
        right = new TextBox(450, 600, 50, GAME_WIDTH, smallFont, "Right", Integer.toString(KeyEvent.VK_D));
        tip = new TextBox(500, 600, 50, GAME_WIDTH, smallerFont, "TIP: Press ESC to return to the main menu", null);
        textBoxArray.add(up);
        textBoxArray.add(left);
        textBoxArray.add(down);
        textBoxArray.add(right);
        // initialize new menu
        // this menu is displayed when the player is rebinding a key
        pauseMenu = new PauseMenu(GAME_HEIGHT/2, 0, 400, 400, GAME_WIDTH, smallFont, "Enter your key", true);
    }

    //call the draw methods in each class to update positions as things move
    public void draw(Graphics g, int playerFrame, int enemyFrame){
        String oldText;
        int middlewareIndex;
        background.draw(g);
        title.draw(g,null, Color.black);
        for (TextBox t: textBoxArray) {
            oldText = t.text;
            middlewareIndex = GameFrame.game.middlewareArray.indexOf(new Middleware(Integer.parseInt(t.id), -1));
            t.text += "(" + (middlewareIndex > -1 ? (char)GameFrame.game.middlewareArray.get(middlewareIndex).newCode:
                    GameFrame.game.middlewareArray.contains(new Middleware(-2, Integer.parseInt(t.id))) ?
                            "None":(char)Integer.parseInt(t.id)) + ")";
            t.draw(g, null, Color.cyan);
            t.text = oldText;
        }
        oldText = textBoxArray.get(currentBox).text;
        tip.draw(g, new Color(0, 0, 0, 0), Color.lightGray);
        middlewareIndex = GameFrame.game.middlewareArray.indexOf(new Middleware(Integer.parseInt(textBoxArray.get(currentBox).id), -1));
        // -2 was chosen as oldCode instead of -1 to prevent conflicts
        textBoxArray.get(currentBox).text += "(" + (middlewareIndex > -1 ? (char)GameFrame.game.middlewareArray.get(middlewareIndex).newCode:
                GameFrame.game.middlewareArray.contains(new Middleware(-2, Integer.parseInt(textBoxArray.get(currentBox).id))) ?
                        "None":(char)Integer.parseInt(textBoxArray.get(currentBox).id)) + ")";
        textBoxArray.get(currentBox).draw(g, Color.gray, Color.blue);
        textBoxArray.get(currentBox).text = oldText;
        if (waitForKey) {
            g.setColor(new Color(255, 255, 255, 100));
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            pauseMenu.draw(g, Color.white, Color.black);
        }
    }

    // doAction is used to change key bind
    public void doAction() {
        changeKeyBind();
    }

    // check if mouse is hovering over textbox
    public boolean hoverCheck(MouseEvent e) {
        for (TextBox t: textBoxArray) {
            if (t.isHover(e.getX(), e.getY())) {
                // set currentBox to the one the mouse is hovering over
                currentBox = textBoxArray.indexOf(t);
                return true;
            }
        }
        return false;
    }

    // change the keybind for a specific control (e.x., rebinding jump from W to another key)
    public void changeKeyBind() {
        // the player can press escape to cancel rebinding keys
        if (lastKeyCode == KeyEvent.VK_ESCAPE) {
            lastKeyCode = -1;
        } else if (lastKeyCode != -1) {
            boolean canRemove = true;
            while (canRemove) {
                // newCode is -1 as it does not matter
                canRemove = GameFrame.game.middlewareArray.remove(new Middleware(Integer.parseInt(textBoxArray.get(currentBox).id), lastKeyCode));
            }
            // add actual middleware
            GameFrame.game.middlewareArray.add(new Middleware(Integer.parseInt(textBoxArray.get(currentBox).id), lastKeyCode));
            // add middleware to redirect default key
            GameFrame.game.middlewareArray.add(new Middleware(-1, Integer.parseInt(textBoxArray.get(currentBox).id)));
            // lastKeyCode is set to -1 to prevent endless execution
            lastKeyCode = -1;
        }
    }

    //if a key is pressed, we'll process it
    public void keyPressed(KeyEvent e) {
        // if the key is not being rebound right now, intercept it
        if (!waitForKey) {
            e = UtilityFunction.intercept(e, GameFrame.game.middlewareArray);
        }
        // if the key is being rebound, process the rebinding
        if (waitForKey) {
            if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                lastKeyCode = e.getKeyCode();
                waitForKey = false;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { // if the player presses ESC, return to the main menu
            ((CardLayout)gameFrame.getLayout()).show(gameFrame, "menu");
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) { // if the player presses ENTER, rebind the key that is the id of the currently selected textbox
            // logic for changing keys starts here
            waitForKey = true;
        } else { // otherwise, move the currently selected textbox up or down
            currentBox = UtilityFunction.processBox(e, currentBox, textBoxArray);
        }
    }
}
