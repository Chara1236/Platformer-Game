// Eric Li, Charlie Zhao, ICS4U, Finished 6/20/2022
// Utility functions to help with common tasks

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

// class is final because no objects will be made from it
public final class UtilityFunction {

    // intercept keystroke
    public static KeyEvent intercept(KeyEvent e, ArrayList<Middleware> middlewareArray) {
        // iterate through each Middleware object in middlewareArray
        for (Middleware m: middlewareArray) {
            if (m.canIntercept(e)) {
                // call interceptKey to intercept the key
                e = m.interceptKey(e);
                return e;
            }
        }
        return e;
    }

    // process VK_UP or VK_DOWN inputs to change the currently selected box
    public static int processBox(KeyEvent e, int currentBox, ArrayList<TextBox> textBoxArray) {
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            // if currentBox > 0, subtract one
            // else, set to TOTAL_BOXES-1
            return currentBox > 0 ? currentBox - 1:textBoxArray.size() - 1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            // if currentBox > total box amount - 1, set to 0
            // else, set to TOTAL_BOXES-1
            return currentBox < textBoxArray.size() - 1 ? currentBox + 1:0;
        }
        return currentBox;
    }

    // randomly generate an integer from low to high
    public static int randInt(int low, int high){
        return (int)(Math.random()*(high-low+1))+low;
    }

    // start playing a sound that is located at filePath
    public static SoundWrapper playSound(String filePath) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        SoundWrapper sound = new SoundWrapper(filePath);
        sound.sound.clip.addLineListener(e -> {
            // close clip after sound is done playing
            if (e.getType() == LineEvent.Type.STOP) {
                sound.sound.close();
            }
        });
        sound.sound.start();
        return sound;
    }
}
