package VCD;

import asciiPanel.AsciiPanel;

import java.util.List;

public class StuffFactory {
    private World world;
    private FieldOfView fov;

    public StuffFactory(World world, FieldOfView fov) {
        this.world = world;
        this.fov = fov;
    }



    // Return back top of the world with this item to win
    public Item newVictoryItem(int depth) {
        Item item = new Item('*', AsciiPanel.brightWhite, "Wizards Orb");
        world.addAtEmptyLocation(item, depth);
        return item;
    }


    // Player character
    public Creature newPlayer(List<String> messages, FieldOfView fov){
        Creature player = new Creature(world, "you", '@', AsciiPanel.brightWhite, 100, 20, 5);
        world.addAtEmptyLocation(player, 0);
        new PlayerAi(player, messages, fov);
        return player;

    }


    // F
    public Creature newCreepyCaveFungus(int depth){
        Creature fungus = new Creature(world, "creepy cave fungus", 'f', AsciiPanel.green, 10, 0, 0);
        world.addAtEmptyLocation(fungus, depth);
        new FungusAi(fungus, this);
        return fungus;
    }


    public Creature newPoisonFungus(int depth){
        Creature fungus = new Creature(world, "poison fungus", 'f', AsciiPanel.red, 15, 0, 0);
        world.addAtEmptyLocation(fungus, depth);
        new FungusAi(fungus, this);
        fungus.changePoisonous(true);
        return fungus;
    }

    public Creature newBat(int depth) {
        Creature bat = new Creature(world, "bat", 'b', AsciiPanel.yellow, 15, 5, 0);
        world.addAtEmptyLocation(bat, depth);
        new BatAi(bat);
        return bat;
    }

    public Creature newRat(int depth, Creature player) {
        Creature rat = new Creature(world, "rat", 'r', AsciiPanel.brightBlack, 15, 3, 0);
        world.addAtEmptyLocation(rat, depth);
        new ZombieAi(rat, player);
        return rat;
    }


    public Creature newZombie(int depth, Creature player){
        Creature zombie = new Creature(world, "zombie", 'z', AsciiPanel.white, 50, 10, 10);
        world.addAtEmptyLocation(zombie, depth);
        new ZombieAi(zombie, player);
        return zombie;
    }


    public Creature newDireZombie(int depth, Creature player){
        Creature zombie = new Creature(world, "dire zombie", 'z', AsciiPanel.brightGreen, 75, 15, 10);
        world.addAtEmptyLocation(zombie, depth);
        new ZombieAi(zombie, player);
        return zombie;
    }


    public Creature newUnicornZombie(int depth, Creature player){
        Creature zombie = new Creature(world, "unicorn zombie", 'z', AsciiPanel.brightMagenta, 30, 30, 5);
        world.addAtEmptyLocation(zombie, depth);
        new ZombieAi(zombie, player);
        return zombie;
    }




    public Item newRock(int depth) {
        Item rock = new Item(',', AsciiPanel.yellow, "rock");
        rock.modifyThrownAttackValue(4);
        world.addAtEmptyLocation(rock, depth);
        return rock;
    }

    public Item newMoss(int depth) {
        Item mossyClump = new Item('.', AsciiPanel.green, "mossy clump");
        mossyClump.modifyThrownAttackValue(0);
        world.addAtEmptyLocation(mossyClump, depth);
        return mossyClump;
    }



    public Item newPointyStick(int depth){
        Item item = new Item(')', AsciiPanel.blue, "pointy stick");
        item.modifyAttackValue(5);
        item.modifyThrownAttackValue(item.attackValue() / 2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newSword(int depth){
        Item item = new Item(')', AsciiPanel.brightWhite, "rusty sword");
        item.modifyAttackValue(10);
        item.modifyThrownAttackValue(item.attackValue() / 2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newClobberinStick(int depth){
        Item item = new Item(')', AsciiPanel.yellow, "clobberin' stick");
        item.modifyAttackValue(5);
        item.modifyDefenseValue(3);
        item.modifyThrownAttackValue(item.attackValue() / 2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }


    public Item newSlingshot(int depth) {
        Item item = new Item('y', AsciiPanel.yellow, "slingshot");
        item.modifyAttackValue(1);
        item.modifyRangedAttackValue(5);
        world.addAtEmptyLocation(item, depth);
        return item;
    }


    public Item newLightArmor(int depth){
        Item item = new Item('[', AsciiPanel.green, "t shirt");
        item.modifyDefenseValue(2);
        item.modifyThrownAttackValue(2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newMediumArmor(int depth){
        Item item = new Item('[', AsciiPanel.white, "magic t-shirt of protection");
        item.modifyDefenseValue(4);
        item.modifyThrownAttackValue(2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newHeavyArmor(int depth){
        Item item = new Item('[', AsciiPanel.brightWhite, "hockey equipment");
        item.modifyDefenseValue(6);
        item.modifyThrownAttackValue(2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }


    public Item randomWeapon(int depth){
        switch ((int)(Math.random() * 3)){
            case 0: return newPointyStick(depth);
            case 1: return newSword(depth);
            case 2: return newSlingshot(depth);
            default: return newClobberinStick(depth);
        }
    }

    public Item randomArmor(int depth){
        switch ((int)(Math.random() * 3)){
            case 0: return newLightArmor(depth);
            case 1: return newMediumArmor(depth);
            default: return newHeavyArmor(depth);
        }
    }

}
