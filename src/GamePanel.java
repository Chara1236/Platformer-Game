/* Eric Li, Charlie Zhao, ICS4U, Finished 2022-06-20

GamePanel class acts as the main "game loop" - continuously runs the game and calls whatever needs to be called

Child of JPanel because JPanel contains methods for drawing to the screen

Implements KeyListener interface to listen for keyboard input

Implements Runnable interface to use "threading" - let the game do two things at once

*/

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

public class GamePanel extends JPanel implements Runnable, KeyListener, Serializable {

  //dimensions of window
  public static final int GAME_WIDTH = 1225;
  public static final int GAME_HEIGHT = 630;

  public static final int fireballSpeed = 6;

  public static Font tutorialFont = new Font(Font.MONOSPACED, Font.BOLD, 36);
  public static Font loreFont = new Font(Font.MONOSPACED, Font.ITALIC + Font.BOLD, 36);
  public static Color tutorialColor = Color.darkGray;
  public static Color loreColor = Color.lightGray;
  public int level = 1;
  public PauseMenu loadingMenu;

  public int bombCount;

  public transient JPanel gameFrame;

  public transient Thread gameThread;
  public transient Image image;
  public transient Graphics graphics;
  public transient boolean isNewStart;
  public Player player;
  public BackgroundImage background, cloudOneBackground, cloudTwoBackground, cloudThreeBackground;
  public int playerFrame, enemyFrame;
  // keeps track of how many ticks has elapsed since last frame change
  public int playerFrameCounter = 0;
  public int timeSinceLastSave = 0;
  public transient boolean isPaused;
  public boolean isDialogue, waitForDialogue, mouseAlreadyTranslated;
  public PauseMenu pauseMenu, pauseMenuExitOne, pauseMenuExitTwo, pauseMenuResume;
  public DialogueMenu dialogueMenu;
  public ArrayList<String> dialogueArray = new ArrayList<String>();

  public BufferedImageWrapper[][][] playerSpriteArray = new BufferedImageWrapper[2][2][11];
  public BufferedImageWrapper[][][] slimeSpriteArray = new BufferedImageWrapper[2][2][3];
  public BufferedImageWrapper[] explosionArray = new BufferedImageWrapper[9];

  public Tile[][]map;
  public ArrayList<Middleware> middlewareArray = new ArrayList<>();

  public ArrayList<Tile>particleTiles = new ArrayList<>();

  public ArrayList<Tile>shootingTiles = new ArrayList<>();

  public ArrayList<FireBall>fireballs = new ArrayList<>();
  public ArrayList<NonPlayer>enemy = new ArrayList<>();
  public ArrayList<StickyBomb>bombs = new ArrayList<>();
  public BombDirectionShow bombDir = null;
  public ArrayList<Particle>particles = new ArrayList<Particle>();
  public Camera camera;

  // image imports begin here
  public BufferedImageWrapper backgroundImage = new BufferedImageWrapper(("img/backgrounds/pointyMountains.png"));
  public BufferedImageWrapper cloud1 = new BufferedImageWrapper(("img/backgrounds/cloud1.png"));
  public BufferedImageWrapper cloud2 = new BufferedImageWrapper(("img/backgrounds/cloud2.png"));
  public BufferedImageWrapper cloud3 = new BufferedImageWrapper(("img/backgrounds/cloud3.png"));
  public BufferedImageWrapper bomb;
  public BufferedImageWrapper narratorPortrait = new BufferedImageWrapper(("img/dialogue/Gunther.png"));
  public BufferedImageWrapper villainPortrait = new BufferedImageWrapper("img/dialogue/Bouncer.png");
  public boolean isContinue;
  public boolean isRunning;
  public ArrayList<WallSign> tutorialSign = new ArrayList<WallSign>();
  public ArrayList<WallSign> loreSign = new ArrayList<WallSign>();
  public boolean isLoaded;


