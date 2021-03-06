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

        HP = HT = 80;
        defenseSkill = 20;

        EXP = 22;

        flying = true;

        properties.add(Property.DEMONIC);
    }

    @Override
    public int attackSkill( Char target ) {
        return 36;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 32, 35 );
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 10);
    }
}
