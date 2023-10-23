package fr.lediamantrouge.servermanager.manager;

import fr.lediamantrouge.servermanager.servermanager.Server;
import org.bukkit.Bukkit;

public class RedisMessagingManager {

    public static void sendStartedMessage() {
        RedisManager.getInstance().getConnection().publish("start", Bukkit.getMotd());
    }

    public static void sendStopMessage() {
        RedisManager.getInstance().getConnection().publish("stop", Bukkit.getMotd());
    }

    public static void sendStopMessage(Server server) {
        RedisManager.getInstance().getConnection().publish("stop", server.getName());
    }
}
