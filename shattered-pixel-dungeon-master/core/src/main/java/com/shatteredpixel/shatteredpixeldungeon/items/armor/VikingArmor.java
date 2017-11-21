package com.shatteredpixel.shatteredpixeldungeon.items.armor;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;


import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.HashMap;

/**
 * Created by dhoang9775 on 11/17/2017.
 */

public class VikingArmor extends ClassArmor{ {
    image = ItemSpriteSheet.ARMOR_DRAGON_KNIGHT;
}

    private HashMap<Callback, Mob> targets = new HashMap<Callback, Mob>();

    @Override
    public void doSpecial() {

        Item proto = new Shuriken();

        for (Mob mob : Dungeon.level.mobs) {
            if (Level.fieldOfView[mob.pos]) {

                Callback callback = new Callback() {
                    @Override
                    public void call() {
                        curUser.attack( targets.get( this ) );
                        targets.remove( this );
                        if (targets.isEmpty()) {
                            curUser.spendAndNext( curUser.attackDelay() );
                        }
                    }
                };

                ((MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class )).
                        reset( curUser.pos, mob.pos, proto, callback );

                targets.put( callback, mob );
            }
        }

        if (targets.size() == 0) {
            GLog.w( Messages.get(this, "no_enemies") );
            return;
        }

        curUser.HP -= (curUser.HP / 3);

        curUser.sprite.zap( curUser.pos );
        curUser.busy();
    }

}