  public GamePanel(JPanel gameFrame) throws IOException, SpriteException, UnsupportedAudioFileException, LineUnavailableException {
    // set gameFrame to enable switching between different panels (in the gameFrame)
    this.gameFrame = gameFrame;
    camera = new Camera(0);
    // initialize map
    map = new Tile[1000][18];
    // create background mountains and images
    background = new BackgroundImage(0, 0, backgroundImage, GAME_WIDTH, GAME_HEIGHT, 10, camera);
    cloudOneBackground = new BackgroundImage(200, 200, cloud1, cloud1.image.getWidth(), cloud1.image.getHeight(), 5, camera);
    cloudTwoBackground = new BackgroundImage(600, 250, cloud2, cloud2.image.getWidth(), cloud3.image.getHeight(), 5, camera);
    cloudThreeBackground = new BackgroundImage(1000, 200, cloud3, cloud2.image.getWidth(), cloud3.image.getHeight(), 5, camera);
    // create pause menu and text in pause menu
    pauseMenu = new PauseMenu(GAME_HEIGHT/2, 100, 400, 400, GAME_WIDTH, new Font(Font.MONOSPACED, Font.BOLD, 60), "Paused", true);
    pauseMenuExitOne = new PauseMenu(GAME_HEIGHT/2, 0, 400, 400, GAME_WIDTH, new Font(Font.MONOSPACED, Font.BOLD, 24), "Press ENTER to return", true);
    pauseMenuExitTwo = new PauseMenu(GAME_HEIGHT/2, -20, 400, 400, GAME_WIDTH, new Font(Font.MONOSPACED, Font.BOLD, 24), "to the main menu", true);
    pauseMenuResume = new PauseMenu(GAME_HEIGHT/2, -50, 400, 400, GAME_WIDTH, new Font(Font.MONOSPACED, Font.BOLD, 18), "(or press ESC to resume)", true);
    // create menu shown when loading game
    loadingMenu = new PauseMenu(GAME_HEIGHT/2, 0, GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH, new Font(Font.MONOSPACED, Font.BOLD, 60), "Loading...", true);
    // create text box shown during dialogue
    dialogueMenu = new DialogueMenu(GAME_HEIGHT-100, 200, new Font(Font.MONOSPACED, Font.BOLD, 20), narratorPortrait, true);
    try {
      // load player sprites from disk here
      for (int i = 0; i < 11; i++) {
        String sprite = (String.format("img/walk/p1_walk%s.png", String.format("%1$2s", i+1).replace(' ', '0')));

        playerSpriteArray[1][0][i] = new BufferedImageWrapper(sprite);
        playerSpriteArray[1][1][i] = new BufferedImageWrapper(sprite);
        playerSpriteArray[0][0][i] = new BufferedImageWrapper(sprite, true);
        playerSpriteArray[0][1][i] = new BufferedImageWrapper(sprite, true);
      }
      for (int i = 0; i < 9; i++) {
        explosionArray[i] = new BufferedImageWrapper(("img/misc/bomb/sonicExplosion0" + i + ".png"));
      }
      // load slime sprites from disk here
      // these variables were not defined above because they are temporary variables
      BufferedImageWrapper[] temporarySlimeArray = {new BufferedImageWrapper(("img/enemy/slime/slimeWalk1.png")),
              new BufferedImageWrapper(("img/enemy/slime/slimeWalk2.png")),
                      new BufferedImageWrapper(("img/enemy/slime/slimeDead.png"))};
      BufferedImageWrapper[] flippedTemporarySlimeArray = {new BufferedImageWrapper((("img/enemy/slime/slimeWalk1.png")), true),
              new BufferedImageWrapper(("img/enemy/slime/slimeWalk2.png"), true),
                      new BufferedImageWrapper(("img/enemy/slime/slimeDead.png"), true)};
      // please note that these sprites are reversed compared to the player sprites
      slimeSpriteArray[0][0] = temporarySlimeArray;
      slimeSpriteArray[0][1] = temporarySlimeArray;
      slimeSpriteArray[1][0] = flippedTemporarySlimeArray;
      slimeSpriteArray[1][1] = flippedTemporarySlimeArray;

      // load bomb sprites
      bomb = new BufferedImageWrapper(("img/misc/bomb.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    player = new Player(GAME_WIDTH/2, GAME_HEIGHT/2, playerSpriteArray); //create a player controlled player, set start location to middle of screenk
    // add mouse and keyboard listeners
    addUserInterface();
    this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
    // indicate that dialogue should be shown on load
    isNewStart = true;
    // allow while look in run() to run
    isRunning = true;
  }

  public void addUserInterface() {
    this.setFocusable(true); //make everything in this class appear on the screen
    this.addKeyListener(this); //start listening for keyboard input
    // request focus when the CardLayout selects this game
    requestFocusable();
    // add the mouse adapter methods
    addMouseListener();
  }

  public void addMouseListener() {
    addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
              try {
                if (isDialogue || isPaused) {
                  mouseAlreadyTranslated = true;
                  keyPressed(new KeyEvent(new Component() {}, 0, -1, 0, KeyEvent.VK_ENTER, (char)KeyEvent.VK_ENTER));
                } else {
                  player.mousePressed(e);
                }
              } catch (SpriteException | IOException ex) {
                throw new RuntimeException(ex);
              }
            }
      public void mouseReleased(MouseEvent e) {
        if (mouseAlreadyTranslated) {
          mouseAlreadyTranslated = false;
        } else if (!isDialogue && !isPaused) {
          try {
            player.mouseReleased(e);
          } catch (IOException | SpriteException ex) {
            throw new RuntimeException(ex);
          }
        }
      }



    });
    addMouseMotionListener(new MouseAdapter() {
      public void mouseDragged(MouseEvent e) {
        try {
          player.mouseDragged(e);
        } catch (IOException | SpriteException ex) {
          throw new RuntimeException(ex);
        }
      }
      public void mouseMoved(MouseEvent e) {
        player.mouseMoved(e);
      }
    });
  }

