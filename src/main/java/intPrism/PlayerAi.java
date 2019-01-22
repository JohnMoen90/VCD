package intPrism;

public class PlayerAi extends CreatureAi {

    public PlayerAi(Creature creature) {
        super(creature);
    }

    // TODO: Add doors
    public void onEnter(int x, int y, Tile tile){
        if (tile.isGround()){
            creature.x = x;
            creature.y = y;
        } else if (tile.isDiggable()) {
            creature.dig(x, y);
        }
    }

}
