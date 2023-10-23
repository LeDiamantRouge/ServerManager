package fr.lediamantrouge.servermanager.bungeecommand;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import fr.lediamantrouge.servermanager.BungeeMain;
import fr.lediamantrouge.servermanager.CommonMain;
import fr.lediamantrouge.servermanager.servermanager.Server;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.concurrent.TimeUnit;

@CommandPermission("servermanager.command")
@CommandAlias("binstances|bsm|bservermanager")
public class InstanceCommand extends BaseCommand {

    @Default
    @HelpCommand
    public void onHelp(CommandSender s, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("templates")
    public class TemplateSubCommand extends BaseCommand {

        @Subcommand("reload")
        public void reloadTemplates(CommandSender s) {
            CommonMain.getInstance().getTemplateManager().reloadTemplates();
            s.sendMessage(new TextComponent("§aToutes les templates ont été rechargé !"));
        }

        @Subcommand("autostart")
        public void autostart(CommandSender s, @Name("on/off") String name) {
            if (name.equals("on")) {
                CommonMain.getInstance().setAutoStart(true);
                s.sendMessage(new TextComponent("§7Le démarrage automatique des serveurs est maintenant défini sur §a" + CommonMain.getInstance().isAutoStart()));
                CommonMain.getInstance().getTemplateManager().updateTemplates();
                return;
            }
            if (name.equals("off")) {
                CommonMain.getInstance().setAutoStart(false);
                s.sendMessage(new TextComponent("§7Le démarrage automatique des serveurs est maintenant défini sur §c" + CommonMain.getInstance().isAutoStart()));
                return;
            }
            s.sendMessage(new TextComponent("§4" + name + " §cn'existe pas comme valeur ! §7(on/off)"));
        }

        @Subcommand("autostart")
        public void autostart(CommandSender s) {
            CommonMain.getInstance().setAutoStart(!CommonMain.getInstance().isAutoStart());
            s.sendMessage(new TextComponent("§7Le démarrage automatique des serveurs est maintenant défini sur §e" + CommonMain.getInstance().isAutoStart()));
            if(CommonMain.getInstance().isAutoStart()) CommonMain.getInstance().getTemplateManager().updateTemplates();
        }
    }

    @Subcommand("server")
    public class ServerSubCommand extends BaseCommand {

        @Subcommand("list")
        public void list(CommandSender s) {
            s.sendMessage("§6Voici la liste des serveurs :");
            for(Server server : CommonMain.getInstance().getServerManager().getServers()) {
                s.sendMessage(new TextComponent("§6" + server.getDisplayName() + " §7(" + server.getName() + ")"));
            }
        }

        @Subcommand("start")
        public void start(CommandSender s, @Name("template") String template) {
            if(CommonMain.getInstance().getTemplateManager().getTemplateByName(template) == null) {
                s.sendMessage("§cCette template n'existe pas !");
                return;
            }
            Server server = CommonMain.getInstance().getServerCreator().createServer(template);
            s.sendMessage("§aLe serveur à été créer avec succès !");
            System.out.println("Le serveur " + server.getDisplayName() + " (" + server.getName() + ") est en cours de démarrage !");
            BungeeMain.getInstance().getProxy().getPlayers().forEach(proxiedPlayer -> {
                if(proxiedPlayer.hasPermission("servermanager.alert")) {
                    proxiedPlayer.sendMessage(new TextComponent("§7Le serveur §e" + server.getDisplayName() + " §7(" + server.getName() + ") est §6en cours de démarrage §7!"));
                }
            });
        }

        @Subcommand("stop")
        public void stop(CommandSender s, @Name("name") String name) {
            Server target = null;
            if (CommonMain.getInstance().getServerManager().getServerByName(name) != null) target = CommonMain.getInstance().getServerManager().getServerByName(name);
            if (CommonMain.getInstance().getServerManager().getServerByDisplayName(name) != null) target = CommonMain.getInstance().getServerManager().getServerByDisplayName(name);
            if(target == null) {
                s.sendMessage("§cCe serveur n'existe pas !");
                return;
            }
            BungeeMain.getInstance().getProtectedServers().add(target.getName());
            Server finalTarget1 = target;
            BungeeMain.getInstance().getProxy().getScheduler().schedule(BungeeMain.getInstance(), () -> {
                BungeeMain.getInstance().getProtectedServers().remove(finalTarget1.getName());
            }, 5L, TimeUnit.SECONDS);
            CommonMain.getInstance().getServerCreator().stopServer(target);
            BungeeMain.getInstance().getProxy().getServers().remove(target.getName());
            System.out.println("Le serveur " + target.getDisplayName() + " (" + target.getName() + ") vient de s'arrêter !");
            Server finalTarget = target;
            BungeeMain.getInstance().getProxy().getPlayers().forEach(proxiedPlayer -> {
                if(proxiedPlayer.hasPermission("servermanager.alert")) {
                    proxiedPlayer.sendMessage(new TextComponent("§7Le serveur §e" + finalTarget.getDisplayName() + " §7(" + finalTarget.getName() + ") vient de s'§carrêter §7!"));
                }
            });
            s.sendMessage("§aLe serveur à été éteint avec succès !");
        }

        @Subcommand("info")
        public void info(CommandSender s, @Name("name") String name) {
            Server target = null;
            if (CommonMain.getInstance().getServerManager().getServerByName(name) != null) target = CommonMain.getInstance().getServerManager().getServerByName(name);
            if (CommonMain.getInstance().getServerManager().getServerByDisplayName(name) != null) target = CommonMain.getInstance().getServerManager().getServerByDisplayName(name);
            if(target == null) {
                s.sendMessage("§cCe serveur n'existe pas !");
                return;
            }
            s.sendMessage(" §6=== Informations du serveur ===" +
                    "\n§cID : §6" + target.getName() +
                    "\n§cNom : §6" + target.getDisplayName() +
                    "\n§cTemplate : §6" + target.getTemplate() +
                    "\n§cRam : §6" + target.getRam() +
                    "\n§cPort : §6" + target.getPort() +
                    "\n §6==============================="
            );
        }
    }
}
