// Eric Li, Charlie Zhao, ICS4U, Finished 6/17/2022
// A wrapper that makes the Sound class serializable
// Please note that this is currently superseded by UtilityFunction.playSound, but is included for extensibility purposes

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

public class SoundWrapper implements Serializable {
    transient public Sound sound;
    public String soundString;


    // please note that not as many constructors were implemented as BufferedImage, as this class was created before most sounds were added;
    // as such, backwards compatibility was not needed
    public SoundWrapper(String soundLocation) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        sound = new Sound(soundLocation);
        soundString = soundLocation;
    }

    public SoundWrapper() {}

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        // write location of .wav file (soundString)
        out.writeObject(soundString);
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException, UnsupportedAudioFileException, LineUnavailableException {
        // read .wav file located at soundString
        Object o;
        o = in.readObject();
        sound = new Sound((String)o);
    }
}
