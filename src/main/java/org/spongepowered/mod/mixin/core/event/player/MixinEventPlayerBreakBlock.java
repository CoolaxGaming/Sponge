/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.mod.mixin.core.event.player;

import com.google.common.base.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.block.BlockBreakEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.player.PlayerBreakBlockEvent;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.Location;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.mod.interfaces.IMixinEvent;
import org.spongepowered.mod.mixin.core.event.block.MixinEventBlock;

@NonnullByDefault
@Mixin(value = BlockEvent.BreakEvent.class, remap = false)
public abstract class MixinEventPlayerBreakBlock extends MixinEventBlock implements PlayerBreakBlockEvent {

    private net.minecraftforge.common.util.BlockSnapshot blockSnapshot;

    @Shadow private int exp;

    @Shadow private EntityPlayer player;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(World world, BlockPos pos, IBlockState state, EntityPlayer player, CallbackInfo ci) {
        this.blockSnapshot = new net.minecraftforge.common.util.BlockSnapshot(world, pos, state);
        if (player instanceof FakePlayer) {
            setSpongeEvent(SpongeEventFactory.createBlockBreak(getGame(), getCause().orNull(), getBlock(), getReplacementBlock(), getExp()));
        }
    }

    @Override
    public Player getEntity() {
        return (Player) this.player;
    }

    @Override
    public Player getUser() {
        return (Player) this.player;
    }

    @Override
    public BlockSnapshot getReplacementBlock() {
        if (this.spongeEvent != null) {
            return ((BlockBreakEvent) this.spongeEvent).getReplacementBlock();
        }
        return (BlockSnapshot) this.blockSnapshot;
    }

    @Override
    public Optional<Cause> getCause() {
        return Optional.fromNullable(new Cause(null, this.player, null));
    }

    @Override
    public int getExp() {
        if (this.spongeEvent != null) {
            return ((BlockBreakEvent) this.spongeEvent).getExp();
        }
        return this.exp;
    }

    @Override
    public void setExp(int exp) {
        if (this.spongeEvent != null) {
            ((BlockBreakEvent) this.spongeEvent).setExp(exp);
        }
        this.exp = exp;
    }

    @Inject(method = "getExpToDrop", at = @At("HEAD"), cancellable = true)
    public void onGetExpToDrop(CallbackInfoReturnable<Integer> cir) {
        if (this.spongeEvent != null) {
            cir.setReturnValue(((BlockBreakEvent) this.spongeEvent).getExp());
        }
    }

    @Inject(method = "setExpToDrop", at = @At("HEAD"))
    public void onSetExpToDrop(int exp, CallbackInfo ci) {
        if (this.spongeEvent != null) {
            ((BlockBreakEvent) this.spongeEvent).setExp(exp);
        }
    }

    @SuppressWarnings("unused")
    private static BlockEvent.BreakEvent fromSpongeEvent(PlayerBreakBlockEvent spongeEvent) {
        Location location = spongeEvent.getBlock();
        World world = (World) spongeEvent.getBlock().getExtent();
        BlockPos pos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos), (EntityPlayer) spongeEvent.getEntity());
        event.setExpToDrop(spongeEvent.getExp());

        ((IMixinEvent) event).setSpongeEvent(spongeEvent);
        return event;
    }
}
