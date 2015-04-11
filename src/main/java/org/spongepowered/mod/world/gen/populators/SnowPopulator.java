/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered.org <http://www.spongepowered.org>
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
package org.spongepowered.mod.world.gen.populators;

import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ICE;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.event.terraingen.TerrainGen;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.mod.interfaces.IFlaggedPopulator;

import java.util.List;
import java.util.Random;

public class SnowPopulator implements IFlaggedPopulator {

    @Override
    public void populate(IChunkProvider provider, Chunk chunk, Random rand, List<String> flags) {
        BlockPos blockpos = new BlockPos(chunk.getBlockMin().getX() + 8, 0, chunk.getBlockMin().getZ() + 8);
        int x = chunk.getPosition().getX();
        int z = chunk.getPosition().getZ();
        World worldObj = (World) chunk.getWorld();
        boolean flag = flags.contains("VILLAGE");

        if (TerrainGen.populate(provider, worldObj, rand, x, z, flag, ICE)) {
            for (int k1 = 0; k1 < 16; ++k1) {
                for (int l1 = 0; l1 < 16; ++l1) {
                    BlockPos blockpos1 = worldObj.getPrecipitationHeight(blockpos.add(k1, 0, l1));
                    BlockPos blockpos2 = blockpos1.down();

                    if (worldObj.canBlockFreezeWater(blockpos2)) {
                        worldObj.setBlockState(blockpos2, Blocks.ice.getDefaultState(), 2);
                    }

                    if (worldObj.canSnowAt(blockpos1, true)) {
                        worldObj.setBlockState(blockpos1, Blocks.snow_layer.getDefaultState(), 2);
                    }
                }
            }
        }
    }

    /**
     * If called by a system which does not recognize {@link IFlaggedPopulator}s
     * then we have to skip calling the forge event as we do not have the chunk
     * provider.
     */
    @Override
    public void populate(Chunk chunk, Random random) {
        BlockPos blockpos = new BlockPos(chunk.getBlockMin().getX() + 8, 0, chunk.getBlockMin().getZ() + 8);
        World worldObj = (World) chunk.getWorld();

        for (int k1 = 0; k1 < 16; ++k1) {
            for (int l1 = 0; l1 < 16; ++l1) {
                BlockPos blockpos1 = worldObj.getPrecipitationHeight(blockpos.add(k1, 0, l1));
                BlockPos blockpos2 = blockpos1.down();

                if (worldObj.canBlockFreezeWater(blockpos2)) {
                    worldObj.setBlockState(blockpos2, Blocks.ice.getDefaultState(), 2);
                }

                if (worldObj.canSnowAt(blockpos1, true)) {
                    worldObj.setBlockState(blockpos1, Blocks.snow_layer.getDefaultState(), 2);
                }
            }
        }
    }

}
