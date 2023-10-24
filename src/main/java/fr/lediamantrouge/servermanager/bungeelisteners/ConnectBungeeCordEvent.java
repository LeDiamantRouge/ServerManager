package fr.lediamantrouge.servermanager.bungeelisteners;

import fr.lediamantrouge.servermanager.BungeeMain;
import fr.lediamantrouge.servermanager.CommonMain;
import fr.lediamantrouge.servermanager.servermanager.Server;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConnectBungeeCordEvent implements Listener {

    @EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        if (e.getTarget().getName().equals("fallback")) {
            List<ServerInfo> lobbys = new ArrayList<>();
            for (Server server : CommonMain.getInstance().getServerManager().getServers()) {
                if (server.getTemplate().equalsIgnoreCase("Lobby") && BungeeMain.getInstance().getProxy().getServerInfo(server.getName()) != null) {
                    lobbys.add(BungeeMain.getInstance().getProxy().getServerInfo(server.getName()));
                }
            }
            if (lobbys.isEmpty()) {
                e.getPlayer().disconnect(new TextComponent("§cAucun serveur trouvé pour vous connecter..."));
                return;
            }
            int random = new Random().nextInt(lobbys.size());
            e.setTarget(lobbys.get(random));
        }
    }

    @EventHandler
    public void onProxyConnect(PreLoginEvent e) {
        boolean found = false;
        for (Server server : CommonMain.getInstance().getServerManager().getServers()) {
            if (server.getTemplate().equalsIgnoreCase("Lobby") && BungeeMain.getInstance().getProxy().getServerInfo(server.getName()) != null) {
                found = true;
            }
        }
        if(!found) {
            e.setCancelled(true);
            e.setCancelReason(new TextComponent("§cAucun serveur trouvé pour vous connecter"));
        }
    }
}
