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
    private String name;
    public String name() { return name; }

    private char glyph;
    public char glyph() { return glyph; }

    private Color color;
    public Color color() { return color; }

    private CreatureAi ai;   // Controls creature type/behavior
    public void setCreatureAi(CreatureAi ai) { this.ai = ai; }

    private int maxHp;
    public int maxHp() { return maxHp; }

    private int hp;
    public int hp() { return hp; }

    private int maxFood;
    public int maxFood() { return maxFood; }

    private int food;
    public int food() { return food; }

    private int attackValue;
    public int attackValue() {
        return attackValue
                + (weapon == null ? 0 : weapon.attackValue())
                + (armor == null ? 0 : armor.attackValue());
    }

    private int defenseValue;
    public int defenseValue() {
        return defenseValue
                + (weapon == null ? 0 : weapon.attackValue())
                + (armor == null ? 0 : armor.attackValue());
    }

    private int visionRadius;
    public int visionRadius() {return visionRadius;}

    private Inventory inventory;
    public Inventory inventory() { return inventory; }

    private Item weapon;
    public Item weapon() { return weapon; }

    private Item armor;
    public Item armor() { return armor; }



    public Creature(World world, String name, char glyph, Color color, int maxHp, int attack, int defense) {
        this.world = world;
        this.name = name;
        this.glyph = glyph;
        this.color = color;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackValue = attack;
        this.defenseValue = defense;
        this.visionRadius = 9;  // TODO: Pass this value through constructor
        this.inventory = new Inventory(20);
        this.maxFood = 1000;
        this.food = maxFood / 3 * 2;
    }

    // Call an update on creature
    public void update() {
        modifyFood(-1);
        ai.onUpdate();
    }

    // Return true if coordinate is a legal move
    public boolean canEnter(int wx, int wy, int wz) {
        return this.world.tile(wx, wy, wz).isGround() && this.world.creature(wx, wy, wz) == null;
    }

    // Returns the tile from coordinate
    public Tile tile(int wx, int wy, int wz) {
        return world.tile(wx, wy, wz);
    }


    // Returns true if creature can see a tile
    public boolean canSee(int wx, int wy, int wz) {
        return ai.canSee(wx, wy, wz);
    }

    // Returns creature from coordinate
    public Creature creature(int wx, int wy, int wz) {
        return world.creature(wx, wy, wz);
    }

    public void modifyFood(int amount) {
        food += amount;

        if (food > maxFood) {
            maxFood = maxFood + food / 2;
            food = maxFood;
            notify("You can't believe your stomach can hold that much!");
            modifyHp(-1);
        } else if (food < 1 && isPlayer()) {
            modifyHp(-1000);
        }
    }

    public boolean isPlayer(){
        return glyph == '@';
    }

    // Creature actions

    public void eat(Item item) {
        if (item.foodValue() < 0)
            notify("Gross!");
        modifyFood(item.foodValue());
        inventory.remove(item);
        unequip(item);
    }


    // Dig through wall
    public void dig(int wx, int wy, int wz) {
        modifyFood(-10);
        world.dig(wx, wy, wz);
        doAction("dig");
    }

    public void pickup(){
        Item item = world.item(x, y, z);

        if (inventory.isFull() || item == null) {
            doAction("grab at the ground");
        } else {
            doAction("pickup a %s", item.name());
            world.remove(x,y,z);
            inventory.add(item);
        }
    }

    // TODO Make it so Items can be dropped if no item spaces are available
    public void drop(Item item) {
        doAction("drop a " + item.name());
        inventory.remove(item);
        unequip(item);
        world.addAtEmptySpace(item, x, y, z);
    }

    public void equip(Item item){
        if (item.attackValue() == 0 && item.defenseValue() == 0)
            return;

        if (item.attackValue() >= item.defenseValue()) {
            unequip(weapon);
            doAction("wield a " + item.name());
            weapon = item;
        } else {
            unequip(armor);
            doAction("put on a " + item.name());
            armor = item;
        }
    }

    public void unequip(Item item){
        if (item == null)
            return;

        if (item == armor){
            doAction("remove a " + item.name());
            armor = null;
        } else if (item == weapon) {
            doAction("put away a " + item.name());
            weapon = null;
        }
    }

    // Move character into target coordinate
    public void moveBy(int mx, int my, int mz) {

        if (mx == 0 && my == 0 && mz == 0)
            return;

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

        modifyFood(-5);

        int amount = Math.max(0, attackValue() - other.defenseValue()); // Get zero or attack - defense, whatever's greater

        amount = (int) (Math.random() * amount) + 1; // Get a random attack

        doAction("attack the '%s' for %d damage", other.name, amount); // Report the action

        other.modifyHp(-amount);    // Change defenders hp
    }

    // Change hp and check if creature dies
    public void modifyHp(int amount) {
        hp += amount;

        if (hp < 1) {
            doAction("die");
            leaveCorpse();
            world.remove(this);
        }
    }

    private void leaveCorpse(){
        Item corpse = new Item('%', color, name + " corpse");
        corpse.modifyFoodValue(maxHp * 3);
        world.addAtEmptySpace(corpse, x, y, z);
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
                else if (other.canSee(x, y, z))   // Make 2nd person for all others
                    other.notify(String.format("The '%s' %s.", name, makeSecondPerson(message)), params);
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
