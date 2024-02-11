// Eric Li, Charlie Zhao, ICS4U, Finished 6/16/2022
// intercepts keystrokes and substitutes new keystrokes in the place of the old keystrokes
// allows for arbitrary replacement of keystrokes, including chained replacement of keystrokes if necessary
// although those features have not yet been implemented
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;

public class Middleware implements Serializable {

    public static ArrayList<Integer> allOldCode = new ArrayList<>();
    public static ArrayList<Integer> allNewCode = new ArrayList<>();
    public final int oldCode;
    public final int newCode;
    public boolean isDestroyed = false;

    // create new Middleware which intercepts newCode and replaces it with oldCode
    Middleware(int oldCode, int newCode) {
        allOldCode.add(oldCode);
        allNewCode.add(newCode);
        this.oldCode = oldCode;
        this.newCode = newCode;
    }

    // checks if the keypress can be intercepted (i.e., if it is the same keyCode as newCode)
    public boolean canIntercept(KeyEvent e) {
        return e.getKeyCode() == newCode && !isDestroyed;
    }

    // intercepts the key if it has been found to be interceptable
    public KeyEvent interceptKey(KeyEvent e) {
        e.setKeyCode(oldCode);
        return e;
    }

    @Override
    public boolean equals(Object o) {
        try {
            Middleware m = (Middleware)o;
            // duck typing equals check
            // if it has the same oldCode, assume it is the same; this is because oldCodes should be unique
            // also makes some corner cases easier
            return this.oldCode == m.oldCode || this.newCode == m.newCode;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
