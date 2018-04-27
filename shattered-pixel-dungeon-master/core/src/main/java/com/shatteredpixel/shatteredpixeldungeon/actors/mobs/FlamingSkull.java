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

        HP = HT = 70;
        defenseSkill = 20;

        EXP = 20;

        flying = true;

        properties.add(Property.DEMONIC);
    }

    @Override
    public int attackSkill( Char target ) {
        return 35;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 22, 30 );
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }

}
