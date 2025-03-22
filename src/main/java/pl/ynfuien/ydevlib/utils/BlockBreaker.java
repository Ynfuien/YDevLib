package pl.ynfuien.ydevlib.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockBreaker {
    /**
     * Same as block.breakNaturally(), but without item drops. Plays the sound and spawns break particles.
     * @param block The block to break
     */
    public static void breakStrikingly(Block block) {
        if (block.isEmpty()) return;

        BlockData blockData = block.getBlockData();
        Location location = block.getLocation();
        World world = location.getWorld();

        world.playEffect(location, Effect.STEP_SOUND, blockData);
        world.spawnParticle(Particle.BLOCK_CRACK, location, 1, blockData);

        block.setType(Material.AIR);
    }
}
