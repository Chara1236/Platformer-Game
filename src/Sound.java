// Eric Li, Charlie Zhao, ICS4U, Finished 6/20/2022
// Utility class to make interacting with sound files easier

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Sound implements Serializable {
    public AudioInputStream audioInputStream;
    public Clip clip;
    public File file;
    public Sound(String filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        file = new File(filePath);
        audioInputStream = AudioSystem.getAudioInputStream(file);
        clip = AudioSystem.getClip();
        clip.open(audioInputStream);
    }
    // start playing sound
    public void start(){
        clip.setFramePosition(0);
        clip.start();
    }

    // close sound after use
    public void close() {
        clip.close();
    }
}
