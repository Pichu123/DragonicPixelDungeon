package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShadowSprite;
import com.watabou.utils.Random;

/**
 * Created by tmori0185 on 2/15/2018.
 */

public class Shadow extends Mob {

    {
        spriteClass = ShadowSprite.class;

        HP = HT = 10;
        defenseSkill = 30;

        baseSpeed = 0.5f;
        EXP = 14;

        state = HUNTING;

        properties.add(Property.DEMONIC);
    }

    @Override
    public int attackSkill( Char target ) {
        return 30;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 30, 40 );
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 8);
    }

}
