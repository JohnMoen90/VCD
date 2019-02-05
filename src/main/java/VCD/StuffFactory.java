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

    public Item newVictoryItem(int depth) {
        Item item = new Item('*', AsciiPanel.brightWhite, "Wizards Orb");
        world.addAtEmptyLocation(item, depth);
        return item;
    }



    public Creature newPlayer(List<String> messages, FieldOfView fov){
        Creature player = new Creature(world, "Player", '@', AsciiPanel.brightWhite, 100, 20, 5);
        world.addAtEmptyLocation(player, 0);
        new PlayerAi(player, messages, fov);
        return player;

    }



    public Creature newFungus(int depth){
        Creature fungus = new Creature(world, "fungus", 'f', AsciiPanel.green, 10, 0, 0);
        world.addAtEmptyLocation(fungus, depth);
        new FungusAi(fungus, this);
        return fungus;
    }

    public Creature newBat(int depth) {
        Creature bat = new Creature(world, "bat", 'b', AsciiPanel.yellow, 15, 5, 0);
        world.addAtEmptyLocation(bat, depth);
        new BatAi(bat);
        return bat;
    }

    public Creature newZombie(int depth, Creature player){
        Creature zombie = new Creature(world, "zombie", 'z', AsciiPanel.white, 50, 10, 10);
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

    public Item newDagger(int depth){
        Item item = new Item(')', AsciiPanel.white, "dagger");
        item.modifyAttackValue(5);
        item.modifyThrownAttackValue(item.attackValue() / 2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newSword(int depth){
        Item item = new Item(')', AsciiPanel.brightWhite, "sword");
        item.modifyAttackValue(10);
        item.modifyThrownAttackValue(item.attackValue() / 2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newStaff(int depth){
        Item item = new Item(')', AsciiPanel.yellow, "staff");
        item.modifyAttackValue(5);
        item.modifyDefenseValue(3);
        item.modifyThrownAttackValue(item.attackValue() / 2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newLightArmor(int depth){
        Item item = new Item('[', AsciiPanel.green, "tunic");
        item.modifyDefenseValue(2);
        item.modifyThrownAttackValue(2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newMediumArmor(int depth){
        Item item = new Item('[', AsciiPanel.white, "chainmail");
        item.modifyDefenseValue(4);
        item.modifyThrownAttackValue(2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newHeavyArmor(int depth){
        Item item = new Item('[', AsciiPanel.brightWhite, "platemail");
        item.modifyDefenseValue(6);
        item.modifyThrownAttackValue(2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }


    public Item randomWeapon(int depth){
        switch ((int)(Math.random() * 3)){
            case 0: return newDagger(depth);
            case 1: return newSword(depth);
            default: return newStaff(depth);
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
