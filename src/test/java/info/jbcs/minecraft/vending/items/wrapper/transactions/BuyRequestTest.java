package info.jbcs.minecraft.vending.items.wrapper.transactions;

import com.kamildanak.minecraft.enderpay.EnderPay;
import com.kamildanak.minecraft.enderpay.Utils;
import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.item.ItemFilledBanknote;
import info.jbcs.minecraft.vending.EnderPayApiUtils;
import info.jbcs.minecraft.vending.TestUtils;
import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.settings.ISettings;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class BuyRequestTest {
    private VendingMachineInvWrapper inventory;

    @BeforeClass
    public static void BeforeClass() {
        Bootstrap.register();
        Vending.settings = mock(ISettings.class);
        when(Vending.settings.shouldCloseOnSoldOut()).thenReturn(false);
        when(Vending.settings.shouldCloseOnPartialSoldOut()).thenReturn(false);
        TestUtils.initializeEnderPay();
    }

    @Before
    public void Before() {
        inventory = TestUtils.createWrapper(true, true);
    }

    @Test
    public void ShouldNotVendWhenEmpty() {
        Assert.assertFalse(inventory.Accept(new BuyRequest(0, ItemStack.EMPTY, true))
                .isTransactionCompleted());
        Assert.assertFalse(inventory.Accept(new BuyRequest(0, ItemStack.EMPTY, false))
                .isTransactionCompleted());
        Assert.assertFalse(inventory.Accept(new BuyRequest(100, new ItemStack(Items.DIAMOND),
                true)).isTransactionCompleted());
        Assert.assertFalse(inventory.Accept(new BuyRequest(100, new ItemStack(Items.DIAMOND),
                false)).isTransactionCompleted());
    }

    @Test
    public void ShouldExtractItems() {
        inventory.getSoldHandler().insertItem(new ItemStack(Items.DIAMOND), false);
        inventory.getStorageHandler().insertItem(new ItemStack(Items.DIAMOND, 6), false);
        for (int i = 0; i < 7; i++) {
            Assert.assertTrue(inventory.Accept(
                    new BuyRequest(0, ItemStack.EMPTY, false)).isTransactionCompleted());
        }
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(0, ItemStack.EMPTY, false)).isTransactionCompleted());
    }

    private ItemStack setTagString(ItemStack stack, String key, String value) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        tag.setString(key, value);
        stack.setTagCompound(tag);
        return stack;
    }

    @Test
    public void ShouldVendItemsWithValidNBTTag() {
        ItemStack stack = setTagString(new ItemStack(Items.DIAMOND), "hello", "world");
        inventory.getSoldHandler().insertItem(stack, false);
        BuyResponse response = inventory.Accept(new BuyRequest(0, ItemStack.EMPTY, false));
        Assert.assertTrue(response.isTransactionCompleted());
        Assert.assertTrue(response.getChange().isEmpty());
        Assert.assertEquals(0, response.getCreditsToReturn());
        Assert.assertEquals(1, response.getReturnedStacks().size());
        Assert.assertEquals("world", response.getReturnedStacks().get(0).getTagCompound().getString("hello"));
    }

    @Test
    public void ShouldExtractItemsOnlyWithValidNBTTag() {
        ItemStack stack1 = setTagString(new ItemStack(Items.DIAMOND), "hello", "world");
        ItemStack stack2 = setTagString(new ItemStack(Items.DIAMOND), "hello", "world");
        ItemStack stack3 = setTagString(new ItemStack(Items.DIAMOND, 2), "hello", "world");
        ItemStack stack4 = setTagString(new ItemStack(Items.DIAMOND), "kitty", "world");
        inventory.getSoldHandler().insertItem(stack1, false);
        inventory.getStorageHandler().insertItem(stack2, false);
        inventory.getStorageHandler().insertItem(stack3, false);
        inventory.getStorageHandler().insertItem(stack4, false);
        for (int i = 0; i < 4; i++) {
            Assert.assertTrue(inventory.Accept(
                    new BuyRequest(0, ItemStack.EMPTY, false)).isTransactionCompleted());
        }
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(0, ItemStack.EMPTY, false)).isTransactionCompleted());
    }

    @Test
    public void ShouldAcceptOnlyItemsWithValidNBTTag() {
        ItemStack stack1 = setTagString(new ItemStack(Items.DIAMOND), "hello", "world");
        ItemStack stack2 = setTagString(new ItemStack(Items.DIAMOND), "hello", "world2");
        inventory.setBoughtItem(stack1.copy());
        Assert.assertTrue(inventory.Accept(
                new BuyRequest(0, stack1.copy(), false)).isTransactionCompleted());
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(0, stack2.copy(), false)).isTransactionCompleted());
    }

    @Test
    public void ShouldNotAcceptFilledBanknotes() {
        inventory.setBoughtItem(EnderPayApi.getBanknote(100));
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(0, EnderPayApi.getBanknote(100), false))
                .isTransactionCompleted());
    }

    @Test
    public void ShouldAcceptEmptyBanknotes() {
        inventory.setBoughtItem(new ItemStack(EnderPay.itemBlankBanknote));
        Assert.assertTrue(inventory.Accept(
                new BuyRequest(0, new ItemStack(EnderPay.itemBlankBanknote), false))
                .isTransactionCompleted());
    }

    @Test
    public void ShouldVendEmptyBanknotes() {
        inventory.getSoldHandler().insertItem(new ItemStack(EnderPay.itemBlankBanknote), false);
        inventory.getStorageHandler().insertItem(new ItemStack(EnderPay.itemBlankBanknote, 5), false);
        for (int i = 0; i < 6; i++) {
            Assert.assertTrue(inventory.Accept(
                    new BuyRequest(0, ItemStack.EMPTY, false)).isTransactionCompleted());
        }
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(0, ItemStack.EMPTY, false)).isTransactionCompleted());
    }

    @Test
    public void ShouldNotVendWhenThereIsNotEnoughSpaceInTheStorage() {
        inventory.getStorageHandler().insertItem(new ItemStack(Items.DIAMOND, Integer.MAX_VALUE), false);
        inventory.setBoughtItem(new ItemStack(Items.DIAMOND));
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND), false)).isTransactionCompleted());
        inventory.getStorageHandler().extractItem(new ItemStack(Items.DIAMOND), false);
        Assert.assertTrue(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND), true)).isTransactionCompleted());
        inventory.setBoughtItem(new ItemStack(Items.DIAMOND, 2));
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND, 2), true))
                .isTransactionCompleted());
    }

    @Test
    public void ShouldNotVendIfThereIsNoSpaceForFilledBanknote() {
        inventory.getStorageHandler().insertItem(new ItemStack(EnderPay.itemBlankBanknote, 2),
                false);
        inventory.getStorageHandler().insertItem(new ItemStack(Items.DIAMOND, Integer.MAX_VALUE), false);
        inventory.setBoughtItem(EnderPayApiUtils.getBanknote(100));
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(200, ItemStack.EMPTY, true))
                .isTransactionCompleted());
        inventory.getStorageHandler().extractItem(new ItemStack(EnderPay.itemBlankBanknote, 1), false);
        Assert.assertTrue(inventory.Accept(
                new BuyRequest(200, ItemStack.EMPTY, true))
                .isTransactionCompleted());
    }

    @Test
    public void ShouldNotVendIfThereIsNoBanknoteToFill() {
        inventory.setBoughtItem(EnderPayApiUtils.getBanknote(100));
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(200, ItemStack.EMPTY, true))
                .isTransactionCompleted());
    }

    @Test
    public void ShouldNotVendIfThereIsNoEnoughCreditsInTheStorage() {
        inventory.setBoughtItem(new ItemStack(Items.DIAMOND));
        ItemStack expiredBanknote = ItemFilledBanknote.getItemStack(100, true);
        NBTTagCompound tag = expiredBanknote.getTagCompound();
        Assert.assertNotNull(tag);
        tag.setLong("DateIssued", Utils.getCurrentDay() - 100);
        expiredBanknote.setTagCompound(tag);
        inventory.getSoldHandler().insertItem(expiredBanknote, false);
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND), true))
                .isTransactionCompleted());
        inventory.getStorageHandler().insertItem(EnderPayApiUtils.getBanknote(99), false);
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND), true))
                .isTransactionCompleted());
        inventory.getStorageHandler().insertItem(EnderPayApiUtils.getBanknote(0), false);
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND), true))
                .isTransactionCompleted());
        inventory.getStorageHandler().insertItem(EnderPayApiUtils.getBanknote(1), false);
        Assert.assertTrue(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND), true))
                .isTransactionCompleted());
    }

    @Test
    public void ShouldNotVendIfThereIsNoEnoughCreditsOffered() {
        inventory.setBoughtItem(EnderPayApiUtils.getBanknote(100));
        inventory.getStorageHandler().insertItem(new ItemStack(EnderPay.itemBlankBanknote, 1), false);
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND), true))
                .isTransactionCompleted());
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(99, new ItemStack(Items.DIAMOND), true))
                .isTransactionCompleted());
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(-1, new ItemStack(Items.DIAMOND), true))
                .isTransactionCompleted());
        Assert.assertTrue(inventory.Accept(
                new BuyRequest(100, new ItemStack(Items.DIAMOND), true))
                .isTransactionCompleted());
    }

    @Test
    public void ShouldNotVendIfThereInvalidItemOffered() {
        inventory.setBoughtItem(new ItemStack(Items.DIAMOND));
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.SADDLE), true))
                .isTransactionCompleted());
        Assert.assertFalse(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND_CHESTPLATE), true))
                .isTransactionCompleted());
        Assert.assertTrue(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND), true))
                .isTransactionCompleted());
    }

    @Test
    public void ShouldNotChangeMachineInventoryWhenSimulateIsTrue() {
        inventory.setBoughtItem(new ItemStack(Items.DIAMOND));
        inventory.getSoldHandler().insertItem(new ItemStack(Items.EMERALD), false);
        inventory.getStorageHandler().insertItem(new ItemStack(Items.EMERALD), false);
        Assert.assertTrue(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND), true))
                .isTransactionCompleted());
        Assert.assertTrue(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND), true))
                .isTransactionCompleted());
        Assert.assertTrue(inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND), true))
                .isTransactionCompleted());
        Assert.assertTrue(ItemStack.areItemStacksEqual(inventory.getStorageHandler().getStackInSlot(0),
                new ItemStack(Items.EMERALD)));
    }

    @Test
    public void ShouldReturnChange() {
        inventory.setBoughtItem(new ItemStack(Items.DIAMOND, 6));
        BuyResponse response = inventory.Accept(
                new BuyRequest(0, new ItemStack(Items.DIAMOND, 10), false));
        Assert.assertTrue(ItemStack.areItemStacksEqual(response.getChange(), new ItemStack(Items.DIAMOND, 4)));
    }

    @Test
    public void ShouldTakeOrGiveCredits() {
        inventory.getSoldHandler().insertItem(EnderPayApiUtils.getBanknote(100), false);
        Assert.assertEquals(inventory.soldCreditsSum(false), 100);
        BuyResponse response = inventory.Accept(new BuyRequest(0, ItemStack.EMPTY, false));
        Assert.assertEquals(0, inventory.soldCreditsSum(false));
        Assert.assertEquals(response.getCreditsToReturn(), 100);
        inventory.setBoughtItem(EnderPayApiUtils.getBanknote(60));
        inventory.getStorageHandler().insertItem(new ItemStack(EnderPay.itemBlankBanknote), false);
        response = inventory.Accept(new BuyRequest(100, ItemStack.EMPTY, false));
        Assert.assertEquals(-60, response.getCreditsToReturn());
    }

    @Test
    public void ShouldNotVendEmptyBanknoteThatIsAboutToBeFilled() {
        inventory.getSoldHandler().insertItem(new ItemStack(EnderPay.itemBlankBanknote, 1), false);
        inventory.getStorageHandler().insertItem(new ItemStack(EnderPay.itemBlankBanknote, 1), false);
        inventory.setBoughtItem(EnderPayApiUtils.getBanknote(100));
        BuyResponse response = inventory.Accept(new BuyRequest(100, ItemStack.EMPTY, false));
        BuyResponse response2 = inventory.Accept(new BuyRequest(100, ItemStack.EMPTY, false));
        Assert.assertTrue(response.isTransactionCompleted());
        Assert.assertTrue(response2.isTransactionCompleted());
    }
}
