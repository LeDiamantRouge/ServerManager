package fr.lediamantrouge.servermanager.bungeelisteners;

import fr.lediamantrouge.servermanager.BungeeMain;
import fr.lediamantrouge.servermanager.CommonMain;
import fr.lediamantrouge.servermanager.servermanager.Server;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ConnectBungeeCordEvent implements Listener {

    @EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        if (e.getTarget().getName().equals("fallback")) {
            for (Server server : CommonMain.getInstance().getServerManager().getServers()) {
                if (server.getTemplate().equalsIgnoreCase("Lobby") && BungeeMain.getInstance().getProxy().getServerInfo(server.getName()) != null) {
                    e.setTarget(BungeeMain.getInstance().getProxy().getServerInfo(server.getName()));
                    return;
                }
            }
            e.getPlayer().disconnect(new TextComponent("§cAucun serveur trouvé pour vous connecter..."));
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
