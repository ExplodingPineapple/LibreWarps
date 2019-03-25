package de.syscy.warpsystem.velocity.base;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import de.codingair.codingapi.bungeecord.files.FileManager;
import de.codingair.codingapi.time.TimeFetcher;
import de.codingair.codingapi.time.Timer;
import de.syscy.warpsystem.transfer.velocity.BungeeDataHandler;
import de.syscy.warpsystem.utils.Manager;
import de.syscy.warpsystem.velocity.base.language.Lang;
import de.syscy.warpsystem.velocity.base.listeners.MainListener;
import de.syscy.warpsystem.velocity.base.managers.DataManager;
import de.syscy.warpsystem.velocity.base.managers.ServerManager;
import net.md_5.bungee.BungeeCord;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(id = "warpsystemvelocity", name = "WarpSystemVelocity", version = "1.0", description = "Warp System proxy plugin", authors = {"CodingAir", "Kage0x3B"})
public class WarpSystem {
	public static final String PERMISSION_TELEPORT_COMMAND = "WarpSystem.TeleportCommand";

	private static WarpSystem instance;

	private final ProxyServer proxy;
	private final Logger logger;
	private final PluginDescription pluginDescription;
	private final File dataDirectory;

	private BungeeDataHandler dataHandler = new BungeeDataHandler(this);
	private FileManager fileManager = new FileManager(this);
	private ServerManager serverManager = new ServerManager();
	private DataManager dataManager = new DataManager();
	private Timer timer = new Timer();

	@Inject
	public WarpSystem(ProxyServer proxy, Logger logger, PluginDescription pluginDescription, @DataDirectory Path dataDirectory) {
		this.proxy = proxy;
		this.logger = logger;
		this.pluginDescription = pluginDescription;
		this.dataDirectory = dataDirectory.toFile();

		proxy.getEventManager().register(this, this);
	}

	@Subscribe
	public void onEnable(ProxyInitializeEvent event) {
		instance = this;
		timer.start();

		log(" ");
		log("________________________________________________________");
		log(" ");
		log("                   WarpSystem [" + pluginDescription.getVersion().orElse("unknown version") + "]");
		log(" ");
		log("Status:");
		log(" ");
		log("Initialize SpigotConnector");

		this.fileManager.loadFile("Config", "velocity/");
		try {
			Lang.initPreDefinedLanguages(this);
		} catch(IOException e) {
			e.printStackTrace();
		}

		this.dataHandler.onEnable();
		MainListener listener = new MainListener();
		proxy.getEventManager().register(this, listener);
		this.dataHandler.register(listener);
		this.serverManager.run();

		log("Loading features");
		boolean createBackup = false;
		if(!this.dataManager.load())
			createBackup = true;

		if(createBackup) {
			log("Loading with errors > Create backup...");
			createBackup();
			log("Backup successfully created");
		}

		this.startAutoSaver();

		timer.stop();

		log(" ");
		log("Done (" + timer.getLastStoppedTime() + "s)");
		log(" ");
		log("________________________________________________________");
		log(" ");
	}

	@Override
	public void onDisable() {
		this.dataHandler.onDisable();
		save(false);
		destroy();
	}

	private void startAutoSaver() {
		WarpSystem.log("Starting AutoSaver");
		BungeeCord.getInstance().getScheduler().schedule(this, () -> save(true), 20, 20, TimeUnit.MINUTES);
	}

	private void destroy() {
		this.dataManager.getManagers().forEach(Manager::destroy);
	}

	private void save(boolean saver) {
		try {
			if(!saver) {
				timer.start();

				log(" ");
				log("________________________________________________________");
				log(" ");
				log("                   WarpSystem [" + getDescription().getVersion() + "]");
				log(" ");
				log("Status:");
				log(" ");
			}

			if(!saver)
				log("Saving features");
			this.dataManager.save(saver);

			if(!saver) {
				timer.stop();

				log(" ");
				log("Done (" + timer.getLastStoppedTime() + "s)");
				log(" ");
				log("________________________________________________________");
				log(" ");
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void createBackup() {
		try {
			getDataFolder().createNewFile();
		} catch(IOException e) {
			e.printStackTrace();
		}

		File backupFolder = new File(getDataFolder().getPath() + "/Backups/", TimeFetcher.getYear() + "_" + (TimeFetcher.getMonthNum() + 1) + "_" + TimeFetcher.getDay() + " " + TimeFetcher.getHour() + "_" + TimeFetcher.getMinute() + "_" + TimeFetcher.getSecond());
		backupFolder.mkdirs();

		for(File file : getDataFolder().listFiles()) {
			if(file.getName().equals("Backups") || file.getName().equals("ErrorReport.txt"))
				continue;
			File dest = new File(backupFolder, file.getName());

			try {
				if(file.isDirectory()) {
					copyFolder(file, dest);
					continue;
				}

				copyFileUsingFileChannels(file, dest);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void copyFolder(File source, File dest) throws IOException {
		dest.mkdirs();
		for(File file : source.listFiles()) {
			File copy = new File(dest, file.getName());

			if(file.isDirectory()) {
				copyFolder(file, copy);
				continue;
			}

			copyFileUsingFileChannels(file, copy);
		}
	}

	private void copyFileUsingFileChannels(File source, File dest) throws IOException {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(source).getChannel();
			outputChannel = new FileOutputStream(dest).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			inputChannel.close();
			outputChannel.close();
		}
	}

	public static WarpSystem getInstance() {
		return instance;
	}

	public BungeeDataHandler getDataHandler() {
		return dataHandler;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public ServerManager getServerManager() {
		return serverManager;
	}

	public static void log(String message) {
		logger.info(message);
	}

	public DataManager getDataManager() {
		return dataManager;
	}

	public ProxyServer getProxy() {
		return proxy;
	}

	public PluginDescription getPluginDescription() {
		return pluginDescription;
	}

	public Logger getLogger() {
		return logger;
	}

	public File getDataDirectory() {
		return dataDirectory;
	}
}
