package intPrism.screens;

import java.awt.event.KeyEvent;
import asciiPanel.AsciiPanel;
import intPrism.World;
import intPrism.WorldBuilder;


public class PlayScreen implements Screen {
    private World world;
    private int centerX;
    private int centerY;
    private int screenWidth;
    private int screenHeight;

    public PlayScreen(){
        screenWidth = 80;
        screenHeight = 21;
        createWorld();
    }


    public void displayOutput(AsciiPanel terminal) {

    }

    public Screen respondToUserInput(KeyEvent key) {
        switch(key.getKeyCode()){
            case KeyEvent.VK_ESCAPE: return new LoseScreen();
            case KeyEvent.VK_ENTER: return new WinScreen();
        }
        return this;
    }

    private void createWorld(){
        world = new WorldBuilder(90, 31).makeCaves().build();
    }

    public int getScrollX() {
        return Math.max(0,Math.min(centerX - screenWidth / 2, world.width() - screenWidth));
    }

    public int getScrollY() {
        return Math.max(0,Math.min(centerY - screenHeight / 2, world.height() - screenHeight));
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++) {
                int wx = x + left;
                int wy = y + top;

                terminal.write(world.glyph(wy,wx), x, y, world.color(wx,wy));
            }
        }

    }

}
