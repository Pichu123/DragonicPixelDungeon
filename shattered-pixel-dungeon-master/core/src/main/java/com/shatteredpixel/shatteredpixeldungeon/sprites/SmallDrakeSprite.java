package com.shatteredpixel.shatteredpixeldungeon.sprites;

/**
 * Created by tmori0185 on 2/28/2018.
 */

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;


public class SmallDrakeSprite extends MobSprite {

    public SmallDrakeSprite() {
        super();

        texture( Assets.SMALLDRAKE );

        TextureFilm frames = new TextureFilm( texture, 22, 24 );

        idle = new Animation( 8, true );
        idle.frames( frames, 0, 1 );

        run = new Animation( 10, true );
        run.frames( frames, 0,1 );

        attack = new Animation( 9, false );
        attack.frames( frames, 2, 3 );

        die = new Animation( 10, false );
        die.frames( frames, 4, 5, 6, 7, 8, 9, 10, 11, 12 );

        play( idle );
    }
}