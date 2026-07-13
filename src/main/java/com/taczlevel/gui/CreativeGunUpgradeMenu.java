package com.taczlevel.gui;

import com.taczlevel.ModBlocks;
import com.taczlevel.ModMenuTypes;
import com.taczlevel.block.entity.CreativeGunUpgradeBlockEntity;
import com.taczlevel.config.ModConfig;
import com.taczlevel.data.GunLevelManager;
import com.taczlevel.event.GunEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class CreativeGunUpgradeMenu extends AbstractContainerMenu {
    private final CreativeGunUpgradeBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public CreativeGunUpgradeMenu(int id, Inventory inv, CreativeGunUpgradeBlockEntity be) {
        super(ModMenuTypes.CREATIVE_GUN_UPGRADE_MENU.get(), id);
        this.blockEntity = be;
        this.access = ContainerLevelAccess.create(be.getLevel(), be.getBlockPos());

        addSlot(new SlotItemHandler(be.getItemHandler(), CreativeGunUpgradeBlockEntity.GUN_SLOT, 10, 18));
        addSlot(new SlotItemHandler(be.getItemHandler(), CreativeGunUpgradeBlockEntity.STAR_SLOT, 10, 44));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inv, j + i * 9 + 9, 10 + j * 18, 144 + i * 18));
            }
        }
        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inv, j, 10 + j * 18, 202));
        }
    }

    public CreativeGunUpgradeMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, (CreativeGunUpgradeBlockEntity) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public CreativeGunUpgradeBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public boolean canUpgrade(int optionIndex) {
        return blockEntity.canUpgrade(optionIndex);
    }

    public int getReloadLevel() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(CreativeGunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.getReloadLevel(gun);
    }

    public int getRecoilLevel() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(CreativeGunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.getRecoilLevel(gun);
    }

    public int getPenLevel() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(CreativeGunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.getPenLevel(gun);
    }

    public int getFireRateLevel() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(CreativeGunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.getFireRateLevel(gun);
    }

    public int getDummyAmmoLevel() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(CreativeGunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.getDummyAmmoLevel(gun);
    }

    public int getExp() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(CreativeGunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.getExp(gun);
    }

    public int getExpToNext() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(CreativeGunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.getExpToNext(gun);
    }

    public int getOverallLevel() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(CreativeGunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.getOverallLevel(gun);
    }

    public boolean hasGun() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(CreativeGunUpgradeBlockEntity.GUN_SLOT);
        return !gun.isEmpty() && GunEvents.isTaczGun(gun);
    }

    public boolean hasStar() {
        ItemStack star = blockEntity.getItemHandler().getStackInSlot(CreativeGunUpgradeBlockEntity.STAR_SLOT);
        return !star.isEmpty() && star.is(ModConfig.STATION.getActivationItem());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index == 0 || index == 1) {
                if (!this.moveItemStackTo(stack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stack.is(ModConfig.STATION.getActivationItem())) {
                    if (!this.moveItemStackTo(stack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.CREATIVE_GUN_UPGRADE_BLOCK.get());
    }
}
