package fr.lediamantrouge.servermanager.bungeepubsub;

import fr.lediamantrouge.servermanager.BungeeMain;
import fr.lediamantrouge.servermanager.CommonMain;
import fr.lediamantrouge.servermanager.servermanager.Server;
import net.md_5.bungee.api.chat.TextComponent;
import redis.clients.jedis.JedisPubSub;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class RedisStartChannel extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equals("start")) {
            Server server = CommonMain.getInstance().getServerManager().getServerByName(message);
            try {
                BungeeMain
                        .getInstance()
                        .getProxy()
                        .getServers()
                        .put(
                                server.getName(),
                                BungeeMain.getInstance().getProxy().constructServerInfo(server.getName(),
                                        new InetSocketAddress(InetAddress.getLocalHost(), server.getPort()),
                                        server.getDisplayName(),
                                        false
                                        ));

                System.out.println("Le serveur " + server.getDisplayName() + " (" + server.getName() + ") vient de s'allumer !");
                BungeeMain.getInstance().getProxy().getPlayers().forEach(proxiedPlayer -> {
                    if(proxiedPlayer.hasPermission("servermanager.alert")) {
                        proxiedPlayer.sendMessage(new TextComponent("§7Le serveur §e" + server.getDisplayName() + " §7(" + server.getName() + ") vient de s'§aallumer §7!"));
                    }
                });
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
