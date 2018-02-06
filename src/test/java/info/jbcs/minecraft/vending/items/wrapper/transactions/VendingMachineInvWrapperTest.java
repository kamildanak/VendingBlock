package info.jbcs.minecraft.vending.items.wrapper.transactions;

import com.kamildanak.minecraft.enderpay.EnderPay;
import info.jbcs.minecraft.vending.EnderPayApiUtils;
import info.jbcs.minecraft.vending.TestUtils;
import info.jbcs.minecraft.vending.forge.LoaderWrapper;
import info.jbcs.minecraft.vending.inventory.InventoryVendingMachine;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static info.jbcs.minecraft.vending.items.wrapper.AdvancedItemHandlerHelper.countNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VendingMachineInvWrapperTest {
    private VendingMachineInvWrapper inventory;

    @Before
    public void Before() {
        Bootstrap.register();
        LoaderWrapper wrapper = mock(LoaderWrapper.class);
        when(wrapper.isModLoaded("enderpay")).thenReturn(true);
        LoaderWrapper.setTestWrapper(wrapper);
        TileEntityVendingMachine machine = mock(TileEntityVendingMachine.class);
        when(machine.isOpen()).thenReturn(true);
        inventory = new VendingMachineInvWrapper(machine, new InventoryVendingMachine(machine));
        TestUtils.initializeEnderPay();
    }

    @Test
    public void IsInventoryEmptyAfterInitialization() {
        Assert.assertEquals(ItemStack.EMPTY, inventory.getBoughtItem());
        Assert.assertEquals(0, countNotNull(inventory.getSoldItemsWithoutFilledBanknotes()));
        Assert.assertEquals(0, countNotNull(inventory.getInventoryItems()));
        Assert.assertEquals(0, inventory.getTotalAvailableCreditSums(true));
        Assert.assertEquals(0, countNotNull(inventory.getSoldItems()));
        Assert.assertEquals(0, inventory.soldCreditsSum(false));
        Assert.assertEquals(0, inventory.boughtCreditsSum(false));
        Assert.assertEquals(true, inventory.hasNothingToSell());
        Assert.assertEquals(true, inventory.hasNothingToBuy());
    }

    @Test
    public void ShouldAcceptOnlyItemsInBuySlot() {
        Assert.assertFalse(inventory.Accepts(new ItemStack(Items.DIAMOND)));
        inventory.setBoughtItem(new ItemStack(Items.DIAMOND, 10));
        Assert.assertTrue(inventory.Accepts(new ItemStack(Items.DIAMOND, 10)));
        Assert.assertTrue(inventory.Accepts(new ItemStack(Items.DIAMOND)));
        Assert.assertFalse(inventory.Accepts(new ItemStack(Items.APPLE)));
    }

    @Test
    public void ShouldVendOnlyItemsInSellSlot() {
        Assert.assertFalse(inventory.Vends(new ItemStack(Items.DIAMOND)));
        inventory.getSoldHandler().insertItem(new ItemStack(Items.DIAMOND, 10), false);
        Assert.assertTrue(inventory.Vends(new ItemStack(Items.DIAMOND, 10)));
        Assert.assertTrue(inventory.Vends(new ItemStack(Items.DIAMOND)));
        Assert.assertFalse(inventory.Vends(new ItemStack(Items.APPLE)));
    }

    @Test
    public void ShouldNotVendFilledBanknote() {
        Assert.assertFalse(inventory.Vends(EnderPayApiUtils.getBanknote(100)));
        inventory.getSoldHandler().insertItem(EnderPayApiUtils.getBanknote(100), false);
        Assert.assertFalse(inventory.Vends(EnderPayApiUtils.getBanknote(100)));
        Assert.assertFalse(inventory.Vends(EnderPayApiUtils.getBanknote(0)));
        Assert.assertFalse(inventory.Vends(new ItemStack(EnderPay.itemBlankBanknote)));
    }

    @Test
    public void ShouldNotAcceptFilledBanknote() {
        Assert.assertFalse(inventory.Accepts(EnderPayApiUtils.getBanknote(100)));
        inventory.setBoughtItem(EnderPayApiUtils.getBanknote(100));
        Assert.assertFalse(inventory.Accepts(EnderPayApiUtils.getBanknote(100)));
        Assert.assertFalse(inventory.Accepts(EnderPayApiUtils.getBanknote(100)));
        Assert.assertFalse(inventory.Accepts(new ItemStack(EnderPay.itemBlankBanknote)));
    }
}
