package info.jbcs.minecraft.vending.block;

import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.NoSuchAccountException;
import com.kamildanak.minecraft.enderpay.api.NotABanknoteException;
import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.init.VendingItems;
import info.jbcs.minecraft.vending.init.VendingSoundEvents;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Objects;

import static info.jbcs.minecraft.vending.General.countNotNull;

public class BlockVendingMachine extends BlockContainer {
    private static final PropertyEnum<EnumSupports> SUPPORT =
            PropertyEnum.create("support", EnumSupports.class);
    private boolean isAdvanced, isMultiple, isOpen;
    private String name;

    public BlockVendingMachine(boolean advanced, boolean multiple, String name) {
        super(Material.GLASS);
        setProperties();

        isAdvanced = advanced;
        isMultiple = multiple;
        isOpen = true;

        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(name);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SUPPORT, EnumSupports.STONE));
    }

    private static boolean checkIfFits(@Nonnull ItemStack bought, @Nonnull ItemStack offered, NonNullList<ItemStack> soldItems, TileEntityVendingMachine tileEntity) {
        if (Loader.isModLoaded("enderpay")) {
            if (bought.isEmpty() && tileEntity.soldCreditsSum() > 0) return true;
            if (Utils.isBanknote(bought) && tileEntity.boughtCreditsSum() == 0)
                return countNotNull(soldItems) > 0;
            if (Utils.isFilledBanknote(bought))
                return (tileEntity.boughtCreditsSum() > 0 && tileEntity.hasPlaceForBanknote());
        }
        if (bought.isEmpty()) return countNotNull(soldItems)>0;
        return tileEntity.doesStackFit(bought) &&
                !offered.isEmpty() &&
                bought.getItem() == offered.getItem() &&
                bought.getItemDamage() == offered.getItemDamage() &&
                offered.getCount() >= bought.getCount() &&
                Objects.equals(bought.getTagCompound(), offered.getTagCompound());
    }

    @Optional.Method(modid = "enderpay")
    private static void takeCredits(EntityPlayer entityplayer, TileEntityVendingMachine tileEntity, @Nonnull ItemStack bought) {
        try {
            long amount = EnderPayApi.getBanknoteOriginalValue(bought);
            EnderPayApi.addToBalance(entityplayer.getUniqueID(), -amount);
            if (tileEntity.isInfinite()) return;
            for (int i = 0; i < 9; i++) {
                ItemStack itemStack = tileEntity.inventory.getStackInSlot(i);
                if (itemStack.isEmpty()) {
                    tileEntity.inventory.setInventorySlotContents(i, EnderPayApi.getBanknote(amount));
                    break;
                }
                if (Utils.isBanknote(itemStack)) {
                    tileEntity.inventory.setInventorySlotContents(i,
                            EnderPayApi.getBanknote(amount + EnderPayApi.getBanknoteCurrentValue(itemStack)));
                    break;
                }
            }
        } catch (NoSuchAccountException | NotABanknoteException ignored) {
        }
    }

    @Optional.Method(modid = "enderpay")
    private static void giveCredits(EntityPlayer entityplayer, TileEntityVendingMachine tileEntity) {
        try {
            long soldAmount = tileEntity.soldCreditsSum();
            EnderPayApi.addToBalance(entityplayer.getUniqueID(), soldAmount);
            if (tileEntity.isInfinite()) return;
            long inventorySum = tileEntity.realInventoryCreditsSum();
            long totalSum = tileEntity.realTotalCreditsSum();
            for (int i = 0; i < 9; i++) {
                if (Utils.isBanknote(tileEntity.inventory.getStackInSlot(i))) {
                    tileEntity.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
            if (inventorySum >= soldAmount) {
                for (int i = 0; i < 9; i++) {
                    if (tileEntity.inventory.getStackInSlot(i).isEmpty()) {
                        tileEntity.inventory.setInventorySlotContents(i,
                                inventorySum - soldAmount > 0 ? EnderPayApi.getBanknote(inventorySum - soldAmount) : ItemStack.EMPTY);
                        break;
                    }
                }
            } else {
                if (tileEntity.isMultiple()) {
                    for (int i = 9; i < 13; i++) {
                        if (Utils.isBanknote(tileEntity.inventory.getStackInSlot(i))) {
                            tileEntity.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                        }
                    }
                    for (int i = 9; i < 13; i++) {
                        if (tileEntity.inventory.getStackInSlot(i).isEmpty()) {
                            tileEntity.inventory.setInventorySlotContents(i, EnderPayApi.getBanknote(totalSum - soldAmount));
                            break;
                        }
                    }
                } else {
                    tileEntity.inventory.setInventorySlotContents(9,
                            totalSum - soldAmount > 0 ? EnderPayApi.getBanknote(totalSum - soldAmount) : ItemStack.EMPTY);
                }
            }
        } catch (NoSuchAccountException e) {
            e.printStackTrace();
        }
    }

    @Optional.Method(modid = "enderpay")
    private static boolean checkIfPlayerHasEnoughtCredits(EntityPlayer entityPlayer, TileEntityVendingMachine tileEntity,
                                                          @Nonnull ItemStack bought) {
        if (Loader.isModLoaded("enderpay")) {
            if (!Utils.isBanknote(bought)) return true;
            try {
                return EnderPayApi.getBalance(entityPlayer.getUniqueID()) >= tileEntity.boughtCreditsSum();
            } catch (NoSuchAccountException ignored) {
                return false;
            }
        }
        return true;
    }

    private void setProperties() {
        setSoundType(SoundType.GLASS);
        setHardness(0.3F);
        setResistance(6000000.0F);
        setBlockUnbreakable();
        setCreativeTab(Vending.tabVending);
    }

    private void vend(World world, BlockPos blockPos, EntityPlayer entityplayer) {
        TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getTileEntity(blockPos);
        if (tileEntity == null) return;
        if (!tileEntity.isOpen()) {
            world.playSound(entityplayer, blockPos, VendingSoundEvents.FORBIDDEN, SoundCategory.MASTER, 0.3f, 0.6f);
            return;
        }

        NonNullList<ItemStack> soldItems = tileEntity.getSoldItems();
        ItemStack bought = tileEntity.getBoughtItems().get(0);
        ItemStack offered = entityplayer.inventory.getCurrentItem();

        boolean playerHasEnoughtCredits = true;
        boolean machineHasEnoughtCredits = true;
        if (Loader.isModLoaded("enderpay")) {
            for (int i = 0; i < soldItems.size(); i++) {
                if (Utils.isBanknote(soldItems.get(i))) soldItems.set(i,ItemStack.EMPTY);
            }
            playerHasEnoughtCredits = checkIfPlayerHasEnoughtCredits(entityplayer, tileEntity, bought);
            machineHasEnoughtCredits = checkIfMachineHasEnoughtCredits(tileEntity);
        }


        boolean fits = checkIfFits(bought, offered, soldItems, tileEntity) && playerHasEnoughtCredits && machineHasEnoughtCredits;
        if (fits && !world.isRemote) {
            giveItems(soldItems, entityplayer, world, blockPos, tileEntity);
            boolean takeItems = true;
            if (Loader.isModLoaded("enderpay")) {
                if (tileEntity.soldCreditsSum() > 0)
                    giveCredits(entityplayer, tileEntity);
                if (tileEntity.boughtCreditsSum() > 0)
                    takeCredits(entityplayer, tileEntity, bought);
                if (tileEntity.getBoughtItems().size() > 0 &&
                        Utils.isBanknote(tileEntity.getBoughtItems().get(0)))
                    takeItems = false;
            }
            if(takeItems) takeItems(entityplayer, tileEntity, bought, offered);

            if (!tileEntity.isInfinite())
                tileEntity.inventory.onInventoryChanged();
        }
        world.playSound(entityplayer, blockPos, fits ? VendingSoundEvents.PROCESSED : VendingSoundEvents.FORBIDDEN,
                SoundCategory.MASTER, 0.3f, 0.6f);
    }

    private boolean checkIfMachineHasEnoughtCredits(TileEntityVendingMachine tileEntity) {
        if (tileEntity.isInfinite() || !Loader.isModLoaded("enderpay")) return true;
        long soldSum = tileEntity.soldCreditsSum();
        long realTotalSum = tileEntity.realTotalCreditsSum();
        return soldSum <= realTotalSum;
    }

    private void takeItems(EntityPlayer entityplayer, TileEntityVendingMachine tileEntity, @Nonnull ItemStack bought, @Nonnull ItemStack offered) {
        if (!offered.isEmpty()) {
            ItemStack paid = offered.splitStack(bought.getCount());
            if (offered.getCount() == 0) {
                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem,ItemStack.EMPTY);
            }

            if (!tileEntity.isInfinite())
                tileEntity.inventory.addItemStackToInventory(paid, 0, 8);
        }
    }

    private void giveItems(NonNullList<ItemStack> soldItems, EntityPlayer entityPlayer, World world, BlockPos blockPos,
                           TileEntityVendingMachine tileEntity) {
        if (countNotNull(soldItems) == 0) return;

        NonNullList<ItemStack> soldItemsOld = NonNullList.withSize(soldItems.size(), ItemStack.EMPTY);
        if (Vending.settings.shouldCloseOnPartialSoldOut())
            for (int i = 0; i < soldItems.size(); i++)
                if (!soldItems.get(i).isEmpty())
                    soldItemsOld.set(i,soldItems.get(i).copy());

        for (ItemStack sold : soldItems) {
            if (sold.isEmpty()) continue;
            NBTTagCompound tag = new NBTTagCompound();
            sold.writeToNBT(tag);
            ItemStack vended = new ItemStack(tag);

            if (!tileEntity.isInfinite()) {
                tileEntity.inventory.takeItems(sold, sold.getItemDamage(), sold.getCount());
            }

            boolean spawnItem = true;
            if (Vending.settings.shouldTransferToInventory()) spawnItem = !entityPlayer.inventory.addItemStackToInventory(vended);
            if (spawnItem) Utils.throwItemAtPlayer(entityPlayer, world, blockPos, vended);
        }
        if (Vending.settings.shouldCloseOnSoldOut() && countNotNull(tileEntity.getSoldItems()) == 0)
            tileEntity.setOpen(false);
        if (Vending.settings.shouldCloseOnPartialSoldOut())
            closeIfSoldChanged(tileEntity, soldItems, soldItemsOld);
    }

    private void closeIfSoldChanged(TileEntityVendingMachine tileEntity,
                                    NonNullList<ItemStack> soldItems, NonNullList<ItemStack> soldItemsOld) {
        for (int i = 0; i < soldItemsOld.size(); i++) {
            //System.out.println(!soldItemsOld.get(i).isEmpty() ? soldItemsOld.get(i).toString() : "null");
            //System.out.println(!tileEntity.getSoldItems().get(i).isEmpty() ? soldItemsOld.get(i).toString() : "null");
            if (soldItemsOld.get(i).isEmpty() && tileEntity.getSoldItems().get(i).isEmpty()) continue;
            if (soldItemsOld.get(i).isEmpty() || tileEntity.getSoldItems().get(i).isEmpty())
                tileEntity.setOpen(false);
            if (soldItemsOld.get(i).getCount() != soldItems.get(i).getCount()) tileEntity.setOpen(false);
        }
    }

    @Override
    public void onBlockClicked(World world, BlockPos blockPos, EntityPlayer entityplayer) {
        TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getTileEntity(blockPos);
        if (tileEntity == null)
            return;

        if (!entityplayer.getDisplayNameString().equals(tileEntity.getOwnerName()) || !tileEntity.inventory.isEmpty()) {
            vend(world, blockPos, entityplayer);
            return;
        }

        dropBlockAsItem(world, blockPos, world.getBlockState(blockPos), 0);
        world.setBlockToAir(blockPos);

        world.playSound(entityplayer, blockPos, VendingSoundEvents.PROCESSED,
                SoundCategory.MASTER, 0.3f, 0.6f);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState state, EntityPlayer entityPlayer, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getTileEntity(blockPos);
        if (tileEntity == null)
            return false;

        if (!entityPlayer.inventory.getCurrentItem().isEmpty() && entityPlayer.inventory.getCurrentItem().getItem() == VendingItems.ITEM_WRENCH) {
            Vending.guiWrench.open(entityPlayer, world, blockPos);
            return true;
        }

        if (entityPlayer.getDisplayNameString().equals(tileEntity.getOwnerName()) && !entityPlayer.isSneaking()) {
            Vending.guiVending.open(entityPlayer, world, blockPos);
            return true;
        }

        if (entityPlayer.capabilities.isCreativeMode && !entityPlayer.isSneaking()) {
            Vending.guiVending.open(entityPlayer, world, blockPos);
            return true;
        }

        vend(world, blockPos, entityPlayer);
        tileEntity.markDirty();
        tileEntity.markBlockForUpdate(blockPos);

        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos) {
        if (!worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)) return false;
        if (!Vending.settings.isPlacingNextToDoorsBlocked()) return true;
        for (int x = -1; x < 2; x++)
            for (int z = -1; z < 2; z++)
                if (worldIn.getBlockState(pos.add(x, 0, z)).getBlock() instanceof BlockDoor) return false;
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos blockPos, IBlockState state, EntityLivingBase entityLiving, @Nonnull ItemStack stack) {
        world.setBlockState(blockPos, getStateFromMeta(stack.getMetadata()));
        TileEntityVendingMachine e = new TileEntityVendingMachine(isAdvanced, false, isMultiple);
        e.setOpen(isOpen);

        if (entityLiving != null) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            e.setOwnerName(player.getDisplayNameString());
            world.setTileEntity(blockPos, e);
        }
    }

    @Override
    @Nonnull
    public TileEntity createNewTileEntity(@Nonnull World var1, int metadata) {
        TileEntityVendingMachine e = new TileEntityVendingMachine(isAdvanced, false, isMultiple);
        e.setOpen(isOpen);

        return e;
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos blockPos, @Nonnull IBlockState state) {
        TileEntityVendingMachine tileEntityChest = (TileEntityVendingMachine) world.getTileEntity(blockPos);

        if (tileEntityChest == null)
            return;

        for (int l = 0; l < tileEntityChest.getSizeInventory(); l++) {
            ItemStack itemstack = tileEntityChest.getStackInSlot(l);
            if (itemstack.isEmpty())
                continue;
            if (l == 10 && tileEntityChest.isAdvanced())
                continue;

            float f = world.rand.nextFloat() * 0.8F + 0.1F;
            float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
            float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
            while (itemstack.getCount() > 0) {
                int i1 = world.rand.nextInt(21) + 10;
                if (i1 > itemstack.getCount()) {
                    i1 = itemstack.getCount();
                }

                itemstack.setCount(itemstack.getCount()-i1);
                NBTTagCompound tag = new NBTTagCompound();
                itemstack.writeToNBT(tag);
                ItemStack toSpawn = new ItemStack(tag);
                toSpawn.setCount(i1);
                EntityItem entityitem = new EntityItem(world, blockPos.getX() + f, blockPos.getY() + f1, blockPos.getZ() + f2, toSpawn);
                float f3 = 0.05F;
                entityitem.motionX = (float) world.rand.nextGaussian() * f3;
                entityitem.motionY = (float) world.rand.nextGaussian() * f3 + 0.2F;
                entityitem.motionZ = (float) world.rand.nextGaussian() * f3;
                world.spawnEntity(entityitem);
            }
        }

        super.breakBlock(world, blockPos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tabs, NonNullList<ItemStack> list) {
        for (int i = 0; i < EnumSupports.length; ++i) list.add(new ItemStack(this, 1, i));
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(SUPPORT, EnumSupports.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(SUPPORT).getMetadata();
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SUPPORT);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0625f, 0.125f, 0.0625f, 0.9375f, 0.9375f, 0.9375f);
    }

    @Override
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Nonnull
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public String getName() {
        return name;
    }

    @Override
    @Nonnull
    public String getLocalizedName() {
        return net.minecraft.client.resources.I18n.format("tile." + getName() + ".name").trim();
    }
}
