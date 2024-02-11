// Eric Li, Charlie Zhao, ICS4U, Completed 6/19/2022
// the NonPlayer class defines behaviour for enemies and characters that are not controlled by the player

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;

public class NonPlayer extends GenericSprite implements Serializable {
  // please note that these are not static, in contrast to the player class
  // as different enemies have different heights
  public int npcWidth;
  public int npcHeight;
  public int currentXDirection, currentYDirection;
  public boolean isDead;

  public int realX;
  public int health;

  public double fadeCounter;

  public BufferedImageWrapper[][][] spriteArray;

  public NonPlayer(int x, int y, BufferedImageWrapper[][][] sprites, int npcWidth, int npcHeight, int health) {
    super(x, y, npcHeight, npcWidth);
    this.health = health;
    spriteArray = sprites;
    this.npcWidth = npcWidth;
    WIDTH = npcWidth;
    this.npcHeight = npcHeight;
    HEIGHT = npcHeight;

    xVelocity = 3;
    fadeCounter = 1;
  }

  // check if the player is colliding with the enemy
  public boolean collidePlayer(Player p){
    return realX + npcWidth > p.x && realX < p.x + Player.PLAYER_WIDTH && y - p.y < Player.PLAYER_HEIGHT && p.y - y < npcHeight;
  }

  // update the realX value of the enemy (this is dependent on camera.x and is the x position seen by the player)
  public void update(){
    realX = x-GameFrame.game.camera.x;
  }

  // move the enemy
  public void move() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
    // don't move the enemy if the enemy is dead
    if (isDead) {
      xVelocity = 0;
    }
    // have the enemy go the opposite direction if the enemy would be colliding with a tile
    if(!canUpdate(xVelocity, 0)){
      xVelocity*=-1;
    }
    // have the enemy stop falling if they are on the ground
    if(!canUpdate(0, yVelocity)){
      if(yVelocity>0){
        while(canUpdate(0,1)){
          y+=1;
        }
        isGrounded = true;
      } else if(yVelocity<0){ // have the enemy stop moving up if they hit something above them
        while(canUpdate(0,-1)){
          y-=1;
        }
      }
      yVelocity = 0;
    }
    // move the object yVelocity and xVelocity pixels away from their current position
    if(canUpdate(0, yVelocity)) {
      y = y + (int) yVelocity;
      x = x + (int) xVelocity;
    }
    // simulate gravity
    if(!isGrounded) {
      yVelocity += 0.3;
    }
    // cap maximum speed
    capSpeed();
  }

  public int draw(Graphics g, int frame) {
    // if the enemy is not dead, animate the enemy sprite by cycling through the frames of the enemy
    if (!isDead) {
      // last frame is reserved for death animation
      frame %= spriteArray[0][0].length - 1;
      // save the current x and y directions so the enemy is facing the right way if it dies
      currentXDirection = (int)(Math.signum(xVelocity) + 1) / 2;
      currentYDirection = (int)(Math.signum(yVelocity) + 1) / 2;
      // x-GameFrame.game.camera.x is used as the camera doesn't follow NPCs
      // draw the enemy
      g.drawImage(spriteArray[currentXDirection][currentYDirection][frame].image, x-GameFrame.game.camera.x, y, null);
      return 1;
    } else {
      // fade the enemy slowly by overlaying a composite on the enemy
      ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)fadeCounter));
      // draw faded enemy
      g.drawImage(spriteArray[currentXDirection][currentYDirection][spriteArray[0][0].length-1].image, x-GameFrame.game.camera.x, (int)(y+HEIGHT/1.7), null);
      // reset composite to not affect other sprites being drawn
      ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
      // increase the amount of fade every tick to a maximum fade amount of 100% (total transparency)
      fadeCounter = Math.max(0, fadeCounter-0.01);
      return 0;
    }
  }
}
