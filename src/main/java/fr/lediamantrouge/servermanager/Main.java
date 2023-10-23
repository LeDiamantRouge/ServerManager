package fr.lediamantrouge.servermanager;

import fr.lediamantrouge.servermanager.manager.RedisCredentials;
import fr.lediamantrouge.servermanager.manager.RedisManager;
import fr.lediamantrouge.servermanager.manager.RedisMessagingManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        FileConfiguration configuration = getConfig();

        RedisCredentials credentials = new RedisCredentials(configuration.getString("redis.host"), configuration.getInt("redis.port"), configuration.getString("redis.password"), configuration.getInt("redis.db"));
        RedisManager.getInstance().connectRedis(credentials);

        RedisMessagingManager.sendStartedMessage();
    }

    @Override
    public void onDisable() {
        RedisMessagingManager.sendStopMessage();
        RedisManager.getInstance().disconnect();
    }
}
