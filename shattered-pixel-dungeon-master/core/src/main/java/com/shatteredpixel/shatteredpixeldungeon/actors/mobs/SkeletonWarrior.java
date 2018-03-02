package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

/**
 * Created by tmori0185 on 1/30/2018.
 */

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonWarriorSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class SkeletonWarrior extends Mob {

    private static final String TXT_HERO_KILLED = "You were killed by the explosion of bones...";

    {
        spriteClass = SkeletonWarriorSprite.class;

        HP = HT = 25;
        defenseSkill = 9;

        EXP = 5;
        maxLvl = 10;

        loot = Generator.Category.WEAPON;
        lootChance = 0.2f;

        properties.add(Property.UNDEAD);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 2, 10 );
    }

    @Override
    public void die( Object cause ) {

        super.die( cause );

        boolean heroKilled = false;
        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            Char ch = findChar( pos + PathFinder.NEIGHBOURS8[i] );
            if (ch != null && ch.isAlive()) {
                int damage = Math.max( 0,  damageRoll() - (ch.drRoll() / 2) );
                ch.damage( damage, this );
                if (ch == Dungeon.hero && !ch.isAlive()) {
                    heroKilled = true;
                }
            }
        }

        if (Dungeon.visible[pos]) {
            Sample.INSTANCE.play( Assets.SND_BONES );
        }

        if (heroKilled) {
            Dungeon.fail( getClass() );
            GLog.n( Messages.get(this, "explo_kill") );
        }
    }

    @Override
    protected Item createLoot() {
        Item loot;
        do {
            loot = Generator.random(Generator.Category.WEAPON);
            //50% chance of re-rolling tier 4 or 5 items
        } while (loot instanceof MeleeWeapon && ((MeleeWeapon) loot).tier >= 4 && Random.Int(2) == 0);
        loot.level(0);
        return loot;
    }

    @Override
    public int attackSkill( Char target ) {
        return 12;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 5);
    }

}

