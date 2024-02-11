// Eric Li, Charlie Zhao, ICS4U, Finished 2022-06-12
//This class controls the x-scrolling aspect for the player
//If you look at the players absolute position(Relative to the screen), the players y position changes, but the x position
//never actually changes.

import java.io.Serializable;

public class Camera implements Serializable {
    public int x;
    public Camera(int x){
        this.x = x;
    }
}
