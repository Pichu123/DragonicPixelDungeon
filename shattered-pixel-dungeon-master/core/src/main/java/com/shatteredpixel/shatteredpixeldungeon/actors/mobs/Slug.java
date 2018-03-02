package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

/**
 * Created by tmori0185 on 2/13/2018.
 */

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShadowSprite;
import com.watabou.utils.Random;

public class Slug extends Mob {

    {
        spriteClass = ShadowSprite.class;

        HP = HT = 25;
        defenseSkill = 20;

        EXP = 14;


        properties.add(Property.DEMONIC);
    }


    @Override
    public int attackSkill( Char target ) {
        return 30;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 10, 16 );
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 8);
    }

}