package fr.lediamantrouge.servermanager.bungeecommand;

import fr.lediamantrouge.servermanager.BungeeMain;
import fr.lediamantrouge.servermanager.CommonMain;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LobbyCommand extends Command {
    public LobbyCommand() {
        super("lobby");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (s instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) s;
            if (!player.getServer().isConnected() || CommonMain.getInstance().getServerManager().getServerByName(player.getServer().getInfo().getName()).getTemplate().equalsIgnoreCase("Lobby")) {
                player.sendMessage(new TextComponent("§cTu es déjà sur un lobby !"));
                return;
            }
            player.connect(BungeeMain.getInstance().getProxy().getServerInfo("fallback"));
        }
    }
}
