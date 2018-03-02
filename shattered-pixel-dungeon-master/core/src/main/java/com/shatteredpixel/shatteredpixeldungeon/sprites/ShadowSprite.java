package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Random;

/**
 * Created by tmori0185 on 2/15/2018.
 */

public class ShadowSprite extends MobSprite {

    public ShadowSprite() {
        super();

        texture( Assets.SHADOWMON );

        TextureFilm frames = new TextureFilm( texture, 17, 16 );

        idle = new Animation( 8, true );
        idle.frames( frames,  1, 1, 1, 1, 1, 1, 2, 3, 3, 3, 3, 3, 3, 3, 1, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 4, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 0, 1, 1, 1, 6, 3, 3, 3, 3 );

        run = new Animation( 8, true );
        run.frames( frames, 1, 1, 1, 1, 1, 1, 2, 3, 3, 3, 3, 3, 3, 3, 1, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 4, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 0, 1, 1, 1, 6, 3, 3, 3, 3 );

        attack = new Animation( 20, false );
        attack.frames( frames,  4, 4, 4, 6, 6  );

        die = new Animation( 15, false );
        die.frames( frames, 0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7, 0, 5, 7 );

        play( idle );
    }

    @Override
    public int blood() {
        return 0xbbcc66;
    }

    @Override
    public void die() {
        Splash.at( center(), blood(), 10 );
        super.die();
    }
}