package com.taczlevel.gui;

import com.taczlevel.ModBlocks;
import com.taczlevel.ModMenuTypes;
import com.taczlevel.block.entity.GunUpgradeBlockEntity;
import com.taczlevel.config.ModConfig;
import com.taczlevel.data.GunLevelManager;
import com.taczlevel.event.GunEvents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class GunUpgradeMenu extends AbstractContainerMenu {
    private final GunUpgradeBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public GunUpgradeMenu(int id, Inventory inv, GunUpgradeBlockEntity be) {
        super(ModMenuTypes.GUN_UPGRADE_MENU.get(), id);
        this.blockEntity = be;
        this.access = ContainerLevelAccess.create(be.getLevel(), be.getBlockPos());

        addSlot(new SlotItemHandler(be.getItemHandler(), GunUpgradeBlockEntity.GUN_SLOT, 10, 18));
        addSlot(new SlotItemHandler(be.getItemHandler(), GunUpgradeBlockEntity.STAR_SLOT, 10, 44));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inv, j + i * 9 + 9, 10 + j * 18, 144 + i * 18));
            }
        }
        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inv, j, 10 + j * 18, 202));
        }
    }

    public GunUpgradeMenu(int id, Inventory inv, RegistryFriendlyByteBuf buf) {
        this(id, inv, (GunUpgradeBlockEntity) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public GunUpgradeBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public int getActiveSlot() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(GunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.getActiveSlot(gun);
    }

    public int getLevel(int optionIndex) {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(GunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.getLevel(gun, optionIndex);
    }

    public int getMaxLevel(int optionIndex) {
        return GunLevelManager.getMaxLevelForOption(optionIndex);
    }

    public boolean isActivated(int optionIndex) {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(GunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.isActivated(gun, optionIndex);
    }

    public boolean isGateUnlocked(int optionIndex) {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(GunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.isGateUnlocked(gun, optionIndex);
    }

    public boolean isAtGateLevel(int optionIndex) {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(GunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.isAtGateLevel(gun, optionIndex);
    }

    public int getExp() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(GunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.getExp(gun);
    }

    public int getExpToNext() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(GunUpgradeBlockEntity.GUN_SLOT);
        int activeSlot = GunLevelManager.getActiveSlot(gun);
        if (activeSlot >= 0) {
            return GunLevelManager.getExpToNextActive(gun);
        }
        return GunLevelManager.getExpToNext(gun);
    }

    public int getOverallLevel() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(GunUpgradeBlockEntity.GUN_SLOT);
        return GunLevelManager.getOverallLevel(gun);
    }

    public boolean hasGun() {
        ItemStack gun = blockEntity.getItemHandler().getStackInSlot(GunUpgradeBlockEntity.GUN_SLOT);
        return !gun.isEmpty() && GunEvents.isTaczGun(gun);
    }

    public boolean hasStar() {
        ItemStack star = blockEntity.getItemHandler().getStackInSlot(GunUpgradeBlockEntity.STAR_SLOT);
        return !star.isEmpty() && star.is(ModConfig.STATION.getActivationItem());
    }

    public boolean canActivate(int optionIndex) {
        return blockEntity.canActivate(optionIndex);
    }

    public boolean canUnlockGate(int optionIndex) {
        return blockEntity.canUnlockGate(optionIndex);
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
        return stillValid(access, player, ModBlocks.GUN_UPGRADE_BLOCK.get());
    }
}
