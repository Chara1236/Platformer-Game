// Eric Li, Charlie Zhao, ICS4U, Finished 5/30/2022
/* Main class starts the game, suppresses all output to console
This also runs the constructor in GameFrame class

This is a common technique among coders to keep things organized (and handy when coding in repl.it since we're forced to call a class Main, which isn't always very descriptive)
*/

import java.io.OutputStream;
import java.io.PrintStream;

class Main {
  public static void main(String[] args) {

    // suppresses logging to console
    System.setOut(new PrintStream(new OutputStream() {
      public void write(int b) {
        // does nothing
      }
    }));
    // suppresses stderr output to console
    System.setErr(new PrintStream(new OutputStream() {
      public void write(int b) {
        // does nothing
      }
    }));

    new GameFrame();

  }
}
