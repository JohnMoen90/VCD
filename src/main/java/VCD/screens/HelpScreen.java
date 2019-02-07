package VCD.screens;

import asciiPanel.AsciiPanel;

import java.awt.event.KeyEvent;

public class HelpScreen implements Screen {

    public void displayOutput(AsciiPanel terminal) {
        terminal.clear();
        terminal.writeCenter("Cave Explorer Junior", 1);
        terminal.write("Your friends dared you to retrieve the fabled wizards orb from the bottom of", 1, 3);
        terminal.write("this creepy cave. Use what you find to survive. Now, go prove yourself!", 1, 4);

        int y = 6;
        terminal.write("[g] or [,] to pick up", 2, y++);
        terminal.write("[d] to drop", 2, y++);
        terminal.write("[e] to eat", 2, y++);
        terminal.write("[w] to wear or wield", 2, y++);
        terminal.write("[t] to throw an item", 2, y++);
        terminal.write("[f] to fire a ranged weapon", 2, y++);
        terminal.write("[?] for help", 2, y++);
        terminal.write("[i] examine your items", 2, y++);
        terminal.write("[x] to look around", 2, y++);

        terminal.writeCenter("-- press any key to continue --", 22);
    }

    public Screen respondToUserInput(KeyEvent key) {
        return null;
    }
}
