package info.jbcs.minecraft.vending.stats;

import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.text.TextComponentTranslation;

public class ModStats {
    public static final StatBase VENDING_MACHINES_USED =
            (new StatBasic("vending:stat.vendingMachinesUsed",
                    new TextComponentTranslation("vending:stat.vendingMachinesUsed")))
                    .registerStat();
    public static final StatBase VENDING_MACHINES_OPENED =
            (new StatBasic("vending:stat.vendingMachinesOpened",
                    new TextComponentTranslation("vending:stat.vendingMachinesOpened")))
                    .registerStat();
    public static final StatBase STORAGE_ATTACHMENTS_OPENED =
            (new StatBasic("vending:stat.storageAttachmentsOpened",
                    new TextComponentTranslation("vending:stat.storageAttachmentsOpened")))
                    .registerStat();
}