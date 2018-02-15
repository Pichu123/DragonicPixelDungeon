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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.ArmorKit;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.TomeOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfLife;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Chilling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.DragonDagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greataxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatshield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Knuckles;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.VikingMace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Boomerang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Dart;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;

public enum HeroClass {

	WARRIOR( "warrior" ),
	MAGE( "mage" ),
	ROGUE( "rogue" ),
	HUNTRESS( "huntress" ),
	DRAGONKNIGHT( "dragonknight" ),
	VIKING( "viking" );

	private String title;

	HeroClass( String title ) {
		this.title = title;
	}

	public void initHero( Hero hero ) {

		hero.heroClass = this;

		initCommon( hero );

		switch (this) {
			case WARRIOR:
				initWarrior( hero );
				break;

			case MAGE:
				initMage( hero );
				break;

			case ROGUE:
				initRogue( hero );
				break;

			case HUNTRESS:
				initHuntress( hero );
				break;

			case DRAGONKNIGHT:
				initDragonKnight( hero );
				break;

			case VIKING:
				initViking( hero );
				break;
		}

		hero.updateAwareness();
	}

	private static void initCommon( Hero hero ) {
		if (!Dungeon.isChallenged(Challenges.NO_ARMOR))
			(hero.belongings.armor = new ClothArmor()).identify();

		if (!Dungeon.isChallenged(Challenges.NO_FOOD))
			new Food().identify().collect();
		MysteryMeat meat;
		for (int x=0; x<25; x++){
			meat = new MysteryMeat();
			meat.collect();
		}
	}

	public Badges.Badge masteryBadge() {
		switch (this) {
			case WARRIOR:
				return Badges.Badge.MASTERY_WARRIOR;
			case MAGE:
				return Badges.Badge.MASTERY_MAGE;
			case ROGUE:
				return Badges.Badge.MASTERY_ROGUE;
			case HUNTRESS:
				return Badges.Badge.MASTERY_HUNTRESS;
			case DRAGONKNIGHT:
				return Badges.Badge.MASTERY_DRAGONKNIGHT;
			case VIKING:
				return Badges.Badge.MASTERY_VIKING;
		}
		return null;
	}

	private static void initWarrior( Hero hero ) {
		(hero.belongings.weapon = new WornShortsword()).identify();
		Dart darts = new Dart( 8 );
		darts.identify().collect();

		Scroll scroll = new ScrollOfMagicMapping();
		scroll.identify();
		for (int x=0; x<25; x++){
			scroll = new ScrollOfMagicMapping();
			scroll.collect();
		}
		PotionOfStrength potion = new PotionOfStrength();
		potion.identify();
		PotionOfHealing potion2 = new PotionOfHealing();
		potion2.identify();
		PotionOfExperience potion3 = new PotionOfExperience();
		potion3.identify();
		for (int i = 0; i < 20 ; i++) {
			potion = new PotionOfStrength();
			potion.collect();
			potion2 = new PotionOfHealing();
			potion2.collect();
			potion3 = new PotionOfExperience();
			potion3.collect();
		}


		Greatsword sword = new Greatsword();
		for (int i = 0; i < 75 ; i++) {
			sword.upgrade();
		}
		sword.identify().collect();



		Dungeon.quickslot.setSlot(1, potion);


		Dungeon.quickslot.setSlot(2, scroll);


		if ( Badges.isUnlocked(Badges.Badge.TUTORIAL_WARRIOR) ){
			if (!Dungeon.isChallenged(Challenges.NO_ARMOR))
				hero.belongings.armor.affixSeal(new BrokenSeal());
			Dungeon.quickslot.setSlot(0, darts);
		} else {
			if (!Dungeon.isChallenged(Challenges.NO_ARMOR)) {
				BrokenSeal seal = new BrokenSeal();
				seal.collect();
				Dungeon.quickslot.setSlot(0, seal);
			}
			Dungeon.quickslot.setSlot(1, darts);
		}

		new PotionOfHealing().identify();
	}

	private static void initMage( Hero hero ) {
		MagesStaff staff;

		if ( Badges.isUnlocked(Badges.Badge.TUTORIAL_MAGE) ){
			staff = new MagesStaff(new WandOfMagicMissile());
		} else {
			staff = new MagesStaff();
			new WandOfMagicMissile().identify().collect();
		}

		(hero.belongings.weapon = staff).identify();
		hero.belongings.weapon.activate(hero);

		Dungeon.quickslot.setSlot(0, staff);

		new ScrollOfUpgrade().identify();
	}

