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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Dragon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.King;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.HealthIndicator;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DragonBossLevel extends Level {
	
	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}

	public enum State{
		START,
		FIRE_ATTACK,
		MAZE,
		FIGHT_ARENA,
		WON
	}
//phase 1: Dragon starts at top of map to come and fight, teleports to top of level to do big fire attack
//phase 2: Dragon makes hole into maze
//phase 3:

	public static final int TOP			= 2;
	public static final int HALL_WIDTH		= 17;
	public static final int HALL_HEIGHT	= 23;
	public static final int CHAMBER_HEIGHT	= 4;

	private static final int WIDTH = 32;
	
	public static final int LEFT	= (WIDTH - HALL_WIDTH) / 2;
	public static final int CENTER	= LEFT + HALL_WIDTH / 2;
	
	private int arenaDoor = 41+25*32;
	private boolean enteredArena = false;
	private boolean keyDropped = false;
	public static State state;
	private static Dragon dragon;

	private ArrayList<Item> storedItems = new ArrayList<>();
	@Override
	public String tilesTex() {
		return Assets.TILES_LAIR;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_HALLS;
	}
	
	private static final String DOOR	= "door";
	private static final String ENTERED	= "entered";
	private static final String DROPPED	= "droppped";
	private static final String DRAGON	        = "dragon";
	private static final String STATE	        = "state";
	private static final String STORED_ITEMS    = "storeditems";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( DOOR, arenaDoor );
		bundle.put( ENTERED, enteredArena );
		bundle.put( DROPPED, keyDropped );
		bundle.put( DRAGON, dragon );
		bundle.put( STATE, state );
		bundle.put( STORED_ITEMS, storedItems);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		arenaDoor = bundle.getInt( DOOR );
		enteredArena = bundle.getBoolean( ENTERED );
		keyDropped = bundle.getBoolean( DROPPED );
		state = bundle.getEnum( STATE, DragonBossLevel.State.class );

		//in some states dragon won't be in the world, in others he will be.
		if (state == DragonBossLevel.State.START || state == DragonBossLevel.State.MAZE) {
			dragon = (Dragon)bundle.get( DRAGON );
		} else {
			for (Mob mob : mobs){
				if (mob instanceof Dragon) {
					dragon = (Dragon) mob;
					break;
				}
			}
		}

		for (Bundlable item : bundle.getCollection(STORED_ITEMS)){
			storedItems.add( (Item)item );
		}
	}
	
	@Override
	protected boolean build() {
		setSize(32, 32);
		map = MAP_START.clone();
		buildFlagMaps();
		cleanWalls();
		state = State.START;
		//Painter.fill( this, LEFT, TOP, HALL_WIDTH, HALL_HEIGHT, Terrain.EMPTY );
//		Painter.fill( this, CENTER, TOP, 1, HALL_HEIGHT, Terrain.EMPTY_SP );
        entrance = MAP_START.length-87;
		int y = TOP + 1;
		while (y < TOP + HALL_HEIGHT) {
			drop( new Gold(), y * width() + CENTER - 2 );
			drop( new Gold(), y * width() + CENTER + 2 );
			y += 2;
		}

		int left = pedestal( true );
		int right = pedestal( false );
//		map[left] = map[right] = Terrain.PEDESTAL;
//		for (int i=left+1; i < right; i++) {
//			map[i] = Terrain.EMPTY_SP;
//		}

//		exit = (TOP - 1) * width() + CENTER;
//		map[exit] = Terrain.LOCKED_EXIT;
//
//		arenaDoor = (TOP + HALL_HEIGHT) * width() + CENTER;
//		map[arenaDoor] = Terrain.DOOR;
//
//		Painter.fill( this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH, CHAMBER_HEIGHT, Terrain.EMPTY );
//		Painter.fill( this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH, 1, Terrain.BOOKSHELF);
//		map[arenaDoor + width()] = Terrain.EMPTY;
//		Painter.fill( this, LEFT, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.BOOKSHELF );
//		Painter.fill( this, LEFT + HALL_WIDTH - 1, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.BOOKSHELF );
//
//		for (int i = 0; i < length(); i ++){
//			if(map[i]==Terrain.ENTRANCE){
//				entrance = map[i];
//			}
//
//		}

//		map[entrance] = Terrain.ENTRANCE;
//
//		for (int i=0; i < length() - width(); i++) {
//			if (map[i] == Terrain.EMPTY ) {
//				map[i] = Terrain.EMPTY_DECO;
//			} else if (map[i] == Terrain.WALL
//					&& DungeonTileSheet.floorTile(map[i + width()])
//					) {
//				map[i] = Terrain.WALL_DECO;
//			}
//		}
		
		return true;
	}

	public void progress(){
		switch (state){
		case START:
			seal();
			for (Mob m : mobs){
				//bring the first ally with you
				if (m.ally){
					m.pos = Dungeon.hero.pos + 1;
					m.sprite.place(m.pos);
					break;
				}
			}
			dragon.state = dragon.HUNTING;
			dragon.pos =  ((HALL_HEIGHT / 2)+1) * width() + ((HALL_WIDTH/2)+1); //in the middle of the fight room
			GameScene.add( dragon );
			for (int i=0; i < PathFinder.NEIGHBOURS8.length; i++) {
				GameScene.add( Blob.seed( dragon.pos + PathFinder.NEIGHBOURS8[i], 30, Fire.class ) );
			}
			dragon.notice();


			state = State.FIRE_ATTACK;
			break;
		case FIRE_ATTACK:
			//changeMap(MAP_MAZE);
//			HealthIndicator.instance.target(null);
//			dragon.sprite.kill();
			break;
		case MAZE:


			state = State.FIGHT_ARENA;
			break;
		case FIGHT_ARENA:
//			dragon.die(Dungeon.hero);
//			dragon.sprite.kill();
            break;
		}


	}

	private void changeMap(int[] map){
		this.map = map.clone();
		buildFlagMaps();
		cleanWalls();

		exit = entrance = 0;
		for (int i = 0; i < length(); i ++)
			if (map[i] == Terrain.ENTRANCE)
				entrance = i;
			else if (map[i] == Terrain.EXIT)
				exit = i;

		visited = mapped = new boolean[length()];
		for (Blob blob: blobs.values()){
			blob.fullyClear();
		}
		addVisuals(); //this also resets existing visuals
		//resetTraps();


		GameScene.resetMap();
		Dungeon.observe();
	}


	private void clearEntities(Room safeArea){
		for (Heap heap : heaps.values()){
			if (safeArea == null || !safeArea.inside(cellToPoint(heap.pos))){
				for (Item item : heap.items)
					storedItems.add(item);
				heap.destroy();
			}
		}
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[Dungeon.level.mobs.size()])){
			if (mob != dragon && (safeArea == null || !safeArea.inside(cellToPoint(mob.pos)))){
				mob.destroy();
				if (mob.sprite != null)
					mob.sprite.killAndErase();
			}
		}
		for (Plant plant : plants.values()){
			if (safeArea == null || !safeArea.inside(cellToPoint(plant.pos))){
				plants.remove(plant.pos);
			}
		}
	}


	public int pedestal( boolean left ) {
		if (left) {
			return (TOP + HALL_HEIGHT / 2) * width() + CENTER - 2;
		} else {
			return (TOP + HALL_HEIGHT / 2) * width() + CENTER + 2;
		}
	}
	
	@Override
	public Mob createMob() {
		return null;
	}
	
	@Override
	protected void createMobs() {
		dragon = new Dragon();
	}
	
	public Actor respawner() {
		return null;
	}
	
	@Override
	protected void createItems() {
		/*Item item = Bones.get();
		if (item != null) {
			int pos;
			do {
				pos =
					Random.IntRange( LEFT + 1, LEFT + HALL_WIDTH - 2 ) +
					Random.IntRange( TOP + HALL_HEIGHT + 1, TOP + HALL_HEIGHT  + CHAMBER_HEIGHT ) * width();
			} while (pos == entrance);
			drop( item, pos ).type = Heap.Type.REMAINS;
		}*/
	}
	
	@Override
	public int randomRespawnCell() {
		int cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
		while (!passable[cell]){
			cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
		}
		return cell;
	}
	
	@Override
	public void press( int cell, Char hero ) {
		
		super.press( cell, hero );
		if (!enteredArena && outsideEntraceRoom( cell ) && hero == Dungeon.hero) {
			enteredArena = true;
            set( arenaDoor, Terrain.LOCKED_DOOR );
            GameScene.updateMap( arenaDoor);
            Dungeon.observe();
			progress();
		}
		if(Blob.volumeAt(cell, Fire.class) > 0){
			Char ch = Actor.findChar(cell );
			if (ch != null) {
				Buff.affect( ch, Burning.class ).reignite( ch );
				ch.damage(10, this);
				//spend(TICK);
			}

		}

	}
	
	@Override
	public Heap drop( Item item, int cell ) {
		
		if (!keyDropped && item instanceof SkeletonKey) {
			
			keyDropped = true;
			unseal();
			
			set( arenaDoor, Terrain.DOOR );
			GameScene.updateMap( arenaDoor );
			Dungeon.observe();
		}
		
		return super.drop( item, cell );
	}

	public static int getTelePos (){
		int newPos = dragon.pos - (7*width());
		return newPos;
	}

	private boolean outsideEntraceRoom( int cell ) {
		return cell / width() < arenaDoor / width();
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(CityLevel.class, "water_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(CityLevel.class, "high_grass_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.ENTRANCE:
				return Messages.get(CityLevel.class, "entrance_desc");
			case Terrain.EXIT:
				return Messages.get(CityLevel.class, "exit_desc");
			case Terrain.WALL_DECO:
			case Terrain.EMPTY_DECO:
				return Messages.get(CityLevel.class, "deco_desc");
			case Terrain.EMPTY_SP:
				return Messages.get(CityLevel.class, "sp_desc");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(CityLevel.class, "statue_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(CityLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals( ) {
		super.addVisuals();
		CityLevel.addCityVisuals(this, visuals);
		return visuals;
	}

	private static final int W = Terrain.WALL;
	private static final int D = Terrain.DOOR;
	private static final int L = Terrain.LOCKED_DOOR;
	private static final int e = Terrain.EMPTY;

	private static final int T = Terrain.INACTIVE_TRAP;

	private static final int E = Terrain.ENTRANCE;
	private static final int X = Terrain.EXIT;

	private static final int M = Terrain.WALL_DECO;
	private static final int P = Terrain.PEDESTAL;

	private static final int[] MAP_START =
			{       W, W, W, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, W, W, W, W, W, W, D, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, E, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W};


	private static final int[] MAP_MAZE =
			{       W, W, W, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, W, W, W, W, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, D, e, e, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, e, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, e, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, e, e, e, W, W, W, e, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, D, e, e, e, W, W, W, e, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, e, e, e, W, W, W, e, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, W, W, W, e, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, W, W, W, e, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, W, W, W, e, W, W, W, W, W,
					W, W, W, W, W, W, W, W, W, D, W, W, W, W, W, W, W, W, W, W, W, W, e, W, W, W, e, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, e, e, e, e, e, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W};

	private static final int[] MAP_ARENA =
			{       W, W, W, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, W, W, W, W, W, W, D, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, E, W, W, W, W, W, W, W, W, W, W, W, W, W, W};

	private static final int[] MAP_END =
			{       W, W, W, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, W, W, W, W, W, W, D, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
					W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, E, W, W, W, W, W, W, W, W, W, W, W, W, W, W};
}