  public void requestFocusable() {
    this.addComponentListener(new ComponentAdapter() {

      @Override
      public void componentShown(ComponentEvent cEvt) {
        Component src = (Component) cEvt.getSource();
        src.requestFocusInWindow();
      }

    });
  }

  // startThread is to be called after the game has started to avoid any issues
  // this allows serialization to work without having to implement a custom readObject method
  public void startThread() {
    //make this class run at the same time as other classes (without this each class would "pause" while another class runs). By using threading we can remove lag, and also allows us to do features like display timers in real time!
    gameThread = new Thread(this);
    gameThread.start();
  }

  //paint is a method in java.awt library that we are overriding. It is a special method - it is called automatically in the background in order to update what appears in the window. You NEVER call paint() yourself
  public void paint(Graphics g){
    //we are using "double buffering here" - if we draw images directly onto the screen, it takes time and the human eye can actually notice flashes of lag as each pixel on the screen is drawn one at a time. Instead, we are going to draw images OFF the screen, then simply move the image on screen as needed. 
    image = createImage(GAME_WIDTH, GAME_HEIGHT); //draw off screen
    graphics = image.getGraphics();
    try {
      draw(graphics, playerFrame);//update the positions of everything on the screen
    } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
      throw new RuntimeException(e);
    }
    g.drawImage(image, 0, 0, this); //move the image on the screen

  }

  //call the draw methods in each class to update positions as things move
  public void draw(Graphics g, int playerFrame) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
    // save old color and font to enable resetting to these after drawing the tutorial
    Color oldColor = g.getColor();
    Font oldFont = g.getFont();
    background.draw(g);
    cloudOneBackground.draw(g);
    cloudTwoBackground.draw(g);
    cloudThreeBackground.draw(g);
    for (WallSign w: tutorialSign) {
      w.draw(g, tutorialColor);
    }
    for (WallSign w: loreSign) {
      w.draw(g, loreColor);
    }
    // reset graphics color to black and font to system default
    g.setColor(oldColor);
    g.setFont(oldFont);
    if (isPaused || isDialogue) {
      // set player frame to 7 to prevent frame from changing when player inputs key presses
      playerFrame = 7;
      Graphics2D g2d = (Graphics2D)(g);
      // remove extraneous details (like bomb counter) from game when paused
      g2d.setPaint(Color.white);
    }
    //Don't want to draw off screen items
    int xMin = Math.max(0,((this.camera.x+GAME_WIDTH)/Tile.length)-(GAME_WIDTH/(2*Tile.length))-5);
    int xMax = Math.min(map.length, 7+xMin + GAME_WIDTH/Tile.length);
    for(int i=xMin; i<xMax; i++){
      for(int j=0; j<map[0].length; j++){
        if(map[i][j]!=null) {
          map[i][j].draw(g);
        }
      }
    }
    // this is not an enhanced for loop to prevent concurrent modification exception
    for(int i=0; i<enemy.size(); i++){
      enemy.get(i).draw(g, enemyFrame);
    }
    // increment current frame player is on if player.draw reports that the player has moved
    playerFrameCounter += player.draw(g, playerFrame);
    for(int i=0; i<bombs.size(); i++){
      // to prevent concurrent modification exception
      // removes exploded bombs, and draws unexploded bombs
      if(i<bombs.size()) {
        if (bombs.get(i).erase) {
          bombs.remove(i);
        } else {
            bombs.get(i).draw(g);
        }
      }
    }
    // draw fireballs if not exploded, otherwise remove fireballs
    for(int i=0; i<fireballs.size(); i++){
      if(fireballs.get(i)!=null&&fireballs.get(i).canUpdate(0,0)) {
        fireballs.get(i).draw(g);
      }
      if(i<fireballs.size()&&fireballs.get(i).dead){
        fireballs.remove(i);
      }


    }

    // show particles
    for(int i=0; i<particles.size(); i++){
      if(i<particles.size()&&particles.get(i)!=null) {
        particles.get(i).draw(g);
        particles.get(i).lifeSpan--;
        if (particles.get(i).lifeSpan <= 0) {
          particles.remove(i);
        }
      }
    }
    // show bomb trajectory preview if player is able to throw a bomb
    if(player.leftMouseDown&&LevelManager.bombs>0&&!player.holdingSteel){
      bombDir = new BombDirectionShow(this.player.x + this.camera.x + player.WIDTH/2, this.player.y+player.HEIGHT/2,
              (Player.mouseX - this.player.x) / 20, (Player.mouseY - this.player.y) / 10);
      bombDir.draw(g);
    }
    // render steel being held based on mouse position
    // additionally, change color of steel depending on whether the player can place the steel or not
    if(player.holdingSteel){
      String filePath = "";
      if(player.canPlaceSteel){
        filePath = "img/tiles/boxes/greenSteel.png";
      } else {
        filePath = "img/tiles/boxes/redSteel.png";
      }
      int x = (player.mouseX + camera.x + GAME_WIDTH / 2) / Tile.length;
      double y = ((player.mouseY / Tile.length))*Tile.length;
      g.drawImage(getImage(filePath),x*Tile.length - (GamePanel.GAME_WIDTH/2)-camera.x,(int)y,Tile.length,Tile.length,null);
    }
    // draw bomb counter (bomb image and amount of bombs remaining)
    g.drawImage(bomb.image,20,20,35,35,null);
    g.drawString("X"+LevelManager.bombs,60,40);
    if (isPaused) {
      // cover background with translucent rectangle
      g.setColor(new Color(255, 255, 255, 100));
      g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
      // draw pauseMenu rectangle and "Paused" text
      pauseMenu.draw(g, Color.white, Color.black);
      // draw instructions in pause menu
      pauseMenuExitOne.draw(g, new Color(0,0, 0, 0), Color.gray);
      pauseMenuExitTwo.draw(g, new Color(0,0, 0, 0), Color.gray);
      pauseMenuResume.draw(g, new Color(0,0, 0, 0), Color.gray);
    } else if (isDialogue) {
      // cover background with translucent rectangle
      g.setColor(new Color(255, 255, 255, 100));
      g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
      try {
        if (waitForDialogue) {
          // draws complete dialogue sentence
          dialogueMenu.currentFrame = dialogueArray.get(0).length();
          dialogueMenu.frameCounter = 0;
          dialogueMenu.draw(g, dialogueArray.get(0), Color.white, Color.black);
        } else if (dialogueMenu.draw(g, dialogueArray.get(0), Color.white, Color.black)) { // draws partial sentence
          // if the partial sentence is the same length as the complete sentence
          // indicate that the game should draw the complete sentence in the future
          waitForDialogue = true;
        }
      } catch (IndexOutOfBoundsException e) {
        // if there is no more dialogue to draw, this means that all the dialogue has already been iterated over and removed
        // therefore, stop drawing the partially translucent rectangle and resume the game
        isDialogue = false;
      }
    }
    if (!isLoaded) {
      // if the game is not loaded, draw the loading screen
      loadingMenu.draw(g, Color.white, Color.black);
    }
  }

  //call the move methods in other classes to update positions
  //this method is constantly called from run(). By doing this, movements appear fluid and natural. If we take this out the movements appear sluggish and laggy
  public void move() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
    // move player character
    player.move();
    // move all enemies
    for (NonPlayer n: enemy) {
      n.move();
    }
    // move bombs flying through the air
    for(int i=0; i<bombs.size(); i++){
      bombs.get(i).move();
    }
    // move generated particles
    for(int i=0; i<particles.size(); i++){
      if(particles.get(i)!=null) {
        particles.get(i).move();
      }
    }
    // move fireballs in flight
    for(int i=0; i<fireballs.size(); i++){
      if(fireballs.get(i)!=null){
        fireballs.get(i).move();
      }
    }
  }

  //handles all collision detection and responds accordingly
  public void checkCollision() {
    // assume player is not grounded until further checks in checkCollision() say that player is grounded
    player.isGrounded = false;
    // regular for loop used to prevent concurrent modification error
    // shift maps in accordance
    for(int i=0; i<map.length; i++){
      for(int j=0; j<map[0].length; j++){
        if(map[i][j]!=null) {
          // updates the realX position (which is affected by camera.x)
          map[i][j].update();
        }
      }
    }
    // update enemy realX positions (which are affected by camera.x)
    for (NonPlayer n: enemy) {
      n.update();
      n.isGrounded = false;
      // kill player if player hits enemy realX
      if(n.collidePlayer(player)&&!n.isDead){
        player.alive = false;
      }
    }

    // prevent player from falling through the floor
    // added to make level editing easier
    if (player.y >= GAME_HEIGHT - Player.PLAYER_HEIGHT) {
        player.y = GAME_HEIGHT - Player.PLAYER_HEIGHT;
        player.yVelocity = 0;
        player.isGrounded = true;
    }

    // prevent enemies from falling through the floor
    // also added to help level editing
    for (NonPlayer n: enemy) {
      if (n.y >= GAME_HEIGHT - n.npcHeight) {
        n.y = GAME_HEIGHT - n.npcHeight;
        n.yVelocity = 0;
        n.isGrounded = true;
      }
    }
}

  //run() method is what makes the game continue running without end. It calls other methods to move objects,  check for collision, and update the screen
  public void run() {
    // if the game was not serialized but rather started, initialize the map through LevelManager
    // also enable dialogue
    if (isNewStart) {
      LevelManager.setLevel(1);
      try {
        player.resetNoSound();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      isDialogue = true;
    } else {
      LevelManager.bombs = bombCount;
    }
    // let draw() know that the game is done loading so it can stop drawing the loading screen
    isLoaded = true;
    //the CPU runs our game code too quickly - we need to slow it down! The following lines of code "force" the computer to get stuck in a loop for short intervals between calling other methods to update the screen.
    long lastTime = System.nanoTime();
    double amountOfTicks = 60;
    double ns = 1000000000/amountOfTicks;
    double delta = 0;
    long now;
    int fireballCounter = 0;
    while(isRunning){ //this is the game loop, terminates on game restart to prevent race conditions. Also reduces lag
      now = System.nanoTime();
      delta = delta + (now-lastTime)/ns;
      lastTime = now;

      //only move objects around and update screen if enough time has passed
      if(delta >= 1){
        if ((!isPaused && !isDialogue) && MenuPanel.gameStart) {
          // only perform game functions if game is not paused or in dialogue
          try {
            move();
          } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            throw new RuntimeException(e);
          }
          // check collisions
          checkCollision();
          // update enemy positions
          updateEnemy();
          // shoot fireballs every 100 ticks
          if(fireballCounter<0){fireballCounter = 100;}
          fireballCounter--;
          if(fireballCounter == 0){
            updateShootingBlock();
          }
          try {
            updateParticle();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          if (playerFrameCounter > 5) {
            // increment player sprite image to be used and keeps it below 12
            playerFrame = (playerFrame + 1) % 11;
            playerFrameCounter -= 5;
          }
        }
        // a new save is made every 100 ticks
        timeSinceLastSave++;
        if (timeSinceLastSave >= 100) {
          timeSinceLastSave -= 100;
          bombCount = LevelManager.bombs;
          try {
            // atomic save to prevent EOF errors
            FileManager.writeObjectToFile("local\\temp_state.dat", this);
            Files.move(Path.of("local", "temp_state.dat"), Path.of("local", "game_state.dat"), ATOMIC_MOVE);
          } catch (IOException | ConcurrentModificationException e) {
            e.printStackTrace();
          }
        }
        repaint();
        delta--;
      }
    }

  }

  // remove dead enemies that have fully faded
  public void updateEnemy(){
    for(int i=0; i<enemy.size(); i++){
      if(enemy.get(i).isDead){
        // enemy.get(i).fadeCounter--;
        if(enemy.get(i).fadeCounter<=0){
          enemy.remove(i);
        }
      }
    }
  }

  // shoot new fireball if enough time has passed
  public void updateShootingBlock(){
    // iterate through every tile that shoots fireballs
    for(Tile i: shootingTiles){
      // shoot fireballs to the left if the shootingTile shoots to the left, etc
      switch (i.shootingDir) {
        case "left" -> fireballs.add(new FireBall(i.x - 20, i.y + Tile.length / 2 - 4, -fireballSpeed, 0, "left", 8, 16));
        case "up" -> fireballs.add(new FireBall(i.x + Tile.length / 2, i.y - 20, 0, -fireballSpeed, "up", 16, 8));
        case "right" -> fireballs.add(new FireBall(i.x + Tile.length + 20, i.y + Tile.length / 2 - 4, fireballSpeed, 0, "right", 8, 16));
        case "down" -> fireballs.add(new FireBall(i.x + Tile.length / 2, i.y + Tile.length + 10, 0, fireballSpeed, "down", 16, 8));
      }
    }
  }
  // create new particles randomly if amount of particles in the particle array is less than 10
  public void updateParticle() throws IOException {
    if(particles.size()<10) {
      for (int i = 0; i < particleTiles.size(); i++) {
        if (UtilityFunction.randInt(1, 20) == 1) {
          // create particle with the lava particle image
          particles.add(new Particle(particleTiles.get(i).x + UtilityFunction.randInt(0, Tile.length), particleTiles.get(i).y + UtilityFunction.randInt(0, Tile.length / 2),
                  UtilityFunction.randInt(-3, 3), UtilityFunction.randInt(-5, 2), UtilityFunction.randInt(5, 9), "img/particles/LavaParticle.png"));
        }
      }
    }
  }

  //if a key is pressed, we'll send it over to the Player class for processing
  public void keyPressed(KeyEvent e){
    // intercept keys through the Middleware class if these are created through the settings
    e = UtilityFunction.intercept(e, middlewareArray);
    // unpause the game if the game is paused, and vice versa
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
      isPaused = !isPaused;
    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      // leave the game if the game was already paused
      // explicitly ignores MouseEvents that were converted to KeyEvents
      // this is to prevent unintentionally quitting the game
      if (isPaused && (e.getWhen() != -1)) {
        ((CardLayout)gameFrame.getLayout()).show(gameFrame, "menu");
      } else if (!waitForDialogue) { // fast-forward the dialogue animation if it is still playing
        waitForDialogue = true;
      } else {
        // play the next dialogue item
        dialogueMenu.currentFrame = 0;
        dialogueMenu.frameCounter = 0;
        // try removing the current dialogue item
        // the exception is ignored if it fails because waitForDialogue is still set to false
        // so it does not matter
        try {
          dialogueArray.remove(0);
        } catch (IndexOutOfBoundsException ignored) {}
        waitForDialogue = false;
      }
    } else {
      try {
        // pass key event to player
        player.keyPressed(e);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  //if a key is released, we'll send it over to the Player class for processing
  public void keyReleased(KeyEvent e){
    // intercept key with new key defined in settings
    e = UtilityFunction.intercept(e, middlewareArray);
    player.keyReleased(e);
    // pressing the P key skips to the next level
    if(e.getKeyCode() == KeyEvent.VK_P && !isDialogue){
      LevelManager.nextLevel();
      try {
        player.resetNoSound();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  //left empty because we don't need it; must be here because it is required to be overridded by the KeyListener interface
  public void keyTyped(KeyEvent e){}

  // read new image with getImage
  public static BufferedImage getImage(String imageLocation) throws IOException {
    return ImageIO.read(new File(imageLocation));
  }

  // flip image horizontally
  public static BufferedImage flipImageHorizontally(BufferedImage originalImage) {
    BufferedImage flippedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    for (int x = 0; x < originalImage.getWidth(); x++) {
      for (int y = 0; y < originalImage.getHeight(); y++) {
        // -1 is added to prevent off-by-one errors
        flippedImage.setRGB(x, y, originalImage.getRGB(originalImage.getWidth()-x-1, y));
      }
    }
    return flippedImage;
  }

}
