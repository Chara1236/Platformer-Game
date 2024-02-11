// Eric Li, Charlie Zhao, ICS4U, completed 6/10/2022
/* GenericSprite class defines behaviours for all objects that move

child of Rectangle because that makes it easy to draw and check for collision

In 2D GUI, basically everything is a rectangle even if it doesn't look like it!
*/

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serializable;

public class GenericSprite extends Rectangle implements Serializable {

  public double yVelocity;
  public double xVelocity;
  public final double speedCapx = 50;

  public final double speedCapy = 20;
  public int WIDTH; //size of ball
  public int HEIGHT; //size of ball
  transient public boolean rightPressed = false;
  transient public boolean leftPressed = false;
  transient public boolean upPressed= false;
  transient public boolean downPressed = false;
  transient public boolean isGrounded = false;

  public boolean isPlayer = false;
  //constructor creates ball at given location with given dimensions
  public GenericSprite(int x, int y, int height, int width){
    super(x, y, width, height);
    WIDTH = width;
    HEIGHT = height;
  }


  //called from GamePanel when any keyboard input is detected
  //updates the direction of the ball based on user input
  //if the keyboard input isn't any of the options (d, a, w, s), then nothing happens
  public void keyPressed(KeyEvent e) throws IOException {

  }

  //called from GamePanel when any key is released (no longer being pressed down)
  //Makes the ball stop moving in that direction
  public void keyReleased(KeyEvent e){

  }

  //called from GamePanel whenever a mouse click is detected
  //changes the current location of the ball to be wherever the mouse is located on the screen
  public void mousePressed(MouseEvent e) throws SpriteException, IOException {

  }

  public void mouseReleased(MouseEvent e) throws IOException, SpriteException {

  }
  public void move() throws IOException, UnsupportedAudioFileException, LineUnavailableException {

  }

  // caps x and y velocity at speedCapx and speedCapy respectively
  public void capSpeed(){
    if(xVelocity>speedCapx){
      xVelocity = speedCapx;
    } else if(xVelocity<-1*speedCapx) {
      xVelocity = -1*speedCapx;
    }
    if(yVelocity>speedCapy){
      yVelocity = speedCapy;
    } else if(yVelocity<-1*speedCapy) {
      yVelocity = -1*speedCapy;
    }
  }

  // checks if the sprite is colliding with a tile
  public boolean collide(Tile tile, double x, double y){
    if(tile==null){return false;}
    if(!tile.collision){
      return false;
    }
    if(x+WIDTH>tile.x&&x<tile.x+Tile.length&&y-tile.y<Tile.length&&tile.y-y<HEIGHT){
      return true;
    }
    return false;
  }

  // checks if the sprite can move x in the x plane and y in the y plane
  public boolean canUpdate(double x, double y) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
    boolean canUpdate = true;
    int lowX = Math.max(0, (this.x+GamePanel.GAME_WIDTH/2)/Tile.length-4);
    int highX = Math.min(lowX + 8, GameFrame.game.map.length);
    int lowY = Math.max(0,(this.y/Tile.length)-6);
    int highY = Math.min(lowY + 12, GameFrame.game.map[0].length);
    for(int i=lowX; i<highX; i++) {
      for (int j = lowY; j < highY; j++) {
        if (GameFrame.game.map[i][j] != null) {
          if (collide(GameFrame.game.map[i][j], this.x + x, this.y + y)) {
            if (GameFrame.game.map[i][j].isFinish&&isPlayer) {
              LevelManager.nextLevel();
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

  //draws the current location of the sprite to the screen
  public void draw(Graphics g) throws IOException, UnsupportedAudioFileException, LineUnavailableException {


  }
  
}
