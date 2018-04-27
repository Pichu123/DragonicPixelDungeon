package com.shatteredpixel.shatteredpixeldungeon.sprites;

/**
 * Created by tmori0185 on 2/28/2018.
 */

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;


public class NecromancerSprite extends MobSprite {

    public NecromancerSprite() {
        super();

        texture( Assets.NECROMANCER );

        TextureFilm frames = new TextureFilm( texture, 22, 23 );

        idle = new Animation( 8, true );
        idle.frames( frames, 0 );

        run = new Animation( 10, true );
        run.frames( frames, 0 );

        attack = new Animation( 9, false );
        attack.frames( frames, 0 );

        die = new Animation( 10, false );
        die.frames( frames, 0 );

        play( idle );
    }
    public void zap( int cell ) {

        turnTo( ch.pos , cell );
        play( zap );

        MagicMissile.boltFromChar( parent,
                MagicMissile.SHADOW,
                this,
                cell,
                new Callback() {
                    @Override
                    public void call() {
                        ((Necromancer)ch).onZapComplete();
                    }
                } );
        Sample.INSTANCE.play( Assets.SND_ZAP );
    }
}