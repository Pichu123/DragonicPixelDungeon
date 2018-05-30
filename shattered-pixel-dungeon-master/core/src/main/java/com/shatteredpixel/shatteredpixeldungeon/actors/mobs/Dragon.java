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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.TomeOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.levels.DragonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DragonSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.HashSet;

import static com.shatteredpixel.shatteredpixeldungeon.levels.DragonBossLevel.CENTER;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Level.passable;

public class Dragon extends Mob {
	
	{
		spriteClass = DragonSprite.class;
		
		HP = HT = 500;
		EXP = 1000;
		defenseSkill = 20;

		HUNTING = new Hunting();

		flying = true; //doesn't literally fly, but he is fleet-of-foot enough to avoid hazards

		properties.add(Property.BOSS);
		properties.add(Property.IMMOVABLE);
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

	public enum SpecialMoves{
		STOMP,
		BITE,
		FIRE_BREATH,
	}

	public static SpecialMoves getRandomSpecial() {
		return SpecialMoves.values()[(int) (Math.random() * SpecialMoves.values().length)];
	}

	private SpecialMoves moveSpecial;
	private boolean specialActive = false;

	private int fireCooldown = 2;
	private boolean fireCharged= false;
	private boolean fireComplete = false;
	private int fireCorner;

	//A way to change the size of the fire ring and retain original values for resetting
	private int fireRingLength = 9;
	private int fireRingHeight = 9;

	private int modLength = fireRingLength;
	private int modHeight = fireRingHeight;

	private final int fireWaveLength = 17;
	private int waveStartRow = 0;

	private final int fireGrowth =4;
	private final int fireAmount = 2;

	public static int fireDamage = 40;

	public static boolean hasJumped = false;

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

