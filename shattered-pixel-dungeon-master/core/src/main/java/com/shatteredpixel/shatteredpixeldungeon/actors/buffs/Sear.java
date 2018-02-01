/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2017 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

public class Sear extends Buff {

	public float cook = 2f;
	MysteryMeat meat = new MysteryMeat();

	@Override
	public boolean act() {
		switch (hero.heroClass) {
			case WARRIOR:
			case ROGUE:
			case HUNTRESS:
			case MAGE:
			case VIKING:
				break;
			case DRAGONKNIGHT:
				if (cook <=0f) {
					for (Item item : hero.belongings) {
						if (item instanceof MysteryMeat) {
							item = item.detach(hero.belongings.backpack);
							ChargrilledMeat steak = new ChargrilledMeat();
							if (!steak.collect(hero.belongings.backpack)) {
								Dungeon.level.drop(steak, hero.pos).sprite.drop();
							}
							GLog.w(Messages.get(this, "burnsup", meat.toString()));

							Heap.burnFX(hero.pos);
							cook = 2f;
						}
					}
//					Heap heap = Dungeon.level.heaps.get(hero.pos);
//					for (Item item : heap.items.toArray( new Item[0] )) {
//						if (item instanceof MysteryMeat) {
//							heap.replace( item, ChargrilledMeat.cook( (MysteryMeat)item ) );
//						}
//					}

//					heap.burn();
//					for (Heap heap : Dungeon.level.heaps.values()) {
////						if (heap.items.contains(meat)) {
////							heap.sear();
////							GLog.w( Messages.get(this, "burnsup", meat.toString()) );
////							Heap.burnFX( hero.pos );
////						}
//
//					}
				}
				else{
					cook-=TICK;

				}

				spend( TICK );
//				for (Item item : hero.belongings) {
//					if (item instanceof MysteryMeat) {
//						item = item.detach(hero.belongings.backpack);
//						ChargrilledMeat steak = new ChargrilledMeat();
//						if (!steak.collect(hero.belongings.backpack)) {
//							Dungeon.level.drop(steak, hero.pos).sprite.drop();
//						}
//						GLog.w(Messages.get(this, "burnsup", item.toString()));
//
//						Heap.burnFX(hero.pos);
//
//					}
//				}
//				spend( TICK );
				break;


		}
		return true;
	}

	@Override
	public int icon() {
		return BuffIndicator.BARKSKIN;
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
}
