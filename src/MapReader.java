// Eric Li, Charlie Zhao, ICS4U, Finished 6/16/2022
// reads map, dialogue, and signs from files and loads them into GamePanel

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class MapReader implements Serializable {
    // loads game map into GamePanel
    /*
    1: Normal Grass
    2: Left Grass:
    3: Right Grass:
    Grass Tiling:
    qwe
    asd
    zxc
    !: Slime
    v= background
    Grass:
    */
    //o = steel
    //   u
    //  h k    Shooting
    //   m
    public static void inputMap(String filePath) throws IOException, SpriteException, UnsupportedAudioFileException, LineUnavailableException {
      int TileX, TileY;
      int x = 0;
      int y = 0;
      // clears existing enemies, particles, fireball shooters, and fireballs
      GameFrame.game.enemy.clear();
      GameFrame.game.particleTiles.clear();
      GameFrame.game.shootingTiles.clear();
      GameFrame.game.fireballs.clear();
      // clears current map
      for(int i=0; i<GameFrame.game.map.length; i++){
          Arrays.fill(GameFrame.game.map[i], null);
      }
      // read new map file
      String file = FileManager.readFile(filePath);
      // converts characters in map file into Tiles
      for(int i=0; i<file.length(); i++){
          // if a newline is reached, reset x position to 0 and increase y position by 1
          if(file.charAt(i)=='\n'){
              y+=1;
              x=0;
          }
          // each x position translates into an actual pixel position of x*Tile.length - (GamePanel.GAME_WIDTH/2) on the screen
          TileX = x*Tile.length - (GamePanel.GAME_WIDTH/2);
          if(y==0){ // if the tile is on the first row, it's shifted to the right to adjust for the start of the file
              TileX += Tile.length;
          }
          // each y position translates into an actual pixel position of y*Tile.length on the screen; please note that Tile.length was used
          // instead of something like Tile.height because Tiles are rectangles
          TileY = y*Tile.length;
          // if the char at the specific position in the file matches one of the options below, a tile with an image located at filePath is inserted at that position with newTile()
          if(file.charAt(i)=='1'){
              newTile("img/tiles/terrain/grass.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='2'){
              newTile("img/tiles/terrain/grassLeft.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='3'){
              newTile("img/tiles/terrain/grassRight.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='q'){
              newTile("img/tiles/terrain/grassTopLeft.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='w'){
              newTile("img/tiles/terrain/grassMid.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='e'){
              newTile("img/tiles/terrain/grassTopRight.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='a'){
              newTile("img/tiles/terrain/grassMiddleLeft.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='s'){
              newTile("img/tiles/terrain/grassCenter.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='d'){
              newTile("img/tiles/terrain/grassMiddleRight.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='z'){
              newTile("img/tiles/terrain/grassBottomLeft.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='x'){
              newTile("img/tiles/terrain/grassBottomMiddle.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='c'){
              newTile("img/tiles/terrain/grassBottomRight.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='r'){
              newTile("img/tiles/terrain/cornerTopLeft.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='t'){
              newTile("img/tiles/terrain/cornerTopRight.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='f'){
              newTile("img/tiles/terrain/cornerBottomLeft.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='g'){
              newTile("img/tiles/terrain/cornerBottomRight.png", x, y, TileX, TileY);
          } else if(file.charAt(i)=='b'){ // this tile has its breakable variable set to true after being created by newTile() to let it be blown up by explosions
              newTile("img/tiles/boxes/box.png", x, y, TileX, TileY);
              GameFrame.game.map[x][y].breakable = true;
          } else if(file.charAt(i)=='!'){ // instead of adding a tile, add an enemy slime at this location
              GameFrame.game.enemy.add(new NonPlayer(TileX, TileY, GameFrame.game.slimeSpriteArray, 50, 28, 100));
          } else if(file.charAt(i)=='+') { // this tile will send the player to the next level when the player steps on it
              newTile("img/tiles/boxes/finish.png", x, y, TileX, TileY);
              GameFrame.game.map[x][y].isFinish = true;
              GameFrame.game.map[x][y].nonBombCollide = true;
          }  else if(file.charAt(i)=='v'){ // this tile is a tile that is part of the background; it is not subject to collisions or bomb explosions
              newTile("img/tiles/background/wall.png", x, y, TileX, TileY);
              GameFrame.game.map[x][y].collision = false;
              GameFrame.game.map[x][y].replaceAble = true;
          } else if(file.charAt(i)=='l'){ // this tile will kill you on contact; lava tiles also generate particles
              newTile("img/tiles/terrain/lava.png", x, y, TileX, TileY);
              GameFrame.game.map[x][y].nonBombCollide = true;
              GameFrame.game.map[x][y].kills = true;
              if(y>0&&GameFrame.game.map[x][y-1]==null) {
                  GameFrame.game.particleTiles.add(GameFrame.game.map[x][y]);
              }
          } else if(file.charAt(i)=='o'){ // steel tiles can be picked up and moved
              newTile("img/tiles/boxes/steel.png", x, y, TileX, TileY);
              GameFrame.game.map[x][y].movable = true;
          } else if(file.charAt(i)=='h'){ // the following tiles shoot fireballs in the directions indicated; these fireballs kill players on contact
              newTile("img/tiles/boxes/boxShootLeft.png", x, y, TileX, TileY);
              GameFrame.game.map[x][y].shootingDir = "left";
              GameFrame.game.shootingTiles.add(GameFrame.game.map[x][y]);
          } else if(file.charAt(i)=='u'){
              newTile("img/tiles/boxes/boxShootUp.png", x, y, TileX, TileY);
              GameFrame.game.map[x][y].shootingDir = "up";
              GameFrame.game.shootingTiles.add(GameFrame.game.map[x][y]);
          } else if(file.charAt(i)=='k'){
              newTile("img/tiles/boxes/boxShootRight.png", x, y, TileX, TileY);
              GameFrame.game.map[x][y].shootingDir = "right";
              GameFrame.game.shootingTiles.add(GameFrame.game.map[x][y]);
          } else if(file.charAt(i)=='m'){
              newTile("img/tiles/boxes/boxShootDown.png", x, y, TileX, TileY);
              GameFrame.game.map[x][y].shootingDir = "down";
              GameFrame.game.shootingTiles.add(GameFrame.game.map[x][y]);
          }
          x+=1; // increment x value by one after every character read
      }
    }

    // return dialogue array given the filePath inputted in inputMath for further processing in LevelManager
    public static String[] inputDialogue(String mapFilePath) throws IOException {
        String filePath = mapFilePath.replace(".txt", "-dialogue.txt"); // format path to open the dialogue file instead
        return FileManager.readFile(filePath).split("\n"); // read and split file, before returning
    }

    // return sign array given the filePath inputted in inputMath for further processing in LevelManager
    public static ArrayList<String[]> inputSign(String mapFilePath) throws IOException {
        String filePath = mapFilePath.replace(".txt", "-signs.txt"); // format path to open the sign file instead
        String[] temporaryStringArray = FileManager.readFile(filePath).split("\n");
        ArrayList<String[]> returnArray = new ArrayList<>(); // create new ArrayList<String[]> and populate it with details about the text
        for (String s: temporaryStringArray) {
            returnArray.add(s.split(" ", 3)); // s[0] = x, s[1] = y, s[2] = text; given that the text often has spaces, the amount of items resulting from the split was limited to three
        }
        return returnArray;
    }

    // populate GamePanel tile array with new tile with TileX and TileY positions
    public static void newTile(String filePath, int x, int y, int TileX, int TileY) throws IOException, SpriteException {
        GameFrame.game.map[x][y]=(new SingleTile(TileX,TileY, new BufferedImageWrapper((filePath))));
    }
}
