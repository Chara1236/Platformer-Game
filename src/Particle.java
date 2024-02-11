// Eric Li, Charlie Zhao, ICS4U, Finished 6/18/22
// create particles when the character walks or on top of lava

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;

public class Particle extends GenericSprite implements Serializable {

    public int xVelocity;

    public int yVelocity;

    public int lifeSpan = 10;

    public BufferedImageWrapper sprite;

    public Particle(int x, int y, int xVelocity, int yVelocity, int length, String filePath) throws IOException {
        //Creates generic sprite class
        super(x,y,length, length);
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        sprite = new BufferedImageWrapper((filePath));
    }

    //Moves the sprite, and gives it gravity
    public void move(){
    x+=xVelocity;
    y+=yVelocity;
    yVelocity+=0.5;
    }

    //Draws the sprite on the screen
    public void draw(Graphics g){
        g.drawImage(sprite.image,x-GameFrame.game.camera.x,y,WIDTH,HEIGHT, null);
    }

}
