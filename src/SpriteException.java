// Eric Li, Charlie Zhao, ICS4U, Finished 5/30/2022
// Exception that occurs when the sprite is not of the right dimensions

public class SpriteException extends Exception {
    public SpriteException() {
        super("Tile sprites must have equal lengths and heights.");
    }
}
