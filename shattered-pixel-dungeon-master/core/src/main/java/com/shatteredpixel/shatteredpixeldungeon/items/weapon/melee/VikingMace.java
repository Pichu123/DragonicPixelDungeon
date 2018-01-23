package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;


import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/**
 * Created by dhoang9775 on 1/23/2018.
 */

public class VikingMace extends MeleeWeapon {
    {
        image = ItemSpriteSheet.VIKING_MACE;

        tier = 1;
        DLY = 2f;
    }

    @Override
    public int max(int lvl) {
        return  Math.round(7.5f*(tier+1)) +    //20 base, up from 15
                lvl*tier; //+4 per level, up from +3
    }

}
