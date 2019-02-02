package VCD;

public class FungusAi extends CreatureAi {
    private StuffFactory factory;
    private int spreadcount;

    public FungusAi(Creature creature, StuffFactory factory) {
        super(creature);
        this.factory = factory;
    }

    public void onUpdate() {
        if (this.spreadcount < 3 && Math.random() < 0.01) {
            this.spread();
        }
    }

    private void spread() {
        int x = this.creature.x + (int) (Math.random() * 11) - 5;
        int y = this.creature.y + (int) (Math.random() * 11) - 5;

        if (!creature.canEnter(x, y, creature.z))
            return;


        Creature child = factory.newFungus(creature.z);
        child.x = x;
        child.y = y;
        child.z = creature.z;
        spreadcount++;

    }
}
