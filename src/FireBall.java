// Eric Li, Charlie Zhao, ICS4U, Finished 6/19/2022
// fireball object that is shot by fireball tiles; this kills players and forces them to dodge

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.IOException;

public class FireBall extends GenericSprite{
    public String spritePath;
    public int realX;

    public boolean dead;

    public int lifeSpan;

    // called every time GamePanel.updateShootingBlock is called
    public FireBall(int x, int y, int xv, int yv, String dir,int height, int width) {
        super(x, y, height, width);
        xVelocity = xv;
        yVelocity = yv;
        if(dir.equals("left")){
            spritePath  = "img/misc/flame/flameLeft.png";
        } else if(dir.equals("right")){
            spritePath  = "img/misc/flame/flameRight.png";
        } else if(dir.equals("up")){
            spritePath  = "img/misc/flame/flameUp.png";
        } else if(dir.equals("down")){
            spritePath  = "img/misc/flame/flameDown.png";
        }
        realX = 0;
        dead = false;
        lifeSpan = 1000;
    }

    // update realX position of fireball (is affected by camera.x instead of being pure x)
    public void update(){
        realX = x-GameFrame.game.camera.x;
    }

    // check if fireball will collide with the player
    public boolean collidePlayer(Player p){
        if(realX+width>p.x&&realX<p.x+Player.PLAYER_WIDTH&&y-p.y<Player.PLAYER_HEIGHT&&p.y-y<height){
            return true;
        }
        return false;
    }
    // kills fireball if fireball has existed for longer than lifeSpan ticks
    // moves fireball and kills it if it hits a block or a player
    // kills player and resets level if it hits a player
    public void move() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        lifeSpan--;
        if(lifeSpan<=0){
            dead = true;
        }
        update();
        if(canUpdate(xVelocity,yVelocity)) {
            x += xVelocity;
            y += yVelocity;
        } else {
            dead = true;
        }
        if(collidePlayer(GameFrame.game.player)){
            dead = true;
            GameFrame.game.player.reset();
        }
    }
    public void draw(Graphics g) throws IOException {
       g.drawImage(GamePanel.getImage(spritePath),x-GameFrame.game.camera.x,y,null);
    }
}
