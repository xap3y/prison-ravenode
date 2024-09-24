package eu.xap3y.prison.services;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.CellType;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.storage.StorageManager;
import eu.xap3y.prison.storage.dto.Cell;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CellService {

    public static final Map<CellType, Cell> cellMapper = new HashMap<>() {{

        // TODO: Load all cells from json file
        put(CellType.STARTER, new Cell(
                "&8Starter Mine",
                null,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTk4MTZiMDA0YzBhMTI0NmVlMjA5NTA5ZmZjOTNhMzAyNGJmMWRmMTliMDk2OTlhYzgzNzlkZTExODYwZjUzMyJ9fX0=",
                0,
                null,
                null,
                null
                )
        );

        put(CellType.BREAD, new Cell(
                "&bBread Mine",
                null,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmU5YmNlODYxOTZhODU3YmYwOTUzMzMxMzZhOTYzMGVjZWNmNjE4OTRiNmE1YzU4NTNlNTkzYWY1M2RhZDYwIn19fQ==",
                0,
                null,
                null,
                null
                )
        );

        put(CellType.MANGO, new Cell(
                "&bMango Mine",
                null,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQ4OTVhYTY3MjQ3YzNlYjQwNmZiOTA1ZDNmNmQzNWFjZDY2MGM2ZjE0YTg1YmNmN2JmYjg5OGI4MjY0NmU3MCJ9fX0=",
                0,
                null,
                null,
                null
            )
        );

        put(CellType.FIBER, new Cell(
                "&fFiber Mine",
                null,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjYzMjk3MGJhNDE2YzZhNTNlYjYyYWIwNGY3ZGRhOWUzNzY2YmZkYWQ0YWQxZTA4MTE0OTc1OWE4MTZlMWE2OSJ9fX0=",
                0,
                null,
                null,
                null
                )
        );

        put(CellType.GRASS, new Cell(
                "&aGrass Mine",
                null,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzE5NGU5ZTc3NTdkMDZkNmY1ZTViZTg0NTQ4YTdjYjUyMTczZDY4Y2NmODAyZDIxMTI3NWQzMWNkYmEwYTA2ZSJ9fX0=",
                0,
                null,
                null,
                null
                )
        );

        put(CellType.FLOWER, new Cell(
                "&dFlower Mine",
                null,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzYyNjc5ZjdkYzQzZmUzM2Y3MGM4NDhkZWZiNDJlY2Y2ZDI0OTUwMDY3MGQ3ZWRkYTJlOGJlOTg2YWM1ZjEwMSJ9fX0=",
                0,
                null,
                null,
                null
                )
        );

        // TODO: Secret mine
    }};


    public static boolean hasUnlockedCell(UUID p0, CellType p1) {
        return PlayerStorage.economy.get(p0).getLevel() >= cellMapper.get(p1).getMinLevel();
    }

    public static void loadCellsLocs() {
        for (CellType cellType : cellMapper.keySet()) {

            //Prison.texter.console("-- Loading cell " + cellType.name());

            Cell cell = cellMapper.get(cellType);
            Location spawn = Prison.INSTANCE.getConfig().getLocation("mines." + cellType.name().toLowerCase() + ".spawn");
            Location pos1 = Prison.INSTANCE.getConfig().getLocation("mines." + cellType.name().toLowerCase() + ".area.loc1");
            Location pos2 = Prison.INSTANCE.getConfig().getLocation("mines." + cellType.name().toLowerCase() + ".area.loc2");

            //Prison.texter.console("SPAWN: " + spawn);
            //Prison.texter.console("LOC1: " + pos1);
            //Prison.texter.console("LOC2: " + pos2);
            cell.setSpawn(spawn);
            cell.setPos1(pos1);
            cell.setPos2(pos2);
            cell.refreshLocs();

            /*cell.setBlocks(new Blocks(new HashMap<>() {{
                put(Material.STONE, new Block(1, 1, 1));
            }}));*/
        }
    }

    public static void resetAllCells() {
        CellType[] cells = CellType.values();

        for (CellType cell : cells) {
            resetCell(cell);
        }
    }

    public static void resetCell(CellType p0) {
        Cell cell = cellMapper.get(p0);
        Location pos1 = cell.getPos1();
        Location pos2 = cell.getPos2();
        Material[] mats = StorageManager.getBlocks().get(p0);

        Bukkit.getScheduler().runTaskAsynchronously(Prison.INSTANCE, () -> {
            Bukkit.getOnlinePlayers().forEach((Player) -> {
                //TODO: Clone position of looped player and subtract 1 from its Y-axis and then check
                if (cell.isInside(Player.getLocation())) {

                    // Get the fucking player out before he die to suffocation
                    Bukkit.getScheduler().runTask(Prison.INSTANCE, () -> Player.teleport(cell.getSpawn()));
                }
            });
        });

        FillService temp = new FillService(pos1, pos2, mats);

        temp.start();

        /* Material and its percentage

        AtomicReference<Integer> buffer = new AtomicReference<>(0);

        Bukkit.getScheduler().runTaskAsynchronously(Prison.INSTANCE, new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                for (int y = pos1.getBlockY(); y <= pos2.getBlockY(); y++) {

                    buffer.set(buffer.get() + 1);
                    // Reset row
                    int chance = random.nextInt(100);
                    Material material;
                    if (chance <= 50) {
                        material = mats[0];
                    } else {
                        material = mats[1];
                    }

                    resetRow(new Location(pos1.getWorld(), pos1.getBlockX(), y, pos1.getBlockZ()), new Location(pos2.getWorld(), pos2.getBlockX(), y, pos2.getBlockZ()), material);
                    if (buffer.get() > 1) {
                        resetRow(new Location(pos1.getWorld(), pos1.getBlockX(), y-2, pos1.getBlockZ()), new Location(pos2.getWorld(), pos2.getBlockX(), y-2, pos2.getBlockZ()), material);
                    }
                    Thread.sleep(100);
                }
            }
        });*/
    }

    /*@Deprecated
    private static void resetRow(Location p0, Location p1, Material p2) {
        for (int x = p0.getBlockX(); x <= p1.getBlockX(); x++) {
            for (int z = p0.getBlockZ(); z <= p1.getBlockZ(); z++) {
                p0.getWorld().getBlockAt(x, p0.getBlockY(), z).setType(p2);
            }
        }
    }*/
}
