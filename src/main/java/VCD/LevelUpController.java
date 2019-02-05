package VCD;

import java.util.ArrayList;
import java.util.List;

public class LevelUpController {

    private static LevelUpOption[] options = new LevelUpOption[]{
            new LevelUpOption("Increased hit points") {
                @Override
                public void invoke(Creature creature) {
                    creature.gainMaxHp();
                }
            },
            new LevelUpOption("Increased attack value") {
                @Override
                public void invoke(Creature creature) {
                    creature.gainAttackValue();
                }
            },
            new LevelUpOption("Increased defense value") {
                @Override
                public void invoke(Creature creature) {
                    creature.gainDefenseValue();
                }
            },
            new LevelUpOption("Increased vision") {
                @Override
                public void invoke(Creature creature) {
                    creature.gainVision();
                }
            },
    };

    public void autoLevelUp(Creature creature) {
        options[(int) (Math.random() * options.length)].invoke(creature);
    }


    public List<String> getLevelUpOptions(){
        List<String> optionStrings = new ArrayList<String>();
        for (int i = 0; i < options.length; i++) {
            optionStrings.add(options[i].name());
        }
        return optionStrings;
    }


    public LevelUpOption getLevelUpOption(String option) {

        for(LevelUpOption levelUpOption : options)
            if (levelUpOption.name().equals(option)) {
                return levelUpOption;
            }

        return null;
    }

}
