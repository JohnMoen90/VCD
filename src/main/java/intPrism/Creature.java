package intPrism;

import java.awt.Color;

public class Creature {
    private World world;

    // TODO convert to private fields with getters and setters
    public int x;
    public int y;

    private char glyph;
    private Color color;
    private CreatureAi ai;


    public Creature(World world, char glyph, Color color){
        this.world = world;
        this.glyph = glyph;
        this.color = color;
    }


    // Give creature ai/ distinguish it from other creatures
    public void setCreatureAi(CreatureAi ai) {this.ai = ai;}

    // Functions for retrieving creature values
    public char glyph() {return glyph; }
    public Color color() {return color;}


    // Creature actions
    public void dig(int wx, int wy) {
        world.dig(wx, wy);
    }

    public void moveBy(int mx, int my){
        Creature other = world.creature(x+mx, y+my);

        if (other == null)
            ai.onEnter(x + mx, y + my, world.tile(x + mx, y + my));
        else
            attack(other);
    }

    public void attack(Creature other){
        world.remove(other);
    }

    public void update(){
        ai.onUpdate();
    }

    public boolean canEnter(int wx, int wy) {
        return this.world.tile(wx, wy).isGround() && this.world.creature(wx, wy) == null;
    }

}
