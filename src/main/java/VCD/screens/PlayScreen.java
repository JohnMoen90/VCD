package VCD.screens;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import VCD.*;
import asciiPanel.AsciiPanel;

/**
 * This is the main game screen, everything is rendered and displayed here
 */
public class PlayScreen implements Screen {

    private int screenWidth;
    private int screenHeight;

    private World world;
    private Creature player;
    private FieldOfView fov;

    private List<String> messages;

    private Screen subscreen;

    public PlayScreen(){

        // World variables
        screenWidth = 80;   // Current defaults TODO: init file for variables
        screenHeight = 23;
        messages = new ArrayList<String>();

        createWorld();
        fov = new FieldOfView(world);

        // Creature Generator
        StuffFactory stuffFactory = new StuffFactory(world, fov);
        createCreatures(stuffFactory);
        createItems(stuffFactory);
    }

    private void createCreatures(StuffFactory stuffFactory){
        player = stuffFactory.newPlayer(messages, fov);

        for (int z = 0; z < world.depth(); z++) {

            // Bats per fungus
            for (int i = 0; i < 8; i++) {   //Hard coded for testing
                stuffFactory.newFungus(z);
            }

            // Bats per floor
            for (int i = 0; i < 20; i++) {   //Hard coded for testing
                stuffFactory.newBat(z);
            }

            //
            for (int i = 0; i < z + 3; i++) {
                stuffFactory.newZombie(z, player);
            }
        }
    }

    private void createItems(StuffFactory factory) {
        for (int z = 0; z < world.depth(); z++) {

            // Rocks
            for (int i = 0; i < world.width() * world.height() / 40; i++) {
                factory.newRock(z);
            }

            // Weapons and armor
            for (int i = 0; i < 5; i++) {
                factory.randomArmor(z);
                factory.randomWeapon(z);
            }
        }
        factory.newVictoryItem(world.depth() - 1);
    }


    /**
     * This method calls writes all screen components to terminal
     *
     */
    public void displayOutput(AsciiPanel terminal) {
        int left = getScrollX();
        int top = getScrollY();

        // Print Map to screen
        displayTiles(terminal, left, top);

        // Print messages to screen
        displayMessages(terminal, messages);

        // Print messages to screen
        String stats = String.format(" %3d/%3d hp %8s", player.hp(), player.maxHp(), hunger());
        terminal.write(stats, 1, 23);

        if (subscreen != null)
            subscreen.displayOutput(terminal);
    }

    /**
     * This method could be used as a view cursor so I'm saving it here
     * //    private void scrollBy(int mx, int my) {
     * //            player.x = Math.max(0, Math.min(player.x + mx, world.width() - 1));
     * //            player.y = Math.max(0, Math.min(player.y + my, world.height() - 1));
     * //        }
     */


    public Screen respondToUserInput(KeyEvent key) {
        int level = player.level();

        if (subscreen != null) {
            subscreen = subscreen.respondToUserInput(key);
        } else {
        switch(key.getKeyCode()) {

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_H:
                player.moveBy(-1, 0, 0);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_L:
                player.moveBy(1, 0, 0);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_K:
                player.moveBy(0, -1, 0);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_J:
                player.moveBy(0, 1, 0);
                break;

            case KeyEvent.VK_Y:
                player.moveBy(-1, -1, 0);
                break;
            case KeyEvent.VK_U:
                player.moveBy(1, -1, 0);
                break;
            case KeyEvent.VK_B:
                player.moveBy(-1, 1, 0);
                break;
            case KeyEvent.VK_N:
                player.moveBy(1, 1, 0);
                break;
            case KeyEvent.VK_D: subscreen = new DropScreen(player); break;
            case KeyEvent.VK_E: subscreen = new EatScreen(player); break;
            case KeyEvent.VK_W: subscreen = new EquipScreen(player); break;
            case KeyEvent.VK_I: subscreen = new ExamineScreen(player); break;

        }
        }

        switch (key.getKeyChar()){
            case 'g':
            case ',' : player.pickup(); break;
            case '<': player.moveBy(0, 0, 1); break;
            case '>':
                if (userIsTryingToExit())
                    return userExits();
                else
                    player.moveBy(0, 0, -1); break;
            case '?': subscreen = new HelpScreen(); break;
        }

        if (player.level() > level)
            subscreen = new LevelUpScreen(player, player.level() - level);

        if (subscreen == null)
            world.update();

        if (player.hp() < 1)
            return new LoseScreen();

        return this;
    }

    private String hunger(){
        if (player.food() < player.maxFood() * 0.1)
            return "Starving";
        else if (player.food() < player.maxFood() * 0.2)
            return "Hungry";
        else if (player.food() > player.maxFood() * 0.9)
            return "Stuffed";
        else if (player.food() > player.maxFood() * 0.8)
            return "Full";
        else
            return "";
    }

    private boolean userIsTryingToExit(){
        return player.z == 0 && world.tile(player.x, player.y, player.z) == Tile.STAIRS_UP;
    }

    private Screen userExits(){
        for (Item item : player.inventory().getItems()) {
            if (item != null && item.name().equals("Wizards Orb"))
                return new WinScreen();
        }
        return new LoseScreen();
    }



    private void createWorld(){
        world = new WorldBuilder(90, 32, 5).makeCaves().build();
    }

    public int getScrollX() {
        return Math.max(0,Math.min(player.x - screenWidth / 2, world.width() - screenWidth));
    }

    public int getScrollY() {
        return Math.max(0,Math.min(player.y - screenHeight / 2, world.height() - screenHeight));
    }


    private void displayTiles(AsciiPanel terminal, int left, int top) {
        fov.update(player.x, player.y, player.z, player.visionRadius());    // Update FOV

        // For every tile on screen
        for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++) {

                // At specified point in relation to top left pixel
                int wx = x + left;
                int wy = y + top;

                // If the player can see it write world tile
                if (player.canSee(wx, wy, player.z)) {
                    terminal.write(world.glyph(wx, wy, player.z), x, y, world.color(wx, wy, player.z));
                } else {    // If not display fov tile
                    terminal.write(fov.tile(wx, wy, player.z).glyph(), x, y, Color.darkGray);
                }

            }
        }

    }

    private void displayMessages(AsciiPanel terminal, List<String> messages) {
        int top = screenHeight - messages.size();
        for (int i = 0; i < messages.size(); i++){
            terminal.writeCenter(messages.get(i), top + i);
        }
        messages.clear();
        //TODO: implement message history
    }

}
