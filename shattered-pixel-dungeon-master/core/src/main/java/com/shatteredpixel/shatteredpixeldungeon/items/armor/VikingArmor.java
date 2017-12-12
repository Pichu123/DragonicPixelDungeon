package com.shatteredpixel.shatteredpixeldungeon.items.armor;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;


import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by dhoang9775 on 11/17/2017.
 */

public class VikingArmor extends ClassArmor{ {
    image = ItemSpriteSheet.ARMOR_DRAGON_KNIGHT;
}

    private HashMap<Callback, Mob> targets = new HashMap<Callback, Mob>();

    @Override
    public void doSpecial() {

        curUser.roar = true;
        Earthroot.Armor defense = curUser.buff(Earthroot.Armor.class) ;

        defense.icon();

        curUser.HP -= (curUser.HP / 3);

        curUser.sprite.zap( curUser.pos );
        //        LinkedHashMap<Buff, BuffIndicator> buffIcons = new LinkedHashMap<>();
//        ArrayList<Buff> newBuffs = new ArrayList<>();
//
//        for (Buff buff : curUser.buffs()) {
//            if (!buffIcons.containsKey(buff)) {
//                BuffIndicator icon = new BuffIndicator(curUser );
//                //add(icon);
//                newBuffs.add(buff);
//                buffIcons.put( buff, icon );
//            }
//        }
//
//        //Buff.affect( curUser, Earthroot.Armor.class );
//        Roots.detach( curUser, Roots.class );
//        curUser.spend(0);
//        curUser.rooted = false;
//        Earthroot.class
//        curUser.remove(curUser);
//        Buff.prolong( curUser, Earthroot.Armor.class, 2 );
//        Earthroot.Armor armor = buff( Earthroot.Armor.class );
//        if (armor != null) {
//            damage = armor.absorb( damage );
//        }
        //curUser.busy();

    }

}
