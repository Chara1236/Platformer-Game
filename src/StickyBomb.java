// Eric Li, Charlie Zhao, ICS4U, Completed 6/20/2022
// StickyBomb class creates a bomb with physics and exploding abilities

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;

public class StickyBomb extends GenericSprite implements Serializable {

    public static final int spriteLength = 35;

    public static final int length = 25;
    public int xVelocity;
    public int yVelocity;
    public boolean isMove;
    public int realX;
    public BufferedImageWrapper sprite ;
    public BufferedImageWrapper[] explosionSpriteArray;

    public int fuse;
    public int explosionPixel = 0;
    public int explosionCounter = 0;

    public boolean alive;

    public boolean erase;



    public StickyBomb(int x, int y,  int xVelocity, int yVelocity, BufferedImageWrapper sprite, BufferedImageWrapper[] explosionSpriteArray){
        //Creates generic sprite class
        super(x,y,length,length);
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.sprite = sprite;
        this.explosionSpriteArray = explosionSpriteArray;
        //Sets default state of bomb
        fuse = GlobalState.second*5;
        isMove = true;
        alive = true;
        erase = false;
    }



    //Updates the realX position of the bomb, which is what is drawn on the screen
    public void update(){
        realX = x - GameFrame.game.camera.x;
    }

    //After the fuse runs out explode
    public void explode() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        UtilityFunction.playSound("sound/explode.wav");
        double yDis = GameFrame.game.player.y+Player.PLAYER_HEIGHT/2-(y+(double)length/2);
        double xDis = GameFrame.game.player.x+Player.PLAYER_WIDTH/2-(realX+(double)length/2);
        double hypo = Math.sqrt(yDis*yDis+xDis*xDis);
        //Propels player away from bomb
        if(hypo<300) {
            if (yDis != 0) {
                GameFrame.game.player.yVelocity += 10 * (yDis) / (hypo);
            }
            if (xDis != 0) {
                GameFrame.game.player.xVelocity += 10 * (xDis) / (hypo);
            }
        }
        //Caps player speed
        GameFrame.game.player.capSpeed();
    alive = false;
    //Kills enemies
    for(int i=0; i<GameFrame.game.enemy.size(); i++){
        double disX = GameFrame.game.enemy.get(i).x+GameFrame.game.enemy.get(i).npcWidth/2 - (x+length/2);
        double disY = GameFrame.game.enemy.get(i).y+GameFrame.game.enemy.get(i).npcHeight/2 - (y+length/2);
        double eHypo = Math.sqrt(disX*disX+disY*disY);
        if(eHypo<105){
            GameFrame.game.enemy.get(i).isDead = true;
        }

    }

    //Blows up boxes in the region
        int lowX = Math.max(0, ((this.x+GamePanel.GAME_WIDTH/2)/Tile.length)-4);
        int highX = Math.min(lowX + 8, GameFrame.game.map.length);
        int lowY = Math.max(0,(this.y/Tile.length)-6);
        int highY = Math.min(lowY + 12, GameFrame.game.map[0].length);
        for(int i=lowX; i<highX; i++) {
            for (int j = lowY; j < highY; j++) {
                if (GameFrame.game.map[i][j] != null && GameFrame.game.map[i][j].breakable) {
                    double disX = (x + WIDTH / 2) - (GameFrame.game.map[i][j].x + Tile.length / 2);
                    double disY = (y + HEIGHT / 2) - (GameFrame.game.map[i][j].y + Tile.length / 2);
                    if (Math.sqrt(disX * disX + disY * disY) < Tile.length * 3) {
                        GameFrame.game.map[i][j] = null;
                    }
                }
            }
        }


    }


    //Checks if the bomb collides with a tile
    public boolean collide(Tile tile, double x, double y){
        if(!tile.collision||tile.nonBombCollide){
            return false;
        }
        if(x+WIDTH>tile.x&&x<tile.x+Tile.length&&y-tile.y<Tile.length&&tile.y-y<HEIGHT){


            return true;
        }
        return false;
    }

    //Updates the position of the bomb, and stops movement if the bomb can no longer move
    public void move() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        boolean checked = false;
        //Caps speed
        if(yVelocity>50){yVelocity=50;}
        update();
        //Updates fuse
        if(fuse>0) {
            fuse-=1;
            if(fuse == 0) {
                explode();
            }
        }
        //If it can move, set movement to true
        if(canUpdate(1,1)&&canUpdate(-1,1)&&canUpdate(0,-1)&&isMove == false&&fuse>0){
            isMove = true;
            xVelocity = 0;
            yVelocity = 0;
        }
        //If isMove is true, and the bomb can update, update the position
        if(isMove) {
            if(canUpdate(xVelocity, 0)&&canUpdate(0, yVelocity)&&!canUpdate(xVelocity, yVelocity)){
                checked = true;
                x += -Math.signum(xVelocity);
                isMove = false;
                int updateAmount = 0;
                //Depending on the xVelocity and yVelocity, check which direction to update in

                if(xVelocity>0&&yVelocity>0){
                    while(canUpdate(updateAmount, updateAmount)){
                        updateAmount++;
                    }
                    x+=updateAmount;
                    y+=updateAmount;
                } else if(xVelocity<0&&yVelocity>0){
                    while(canUpdate(-updateAmount, updateAmount)){
                        updateAmount++;
                    }
                    x-=updateAmount;
                    y+=updateAmount;
                } else if(xVelocity>0&&yVelocity<0){
                    while(canUpdate(updateAmount, -updateAmount)){
                        updateAmount++;
                    }
                    x+=updateAmount;
                    y-=updateAmount;
                } else if(xVelocity<0&&yVelocity<0){
                    while(canUpdate(-updateAmount, -updateAmount)){
                        updateAmount++;
                    }
                    x-=updateAmount;
                    y-=updateAmount;
                }
                xVelocity = 0;
                yVelocity = 0;
            }
            if(!canUpdate(xVelocity, 0)){
                checked = true;
                isMove = false;
                int updateAmount = 0;
                if(xVelocity>0){
                    while(canUpdate(updateAmount, 0)){
                        updateAmount++;
                    }
                    x+=updateAmount-1;
                } else if(xVelocity<0){
                    while(canUpdate(updateAmount, 0)){
                        updateAmount--;
                    }
                    x+=updateAmount+1;
                }
                xVelocity = 0;
            }
            if(!canUpdate(0, yVelocity)){
                checked = true;
                isMove = false;
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
            if(canUpdate(xVelocity, yVelocity)&&!checked) {
                y = y + (int) yVelocity;
                x = x + (int) xVelocity;
            } else {
                isMove = false;
            }
            yVelocity+=3;
        }

    }



    //Draws the bomb

    public void draw(Graphics g) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (explosionCounter >= 2) {
            explosionPixel += 1;
            explosionCounter -= 2;
        }
        //If the bomb is alive, don't animate explosion, else do.
        if(alive) {
            g.drawImage(sprite.image, x - GameFrame.game.camera.x - (spriteLength-length)/2, y - (spriteLength-length)/2, spriteLength, spriteLength, null);
            //g.drawRect(x-GameFrame.game.camera.x,y,length,length);
        } else if (explosionPixel < explosionSpriteArray.length - 1) {
            // please note that the explosion is completely centered on the x plane ("5*") but tends upwards on the y plane ("10*")
            g.drawImage(explosionSpriteArray[explosionPixel].image, x - GameFrame.game.camera.x - 5*explosionPixel,
                    y-10*explosionPixel, spriteLength+10*explosionPixel, spriteLength+10*explosionPixel, null);
            explosionCounter += 1;
        }  else {
            erase = true;
        }
    }
}