	private static void initRogue( Hero hero ) {
		(hero.belongings.weapon = new Dagger()).identify();

		CloakOfShadows cloak = new CloakOfShadows();
		(hero.belongings.misc1 = cloak).identify();
		hero.belongings.misc1.activate( hero );

		Dart darts = new Dart( 8 );
		darts.identify().collect();

		Dungeon.quickslot.setSlot(0, cloak);
		Dungeon.quickslot.setSlot(1, darts);

		new ScrollOfMagicMapping().identify();
	}

	private static void initHuntress( Hero hero ) {

		(hero.belongings.weapon = new Knuckles()).identify();
		Boomerang boomerang = new Boomerang();
		boomerang.identify().collect();

		Dungeon.quickslot.setSlot(0, boomerang);

		new PotionOfMindVision().identify();
	}

	private static void initDragonKnight( Hero hero){
		(hero.belongings.weapon = new DragonDagger()).identify();

		TomeOfMastery tome = new TomeOfMastery();
		tome.identify().collect();

		Scroll scroll = new ScrollOfMagicMapping();
		scroll.identify();
		for (int x=0; x<25; x++){
			scroll = new ScrollOfMagicMapping();
			scroll.collect();
		}
		Scroll scroll2 = new ScrollOfUpgrade();
		scroll2.identify();
		for (int x=0; x<25; x++){
			scroll2 = new ScrollOfUpgrade();
			scroll2.collect();
		}

		Scroll scroll3 = new ScrollOfTerror();
		scroll3.identify();
		for (int x=0; x<25; x++){
			scroll3 = new ScrollOfTerror();
			scroll3.collect();
		}

		PotionOfStrength potion = new PotionOfStrength();
		potion.identify();
		PotionOfHealing potion2 = new PotionOfHealing();
		potion2.identify();
		PotionOfExperience potion3 = new PotionOfExperience();
		potion3.identify();
		PotionOfLiquidFlame potion4 = new PotionOfLiquidFlame();
		potion4.identify();
		for (int i = 0; i < 20 ; i++) {
			potion = new PotionOfStrength();
			potion.collect();
			potion2 = new PotionOfHealing();
			potion2.collect();
			potion3 = new PotionOfExperience();
			potion3.collect();
			potion4 = new PotionOfLiquidFlame();
			potion4.collect();
		}


		Greatsword sword = new Greatsword();
		for (int i = 0; i < 75 ; i++) {
			sword.upgrade();
		}
		sword.identify().collect();

		RunicBlade blade = new RunicBlade();
		for (int i = 0; i < 3 ; i++) {
			blade.upgrade();
		}
		blade.identify().collect();

		Food food = new Food();
		for (int i = 0; i < 100 ; i++) {
			food.identify().collect();
		}

		PlateArmor armor = new PlateArmor();
		for (int i = 0; i < 75 ; i++) {
			armor.upgrade();
		}
		armor.identify().collect();

		RingOfLife ring = new RingOfLife();
		/*for (int i = 0; i <25 ; i++){
			ring.upgrade();
		}*/
		ring.identify().collect();

		RingOfMight ring2 = new RingOfMight();
		ring2.identify().collect();

		new PotionOfLiquidFlame().identify();
		ArmorKit kit = new ArmorKit();
		kit.collect();
	}
	private static void initViking( Hero hero ) {

		(hero.belongings.weapon = new VikingMace()).identify();

		//new PotionOf().identify();
		Scroll scroll = new ScrollOfMagicMapping();
		scroll.identify();
		for (int x=0; x<25; x++){
			scroll = new ScrollOfMagicMapping();
			scroll.collect();
		}


		PotionOfStrength potion = new PotionOfStrength();
		potion.identify();
		PotionOfHealing potion2 = new PotionOfHealing();
		potion2.identify();
		PotionOfExperience potion3 = new PotionOfExperience();
		potion3.identify();
		PotionOfMindVision potion4 = new PotionOfMindVision();
		potion4.identify();
		PotionOfFrost potion5 = new PotionOfFrost();
		potion5.identify();
		for (int i = 0; i < 20 ; i++) {
			potion = new PotionOfStrength();
			potion.collect();
			potion2 = new PotionOfHealing();
			potion2.collect();
			potion3 = new PotionOfExperience();
			potion3.collect();
			potion4 = new PotionOfMindVision();
			potion4.collect();
			potion5 = new PotionOfFrost();
			potion5.collect();
		}

		WandOfCorruption wand = new WandOfCorruption();
		for (int i = 0; i < 75; i++) {
			wand.upgrade();
		}
		wand.identify().collect();

		TomeOfMastery tome = new TomeOfMastery();
		tome.identify().collect();

		Greatsword sword = new Greatsword();
		for (int i = 0; i < 75 ; i++) {
			sword.upgrade();
		}
		sword.identify().collect();
//		sword.enchant(Weapon.Enchantment.random());

//		Knuckles wep = new Knuckles();
//		wep.identify().collect();
//		wep.enchant(Weapon.Enchantment.random());

		Quarterstaff staff = new Quarterstaff();
		for (int i = 0; i < 3; i++) {
			staff.upgrade();
		}
		staff.identify().collect();

		Food food = new Food();
		for (int i = 0; i < 100 ; i++) {
			food.identify().collect();
		}

		PlateArmor armor = new PlateArmor();
		for (int i = 0; i < 75 ; i++) {
			armor.upgrade();
		}
		armor.identify().collect();

		RingOfHaste ring = new RingOfHaste();
		for (int i = 0; i < 75 ; i++) {
			ring.upgrade();
		}
		ring.identify().collect();

		new ScrollOfRemoveCurse().identify();

		ArmorKit kit = new ArmorKit();
		kit.collect();
	}
	
