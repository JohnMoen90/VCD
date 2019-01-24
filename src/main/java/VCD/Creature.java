package VCD;

import java.awt.Color;

public class Creature {
    private World world;

    // TODO convert to private fields with getters and setters
    // Creature coordinates
    public int x;
    public int y;
    public int z;

    // Creature attributes
    private char glyph;
    private Color color;
    private CreatureAi ai;

    private int maxHp;

    public int maxHp() {
        return maxHp;
    }

    private int hp;

    public int hp() {
        return hp;
    }

    private int attackValue;

    public int attackValue() {
        return attackValue;
    }

    private int defenseValue;

    public int defenseValue() {
        return defenseValue;
    }


    public Creature(World world, char glyph, Color color, int maxHp, int attack, int defense) {
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackValue = attack;
        this.defenseValue = defense;
    }


    // Give creature ai/ distinguish it from other creatures
    public void setCreatureAi(CreatureAi ai) {
        this.ai = ai;
    }

    // Functions for retrieving creature values
    public char glyph() {
        return glyph;
    }

    public Color color() {
        return color;
    }


    // Creature actions
    public void dig(int wx, int wy, int wz) {
        world.dig(wx, wy, wz);
        doAction("dig");
    }


    public void moveBy(int mx, int my, int mz) {

        // Save coordinate to move to
        Tile tile = world.tile(x + mx, y + my, z + mz);

        // If the coordinate is above or below, immediately check for stairs
        if (mz == -1) {
            if (tile == Tile.STAIRS_DOWN) {
                doAction("walk up the stairs to level %d", z + mz + 1);
            } else {
                doAction("try to go up but are stopped by the cave ceiling");
                return;
            }
        } else if (mz == 1) {
            if (tile == Tile.STAIRS_UP) {
                doAction("walk down the stairs to level %d", z + mz + 1);
            } else {
                doAction("try to go down but are stopped by the cave floor");
                return;
            }
        }

        // If there is a creature, attack it
        Creature other = world.creature(x + mx, y + my, z + mz);
        if (other == null)
            ai.onEnter(x + mx, y + my, z + mz, tile);
        else
            attack(other);
    }

    // Simple Attack function
    public void attack(Creature other) { // Other is the creature being attacked

        int amount = Math.max(0, attackValue() - other.defenseValue()); // Get zero or attack - defense, whatever's greater

        amount = (int) (Math.random() * amount) + 1; // Get a random attack

        doAction("attack the '%s' for %d damage", other.glyph, amount); // Report the action

        other.modifyHp(-amount);    // Change defenders hp
    }

    // Change hp and check if creature dies
    public void modifyHp(int amount) {
        hp += amount;

        if (hp < 1)
            doAction("die");
        world.remove(this);
    }

    // Call an update on creature
    public void update() {
        ai.onUpdate();
    }

    // Return true if coordinate is a legal move
    public boolean canEnter(int wx, int wy, int wz) {
        return this.world.tile(wx, wy, wz).isGround() && this.world.creature(wx, wy, wz) == null;
    }

    // Send message to creature
    public void notify(String message, Object... params) {
        ai.onNotify(String.format(message, params));
    }

    //
    public void doAction(String message, Object... params) {

        // Set radius of the notification
        int r = 9;
        for (int ox = -r; ox < r + 1; ox++) {  // for -r to r
            for (int oy = -r; oy < r + 1; oy++) {  // for -r to r
                if (ox * ox + oy * oy > r * r) // if x*x + y*y is greater than r*^2, skip it
                    continue;

                //get creature from tile at coordinate
                Creature other = world.creature(x + ox, y + oy, z);
                if (other == null)
                    continue;
                if (other == this)
                    other.notify("You " + message + ".", params); // Person doing action gets 1st person
                else    // Make 2nd person for all others
                    other.notify(String.format("The '%s' %s.", glyph, makeSecondPerson(message)), params);
            }
        }
    }

    private String makeSecondPerson(String text) {

        int space = text.indexOf(" ");

        if (space == -1) {
            return text + "s";
        } else {
            return text.substring(0, space) + "s" + text.substring(space);
        }

//        String[] words = text.split(" ");
//        words[0] = words[0] + "s";
//
//        StringBuilder builder = new StringBuilder();
//        for (String word : words) {
//            builder.append(" ");
//            builder.append(word);
//        }
//
//        return builder.toString().trim();
//    }
    }
}
