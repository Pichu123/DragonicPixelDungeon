package com.shatteredpixel.shatteredpixeldungeon.sprites;

/**
 * Created by tmori0185 on 2/28/2018.
 */

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;


public class AinsleySprite extends MobSprite {

    public AinsleySprite() {
        super();

        texture( Assets.AINSLEY );

        TextureFilm frames = new TextureFilm( texture, 38, 58 );

        idle = new Animation( 2, true );
        idle.frames( frames, 0 );

        run = new Animation( 10, true );
        run.frames( frames, 0 );

        attack = new Animation( 15, false );
        attack.frames( frames, 0 );

        die = new Animation( 10, false );
        die.frames( frames, 0 );

        play( idle );
    }
}