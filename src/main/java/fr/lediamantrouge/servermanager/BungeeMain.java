package fr.lediamantrouge.servermanager;

import co.aikar.commands.BungeeCommandManager;
import com.google.common.io.ByteStreams;
import fr.lediamantrouge.servermanager.bungeelisteners.DisconnectServerEvent;
import fr.lediamantrouge.servermanager.bungeecommand.InstanceCommand;
import fr.lediamantrouge.servermanager.bungeecommand.LobbyCommand;
import fr.lediamantrouge.servermanager.bungeelisteners.ConnectBungeeCordEvent;
import fr.lediamantrouge.servermanager.bungeepubsub.RedisChannel;
import fr.lediamantrouge.servermanager.manager.RedisCredentials;
import fr.lediamantrouge.servermanager.manager.RedisManager;
import fr.lediamantrouge.servermanager.servermanager.Server;
import fr.lediamantrouge.servermanager.templatemanager.BungeeTemplateManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import redis.clients.jedis.JedisPubSub;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Getter
public final class BungeeMain extends Plugin {

    @Getter
    private static BungeeMain instance;
    private BungeeCommandManager bungeeCommandManager;
    private List<String> protectedServers = new ArrayList<>();

    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;
        CommonMain.getInstance().setType("Bungee");
        CommonMain.getInstance().setTemplateManager(new BungeeTemplateManager());

        System.out.println("Register commands...");

        bungeeCommandManager = new BungeeCommandManager(this);
        bungeeCommandManager.enableUnstableAPI("help");
        bungeeCommandManager.registerCommand(new InstanceCommand());

        getProxy().getPluginManager().registerCommand(this, new LobbyCommand());

        System.out.println("Commands registered!");

        System.out.println("Register listeners...");

        getProxy().getPluginManager().registerListener(this, new ConnectBungeeCordEvent());
        getProxy().getPluginManager().registerListener(this, new DisconnectServerEvent());

        System.out.println("Listeners registered!");

        System.out.println("Loading config...");

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.createNewFile();
            try (InputStream is = getResourceAsStream("config.yml");
                 OutputStream os = Files.newOutputStream(configFile.toPath())) {
                ByteStreams.copy(is, os);
            }
        }

        Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

        System.out.println("Config loaded!");

        System.out.println("Connecting redis...");

        RedisCredentials credentials = new RedisCredentials(configuration.getString("redis.host"), configuration.getInt("redis.port"), configuration.getString("redis.password"), configuration.getInt("redis.db"));
        RedisManager.getInstance().connectRedis(credentials);

        System.out.println("Connected redis!");

        System.out.println("Register servers...");

        for(Server server : CommonMain.getInstance().getServerManager().getServers()) {
            try {
                getProxy()
                        .getServers()
                        .put(server.getName(),
                                BungeeMain.getInstance().getProxy().constructServerInfo(server.getName(),
                                        new InetSocketAddress(InetAddress.getLocalHost(), server.getPort()),
                                        server.getDisplayName(),
                                        false));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Servers registered!");

        getProxy().getScheduler().runAsync(this, () -> {
            System.out.println("Starting all pubsub...");
            JedisPubSub sub = new RedisChannel();
            RedisManager.getInstance().getConnection().subscribe(sub, "start", "stop", "created");
        });

        System.out.println("Started all pubsub!");
    }

    @Override
    public void onDisable() {

    }
}
