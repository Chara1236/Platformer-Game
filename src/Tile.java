// Eric Li, Charlie Zhao, ICS4U, Completed 6/20/2022
// Tile class allows the creation of tiles which will serve as the playing field of the platformer
import java.awt.*;
import java.io.Serializable;

// all tiles must be squares
public class Tile implements Serializable {
    public int x;
    public int y;

    public boolean collision;

    public boolean nonBombCollide;
    public boolean isFinish;

    public boolean kills;

    public boolean breakable;

    public boolean movable;
    public int realX;
    public static final int length = 35;

    public boolean replaceAble;

    public Tile previousBlock;

    public String shootingDir;


    public Tile(int x, int y){
        //Sets default state of tile
        isFinish = false;
        collision = true;
        kills = false;
        this.x = x;
        this.y = y;
        nonBombCollide = false;
        breakable = false;
        movable = false;
        replaceAble = false;
        previousBlock = null;
        shootingDir = "none";
    }

    //Updates the real x position of the bomb, what is drawn on the screen
    public void update(){
        realX = x-GameFrame.game.camera.x;
    }

    //Draws the tile on the screen
    public void draw(Graphics g){
        g.setColor(Color.black);
        g.fillRect(x-GameFrame.game.camera.x, y, length, length);
    }
}
