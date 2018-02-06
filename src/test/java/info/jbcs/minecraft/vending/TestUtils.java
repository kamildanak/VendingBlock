package info.jbcs.minecraft.vending;

import com.kamildanak.minecraft.enderpay.EnderPay;
import com.kamildanak.minecraft.enderpay.economy.Account;
import com.kamildanak.minecraft.enderpay.economy.DayHelper;
import com.kamildanak.minecraft.enderpay.economy.PlayerHelper;
import com.kamildanak.minecraft.enderpay.item.ItemBlankBanknote;
import com.kamildanak.minecraft.enderpay.item.ItemFilledBanknote;
import info.jbcs.minecraft.vending.forge.LoaderWrapper;
import info.jbcs.minecraft.vending.inventory.InventoryVendingMachine;
import info.jbcs.minecraft.vending.items.wrapper.transactions.VendingMachineInvWrapper;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {
    public static VendingMachineInvWrapper createWrapper(boolean isEnderPayLoaded, boolean isOpen) {
        LoaderWrapper wrapper = mock(LoaderWrapper.class);
        when(wrapper.isModLoaded("enderpay")).thenReturn(isEnderPayLoaded);
        LoaderWrapper.setTestWrapper(wrapper);
        TileEntityVendingMachine machine = mock(TileEntityVendingMachine.class);
        when(machine.isOpen()).thenReturn(isOpen);
        return new VendingMachineInvWrapper(machine, new InventoryVendingMachine(machine));
    }

    public static void initializeEnderPay() {
        EnderPay.settings = mock(com.kamildanak.minecraft.enderpay.proxy.Settings.class);
        when(EnderPay.settings.getDaysAfterBanknotesExpires()).thenReturn(1);
        when(EnderPay.settings.isStampedMoney()).thenReturn(true);
        Account.setInterfaces(EnderPay.settings,
                new DayHelper(), new PlayerHelper());
        EnderPay.itemBlankBanknote = new ItemBlankBanknote("blank_banknote");
        EnderPay.itemFilledBanknote = new ItemFilledBanknote("filled_banknote");
    }
}
