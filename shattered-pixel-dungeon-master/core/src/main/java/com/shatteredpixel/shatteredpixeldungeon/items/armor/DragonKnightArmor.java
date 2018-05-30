package com.shatteredpixel.shatteredpixeldungeon.items.armor;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
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
 * Created by dhoang9775 on 10/16/2017.
 */

public class DragonKnightArmor extends ClassArmor {
    {
        image = ItemSpriteSheet.ARMOR_DRAGON_KNIGHT;
    }

    private HashMap<Callback, Mob> targets = new HashMap<Callback, Mob>();

    @Override
    public void doSpecial() {

        for (Mob mob : Dungeon.level.mobs) {
            if (Level.fieldOfView[mob.pos]) {
                if(mob.buff(Bleeding.class)==null && !mob.properties().contains(Char.Property.BOSS)){
                    Buff.affect( mob, Bleeding.class ).set( 2*mob.HP/7 );
                }
                if(mob.buff(Cripple.class)!=null){
                    Buff.prolong( mob, Cripple.class, Cripple.DURATION-1) ;
                }
                else{
                    Buff.affect( mob, Cripple.class, Cripple.DURATION-1) ;
                }


            }
        }

        curUser.HP -= (curUser.HP / 3);

        curUser.spend( Actor.TICK );


        curUser.sprite.zap( curUser.pos );

    }

}