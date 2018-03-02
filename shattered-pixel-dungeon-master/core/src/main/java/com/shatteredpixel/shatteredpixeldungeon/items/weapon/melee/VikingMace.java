package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;


import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/**
 * Created by dhoang9775 on 1/23/2018.
 */

public class VikingMace extends MeleeWeapon {
    {
        image = ItemSpriteSheet.VIKING_MACE;

        tier = 1;
        DLY = 1.5f; //0.67x speed
    }

    @Override
    public int max(int lvl) {
        return  Math.round(7.5f*(tier+1)) +    //15 base, up from 10
                lvl*tier;
    }

}
