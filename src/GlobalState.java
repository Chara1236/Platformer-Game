// Eric Li, Charlie Zhao, ICS4U, Finished 6/19/2022
// determines the standard time unit for exploding objects (i.e., bombs)
// while not very useful now, GlobalState was added to increase extensibility in the future

import java.io.Serializable;

public class GlobalState implements Serializable {
    // each object takes GlobalState.second * 5 ticks to explode
    public static final int second = 10;
}