		if(beforeHitHP > 9*HT/10 && HP <= 9*HT/10){
			HP = 9*(HT/10)-1;
			yell(Messages.get(this, "fireattack"));
			int oppositeHero = enemy.pos + (enemy.pos - this.pos);
			Ballistica trajectory = new Ballistica(enemy.pos, oppositeHero, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(enemy, trajectory, 5);
			fireCooldown = 0;

		}

		//phase 1 of the fight is over
		if (beforeHitHP > 3*HT/4 && HP <= 3*HT/4){
			if(!fireComplete){
				HP = 3*(HT/4)+1;
			}
			else if(fireComplete && HP <= 3*HT/4 + 1){
				HP = 3*(HT/4)-1;
				yell(Messages.get(this, "fireattack"));
				//((DragonBossLevel) Dungeon.level).progress();
				jump();



				//fireWave();

//				fireWave();


			}
			//jump();
		}

		//phase 2 of the fight is over
		if (beforeHitHP > HT/2 && HP <= HT/2){
			HP = (HT/2)-1;
			yell(Messages.get(this, "maze"));
			if(DragonBossLevel.state== DragonBossLevel.State.FIRE_ATTACK){
				DragonBossLevel.state = DragonBossLevel.State.MAZE;
			}
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

		for (int i = 1; i <= 5; i++) {
			if(Dungeon.level.adjacent(pos + i, enemy.pos) || Dungeon.level.adjacent(pos - i, enemy.pos)){
				enemyNear = true;
			}
			if(Dungeon.level.adjacent((pos + PathFinder.NEIGHBOURS8[i] + PathFinder.NEIGHBOURS8[i]), enemy.pos) || Dungeon.level.adjacent((pos + PathFinder.NEIGHBOURS8[i]+ PathFinder.NEIGHBOURS8[i]) - i, enemy.pos)){
				enemyNear = true;
			}

		}
		return enemyNear;
	}

	//tengu's attack is always visible
	@Override
	protected boolean doAttack(Char enemy) {
		if(moveSpecial!=null){
			specialActive = true;
			switch (moveSpecial) {
				case STOMP:
					sprite.attack(enemy.pos);
                    int oppositeHero = enemy.pos + (enemy.pos - this.pos);
                    Ballistica trajectory = new Ballistica(enemy.pos, oppositeHero, Ballistica.MAGIC_BOLT);
                    WandOfBlastWave.throwChar(enemy, trajectory, 2);
					break;
				case BITE:
                    sprite.attack(enemy.pos);
                    Buff.affect(enemy, Bleeding.class).set(damageRoll());
                    Splash.at( enemy.sprite.center(), -PointF.PI / 2, PointF.PI / 6, enemy.sprite.blood(), 10 );
					break;
				case FIRE_BREATH:
					sprite.zap(enemy.pos);
					MagicMissile.boltFromChar(this.sprite.parent,
							MagicMissile.FIRE_CONE,
							this.sprite,
							enemy.pos,
							new Callback() {
								@Override
								public void call() {

								}
							}
					);
					Sample.INSTANCE.play( Assets.SND_ZAP );
					Buff.affect(enemy, Burning.class).reignite(enemy);
					enemy.damage( fireDamage/2, this);
					break;
				default:
					break;
			}
			specialActive=false;
			moveSpecial = null;
		}
		else if(moveSpecial == null && canAttack(enemy)){
			sprite.attack(enemy.pos);
		}

		//fire wave attack
		if(hasJumped && DragonBossLevel.state== DragonBossLevel.State.FIRE_ATTACK){
			if(waveStartRow <25){
				fireWave(waveStartRow);
				waveStartRow++;
			}
			else{
				waveStartRow =0;
			}
		}

		//fire ring attack
		else if (fireCharged && fireCooldown==0){
			if(modHeight <24 && modLength <24 && DragonBossLevel.state != DragonBossLevel.State.FIGHT_ARENA) {
				fireRings(modHeight, modLength);
				modHeight += fireGrowth;
				modLength += fireGrowth;
			}
			else{
				fireComplete = true;
				fireCharged = false;
				fireCooldown=2;
				if(fireRingHeight == 9 && fireRingLength == 9 ){
				    fireRingHeight =7;
				    fireRingLength =7;
                }
                else{
                    fireRingHeight =9;
                    fireRingLength =9;
                }
				modHeight= fireRingHeight;
				modLength= fireRingLength;
			}
		}

		spend( attackDelay() );

		return true;
}

    public void fireRings(int height, int length){
//		for (int i=0; i < PathFinder.NEIGHBOURS8.length; i++) {
//			GameScene.add( Blob.seed( pos + PathFinder.NEIGHBOURS8[i], fireAmount, Fire.class ) );
//		}


		for (int i = 0; i < height; i++) {
            //Where the fire ring should begin on each new row
            fireCorner = (((-height/2)+i)*DragonBossLevel.width())-(length/2);
			for(int j = 0; j<length; j++){
			    if (i==0 || i==height-1) { //Top and bottom row of fire ring
					if(passable[pos + fireCorner + j] && pos + fireCorner + j >0 ) {
						GameScene.add(Blob.seed(pos + fireCorner + j, fireAmount, Fire.class));
						Char ch = Actor.findChar(pos + fireCorner + j);
						if (ch != null && !(ch instanceof Dragon)){
							Buff.affect(ch, Burning.class).reignite(ch);
							ch.damage( fireDamage, this);
						}
					}
                }
			}
			//Fills in middle row(s)
			if(i!=0 && i!=height-1) {
				if(passable[pos + fireCorner] && pos + fireCorner > 0 ) {
					GameScene.add(Blob.seed(pos + fireCorner, fireAmount, Fire.class));
					Char ch = Actor.findChar(pos + fireCorner);
					if (ch != null && !(ch instanceof Dragon)) {
						Buff.affect(ch, Burning.class).reignite(ch);
						ch.damage(damageRoll() + 20, this);
					}
				}
				if(passable[pos + fireCorner + (length - 1)] && pos + fireCorner + (length - 1) > 0) {
					GameScene.add(Blob.seed(pos + fireCorner + (length - 1), fireAmount, Fire.class));
					Char ch = Actor.findChar(pos + fireCorner + (length - 1));
					if (ch != null && !(ch instanceof Dragon)) {
						Buff.affect(ch, Burning.class).reignite(ch);
						ch.damage(damageRoll() + 20, this);
					}
				}
			}
		}
//		if(height<15 || length <15){
//			fireRings(height+4,length+4);
//		}

    }

    public void fireWave(int height){
			//Where the fire ring should begin on each new row
			fireCorner = (((-10 / 2) + height) * DragonBossLevel.width()) - (fireWaveLength / 2);
			if(height >=1 && height<=4) {
				fireWaveCheck(0);
				fireWaveCheck(1);
				fireWaveCheck(2);
				fireWaveCheck(14);
				fireWaveCheck(15);
				fireWaveCheck(16);
			}
			else {
				for (int j = 0; j < fireWaveLength; j++) {
					//All rows of fire wave filling from left to right
					if (passable[pos + fireCorner + j]) {
						GameScene.add(Blob.seed(pos + fireCorner + j, fireAmount, Fire.class));
						Char ch = Actor.findChar(pos + fireCorner + j);
						if (ch != null && !(ch instanceof Dragon)) {
							Buff.affect(ch, Burning.class).reignite(ch);
							ch.damage(fireDamage, this);
						}
					}


				}
			}

	}

	public void fireWaveCheck(int offset){
		if (passable[pos + fireCorner + offset]) {
			GameScene.add(Blob.seed(pos + fireCorner + offset, fireAmount, Fire.class));
            Char ch = Actor.findChar(pos + fireCorner + offset);
            if (ch != null && !(ch instanceof Dragon)) {
                Buff.affect(ch, Burning.class).reignite(ch);
                ch.damage(fireDamage, this);
            }
		}
	}

	private void jump() {
		int newPos;
		newPos = DragonBossLevel.getTelePos();
		if (Dungeon.visible[pos]) CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );
		sprite.move( pos, newPos );
		move( newPos );
		
		if (Dungeon.visible[newPos]) CellEmitter.get( newPos ).burst( Speck.factory( Speck.WOOL ), 6 );
			Sample.INSTANCE.play( Assets.SND_PUFF );
		hasJumped = true;
		for (int i = 1; i <= 5; i++) {
			passable[(CENTER)] = true;
			passable[(CENTER) + i] = true;
			passable[(CENTER) - i] = true;

		}
		for (int i = 1; i <= 5; i++) {
			passable[(pos)] = false;
			passable[(pos) + i] = false;
			passable[(pos) - i] = false;

		}
		spend(attackDelay());
	}
	
	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		if (HP <= HT/2) BossHealthBar.bleed(true);
		if (HP == HT) {
			yell(Messages.get(this, "start"));
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

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();
	static {
		IMMUNITIES.add( Fire.class);
		IMMUNITIES.add( Burning.class );
		IMMUNITIES.add( Blazing.class );
		IMMUNITIES.add( WandOfFireblast.class );
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
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

			if (hasJumped && DragonBossLevel.state==DragonBossLevel.State.FIRE_ATTACK) {
				return doAttack(enemy);
			}
            if(fireCooldown == 0 && DragonBossLevel.state==DragonBossLevel.State.FIRE_ATTACK){
               fireCharged=true;
               return doAttack(enemy);
            }
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )  ) {
				if(Random.Int(2) == 0 && !specialActive){
					moveSpecial = Dragon.getRandomSpecial();
				}
//				moveSpecial = Dragon.getRandomSpecial();
//				moveSpecial = SpecialMoves.FIRE_BREATH;
				return doAttack( enemy );

			} else {

				if (enemyInFOV) {
					target = enemy.pos;
					if(DragonBossLevel.state==DragonBossLevel.State.FIRE_ATTACK){
						fireCooldown = 0;
						fireCharged=true;
						return doAttack(enemy);
					}
				} else {
					chooseEnemy();
					target = enemy.pos;
					if(DragonBossLevel.state==DragonBossLevel.State.FIRE_ATTACK && !canAttack(enemy)){
						fireCooldown = 0;
						fireCharged=true;
						return doAttack(enemy);

					}
				}

				spend( TICK );
				return true;

			}
		}
	}
}
