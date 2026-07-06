package com.taczlevel.block.entity;

import com.taczlevel.ModBlockEntities;
import com.taczlevel.config.ModConfig;
import com.taczlevel.data.GunLevelManager;
import com.taczlevel.event.GunEvents;
import com.taczlevel.gui.CreativeGunUpgradeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreativeGunUpgradeBlockEntity extends BlockEntity implements MenuProvider {
    public static final int GUN_SLOT = 0;
    public static final int STAR_SLOT = 1;

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case GUN_SLOT -> GunEvents.isTaczGun(stack);
                case STAR_SLOT -> true;
                default -> false;
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public CreativeGunUpgradeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_GUN_UPGRADE_BE.get(), pos, state);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public boolean performUpgrade(int optionIndex) {
        if (level == null || level.isClientSide) return false;

        ItemStack gun = itemHandler.getStackInSlot(GUN_SLOT);

        if (gun.isEmpty() || !GunEvents.isTaczGun(gun)) return false;

        if (GunLevelManager.isActivated(gun, optionIndex)) return false;

        boolean success = switch (optionIndex) {
            case 0 -> GunLevelManager.upgradeReload(gun);
            case 1 -> GunLevelManager.upgradeRecoil(gun);
            case 2 -> GunLevelManager.upgradePen(gun);
            case 3 -> GunLevelManager.upgradeFireRate(gun);
            default -> false;
        };

        if (success) {
            setChanged();
            if (ModConfig.SOUND.enabled.get()) {
                float vol = ModConfig.SOUND.volume.get().floatValue();
                float pitch = ModConfig.SOUND.pitch.get().floatValue();
                level.playSound(null, worldPosition, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, vol, pitch);
            }
        }

        return success;
    }

    public boolean canUpgrade(int optionIndex) {
        ItemStack gun = itemHandler.getStackInSlot(GUN_SLOT);

        if (gun.isEmpty() || !GunEvents.isTaczGun(gun)) return false;
        if (GunLevelManager.isActivated(gun, optionIndex)) return false;

        return GunLevelManager.getLevel(gun, optionIndex) < GunLevelManager.getMaxLevelForOption(optionIndex);
    }

    public void drops() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && level != null) {
                Block.popResource(level, worldPosition, stack);
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.taczlevel.creative_gun_upgrade");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new CreativeGunUpgradeMenu(id, inv, this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
    }
}
