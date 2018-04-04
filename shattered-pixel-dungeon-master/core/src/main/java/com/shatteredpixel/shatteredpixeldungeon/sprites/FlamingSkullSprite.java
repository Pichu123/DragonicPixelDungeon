package com.shatteredpixel.shatteredpixeldungeon.sprites;

/**
 * Created by tmori0185 on 2/28/2018.
 */

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;


public class FlamingSkullSprite extends MobSprite {

    public FlamingSkullSprite() {
        super();

        texture( Assets.FLAMINGSKULL );

        TextureFilm frames = new TextureFilm( texture, 12, 15 );

        idle = new Animation( 10, true );
        idle.frames( frames, 0, 1, 2 );

        run = new Animation( 10, true );
        run.frames( frames, 0, 1, 2 );

        attack = new Animation( 9, false );
        attack.frames( frames, 3, 4 );

        die = new Animation( 7, false );
        die.frames( frames, 5, 6, 5, 6, 5, 6, 7 );

        play( idle );
    }
}