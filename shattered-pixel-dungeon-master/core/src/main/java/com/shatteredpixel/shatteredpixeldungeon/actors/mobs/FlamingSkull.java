package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FlamingSkullSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SmallDrakeSprite;
import com.watabou.utils.Random;

/**
 * Created by tmori0185 on 2/15/2018.
 */

public class FlamingSkull extends Mob {

    {
        spriteClass = FlamingSkullSprite.class;

        HP = HT = 1;
        defenseSkill = 3;

        EXP = 14;

        flying = true;

        properties.add(Property.DEMONIC);
    }

    @Override
    public int attackSkill( Char target ) {
        return 1;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 4 );
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }

}
