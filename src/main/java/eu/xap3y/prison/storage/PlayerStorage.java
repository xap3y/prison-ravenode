package eu.xap3y.prison.storage;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.storage.dto.PlayerDto;
import eu.xap3y.prison.storage.dto.PlayersJson;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.UUID;

public class PlayerStorage {

    @Getter
    public static final HashMap<UUID, PlayerDto> economy = new HashMap<>();

    private static final File file = new File(Prison.INSTANCE.getDataFolder(), "players.json");

    private static void addPlayer(UUID uuid) {
        economy.put(uuid, new PlayerDto(0, 0, 0, 0));
        //Prison.texter.console("&aPlayer &e" + uuid + " &ahas been added to the economy.");
        savePlayers();
    }

    @SneakyThrows
    public static void savePlayers() {

        if (!file.exists())
            file.createNewFile();

        HashMap<UUID, PlayerDto> temp = loadPlayers();
        if (temp == null) {
            writePlayers(economy);
            return;
        }

        economy.forEach((uuid, playerDto) -> {
            if (temp.containsKey(uuid)) {
                temp.replace(uuid, economy.get(uuid));
            } else {
                temp.put(uuid, economy.get(uuid));
            }
        });

        writePlayers(temp);
    }

    @SneakyThrows
    private static void writePlayers(HashMap<UUID, PlayerDto> temp) {
        if (!file.exists())
            file.createNewFile();

        String json = Prison.gson.toJson(new PlayersJson(temp));
        FileWriter fw = new FileWriter(file);
        fw.write(json);
        fw.flush();
        fw.close();
    }

    @SneakyThrows
    private static HashMap<UUID, PlayerDto> loadPlayers() {

        HashMap<UUID, PlayerDto> temp;
        FileReader fr = new FileReader(file);
        try {
            temp = Prison.gson.fromJson(fr, PlayersJson.class).getEconomy();
        } catch (NullPointerException e) {
            fr.close();
            return new HashMap<>();
        }
        fr.close();
        return temp;
    }

    @SneakyThrows
    public static void loadPlayersFromStorage() {

        if (!file.exists())
            file.createNewFile();

        economy.clear();
        try {
            economy.putAll(loadPlayers());
        } catch (NullPointerException e) {
            //  Do nothing
        }
    }

    @SneakyThrows
    public static void loadPlayerFromStorage(UUID p0) {
        if (!file.exists())
            file.createNewFile();

        PlayerDto temp = loadPlayers().get(p0);

        if (temp == null) {
            addPlayer(p0);
            return;
        }

        economy.put(p0, temp);
    }

    public static void add(UUID p, double xp, double money) {
        PlayerDto playerDto = economy.get(p);
        playerDto.setXp(playerDto.getXp() + xp);
        playerDto.setCoins(playerDto.getCoins() + money);
        economy.put(p, playerDto);

        // TODO -- schedule save task
        savePlayers();
    }
}
