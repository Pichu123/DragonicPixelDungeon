package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SmallDrakeSprite;
import com.watabou.utils.Random;

/**
 * Created by tmori0185 on 2/15/2018.
 */

public class SmallDrake extends Mob {

    {
        spriteClass = SmallDrakeSprite.class;

        HP = HT = 30;
        defenseSkill = 15;

        EXP = 14;

        flying = true;

        properties.add(Property.DEMONIC);
    }

    @Override
    public int attackSkill( Char target ) {
        return 16;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 5, 18 );
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }

}
