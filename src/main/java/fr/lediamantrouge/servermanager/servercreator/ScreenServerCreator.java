package fr.lediamantrouge.servermanager.servercreator;

import fr.lediamantrouge.servermanager.BungeeMain;
import fr.lediamantrouge.servermanager.CommonMain;
import fr.lediamantrouge.servermanager.servermanager.Server;
import fr.lediamantrouge.servermanager.templatemanager.Template;
import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ScreenServerCreator implements IServerCreator {

    private final Path mainFile = Paths.get("/home/serveur");

    private final Path templatesFile = Paths.get(this.mainFile + "/TEMPLATES");

    private final Path serversFile = Paths.get(this.mainFile + "/SERVERS");

    private final Path CoreFilePath = Paths.get(this.mainFile + "/Proxy/plugins/ServerManager.jar");
    private final Path CoreConfigPath = Paths.get(this.mainFile + "/Proxy/plugins/ServerManager/config.yml");

    @Override
    public Server createServer(String template) {
        int port = ServerCreatorUtils.nextFreePort();

        String name = ServerCreatorUtils.foundId();

        Template templateObj = CommonMain.getInstance().getTemplateManager().getTemplateByName(template);
        if(templateObj == null) {
            throw new RuntimeException("Template not found !");
        }

        Server server = new Server(
                name,
                templateObj.getFormat()
                        .replace("%randomID%", String.valueOf(new Random().nextInt(99999)))
                        .replace("%randomLetter%", ServerCreatorUtils.foundId())
                        .replace("%incrID%", String.valueOf(CommonMain.getInstance().getTemplateManager().getTotalInTemplate(templateObj) + 1)
                        ),
                port,
                template,
                templateObj.getRam(),
                false
                );

        CommonMain.getInstance().getServerManager().createServer(server);

        new Thread(() -> {
            try {
                startServer(server);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return server;
    }

    @SneakyThrows
    @Override
    public void startServer(Server server) {
        String folder = this.serversFile + "/" + server.getName();

        String templateFile = this.templatesFile + "/" + server.getTemplate();
        String all = this.templatesFile + "/GLOBAL/SERVER";

        try {
            Files.createDirectories(Paths.get(folder));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (File allFiles : Objects.requireNonNull(new File(all).listFiles())) {
            File newFile = new File(folder + "/" + allFiles.getName());
            if (newFile.exists()) {
                newFile.deleteOnExit();
            }
            File file = new File(folder + "/" + allFiles.getName());
            CustomFileUtils.copydir(allFiles, file);
        }

        for (File original : Objects.requireNonNull((new File(templateFile)).listFiles())) {
            File newFile = new File(folder + "/" + original.getName());
            if (newFile.exists()) {
                newFile.deleteOnExit();
            }
            File file = new File(folder + "/" + original.getName());
            CustomFileUtils.copydir(original, file);
        }

        Files.createDirectories(Paths.get(folder + "/plugins/ServerManager"));
        CustomFileUtils.copydir(CoreFilePath.toFile(), Paths.get(folder + "/plugins/ServerManager.jar").toFile());
        CustomFileUtils.copydir(CoreConfigPath.toFile(), Paths.get(folder + "/plugins/ServerManager/config.yml").toFile());

        File path = new File(folder + "/server.properties");
        Properties props = new Properties();
        try {
            props.load(Files.newInputStream(path.toPath()));
            props.put("motd", server.getName());
            props.put("server-port", String.valueOf(server.getPort()));
            props.store(Files.newOutputStream(path.toPath()), "Minecraft server properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File startScript = new File(folder + "/start.sh");
        if (!startScript.exists()) {
            String scriptContent = "nice -n 4 screen -dmS " + server.getName() + " java -Djava.awt.headless=true -jar -Xms1G -Xmx" + CommonMain.getInstance().getTemplateManager().getTemplateByName(server.getTemplate()).getRam() + "G server.jar";
            Writer output;
            try {
                output = new BufferedWriter(new FileWriter(folder + "/start.sh"));
                output.write(scriptContent);
                output.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                StringBuilder fileContent = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new FileReader(folder + "/start.sh"));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    line = line.replace("%NAME%", server.getName());
                    line = line.replace("%RAM%", String.valueOf(server.getRam()));
                    fileContent.append(line).append("\n");
                }

                bufferedReader.close();

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(folder + "/start.sh"));

                bufferedWriter.write(fileContent.toString());

                bufferedWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        File startScriptCreated = new File(folder + "/start.sh");
        if (startScriptCreated.exists()) {
            startScriptCreated.setExecutable(true);
        }
        File targetDirectory = new File(folder);

        try {
            new ProcessBuilder("./start.sh").directory(targetDirectory).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopServer(Server server) {
        CommonMain.getInstance().getServerManager().stopServer(server);

        Template template = CommonMain.getInstance().getTemplateManager().getTemplateByName(server.getTemplate());
        if (template != null) {
            CommonMain.getInstance().getTemplateManager().updateTemplates();
        }

        Runtime runtime = Runtime.getRuntime();

        String folder = this.serversFile + "/" + server.getName();

        try {
            Process process = runtime.exec("screen -X -S " + server.getName() + " kill");
            process.waitFor();
            File file = new File(folder);
            CommonMain.getInstance().getScheduler().asyncLater(() -> removeServer(file), 5L, TimeUnit.SECONDS);

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeServer(File file) {
        File[] fichiers = file.listFiles();
        if (fichiers != null) {
            for (File fichier : fichiers) {
                if (fichier.isDirectory()) {
                    removeServer(fichier);
                } else {
                    fichier.delete();
                }
            }
        }
        file.delete();
    }
}
