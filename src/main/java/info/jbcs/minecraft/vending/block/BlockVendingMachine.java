package info.jbcs.minecraft.vending.block;

import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.NoSuchAccountException;
import com.kamildanak.minecraft.enderpay.api.NotABanknoteException;
import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.Vending;
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
import net.minecraft.item.Item;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

import static info.jbcs.minecraft.vending.General.countNotNull;

public class BlockVendingMachine extends BlockContainer {
    private static final PropertyEnum<EnumSupports> SUPPORT = PropertyEnum.create("support", EnumSupports.class);
    private boolean isAdvanced, isMultiple, isOpen;
    private String name;

    public BlockVendingMachine(boolean advanced, boolean multiple, String name) {
        super(Material.GLASS);
        setProperties();
        register(name);

        isAdvanced = advanced;
        isMultiple = multiple;
        isOpen = true;
    }

    private static boolean checkIfFits(ItemStack bought, ItemStack offered, ItemStack[] soldItems, TileEntityVendingMachine tileEntity) {
        if (Loader.isModLoaded("enderpay")) {
            if (bought == null && tileEntity.soldCreditsSum() > 0) return true;
            if (Utils.isBanknote(bought) && tileEntity.boughtCreditsSum() == 0)
                return countNotNull(soldItems) > 0;
            if (Utils.isFilledBanknote(bought))
                return (tileEntity.boughtCreditsSum() > 0 && tileEntity.hasPlaceForBanknote());
        }
        if (bought == null) return countNotNull(soldItems)>0;
        return tileEntity.doesStackFit(bought) &&
                offered != null &&
                bought.getItem() == offered.getItem() &&
                bought.getItemDamage() == offered.getItemDamage() &&
                offered.stackSize >= bought.stackSize &&
                Objects.equals(bought.getTagCompound(), offered.getTagCompound());
    }

    @Optional.Method(modid = "enderpay")
    private static void takeCredits(EntityPlayer entityplayer, TileEntityVendingMachine tileEntity, ItemStack bought) {
        try {
            long amount = EnderPayApi.getBanknoteOriginalValue(bought);
            EnderPayApi.addToBalance(entityplayer.getUniqueID(), -amount);
            if (tileEntity.isInfinite()) return;
            for (int i = 0; i < 9; i++) {
                ItemStack itemStack = tileEntity.inventory.getStackInSlot(i);
                if (itemStack == null) {
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
                    tileEntity.inventory.setInventorySlotContents(i, null);
                }
            }
            if (inventorySum >= soldAmount) {
                for (int i = 0; i < 9; i++) {
                    if (Objects.equals(tileEntity.inventory.getStackInSlot(i), null)) {
                        tileEntity.inventory.setInventorySlotContents(i,
                                inventorySum - soldAmount > 0 ? EnderPayApi.getBanknote(inventorySum - soldAmount) : null);
                        break;
                    }
                }
            } else {
                if (tileEntity.isMultiple()) {
                    for (int i = 9; i < 13; i++) {
                        if (Utils.isBanknote(tileEntity.inventory.getStackInSlot(i))) {
                            tileEntity.inventory.setInventorySlotContents(i, null);
                        }
                    }
                    for (int i = 9; i < 13; i++) {
                        if (Objects.equals(tileEntity.inventory.getStackInSlot(i), null)) {
                            tileEntity.inventory.setInventorySlotContents(i, EnderPayApi.getBanknote(totalSum - soldAmount));
                            break;
                        }
                    }
                } else {
                    tileEntity.inventory.setInventorySlotContents(9,
                            totalSum - soldAmount > 0 ? EnderPayApi.getBanknote(totalSum - soldAmount) : null);
                }
            }
        } catch (NoSuchAccountException e) {
            e.printStackTrace();
        }
    }

