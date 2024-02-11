import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class LevelManager implements Serializable {
    public static int xSpawn = -400;
    public static int ySpawn = 450;

    public static String filePath;

    public static int bombs;
    // set current level, then load the map, enemies, dialogue, signs, and bomb count for that level
    public static void setLevel(int level, boolean hasDied){
        // remove all current bombs and fireballs
        GameFrame.game.bombs.clear();
        GameFrame.game.fireballs.clear();
        // set the player velocity to zero
        GameFrame.game.player.yVelocity = 0;
        GameFrame.game.player.xVelocity = 0;
        GameFrame.game.level = level;
        GameFrame.game.player.alive = true;
        // change spawn coordinates, bomb count, and save file path based on level inputted
        if(level == 1){
            // spawn coordinates: -400/450
            xSpawn = -400;
            ySpawn = 450;
            filePath = "saves/Level1.txt";
            bombs = 8;
        } else if(level == 2){
            // spawn coordinates: -400/400
            xSpawn = -400;
            ySpawn = 400;
            filePath = "saves/Level2.txt";
            bombs = 3;
        } else if(level == 3){
            // spawn coordinates: -800/100
            xSpawn = -800;
            ySpawn = 100;
            filePath = "saves/Level3.txt";
            bombs = 4;
        } else if(level == 4){
            //-1100/460
            xSpawn = -1100;
            ySpawn = 460;
            filePath = "saves/Level4.txt";
            bombs = 6;
        } else if(level == 5){
            //-1100/360
            xSpawn = -1100;
            ySpawn = 360;
            filePath = "saves/Level5.txt";
            bombs = 1;
        } else if(level == 6){
            //0/50
            xSpawn = 0;
            ySpawn = 50;
            filePath = "saves/Level6.txt";
            bombs = 999999;
        }
        try {
            // load map into GamePanel
            MapReader.inputMap(filePath);
            // if the player has not died yet (i.e., first time seeing the dialogue), load it
            if (!hasDied) {
                // do not load dialogue if there is no dialogue to load
                if (!(MapReader.inputDialogue(filePath)[0].equals("$Empty"))) {
                    // convert dialogue from String[] to ArrayList<String>, and load it into GamePanel
                    GameFrame.game.dialogueArray = new ArrayList<>(Arrays.asList(MapReader.inputDialogue(filePath)));
                    // if the dialogue file starts with $Villain, have the portrait display on the right side of the screen, and display the alternate portrait
                    if (GameFrame.game.dialogueArray.get(0).contains("$Villain")) {
                        // delete the first item in the array so the villain does not say "$Villain"
                        GameFrame.game.dialogueArray.remove(0);
                        // display portrait on right side of screen
                        GameFrame.game.dialogueMenu.isNarrator = false;
                        // change portrait
                        GameFrame.game.dialogueMenu.portrait = GameFrame.game.villainPortrait;
                    }
                    // reset dialogue frame to zero, so it restarts the animation every time the dialogue is loaded
                    GameFrame.game.dialogueMenu.currentFrame = 0;
                    GameFrame.game.dialogueMenu.frameCounter = 0;
                    // tell the GameFrame to load the DialogueMenu
                    GameFrame.game.isDialogue = true;
                }
            }
            // reset signs
            GameFrame.game.tutorialSign.clear();
            GameFrame.game.loreSign.clear();
            // temporary boolean, so only declared here
            boolean stillTutorial = true;
            for (String[] sA: MapReader.inputSign(filePath)) {
                // if the line contains "/", skip the line, stop adding signs to tutorialSign and instead add signs to loreSign
                // this is important because loreSigns and tutorialSigns have different colours and font types
                if (sA[0].contains("/")) {
                    stillTutorial = false;
                } else if (stillTutorial) {
                    // add sign to tutorialSign
                    GameFrame.game.tutorialSign.add(new WallSign(Integer.parseInt(sA[0]), Integer.parseInt(sA[1]), GamePanel.tutorialFont, sA[2]));
                } else {
                    // add sign to loreSign if stillTutorial is false
                    GameFrame.game.loreSign.add(new WallSign(Integer.parseInt(sA[0]), Integer.parseInt(sA[1]), GamePanel.loreFont, sA[2]));
                }
            }
        } catch (IOException | SpriteException | UnsupportedAudioFileException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        GameFrame.game.player.alive = true;
    }


    //Gives the player bombs
    public static void setBombs(){
        if(GameFrame.game.level == 1){
            bombs = 8;
        } else if(GameFrame.game.level == 2){
            bombs = 3;
        } else if(GameFrame.game.level == 3){
            bombs = 4;
        } else if(GameFrame.game.level == 4){
            bombs = 5;
        } else if(GameFrame.game.level == 5){
            bombs = 1;
        } else if(GameFrame.game.level == 6){
            bombs = 999999;
        }
    }
    // overloaded setLevel that accepts only a level argument
    public static void setLevel(int level) {
        setLevel(level, false);
    }
    // go to the next level
    public static void nextLevel(){
        setLevel(GameFrame.game.level+1);
    }
}
