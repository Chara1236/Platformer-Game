// Eric Li, Charlie Zhao, ICS4U, Completed 6/20/2022
/* GameFrame class establishes the frame (window) for the game
It is a child of JFrame because JFrame manages frames
Creates new JPanel child, and adds GamePanel, MenuPanel, and SettingPanel classes to it, allowing for navigation between them
Also initializes and deserializes them as necessary
*/

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class GameFrame extends JFrame{

  MenuPanel menu;
  public static GamePanel game;
  public static CameraPanel main;
  SettingPanel settings;

  public GameFrame(){
    try {
      // CameraPanel is child of JPanel with camera object
      main = new CameraPanel();
      // CardLayout is used to allow navigating between the different "cards" in the menu
      main.setLayout(new CardLayout());
      try {
        // attempts to read GamePanel object from file
        // if it succeeds, this becomes the main game
        // the second argument includes all the classes that are used by the GamePanel object
        // this ensures that attempting to execute malicious code by tampering with saves will be more difficult, as payloads would have to stick to these classes
        // please note that it is not a perfect mitigation though
        game = (GamePanel)FileManager.readObjectFromFile("local/game_state.dat",
                Arrays.asList("FireBall", "GamePanel", "javax.swing.JPanel", "javax.swing.JComponent", "java.awt.Container",
                        "java.awt.Component", "javax.swing.plaf.ColorUIResource", "java.awt.Color",
                        "javax.swing.plaf.FontUIResource", "java.awt.Font", "java.util.Locale", "java.awt.Dimension",
                        "java.awt.ComponentOrientation", "[Ljava.awt.Component;", "java.awt.FlowLayout",
                        "javax.swing.event.EventListenerList", "BackgroundImage", "BufferedImageWrapper",
                        "java.lang.Boolean", "Camera", "BombDirectionShow", "StickyBomb", "GenericSprite",
                        "java.awt.Rectangle", "java.util.ArrayList", "DialogueMenu", "TextBox", "NonPlayer",
                        "[[[LBufferedImageWrapper;", "[[LBufferedImageWrapper;", "[LBufferedImageWrapper;", "PauseMenu",
                        "WallSign", "[[LTile;", "[LTile;", "SingleTile", "Tile", "Particle", "Player"));
        game.gameFrame = main;
        // shows that the game can be continued, as it was loaded from the file
        game.isContinue = true;
        // requests focus from OS; this is needed because otherwise Windows will not pass keystrokes to game
        game.requestFocusable();
        // add mouse listener to game
        game.addMouseListener();
      } catch (IOException | ClassNotFoundException | ClassCastException | SecurityException e) {
        // if an exception occurs during serialization, it is not a game-breaking exception; it is logged in the console and a new game is created
        System.out.println("[LOG] " + e.toString().replace("Exception", "NotAnError"));
        game = new GamePanel(main); //run GamePanel constructor
      }
      // start game thread to allow the game to run
      game.startThread();
      // save game after load to prevent lag spikes during the game
      FileManager.writeObjectToFile("local\\temp_state.dat", game);
      // create menu screen and settings screen
      menu = new MenuPanel(main);
      settings = new SettingPanel(main);
      // adds all three panels to the CardLayout CameraPanel main, to allow for navigation between them
      // the menu screen is added first because it is the first screen to show upon loading the game
      main.add(menu, "menu");
      main.add(settings, "settings");
      main.add(game, "game");
    } catch (IOException | SpriteException | UnsupportedAudioFileException | LineUnavailableException e) {
      throw new RuntimeException(e);
    }
    this.add(main);
    this.setTitle("Kenney"); //set title for frame
    // set game icon and ignore exception (failing to set icon doesn't otherwise break program)
    try {
      this.setIconImage(GamePanel.getImage("img/misc/favicon.png"));
    } catch (IOException ignored) {}
    this.setResizable(false); //frame can't change size
    this.setBackground(Color.white);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //X button will stop program execution
    this.pack();//makes components fit in window - don't need to set JFrame size, as it will adjust accordingly
    this.setVisible(true); //makes window visible to user
    this.setLocationRelativeTo(null);//set window in middle of screen
  }
  
}
