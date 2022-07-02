package dev.ortega.correio;

import dev.ortega.correio.command.MailCommand;
import dev.ortega.correio.database.ConnectionManager;
import dev.ortega.correio.inventory.InventoryManager;
import dev.ortega.correio.listeners.AccountEvent;
import dev.ortega.correio.objects.AccountManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public class Vulcanth extends JavaPlugin {
	
	private ConnectionManager connectionManager;
	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	
	private AccountManager accountManager;
	public AccountManager getAccountManager() {
		return accountManager;
	}
	
        private InventoryManager invManager;
        public InventoryManager invManager() {
    	        return invManager; 
        }

	
	
	public static Vulcanth getInstance() {
		return getPlugin(Vulcanth.class);
	}
	
	@Override
	public void onEnable() {
			Bukkit.getConsoleSender().sendMessage("§8[VulcanthStudios] §aCorreios iniciado com sucesso.");
		
		saveDefaultConfig();
		
		
		this.accountManager = new AccountManager();
		this.connectionManager = new ConnectionManager();

		this.invManager = new InventoryManager(this);
		this.invManager.init();
		
		events();
		commands();
		
		Bukkit.getOnlinePlayers().forEach(p -> accountManager.loadAccount(p));
		
	}
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("§8[VulcanthStudios] §aCorreios desativado com sucesso.");
		
	}
	
	public void events() {
		getServer().getPluginManager().registerEvents(new AccountEvent(), this);
	}
	
	public void commands() {
		getCommand("correio").setExecutor(new MailCommand());
	}

}
