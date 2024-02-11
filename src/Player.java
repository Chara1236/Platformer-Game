// Eric Li, Charlie Zhao, ICS4U, Completed 6/20/2022
// Player class defines behaviours for the player controlled character

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class Player extends GenericSprite {
  public static final int PLAYER_WIDTH = 52;
  public static final int PLAYER_HEIGHT = 94;

  public static final double reach = 0.89;
  public static final int steelReachRange = 4*Tile.length;
  public int lastXDirection, lastYDirection, lastFrame;

  public boolean alive;
  public transient Sound jump = new Sound("sound/jump.wav");

  public static int mouseX;
  public static int mouseY;

  public transient boolean leftMouseDown;

  public transient boolean rightMouseDown;
  boolean holdingSteel;

  boolean canPlaceSteel;

  public int pickupDelay = 5;

  // sA[0] is -x, -y
  // sA[1] is x, -y
  // sA[2] is -x, y
  // sA[3] is x, y
  public BufferedImageWrapper[][][] spriteArray;
  public boolean leftClickPlacedSteel;

  public Player(int x, int y, BufferedImageWrapper[][][] sprites) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
    //Creates the generic sprite via super function
    super(x, y, PLAYER_HEIGHT, PLAYER_WIDTH);
    //Sets default state of player
    spriteArray = sprites;
    alive = true;
    isPlayer = true;
    leftMouseDown = false;
    holdingSteel = false;
    canPlaceSteel = false;
    rightPressed = false;
    leftPressed = false;
    upPressed= false;
    downPressed = false;
    isGrounded = false;
  }



  //Allows the plays to move depending on which keys are pressed.
  //Or reset if R is pressed
  public void keyPressed(KeyEvent e) throws IOException {
    if(e.getKeyCode() == KeyEvent.VK_D){
      rightPressed = true;
    }
    if(e.getKeyCode() == KeyEvent.VK_A){
      leftPressed = true;
    }
    if(e.getKeyCode() == KeyEvent.VK_W){
      upPressed = true;
    }
    if(e.getKeyCode() == KeyEvent.VK_S){
      downPressed = true;
    }
    if(e.getKeyCode() == KeyEvent.VK_R){
      resetNoSound();
    }
  }

  //Stops movement when player releases keys
  public void keyReleased(KeyEvent e) {
    if(e.getKeyCode() == KeyEvent.VK_D){
      rightPressed = false;
    }
    if(e.getKeyCode() == KeyEvent.VK_A){
      leftPressed = false;
    }
    if(e.getKeyCode() == KeyEvent.VK_W){
      upPressed = false;
    }
    if(e.getKeyCode() == KeyEvent.VK_S){
      downPressed = false;
    }
  }

  //Checks if the player overlaps with a tile
  public boolean collide(Tile tile, double x, double y){
    if(tile==null){return false;}
    if(!tile.collision){
      return false;
    }
    return x + WIDTH > tile.realX && x < tile.realX + Tile.length && y - tile.y < Tile.length && tile.y - y < HEIGHT;
  }


  //Checks if the player can change their x position by some amount, and y position by amount.
  //If any tiles overlap the player, it can't update.
  //Kills the player if they touch lava and sets the game to the next level if they finish.
  public boolean canUpdate(double x, double y) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
    if(this.y+y<=-(HEIGHT+Tile.length)){
      return false;
    }
    boolean canUpdate = true;
    //To reduce lag, we only check the tiles near by, which the boundaries are set
    //By these 4 variables.
    int lowX = Math.max(0, ((GameFrame.game.camera.x+GamePanel.GAME_WIDTH)/Tile.length)-4);
    int highX = Math.min(lowX + 8, GameFrame.game.map.length);
    int lowY = Math.max(0,(this.y/Tile.length)-6);
    int highY = Math.min(lowY + 12, GameFrame.game.map[0].length);
    //Actually checks the blocks around and if they collide with the player.
    for(int i=lowX; i<highX; i++) {
      for (int j = lowY; j < highY; j++) {
        if (GameFrame.game.map != null) {
          if (collide(GameFrame.game.map[i][j], this.x + x, this.y + y)) {
            //Next level if the block is the finish line
            if (GameFrame.game.map[i][j].isFinish) {
              LevelManager.nextLevel();
              GameFrame.game.player.resetNoSound();
              return true;
            }
            //Reset the level if the player touches lava
            if (GameFrame.game.map[i][j].kills) {
              GameFrame.game.player.reset();
              return true;
            }
            canUpdate = false;
            break;
          }
        }
      }
    }

    return canUpdate;
  }


  //Checks if player can place steel, and changes the canPlaceSteel variable to true or false
  public void updatePlaceSteel(int x, int y) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
    canPlaceSteel = true;
    boolean adjacent = false;
    int realX = x*Tile.length-GameFrame.game.camera.x;
    double xDis = (realX - GamePanel.GAME_WIDTH/2+ Tile.length/2) - (this.x+WIDTH/2);
    double yDis = (y*Tile.length + Tile.length/2) - (this.y+HEIGHT/2);
    double hypo = Math.sqrt(xDis*xDis+yDis*yDis);
    int xx = (mouseX + GameFrame.game.camera.x + GamePanel.GAME_WIDTH / 2) / Tile.length;
    int yy = (mouseY / Tile.length);
    //If you are off the screen, you can't place
    if(this.y<0){
      canPlaceSteel = false;
      return;
    }
    //If you are falling, you can't place
    if(canUpdate(0,2)){
      canPlaceSteel = false;
      return;
    }
    //If the block is over an enemy, you can't place
    for(NonPlayer e: GameFrame.game.enemy){
      int TileX = xx*Tile.length;
      int TileY = yy*Tile.length;
      int ex = (e.x+GamePanel.GAME_WIDTH/2);
      if(TileX<=ex+e.WIDTH&&ex<=TileX+Tile.length&&TileY<=e.y+e.HEIGHT&&e.y<=TileY+Tile.length){
        canPlaceSteel = false;
        return;
      }
    }
    //If the block is over the player, you can't place
    if(Math.abs(xDis)<(WIDTH+Tile.length)/2+5&&Math.abs(yDis)<(HEIGHT+Tile.length)/2){
      canPlaceSteel = false; return;
    }


    //If the block isn't adjacent to any tile, you can't place
    int[][]check = {{1,0},{0,1},{-1,0},{0,-1}};
    for(int[]a: check){
      try{
        if(GameFrame.game.map[x+a[0]][y+a[1]]!=null&&!GameFrame.game.map[x+a[0]][y+a[1]].replaceAble&&!GameFrame.game.map[x+a[0]][y+a[1]].kills){
          adjacent = true;
          break;
        }
      } catch(Exception ignored){

      }
    }
    if(!adjacent){canPlaceSteel = false; return;}

    //If the block is out of range, you can't place
    if(hypo>steelReachRange){canPlaceSteel = false; return;}

    //If there is a non-replacable block, you can't place
    if(GameFrame.game.map[x][y]!=null&&!GameFrame.game.map[x][y].replaceAble){canPlaceSteel = false; return;}

    //If you can't reach an area because of blocks in the way, you can't place.
    if(!canReach(xx,yy)){canPlaceSteel = false;}
  }


  //Moves the player with physics.
  public void move() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
    //To prevent spamming steel block, we have pickup delay for the steel blocks
    pickupDelay = Math.max(0,pickupDelay - 1);
    int Tilex = (mouseX + GameFrame.game.camera.x + GamePanel.GAME_WIDTH / 2) / Tile.length;
    int Tiley = (mouseY / Tile.length);

    //If the player is holding steel, we update to see if they can updatePlaceSteel.
    if(holdingSteel){
      updatePlaceSteel(Tilex,Tiley);
    }
    //Stops player from glitching into corner
    if(canUpdate(xVelocity, 0)&&canUpdate(0, yVelocity)&&!canUpdate(xVelocity, yVelocity)){
      GameFrame.game.camera.x -= Math.signum(xVelocity);
    }

    //Prevents player from passing tiles in the x-direction
    if(!canUpdate(xVelocity, 0)){
      int updateAmount = 0;
      if(xVelocity>0){
        while(canUpdate(updateAmount, 0)){
          updateAmount++;
        }
        GameFrame.game.camera.x+=updateAmount-1;
      } else if(xVelocity<0){
        while(canUpdate(updateAmount, 0)){
          updateAmount--;
        }
        GameFrame.game.camera.x+=updateAmount+1;
      }
      //xVelocity = 0;
    }
    //Prevents player from passing blocks from the y direction
     if(!canUpdate(0, yVelocity)){
      if(yVelocity>0){
        while(canUpdate(0,1)){
          y+=1;
        }
        isGrounded = true;

      } else if(yVelocity<0){
        while(canUpdate(0,-1)){
          y-=1;
        }
      }
      yVelocity = 0;
    }
     //If the player can update their position due to nothing being in the way, update their position.
    if(canUpdate(xVelocity, yVelocity)) {
      y = y + (int) yVelocity;
      GameFrame.game.camera.x = GameFrame.game.camera.x + (int) xVelocity;
    } else if(canUpdate(0,yVelocity)){
      y = y + (int) yVelocity;
      xVelocity*=0.75;
    } else if(canUpdate(xVelocity,0)){
      GameFrame.game.camera.x = GameFrame.game.camera.x + (int) xVelocity;

    }

    //Give the player xVelocity if they press left or right
    //Add particles if the player is walking
    if(rightPressed){
      if(isGrounded){
       addParticle(-1);
      }
      if(xVelocity<5) {
        xVelocity += 1;
      }
    }
    if(leftPressed) {
      if(isGrounded){
        addParticle(1);
      }
      if(xVelocity>-5) {
        xVelocity -= 1;
      }
    }

    //Checks if the player can jump
    //Conditions: They press up, they are on the ground, they are not holding steel
    if(upPressed&&isGrounded&&!holdingSteel){
      y-=1;
      isGrounded = false;
      if(canUpdate(0,-8)) {
        if (jump == null) {
          try {
            jump = new Sound("sound/jump.wav");
          } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            throw new RuntimeException(e);
          }
        }
        jump.start();
      }
      yVelocity = -10;
    }
    xVelocity *= 0.93;

    //Adds gravity, and allows player to fall down faster.
    if(!isGrounded) {
      yVelocity += 0.3;
      if(downPressed){
        yVelocity+=1;
      }
    }

    //If the player is dead, reset map
    if(!alive){
      alive = true;
      reset();
    }
    //Speed limit to player
    capSpeed();

  }

  //Spawn the player with the death sound
  public void reset() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
    UtilityFunction.playSound("sound/OOF.wav");
    holdingSteel = false;
    LevelManager.setLevel(GameFrame.game.level, true);
    GameFrame.game.camera.x = LevelManager.xSpawn;
    y = LevelManager.ySpawn;
    GameFrame.game.bombs.clear();
    LevelManager.setBombs();

  }

  //Spawns the player without sound
  public void resetNoSound() throws IOException {
    holdingSteel = false;
    LevelManager.setLevel(GameFrame.game.level, true);
    GameFrame.game.camera.x = LevelManager.xSpawn;
    y = LevelManager.ySpawn;
    GameFrame.game.bombs.clear();
    LevelManager.setBombs();
  }

  //Handles events when the player presses a mouse button.
  //Left for throwing bombs, and right for picking and placing steel
  public void mousePressed(MouseEvent e) throws SpriteException, IOException {
    mouseX = e.getX();
    mouseY = e.getY();
    //If the player left clicks and is holding steel and can place it, place the steel
    if(e.getButton()==MouseEvent.BUTTON1) {
      int x = (mouseX + GameFrame.game.camera.x + GamePanel.GAME_WIDTH / 2) / Tile.length;
      int y = (mouseY / Tile.length);
      if (holdingSteel && ((GameFrame.game.map[x][y] == null || GameFrame.game.map[x][y].replaceAble) && canPlaceSteel)) {
        Tile temp = GameFrame.game.map[x][y];
        GameFrame.game.map[x][y] = new SingleTile(x * Tile.length - (GamePanel.GAME_WIDTH / 2), y * Tile.length, new BufferedImageWrapper(("img/tiles/boxes/steel.png")));
        GameFrame.game.map[x][y].movable = true;
        GameFrame.game.map[x][y].previousBlock = temp;
        holdingSteel = false;
        leftClickPlacedSteel = true;
      } else {
        leftMouseDown = true;
      }
    }
    //If the player right clicks, either pick up steel, or places it down depending if the player
    //is already holding steel
    if(e.getButton()==MouseEvent.BUTTON3) {
      int x = (mouseX + GameFrame.game.camera.x + GamePanel.GAME_WIDTH / 2) / Tile.length;
      int y = (mouseY / Tile.length);
      rightMouseDown = true;
      if (pickupDelay == 0) {
        pickupDelay = 5;
        if (!holdingSteel) {
          if (GameFrame.game.map[x][y] != null && GameFrame.game.map[x][y].movable) {
            double xDis = (this.x + WIDTH / 2) - (GameFrame.game.map[x][y].realX + Tile.length / 2);
            double yDis = (this.y + HEIGHT / 2) - (GameFrame.game.map[x][y].y + Tile.length / 2);
            double hypo = Math.sqrt(xDis * xDis + yDis * yDis);
            if (hypo < steelReachRange) {
              holdingSteel = true;
              if (GameFrame.game.map[x][y].previousBlock != null) {
                GameFrame.game.map[x][y] = GameFrame.game.map[x][y].previousBlock;
              } else {
                GameFrame.game.map[x][y] = null;
              }
            }
          }
        } else if (holdingSteel && ((GameFrame.game.map[x][y] == null || GameFrame.game.map[x][y].replaceAble) && canPlaceSteel)) {
          Tile temp = GameFrame.game.map[x][y];
          GameFrame.game.map[x][y] = new SingleTile(x * Tile.length - (GamePanel.GAME_WIDTH / 2), y * Tile.length, new BufferedImageWrapper(("img/tiles/boxes/steel.png")));
          GameFrame.game.map[x][y].movable = true;
          GameFrame.game.map[x][y].previousBlock = temp;
          holdingSteel = false;
        }

      }
    }
    }



    //Using BFS check if there is a physical path where the player can reach a block
  public boolean canReach(int x, int y){
    try {
      int pX = (int) (((double) GameFrame.game.camera.x + GamePanel.GAME_WIDTH) / Tile.length);
      int pY = (int) (((double) this.y + HEIGHT / 2) / Tile.length);
      if (pY < 0) {
        return false;
      }
      int[][] check = {{0, 1}, {1, 0}, {-1, 0}, {0, -1}};
      int[][] dis = new int[1000][18];
      for (int[] a : dis) {
        Arrays.fill(a, Integer.MAX_VALUE);
      }
      boolean[][] vis = new boolean[1000][18];
      LinkedList<Integer> xx = new LinkedList<>();
      LinkedList<Integer> yy = new LinkedList<>();
      xx.add(pX);
      yy.add(pY);
      dis[pX][pY] = 0;
      vis[pX][pY] = true;
      while (!xx.isEmpty()) {
        int tempX = xx.poll();
        int tempY = yy.poll();
        for (int[] a : check) {
          try {
            int newX = tempX + a[0];
            int newY = tempY + a[1];
            if (dis[tempX][tempY] + 1 < (Math.min(6, dis[newX][newY]))) {
              dis[newX][newY] = dis[tempX][tempY] + 1;
              if (GameFrame.game.map[newX][newY] == null || GameFrame.game.map[newX][newY].replaceAble) {
                xx.add(newX);
                yy.add(newY);
              }
            }
          } catch (Exception ignored) {}
        }
      }
      return dis[x][y] <= 6;
    }catch(Exception e){
      return false;
    }
  }

  //When the player drags the mouse, update the the x and y position of the mouse
  public void mouseDragged(MouseEvent e) throws IOException, SpriteException {

    mouseX = e.getX();
    mouseY = e.getY();
  }

  //When the player moves the mouse, update the x and y position of the mouse
  public void mouseMoved(MouseEvent e) {

    mouseX = e.getX();
    mouseY = e.getY();
  }

  //Handles actions when the player releases a mouse button
  public void mouseReleased(MouseEvent e) throws IOException, SpriteException {
    mouseX = e.getX();
    mouseY = e.getY();
    //If it's the left button, throw a bomb, given they have bombs
    if(e.getButton()==MouseEvent.BUTTON1) {
      leftMouseDown = false;
      if (leftClickPlacedSteel) {
        leftClickPlacedSteel = false;
      } else {
        if (GameFrame.game.bombs.size() < 2 && LevelManager.bombs > 0 && !holdingSteel) {
          LevelManager.bombs--;
          GameFrame.game.bombs.add(new StickyBomb(GameFrame.game.player.x + GameFrame.game.camera.x + WIDTH / 2, GameFrame.game.player.y + HEIGHT / 2,
                  (mouseX - GameFrame.game.player.x) / 20, (mouseY - GameFrame.game.player.y) / 10, GameFrame.game.bomb, GameFrame.game.explosionArray));
        }
      }
    }
    //If its right click, simply set rightMouseDown to false
    if(e.getButton()==MouseEvent.BUTTON3){
      rightMouseDown = false;

    }
  }

  //Adds walking particles to the player
  public void addParticle(int x) throws IOException {
    if(UtilityFunction.randInt(1,3)==3) {
      GameFrame.game.particles.add(new Particle(this.x + GameFrame.game.camera.x + WIDTH / 2 + UtilityFunction.randInt(-PLAYER_WIDTH / 2, PLAYER_WIDTH / 2)
              , (int) (y + HEIGHT * 0.95), UtilityFunction.randInt(-2, 2) + x, UtilityFunction.randInt(-4, 1), UtilityFunction.randInt(1, 7), "img/particles/GrassParticle.png"));
    }
    }

    //Draws the player on the screen
  public int draw(Graphics g, int frame) {
    frame %= spriteArray[0][0].length;
    //Draws the pickup range for steel if the player is holding down right click
    if(rightMouseDown){
      g.drawOval((int)(x+WIDTH/2-(reach*steelReachRange)),(int)(y+HEIGHT/2-(reach*steelReachRange)), (int)(reach*steelReachRange*2),(int)(reach*steelReachRange*2));
    }

    //If the player isn't pressing up/left/right, dont animate sprite
    if (!upPressed && !leftPressed && !rightPressed) {
      g.drawImage(spriteArray[lastXDirection][lastYDirection][7].image, x-10, y, null);
      return 0;
    } else {
      //If they do click one of those keys, animate the sprite
      lastXDirection = (int)(Math.signum(xVelocity) + 1) / 2;
      lastYDirection = (int)(Math.signum(yVelocity) + 1) / 2;
      lastFrame = frame;
      g.drawImage(spriteArray[lastXDirection][lastYDirection][frame].image, x-10, y, null);
      return 1;
    }


  }

}
