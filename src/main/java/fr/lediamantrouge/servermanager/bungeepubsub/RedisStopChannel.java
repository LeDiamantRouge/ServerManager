package fr.lediamantrouge.servermanager.bungeepubsub;

import fr.lediamantrouge.servermanager.BungeeMain;
import fr.lediamantrouge.servermanager.CommonMain;
import fr.lediamantrouge.servermanager.servermanager.Server;
import net.md_5.bungee.api.chat.TextComponent;
import redis.clients.jedis.JedisPubSub;

public class RedisStopChannel extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equals("stop")) {
            Server server = CommonMain.getInstance().getServerManager().getServerByName(message);
            if (server == null || BungeeMain.getInstance().getProtectedServers().contains(server.getName())) return;
            CommonMain.getInstance().getServerCreator().stopServer(server);
            BungeeMain.getInstance().getProxy().getServers().remove(server.getName());
            System.out.println("Le serveur " + server.getDisplayName() + " (" + server.getName() + ") vient de s'arrêter !");
            BungeeMain.getInstance().getProxy().getPlayers().forEach(proxiedPlayer -> {
                if(proxiedPlayer.hasPermission("servermanager.alert")) {
                    proxiedPlayer.sendMessage(new TextComponent("§7Le serveur §e" + server.getDisplayName() + " §7(" + server.getName() + ") vient de s'§carrêter §7!"));
                }
            });
        }
    }
}