	public String title() {
		return Messages.get(HeroClass.class, title);
	}
	
	public String spritesheet() {
		
		switch (this) {
		case WARRIOR:
			return Assets.WARRIOR;
		case MAGE:
			return Assets.MAGE;
		case ROGUE:
			return Assets.ROGUE;
		case HUNTRESS:
			return Assets.HUNTRESS;
        case DRAGONKNIGHT:
			return Assets.DRAGONKNIGHT;
		case VIKING:
			return Assets.VIKING;
		}
		
		return null;
	}
	
	public String[] perks() {
		
		switch (this) {
		case WARRIOR:
			return new String[]{
					Messages.get(HeroClass.class, "warrior_perk1"),
					Messages.get(HeroClass.class, "warrior_perk2"),
					Messages.get(HeroClass.class, "warrior_perk3"),
					Messages.get(HeroClass.class, "warrior_perk4"),
					Messages.get(HeroClass.class, "warrior_perk5"),
			};
		case MAGE:
			return new String[]{
					Messages.get(HeroClass.class, "mage_perk1"),
					Messages.get(HeroClass.class, "mage_perk2"),
					Messages.get(HeroClass.class, "mage_perk3"),
					Messages.get(HeroClass.class, "mage_perk4"),
					Messages.get(HeroClass.class, "mage_perk5"),
			};
		case ROGUE:
			return new String[]{
					Messages.get(HeroClass.class, "rogue_perk1"),
					Messages.get(HeroClass.class, "rogue_perk2"),
					Messages.get(HeroClass.class, "rogue_perk3"),
					Messages.get(HeroClass.class, "rogue_perk4"),
					Messages.get(HeroClass.class, "rogue_perk5"),
					Messages.get(HeroClass.class, "rogue_perk6"),
			};
		case HUNTRESS:
			return new String[]{
					Messages.get(HeroClass.class, "huntress_perk1"),
					Messages.get(HeroClass.class, "huntress_perk2"),
					Messages.get(HeroClass.class, "huntress_perk3"),
					Messages.get(HeroClass.class, "huntress_perk4"),
					Messages.get(HeroClass.class, "huntress_perk5"),
			};
        case DRAGONKNIGHT:
            return new String[]{
                    Messages.get(HeroClass.class, "dragonknight_perk1"),
                    Messages.get(HeroClass.class, "dragonknight_perk2"),
                    Messages.get(HeroClass.class, "dragonknight_perk3"),
                    Messages.get(HeroClass.class, "dragonknight_perk4"),
                    Messages.get(HeroClass.class, "dragonknight_perk5"),
            };
	    case VIKING:
			return new String[]{
					Messages.get(HeroClass.class, "viking_perk1"),
					Messages.get(HeroClass.class, "viking_perk2"),
					Messages.get(HeroClass.class, "viking_perk3"),
					Messages.get(HeroClass.class, "viking_perk4"),
			};
		}
		
		return null;
	}

	private static final String CLASS	= "class";
	
	public void storeInBundle( Bundle bundle ) {
		bundle.put( CLASS, toString() );
	}
	
	public static HeroClass restoreInBundle( Bundle bundle ) {
		String value = bundle.getString( CLASS );
		return value.length() > 0 ? valueOf( value ) : ROGUE;
	}
}