    @Optional.Method(modid = "enderpay")
    private static boolean checkIfPlayerHasEnoughtCredits(EntityPlayer entityPlayer, TileEntityVendingMachine tileEntity,
                                                          ItemStack bought) {
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

    private void register(String name) {
        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(name);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SUPPORT, EnumSupports.STONE));
        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this).setRegistryName(this.getRegistryName()).setHasSubtypes(true).setMaxDamage(0));
    }

    private void vend(World world, BlockPos blockPos, EntityPlayer entityplayer) {
        TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getTileEntity(blockPos);
        if (tileEntity == null) return;
        if (!tileEntity.isOpen()) {
            world.playSound(entityplayer, blockPos, Vending.sound_forbidden, SoundCategory.MASTER, 0.3f, 0.6f);
            return;
        }

        ItemStack[] soldItems = tileEntity.getSoldItems();
        ItemStack bought = tileEntity.getBoughtItems()[0];
        ItemStack offered = entityplayer.inventory.getCurrentItem();

        boolean playerHasEnoughtCredits = true;
        boolean machineHasEnoughtCredits = true;
        if (Loader.isModLoaded("enderpay")) {
            for (int i = 0; i < soldItems.length; i++) {
                if (Utils.isBanknote(soldItems[i])) soldItems[i] = null;
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
                if (tileEntity.getBoughtItems().length > 0 &&
                        Utils.isBanknote(tileEntity.getBoughtItems()[0]))
                    takeItems = false;
            }
            if(takeItems) takeItems(entityplayer, tileEntity, bought, offered);

            if (!tileEntity.isInfinite())
                tileEntity.inventory.onInventoryChanged();
        }
        world.playSound(entityplayer, blockPos, fits ? Vending.sound_processed : Vending.sound_forbidden, SoundCategory.MASTER, 0.3f, 0.6f);
    }

    private boolean checkIfMachineHasEnoughtCredits(TileEntityVendingMachine tileEntity) {
        if (tileEntity.isInfinite() || !Loader.isModLoaded("enderpay")) return true;
        long soldSum = tileEntity.soldCreditsSum();
        long realTotalSum = tileEntity.realTotalCreditsSum();
        return soldSum <= realTotalSum;
    }

    private void takeItems(EntityPlayer entityplayer, TileEntityVendingMachine tileEntity, ItemStack bought, ItemStack offered) {
        if (offered != null) {
            ItemStack paid = null;
            if(bought!=null)
            paid = offered.splitStack(bought.stackSize);
            if (offered.stackSize == 0) {
                entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
            }

            if (!tileEntity.isInfinite() && paid!=null)
                tileEntity.inventory.addItemStackToInventory(paid, 0, 8);
        }
    }

    private void giveItems(ItemStack[] soldItems, EntityPlayer entityPlayer, World world, BlockPos blockPos,
                           TileEntityVendingMachine tileEntity) {
        if (countNotNull(soldItems) == 0) return;

        ItemStack[] soldItemsOld = new ItemStack[soldItems.length];
        if (Vending.close_on_partial_sold_out)
            for (int i = 0; i < soldItems.length; i++)
                if (soldItems[i] != null)
                    soldItemsOld[i] = soldItems[i].copy();

        for (ItemStack sold : soldItems) {
            if (sold == null) continue;
            NBTTagCompound tag = new NBTTagCompound();
            sold.writeToNBT(tag);
            ItemStack vended = ItemStack.loadItemStackFromNBT(tag);

            if (!tileEntity.isInfinite()) {
                tileEntity.inventory.takeItems(sold, sold.getItemDamage(), sold.stackSize);
            }

            boolean spawnItem = true;
            if (Vending.transfer_to_inventory) spawnItem = !entityPlayer.inventory.addItemStackToInventory(vended);
            if (spawnItem) Utils.throwItemAtPlayer(entityPlayer, world, blockPos, vended);
        }
        if (Vending.close_on_sold_out && countNotNull(tileEntity.getSoldItems()) == 0)
            tileEntity.setOpen(false);
        if (Vending.close_on_partial_sold_out)
            closeIfSoldChanged(tileEntity, soldItems, soldItemsOld);
    }

    private void closeIfSoldChanged(TileEntityVendingMachine tileEntity,
                                    ItemStack[] soldItems, ItemStack[] soldItemsOld) {
        for (int i = 0; i < soldItemsOld.length; i++) {
            //System.out.println(soldItemsOld[i] != null ? soldItemsOld[i].toString() : "null");
            //System.out.println(tileEntity.getSoldItems()[i] != null ? soldItemsOld[i].toString() : "null");
            if (soldItemsOld[i] == null && tileEntity.getSoldItems()[i] == null) continue;
            if (soldItemsOld[i] == null || tileEntity.getSoldItems()[i] == null)
                tileEntity.setOpen(false);
            if (soldItemsOld[i].stackSize != soldItems[i].stackSize) tileEntity.setOpen(false);
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

        world.playSound(entityplayer, blockPos, Vending.sound_processed, SoundCategory.MASTER, 0.3f, 0.6f);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState state, EntityPlayer entityPlayer, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getTileEntity(blockPos);
        if (tileEntity == null)
            return false;

        if (entityPlayer.inventory.getCurrentItem() != null && entityPlayer.inventory.getCurrentItem().getItem() == Vending.itemWrench) {
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
        if (!Vending.block_placing_next_to_doors) return true;
        for (int x = -1; x < 2; x++)
            for (int z = -1; z < 2; z++)
                if (worldIn.getBlockState(pos.add(x, 0, z)).getBlock() instanceof BlockDoor) return false;
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos blockPos, IBlockState state, EntityLivingBase entityLiving, ItemStack stack) {
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
            if (itemstack == null)
                continue;
            if (l == 10 && tileEntityChest.isAdvanced())
                continue;

            float f = world.rand.nextFloat() * 0.8F + 0.1F;
            float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
            float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
            while (itemstack.stackSize > 0) {
                int i1 = world.rand.nextInt(21) + 10;
                if (i1 > itemstack.stackSize) {
                    i1 = itemstack.stackSize;
                }

                itemstack.stackSize -= i1;
                NBTTagCompound tag = new NBTTagCompound();
                itemstack.writeToNBT(tag);
                ItemStack toSpawn = ItemStack.loadItemStackFromNBT(tag);
                toSpawn.stackSize = i1;
                EntityItem entityitem = new EntityItem(world, blockPos.getX() + f, blockPos.getY() + f1, blockPos.getZ() + f2, toSpawn);
                float f3 = 0.05F;
                entityitem.motionX = (float) world.rand.nextGaussian() * f3;
                entityitem.motionY = (float) world.rand.nextGaussian() * f3 + 0.2F;
                entityitem.motionZ = (float) world.rand.nextGaussian() * f3;
                world.spawnEntityInWorld(entityitem);
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
    public void getSubBlocks(@Nonnull Item item, CreativeTabs par2CreativeTabs, List<ItemStack> list) {
        for (int i = 0; i < EnumSupports.length; ++i) list.add(new ItemStack(item, 1, i));
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
