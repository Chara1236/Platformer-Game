// Eric Li, Charlie Zhao, ICS4U, Finished 6/17/2022
/* MenuPanel class acts as the menu - launches the other panels that need to be called

Child of JPanel because JPanel contains methods for drawing to the screen

Implements KeyListener interface to listen for keyboard input

Implements Runnable interface to use "threading" - let the game do two things at once

*/

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MenuPanel extends JPanel implements Runnable, KeyListener{

    //dimensions of window
    public static final int GAME_WIDTH = 1225;
    public static final int GAME_HEIGHT = 630;

    public CameraPanel gameFrame;

    public static Camera camera;
    public Thread gameThread;
    public Image image;
    public Graphics graphics;
    public BackgroundImage background;
    public TextBox title, enter, settings, continueGame;
    public ArrayList<TextBox> textBoxArray = new ArrayList<>();
    public Font standardFont = new Font(Font.MONOSPACED, Font.BOLD, 60);
    public int playerFrame, enemyFrame;
    // keeps track of how many ticks has elapsed since last frame change
    public int currentBox = 0;

    public static boolean gameStart = false;

    public BufferedImageWrapper backgroundImage = new BufferedImageWrapper(("img/backgrounds/pointyMountains.png"));

    public MenuPanel(CameraPanel gameFrame) throws IOException, SpriteException, UnsupportedAudioFileException, LineUnavailableException {
        this.gameFrame = gameFrame;
        // initialize shared camera to ensure that the background stays consistent between MenuPanel and SettingPanel
        camera = gameFrame.camera;
        // create title textbox
        title = new TextBox(100, 400, 100, GAME_WIDTH, standardFont, "Platformer", null);
        // initialize selectable menu options and add the text boxes to the textBoxArray, which controls which text box is currently highlighted
        continueGame = new TextBox(300, 600, 100, GAME_WIDTH, standardFont, "Continue", "game");
        enter = new TextBox(400, 600, 100, GAME_WIDTH, standardFont, "Start Game", "game-start");
        settings = new TextBox(500, 600, 100, GAME_WIDTH, standardFont, "Settings", "settings");
        textBoxArray.add(enter);
        textBoxArray.add(settings);
        // if there is a loadable save, isContinue is set to true and the Continue textbox is selectable
        if (GameFrame.game.isContinue) {
            textBoxArray.add(0, continueGame);
        }

        background = new BackgroundImage(0, 0, backgroundImage, GAME_WIDTH, GAME_HEIGHT, 10, camera);
        this.setFocusable(true); //make everything in this class appear on the screen
        this.addKeyListener(this); //start listening for keyboard input
        // request focus when the CardLayout selects this game
        // this allows the OS to forward HID input to this panel
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent cEvt) {
                Component src = (Component) cEvt.getSource();
                src.requestFocusInWindow();
            }

        });
        //add the MousePressed method from the MouseAdapter - by doing this we can listen for mouse input. We do this differently from the KeyListener because MouseAdapter has SEVEN mandatory methods - we only need one of them, and we don't want to make 6 empty methods
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // check if the mouse is hovering over a textbox; if it is, run the action connected to the textbox
                if (hoverCheck(e)) {
                    keyPressed(new KeyEvent(new Component() {
                    }, 0, -1, 0, KeyEvent.VK_ENTER, (char)KeyEvent.VK_ENTER));
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                // check if the mouse is now hovering over a text box; if it is, select that textbox
                hoverCheck(e);
            }
        });
        this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

        //make this class run at the same time as other classes (without this each class would "pause" while another class runs). By using threading we can remove lag, and also allows us to do features like display timers in real time!
        gameThread = new Thread(this);
        gameThread.start();
    }

    //paint is a method in java.awt library that we are overriding. It is a special method - it is called automatically in the background in order to update what appears in the window. You NEVER call paint() yourself
    public void paint(Graphics g){
        //we are using "double buffering here" - if we draw images directly onto the screen, it takes time and the human eye can actually notice flashes of lag as each pixel on the screen is drawn one at a time. Instead, we are going to draw images OFF the screen, then simply move the image on screen as needed.
        image = createImage(GAME_WIDTH, GAME_HEIGHT); //draw off screen
        graphics = image.getGraphics();
        draw(graphics, playerFrame, enemyFrame);//update the positions of everything on the screen
        g.drawImage(image, 0, 0, this); //move the image on the screen

    }

    //call the draw methods in each class to update positions as things move
    public void draw(Graphics g, int playerFrame, int enemyFrame){
        // draw background
        background.draw(g);
        // draw title
        title.draw(g,null, Color.black);
        // if there is no loadable save, gray out the Continue textbox
        if (!GameFrame.game.isContinue) {
            continueGame.draw(g, null, Color.gray);
        }
        // draw each selectable textBox in the textbox array
        for (TextBox t: textBoxArray) {
            t.draw(g, null, Color.cyan);
        }
        // overwrite the selectable text box with a background color and a different text color (blue instead of cyan)
        textBoxArray.get(currentBox).draw(g, Color.gray, Color.blue);
    }

    // this function does nothing, but was retained to allow the children of this class to implement their own functions without also having to reimplement run()
    // e.x., a child could override doAction() to launch someFunction()
    public void doAction(){
    }

    //run() method is what makes the game continue running without end. It calls other methods to move objects,  check for collision, and update the screen
    public void run(){
        //the CPU runs our game code too quickly - we need to slow it down! The following lines of code "force" the computer to get stuck in a loop for short intervals between calling other methods to update the screen.
        long lastTime = System.nanoTime();
        double amountOfTicks = 60;
        double ns = 1000000000/amountOfTicks;
        double delta = 0;
        long now;

        while(true){ //this is the infinite game loop
            now = System.nanoTime();
            delta = delta + (now-lastTime)/ns;
            lastTime = now;

            //only move objects around and update screen if enough time has passed
            if(delta >= 1){
                doAction();
                // shift the camera to cause the parallax effect
                camera.x += 10;
                repaint();
                delta--;
            }
        }
    }

    // check if the mouse is hovering over a textbox; if the mouse is hovering over a textbox, select the textbox hovered over
    public boolean hoverCheck(MouseEvent e) {
        for (TextBox t: textBoxArray) {
            // select the textbox hovered over if it is hovering over a textbox
            if (t.isHover(e.getX(), e.getY())) {
                currentBox = textBoxArray.indexOf(t);
                return true;
            }
        }
        return false;
    }

    //if a key is pressed, we'll process it
    public void keyPressed(KeyEvent e) {
        // intercept keypresses and replace them with previously defined keypresses through the Middleware class
        e = UtilityFunction.intercept(e, GameFrame.game.middlewareArray);
        // if the keypress is ENTER, run the action indicated by the connected textbox's id
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // indicate that the game is starting if the player presses "Continue" or "New Game"
            if(textBoxArray.get(currentBox).id.contains("game")){
                gameStart = true;
            }
            // always unpause game, no matter what screen is navigated to
            GameFrame.game.isPaused = false;
            // logic for different screens starts here
            // if the user presses "New Game", reset GamePanel and navigate to it
            if (textBoxArray.get(currentBox).id.equals("game-start")) {
                try {
                    // temporary variable so was not declared earlier
                    // save middleware array to re-add after game is created again
                    ArrayList<Middleware> oldMiddlewareArray = (ArrayList<Middleware>)GameFrame.game.middlewareArray.clone();
                    // remove the old GamePanel from the CardLayout CameraPanel
                    GameFrame.main.remove(GameFrame.game);
                    // stop the run() while loop, effectively killing the thread
                    GameFrame.game.isRunning = false;
                    // reset player velocities to prevent race conditions
                    GameFrame.game.player.xVelocity = 0;
                    GameFrame.game.player.yVelocity = 0;
                    // reset the tile map to prevent race conditions
                    GameFrame.game.map = new Tile[1000][18];
                    GameFrame.game = new GamePanel(GameFrame.main); //run GamePanel constructor
                    // readd middleware array
                    GameFrame.game.middlewareArray = oldMiddlewareArray;
                    // make it so that the game can be resumed if the player leaves
                    GameFrame.game.isContinue = true;
                    textBoxArray.add(continueGame);
                    // start the game
                    GameFrame.game.startThread();
                    // add the game to the CardLayout CameraPanel, enabling navigation
                    GameFrame.main.add(GameFrame.game, "game");
                } catch (IOException | SpriteException | UnsupportedAudioFileException | LineUnavailableException ex) {
                    ex.printStackTrace();
                }
                // switch to the game panel
                ((CardLayout)gameFrame.getLayout()).show(gameFrame, "game");
            } else {
                // switch to the panel indicated by the id of the textbox
                ((CardLayout) gameFrame.getLayout()).show(gameFrame, textBoxArray.get(currentBox).id);
            }
        } else {
            // if the keypress is not ENTER, either select the textbox above or below the currently selected textbox
            currentBox = UtilityFunction.processBox(e, currentBox, textBoxArray);
        }
    }

    public static void deleteFiles(File dirPath) {
        File[] filesList = dirPath.listFiles();
        for(File file : filesList) {
            if(file.isFile()) {
                file.delete();
            } else {
                deleteFiles(file);
            }
        }
    }
    // left empty
    public void keyReleased(KeyEvent e){

    }

    //left empty because we don't need it; must be here because it is required to be overridded by the KeyListener interface
    public void keyTyped(KeyEvent e){

    }

}
