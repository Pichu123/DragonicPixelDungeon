package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FlamingSkullSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NecromancerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SmallDrakeSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.lang.annotation.Target;
import java.util.ArrayList;

import javax.security.auth.callback.Callback;

/**
 * Created by tmori0185 on 2/15/2018.
 */

public class Necromancer extends Mob implements Callback {
    private static final float TIME_TO_ZAP	= 1f;
    int spawn = 0;

    {
        spriteClass = NecromancerSprite.class;

        HP = HT = 1;
        defenseSkill = 3;

        EXP = 14;

        properties.add(Property.DEMONIC);

        spend(TICK);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 4 );
    }

    @Override
    public int attackSkill( Char target ) {
        if(spawn >= 3 && state == HUNTING && target.equals(Dungeon.hero)) {
            spawn = 0;
            for (int t = 0; t < 3; t++) {
                ArrayList<Integer> spawnPoints = new ArrayList<>();

                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) == null && (Level.passable[p] || Level.avoid[p])) {
                        spawnPoints.add(p);
                    }
                }
                if (spawnPoints.size() > 0) {
                    Wraith wraith = new Wraith();
                    wraith.pos = Random.element(spawnPoints);

                    GameScene.add(wraith);
                    Actor.addDelayed(new Pushing(wraith, pos, wraith.pos), -1);
                }
            }
        }
        else if (spawn < 3){
            spawn++;
        }
        else{
        }

        return 5;
    }

    protected boolean canAttack( Char enemy ) {
        return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
    }

    protected boolean doAttack( Char enemy ) {

        if (Dungeon.level.adjacent( pos, enemy.pos )) {

            return super.doAttack( enemy );

        } else {

            boolean visible = Level.fieldOfView[pos] || Level.fieldOfView[enemy.pos];
            if (visible) {
                sprite.zap( enemy.pos );
            } else {
                zap();
            }

            return !visible;
        }
    }

    private void zap() {
        spend( TIME_TO_ZAP );

        if (hit( this, enemy, true )) {
            if (enemy == Dungeon.hero && Random.Int( 2 ) == 0) {
                Buff.prolong( enemy, Weakness.class, Weakness.duration( enemy ) );
            }

            int dmg = Random.Int( 12, 18 );
            enemy.damage( dmg, this );

            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Dungeon.fail( getClass() );
                GLog.n( Messages.get(this, "bolt_kill") );
            }
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }
    }

    public void onZapComplete() {
        zap();
        next();
    }

  //@Override
    public void call() {
        next();
    }


    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }

}
