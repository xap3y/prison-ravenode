package eu.xap3y.prison.storage.dto;

import kotlin.Triple;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

public @Data class Arena {

    private final String name;

    private final List<Block> blocks;

    private final String world;

    private final List<Triple<Integer, Integer, Integer>> locations;

    private final int refreshTime;

    public Arena(String name, List<Block> blocks, String world, List<Triple<Integer, Integer, Integer>> locations, int refreshTime) {
        this.name = name;
        this.blocks = blocks;
        this.world = world;
        this.locations = locations;
        this.refreshTime = refreshTime;
    }

    public void fillBlocks() {
        locations.forEach(location -> {
            //Bukkit.getWorld("world").getBlockAt().setType(Material.DIRT);
        });
    }
}
