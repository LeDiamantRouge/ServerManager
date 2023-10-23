package fr.lediamantrouge.servermanager.bungeelisteners;

import fr.lediamantrouge.servermanager.BungeeMain;
import fr.lediamantrouge.servermanager.CommonMain;
import fr.lediamantrouge.servermanager.servermanager.Server;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class DisconnectServerEvent implements Listener {

    @EventHandler
    public void onServerKick(ServerKickEvent e) {
        Server server = CommonMain.getInstance().getServerManager().getServerByName(e.getKickedFrom().getName());
        if(e.getState().equals(ServerKickEvent.State.CONNECTING)) {
            e.setCancelled(true);
            if(BaseComponent.toLegacyText(e.getKickReasonComponent()).equals("none")) return;
            e.getPlayer().sendMessage(new TextComponent("§cEchec de connexion vers le serveur §a" + (server == null ? e.getKickedFrom().getName() : server.getDisplayName()) + " §c: §a" +
                    BaseComponent.toLegacyText(e.getKickReasonComponent())));
            return;
        }
        if(e.getPlayer().isConnected() && e.getState().equals(ServerKickEvent.State.CONNECTED)) {
            e.setCancelled(true);
            e.setCancelServer(BungeeMain.getInstance().getProxy().getServerInfo("fallback"));
            if(BaseComponent.toLegacyText(e.getKickReasonComponent()).equals("none")) return;
            e.getPlayer().sendMessage(new TextComponent("§f\n" +
                    " §cVous avez été expulsé du serveur §a" + (server == null ? e.getKickedFrom().getName() : server.getDisplayName()) + "\n" +
                    " §cRaison : §a" + BaseComponent.toLegacyText(e.getKickReasonComponent()) + "\n" +
                    "§f"
            ));
        }
    }
}
