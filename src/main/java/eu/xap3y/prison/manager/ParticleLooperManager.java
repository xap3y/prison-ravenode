package eu.xap3y.prison.manager;

import eu.xap3y.prison.Prison;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Data
public class ParticleLooperManager {

    public ParticleLooperManager(
            Player player
    ) {
        this.p0 = player;
        init();
    }

    private List<Location> locationList = new ArrayList<>();

    private Player p0;

    public void addLocation(Location location) {
        locationList.add(location);
    }

    public void setPlayer(Player player) {
        this.p0 = player;
    }

    private BukkitTask task;

    public void init() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Prison.INSTANCE, () -> {
            Location[] locTemp = locationList.toArray(new Location[0]);
            for (Location loc : locTemp) {
                Prison.parApi.LIST_1_8.REDSTONE
                        .packetColored(false, loc, Color.RED)
                        .sendTo(p0);
            }
        }, 0L, 3L);
    }

    public void destroy() {
        locationList.clear();
        task.cancel();
    }
}
