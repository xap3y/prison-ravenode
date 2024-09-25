package eu.xap3y.prison.storage;

import eu.xap3y.prison.api.enums.CellType;
import eu.xap3y.prison.services.CellService;
import eu.xap3y.prison.storage.dto.Block;
import eu.xap3y.prison.storage.dto.Cell;
import lombok.Getter;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class StorageManager {

    @Getter
    public static HashMap<CellType, List<Material>> blocks = new HashMap<>();

    public static CellType getType(Material material) {
        for (CellType type : blocks.keySet()) {
            for (Material mat : blocks.get(type)) {
                if (mat == material) {
                    return type;
                }
            }
        }
        return null;
    }

    public static void loadBlocks() {
        CellService.cellMapper.forEach((cellType, cell) -> {

            Material[] mats = new Material[cell.blockArray.length];
            for (int j=0;j<cell.blockArray.length;j++) {
                mats[j] = cell.blockArray[j].getMat();
            }

            blocks.put(cellType, List.of(mats));
        });
    }

    public static @Nullable Block getBlock(Cell cell, Material mat) {
        Block[] blockArr = cell.blockArray;

        for (Block block : blockArr) {
            if (block.getMat() == mat) {
                return block;
            }
        }
        return null;
    }

    /*
    private static final File file = new File(Prison.INSTANCE.getDataFolder(), "blocks.json");

    @SneakyThrows
    public static void saveBlocks() {

        if (!file.exists())
            Prison.INSTANCE.saveResource("blocks.json", false);
        else {
            return;
        }

        *//* XP
        STONE	10	10
        BRICKS	20	20
        DIORITE	30	30
        ANDESITE	40	40
        SANDSTONE	50	50
        TERRACOTA 	60	60
        BLOCK OF COPPER	70	70
        COAL	80	80
        IRON	90	90
        GOLD	110	110
        LAPIS	120	120
        DIAMOND	130	130
        EMERALD	140	140
        NETHERITE	150	150
         *//*

        *//*
        Bread mine	6-10	Bricks & Mud_Bricks are level 6/10
        Mango Mine	11-20	Diorite & Polished_drioirte are level 11/20
        Fiber mine	21-30	Andesite & Polished_andesite are level 21/30
        Grass mine	31-40	Sand & sandstone are levels 31/40
        Flower mine	41-50	terracota & Packed_mud are levels 41/50
         *//*

        Blocks blocks = new Blocks(new HashMap<>() {{
            put(CellType.STARTER,  new Material[] {
                    Material.STONE, Material.COBBLESTONE
            });

            put(CellType.BREAD, new Material[] {
                    Material.MUD_BRICKS, Material.BRICKS
            });

            put(CellType.MANGO,  new Material[] {
                    Material.DIORITE, Material.POLISHED_DIORITE
            });

            put(CellType.FIBER,  new Material[] {
                    Material.ANDESITE, Material.POLISHED_ANDESITE
            });

            put(CellType.GRASS,  new Material[] {
                    Material.SAND, Material.SANDSTONE
            });

            put(CellType.FLOWER,  new Material[] {
                    Material.TERRACOTTA, Material.PACKED_MUD
            });
        }});

        String json = Prison.gson.toJson(blocks);

        // Save json to file
        FileWriter fw = new FileWriter(file);
        fw.write(json);
        fw.flush();
        fw.close();
    }

    @SneakyThrows
    public static void loadBlocks() {
        // Load blocks from file
        FileReader fr = new FileReader(file);
        blocks = Prison.gson.fromJson(fr, Blocks.class).getBlocks();
        fr.close();
    }*/


}
