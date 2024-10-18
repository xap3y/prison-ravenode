package eu.xap3y.prison.services;

import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.storage.dto.Block;
import eu.xap3y.prison.util.Utils;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;


public class BreakService {

    private static double multiplierAdder = 0.0;

    public static void process(Block block, Player p0, boolean sound) {

        final ItemStack tool = p0.getInventory().getItemInMainHand();

        float multiplier = LevelService.playerCache.getMultiplier(p0.getUniqueId());

        if (tool.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
            int lootLvl = tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            int chance = ConfigDb.fortuneMapper.getChance(lootLvl);

            if (new Random().nextInt(100) < chance) {
                multiplier += ConfigDb.fortuneMapper.getMultiplier(lootLvl)-1f;
            }
        }

        final double xp = Utils.fixDecimals(block.getXp() * (multiplier + multiplierAdder));

        final double coins = Utils.fixDecimals(block.getCoins() * (multiplier + multiplierAdder));

        PlayerStorage.add(p0.getUniqueId(), xp, coins, 1);
        LevelService.fixExp(p0.getUniqueId());

        //p0.getInventory().addItem(new ItemStack(Utils.remapDrop(block.getMat()), 1));

        if (sound) Utils.displayAction(p0, "§a+ " + xp + " XP  &r|  §6+ " + coins + " $", 60);
        if (sound) p0.playSound(p0, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.8f);

        if (multiplierAdder > 0) {
            multiplierAdder = 0.0;
        }
    }

    public static void process(Block block, Player p0) {
        process(block, p0, true);
    }

    public static void setNextMultiplierAdder(double multiplierAdder) {
        BreakService.multiplierAdder = multiplierAdder;
    }
}
