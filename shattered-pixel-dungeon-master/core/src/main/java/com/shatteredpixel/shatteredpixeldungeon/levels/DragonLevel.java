package com.shatteredpixel.shatteredpixeldungeon.levels;


import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;

import com.shatteredpixel.shatteredpixeldungeon.levels.painters.DragonPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisarmingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FireTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.LightningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.OozeTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SpearTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.VenomTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WeakeningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * Created by dhoang9775 on 2/22/2018.
 */

public class DragonLevel extends RegularLevel {

    {
        color1 = 0x4b6636;
        color2 = 0xf2f2f2;
    }

    @Override
    protected int standardRooms() {
        //9 to 11, average 9.26
        return 9+Random.chances(new float[]{15, 3, 1});
    }

    @Override
    protected int specialRooms() {
        //2 to 4, average 2.73
        return 2 + Random.chances(new float[]{5, 4, 2});
    }

    @Override
    public String tilesTex() {
        return Assets.TILES_LAIR;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_LAIR;
    }

    @Override
    protected Painter painter() {
        return new DragonPainter()
                .setWater(feeling == Feeling.WATER ? 0.70f : 0.15f, 6)
                .setGrass(feeling == Feeling.GRASS ? 0.65f : 0.10f, 3)
                .setTraps(nTraps(), trapClasses(), trapChances());
    }

    @Override
    protected Class<?>[] trapClasses() {
        return new Class[]{ BlazingTrap.class, FireTrap.class, ExplosiveTrap.class, DisintegrationTrap.class, VenomTrap.class,
                GrippingTrap.class, PitfallTrap.class, LightningTrap.class, OozeTrap.class, WeakeningTrap.class,
                CursingTrap.class, GrimTrap.class, GuardianTrap.class, SummoningTrap.class, TeleportationTrap.class,
                DisarmingTrap.class, DistortionTrap.class, WarpingTrap.class};
    }

    @Override
    protected float[] trapChances() {
        return new float[]{ 10, 10, 10, 8, 8,
                4, 4, 4, 4, 4,
                2, 2, 2, 2, 2,
                1, 1, 1 };
    }

    @Override
    protected void createItems() {
        super.createItems();

    }

    @Override
    public String tileName( int tile ) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(DragonLevel.class, "water_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(DragonLevel.class, "high_grass_name");
            default:
                return super.tileName( tile );
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.ENTRANCE:
                return Messages.get(DragonLevel.class, "entrance_desc");
            case Terrain.EXIT:
                return Messages.get(DragonLevel.class, "exit_desc");
            case Terrain.HIGH_GRASS:
                return Messages.get(DragonLevel.class, "high_grass_desc");
            case Terrain.WALL_DECO:
            case Terrain.EMPTY_DECO:
                return Messages.get(DragonLevel.class, "deco_desc");
            case Terrain.EMPTY_SP:
                return Messages.get(DragonLevel.class, "sp_desc");
            case Terrain.STATUE:
                return Messages.get(DragonLevel.class, "statue_desc");
            case Terrain.STATUE_SP:
                return Messages.get(DragonLevel.class, "statue_sp_desc");
            case Terrain.WATER:
                return Messages.get(DragonLevel.class, "water_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(DragonLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc( tile );
        }
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        addLairVisuals( this, visuals );
        return visuals;
    }

    public static void addLairVisuals( Level level, Group group ) {
        for (int i=0; i < level.length(); i++) {
            if (level.map[i] == Terrain.WALL_DECO) {
                group.add( new Smoke( i ) );
            }
        }
    }

    private static class Smoke extends Emitter {

        private int pos;

        private static final Emitter.Factory factory = new Factory() {

            @Override
            public void emit( Emitter emitter, int index, float x, float y ) {
                SmokeParticle p = (SmokeParticle)emitter.recycle( SmokeParticle.class );
                p.reset( x, y );
            }
        };

        public Smoke( int pos ) {
            super();

            this.pos = pos;

            PointF p = DungeonTilemap.tileCenterToWorld( pos );
            pos( p.x - 6, p.y - 4, 12, 12 );

            pour( factory, 0.2f );
        }

        @Override
        public void update() {
            if (visible = Dungeon.visible[pos]) {
                super.update();
            }
        }
    }

    public static final class SmokeParticle extends PixelParticle {

        public SmokeParticle() {
            super();

            color( 0x000000 );
            speed.set( Random.Float( -2, 4 ), -Random.Float( 3, 6 ) );
        }

        public void reset( float x, float y ) {
            revive();

            this.x = x;
            this.y = y;

            left = lifespan = 2f;
        }

        @Override
        public void update() {
            super.update();
            float p = left / lifespan;
            am = p > 0.8f ? 1 - p : p * 0.25f;
            size( 6 - p * 3 );
        }
    }
}
