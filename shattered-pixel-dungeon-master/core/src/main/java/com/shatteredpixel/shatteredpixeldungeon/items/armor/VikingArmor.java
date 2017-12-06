package com.shatteredpixel.shatteredpixeldungeon.items.armor;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
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
        Char ch = Actor.findChar(curUser.pos);

        if (ch == Dungeon.hero) {
            Buff.affect( ch, Earthroot.Armor.class ).level(ch.HT);

        }
        curUser.spend(0);
        curUser.rooted = false;
        Earthroot.class
        curUser.remove(curUser);
//        Buff.prolong( curUser, Earthroot.Armor.class, 2 );
//        Earthroot.Armor armor = buff( Earthroot.Armor.class );
//        if (armor != null) {
//            damage = armor.absorb( damage );
//        }

        curUser.HP -= (curUser.HP / 3);

        curUser.sprite.zap( curUser.pos );
        curUser.busy();

    }

}
