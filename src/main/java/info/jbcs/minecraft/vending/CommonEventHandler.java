package info.jbcs.minecraft.vending;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommonEventHandler
{
    @SubscribeEvent
    public void onEntityConstructed(EntityEvent.EntityConstructing event)
    {
        //if (event.entity instanceof EntityPlayer)
        //{
            //PlayerWeaponData.initPlayerWeaponData((EntityPlayer) event.entity);
        //}
    }
}
