package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;



public class DragonDagger extends MeleeWeapon{
    {
        image = ItemSpriteSheet.DRAGON_DAGGER;

        tier = 1;
    }

    @Override
    public int max(int lvl) {
        return  4*(tier+1) +    //8 base, down from 10
                lvl*(tier+1);   //scaling unchanged
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                //deals avg damage to max on surprise, instead of min to max.
                int damage = imbue.damageFactor(Random.NormalIntRange((min() + max()) / 2, max()));
                int exStr = hero.STR() - STRReq();
                if (exStr > 0) {
                    damage += Random.IntRange(0, exStr);
                }
                return damage;
            }
        }
        return super.damageRoll(owner);
    }
}
