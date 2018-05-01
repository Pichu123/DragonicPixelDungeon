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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.TomeOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.levels.DragonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SpearTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DragonSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EyeSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TenguSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Dragon extends Mob {
	
	{
		spriteClass = DragonSprite.class;
		
		HP = HT = 500;
		EXP = 1000;
		defenseSkill = 20;

		HUNTING = new Hunting();

		flying = true; //doesn't literally fly, but he is fleet-of-foot enough to avoid hazards

		properties.add(Property.BOSS);
	}
	
	@Override
	protected void onAdd() {
		//when he's removed and re-added to the fight, his time is always set to now.
		spend(-cooldown());
		super.onAdd();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 6, 20 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 20;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 5);
	}

	private int beamCooldown = 2;
	public boolean beamCharged= false;
	public int fireCorner;

	//A way to change the size of the fire ring and retain original values for resetting
	public final int fireLength = 7;
	public final int fireHeight = 7;

	public int modLength = fireLength;
	public int modHeight = fireHeight;

	public final int fireGrowth =4;
	public final int fireAmount = 2;

	@Override
	public void damage(int dmg, Object src) {

		int beforeHitHP = HP;
		super.damage(dmg, src);
		dmg = beforeHitHP - HP;

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null) {
			int multiple = beforeHitHP > HT/2 ? 1 : 4;
			lock.addTime(dmg*multiple);
		}

		//phase 1 of the fight is over
		if (beforeHitHP > 3*HT/4 && HP <= 3*HT/4){
			HP = 3*(HT/4)-1;
			yell(Messages.get(Tengu.class, "notice_mine"));
			((DragonBossLevel)Dungeon.level).progress();
			//jump();
		}

		//phase 2 of the fight is over
		if (beforeHitHP > HT/2 && HP <= HT/2){
			HP = (HT/2)-1;
			yell(Messages.get(this, "interesting"));
			((DragonBossLevel)Dungeon.level).progress();
			BossHealthBar.bleed(true);
		}

		//phase 3 of the fight is over
		if (HP == 0 && beforeHitHP <= HT/2) {
			((DragonBossLevel)Dungeon.level).progress();
			return;
		}


	}

	@Override
	public boolean isAlive() {
		return Dungeon.level.mobs.contains(this); //Tengu has special death rules, see prisonbosslevel.progress()
	}

	@Override
	public void die( Object cause ) {
		
		if (Dungeon.hero.subClass == HeroSubClass.NONE) {
			Dungeon.level.drop( new TomeOfMastery(), pos ).sprite.drop();
		}
		
		GameScene.bossSlain();
		super.die( cause );
		
		Badges.validateBossSlain();

		LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
		if (beacon != null) {
			beacon.upgrade();
		}
		
		yell( Messages.get(Tengu.class, "defeated") );
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		boolean enemyNear = false;
		for (int i  : PathFinder.NEIGHBOURS8){
			Char ch = Actor.findChar(i);
			if (ch != null){
				enemyNear=true;
				break;
			}
		}
		return enemyNear;
	}

	//tengu's attack is always visible
	@Override
	protected boolean doAttack(Char enemy) {
		sprite.attack( enemy.pos );

		if (beamCharged && beamCooldown==0){
			if(modHeight <20 ||modLength <20) {
				//spend( attackDelay()*2f );
				//beamCharged = false;
				//beamCooldown=2;
				//fireAttack(modHeight, modLength);
				//
				//spend( attackDelay() );
				modHeight += fireGrowth;
				modLength += fireGrowth;
				//return true;
			}
			else{
				beamCharged = false;
				beamCooldown=2;
				modHeight=fireHeight;
				modLength=fireLength;
				//((DragonBossLevel)Dungeon.level).progress();
				//return true;
			}
		}
		spend( attackDelay() );

		return true;
}

    public void fireAttack(int height, int length){
//        int y = DragonBossLevel.TOP + 1;
//		while (y < DragonBossLevel.TOP + DragonBossLevel.HALL_HEIGHT) {
//			for (int i = DragonBossLevel.CENTER-2; i <= DragonBossLevel.CENTER + 2; i++) {
//				GameScene.add( Blob.seed( y * DragonBossLevel.width() + i, 2, Fire.class ) );
//			}
//			y += 1;
//		}
		for (int i=0; i < PathFinder.NEIGHBOURS8.length; i++) {
			GameScene.add( Blob.seed( pos + PathFinder.NEIGHBOURS8[i], fireAmount, Fire.class ) );
		}


		for (int i = 0; i < height; i++) {
            //Where the fire ring should begin on each new row
            fireCorner = (((-height/2)+i)*DragonBossLevel.width())-(length/2);
			for(int j = 0; j<length; j++){
			    if (i==0 || i==height-1) { //Top and bottom row of fire ring
					if(Level.passable[pos + fireCorner + j] ) {
						GameScene.add(Blob.seed(pos + fireCorner + j, fireAmount, Fire.class));
						Char ch = Actor.findChar(pos + fireCorner + j);
						if (ch != null) {
							Buff.affect(ch, Burning.class).reignite(ch);
							ch.damage( 40, this);
						}
					}
                }
			}
			//Fills in middle row(s)
			if(i!=0 && i!=height-1) {
				if(Level.passable[pos + fireCorner]) {
					GameScene.add(Blob.seed(pos + fireCorner, fireAmount, Fire.class));
					Char ch = Actor.findChar(pos + fireCorner);
					if (ch != null) {
						Buff.affect(ch, Burning.class).reignite(ch);
						ch.damage(damageRoll() + 20, this);
					}
				}
				if(Level.passable[pos + fireCorner + (length - 1)]) {
					GameScene.add(Blob.seed(pos + fireCorner + (length - 1), fireAmount, Fire.class));
					Char ch = Actor.findChar(pos + fireCorner + (length - 1));
					if (ch != null) {
						Buff.affect(ch, Burning.class).reignite(ch);
						ch.damage(damageRoll() + 20, this);
					}
				}
			}
		}
//		if(height<15 || length <15){
//			fireAttack(height+4,length+4);
//		}

    }


	public void jump() {
		if (enemy == null) enemy = chooseEnemy();

		int newPos;
		//if we're in phase 1, want to warp around within the room
		newPos = DragonBossLevel.getTelePos();
		//otherwise go wherever, as long as it's a little bit away
		if (Dungeon.visible[pos]) CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );


		sprite.move( pos, newPos );
		move( newPos );
		
		if (Dungeon.visible[newPos]) CellEmitter.get( newPos ).burst( Speck.factory( Speck.WOOL ), 6 );
		Sample.INSTANCE.play( Assets.SND_PUFF );
		
		//spend( 1 / speed() );
	}
	
	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		if (HP <= HT/2) BossHealthBar.bleed(true);
		if (HP == HT) {
			yell(Messages.get(Tengu.class, "notice_mine", Dungeon.hero.givenName()));
		} else {
			yell(Messages.get(Tengu.class, "notice_face", Dungeon.hero.givenName()));
		}
	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();
	static {
		RESISTANCES.add( ToxicGas.class );
		RESISTANCES.add( Poison.class );
		RESISTANCES.add( Grim.class );
		RESISTANCES.add( ScrollOfPsionicBlast.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		BossHealthBar.assignBoss(this);
		if (HP <= HT/2) BossHealthBar.bleed(true);
	}

	//tengu is always hunting
	private class Hunting extends Mob.Hunting{

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			enemySeen = enemyInFOV;

			if (beamCooldown > 0 && DragonBossLevel.state==DragonBossLevel.State.FIRE_ATTACK) {
				beamCooldown--;

			}
            else if(beamCooldown == 0 && DragonBossLevel.state==DragonBossLevel.State.FIRE_ATTACK){
                beamCharged=true;
                return doAttack(enemy);
            }
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy ) && DragonBossLevel.state!=DragonBossLevel.State.FIRE_ATTACK ) {

				return doAttack( enemy );

			} else {

				if (enemyInFOV) {
					target = enemy.pos;
				} else {
					chooseEnemy();
					target = enemy.pos;
				}

				spend( TICK );
				return true;

			}
		}
	}
}
