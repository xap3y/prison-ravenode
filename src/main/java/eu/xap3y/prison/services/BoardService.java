package eu.xap3y.prison.services;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.CellType;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.storage.dto.Cell;
import eu.xap3y.prison.util.Utils;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class BoardService {

    // TODO: Make board configurable in config.yml

    private static final Map<UUID, FastBoard> boards = new HashMap<>();

    public static FastBoard getBoard(UUID uuid) {
        return boards.get(uuid);
    }

    public static void addBoard(Player p0) {
        FastBoard board = new FastBoard(p0);

        board.updateTitle(Component.text("§e۞     §6§lPRISON    §e۞"));

        boards.put(p0.getUniqueId(), board);
        modifyBoard(p0.getUniqueId(), board);
    }

    public static void removeBoard(UUID p0) {
        boards.get(p0).delete();
        boards.remove(p0);
    }

    public static void updateBoard(UUID p0) {
        updateBoard(p0, false);
    }

    public static void updateBoard(UUID p0, boolean lvlUp) {
        FastBoard board = boards.get(p0);
        if (board == null) {
            return;
        }

        if (Prison.DEBUG) updateBoardDebug(p0, board);
        else modifyBoard(p0, board, lvlUp);
    }

    private static void modifyBoard(UUID p0, FastBoard board) {
        if (Prison.DEBUG) updateBoardDebug(p0, board);
        else modifyBoard(p0, board, false);
    }

    private static void updateBoardDebug(UUID p0, FastBoard board) {

        Cell starter = CellService.cellMapper.get(CellType.STARTER);

        String nextRefill = LevelService.shortProgress(p0, 10);

        board.updateLines(
                Component.empty(),
                Component.text("§b§l✦  §e§ldebug board  §b§l✦"),
                Component.text("§3♦ §feconomy: §6" + PlayerStorage.economy.get(p0).getXp() + "xp §a" + (int) PlayerStorage.economy.get(p0).getCoins() + "$"),
                Component.text("§3♢ §fmine: §9" + starter.getName()),
                Component.text("§3♦ §flast refill: §a" + starter.getLastReset()),
                Component.text("§3♢ §fis-empty: §c" + starter.isEmpty()),
                Component.empty(),
                Component.text("§3₪ §e§lnext refill"),
                Component.text("  §3§l➥ §7❰ " + nextRefill + " §7❱"),
                Component.empty(),
                Component.text("§fPAPI: §e0"),
                //Component.text("§fC_BLOCK[0]: §e" + starter.getBlockArray()[0]),
                //Component.text("§fC_BLOCK[1]: §e" + starter.getBlockArray()[1]),
                Component.text("§fCACHE §e" + LevelService.playerCache.get(p0)),
                Component.empty(),
                Component.text("§eplay.ravenode.nl")
        );
    }

    private static void modifyBoard(UUID p0, FastBoard board, boolean lvlUp) {
        String levelProgressBar = LevelService.shortProgress(p0, 10);

        String text;

        if (!lvlUp) {
            text = "  §3§l➥ §7❰ " + levelProgressBar + " §7❱";
        } else {
            text = "    §f❰ §eʟᴇᴠᴇʟ ᴜᴘ §f❱";
        }

        // FONT URL = https://lingojam.com/CoolTextFonts (the 13th one)
        board.updateLines(
                Component.empty(),
                Component.text("§b§l✦  §e§lᴘʟᴀʏᴇʀ ɪɴꜰᴏ  §b§l✦"),
                Component.text("§3♦ §fʟᴇᴠᴇʟ: §6" + PlayerStorage.economy.get(p0).getLevel()),
                Component.text("§3♢ §fxᴘ: §9" + Utils.fixDecimals(PlayerStorage.economy.get(p0).getXp())),
                Component.text("§3♦ §fᴘᴜʀꜱᴇ: §a" + (int) PlayerStorage.economy.get(p0).getCoins() + "$"),
                Component.text("§3♢ §fᴘʀᴇꜱᴛɪɢᴇꜱ: §c" + PlayerStorage.economy.get(p0).getPrestiges()),
                Component.empty(),
                Component.text("§3₪ §e§lʟᴇᴠᴇʟ ᴘʀᴏɢʀᴇꜱꜱ"),
                Component.text(text),
                /*Component.empty(),
                Component.text("§f§lᴍɪɴᴇ"),
                Component.text("§aStarter Mine"),*/
                Component.empty(),
                Component.text("    §eplay.ravenode.nl")
        );
    }
}
