package dev.ortega.correio.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import dev.ortega.correio.Vulcanth;
import dev.ortega.correio.utils.Serialize;

public class AccountManager {

	private List<Account> accounts;

	public List<Account> getAccounts() {
		return accounts;
	}

	public AccountManager() {
		accounts = new ArrayList<Account>();
	}

	public void register(Account account) {
		accounts.add(account);
	}

	public void remove(Account account) {
		accounts.remove(account);
	}

	public Account get(UUID uuid) {
		return accounts.stream().filter(account -> account.getUuid().equals(uuid)).findFirst().orElse(null);
	}
	
	@SuppressWarnings("deprecation")
	public void loadAsync(Player p) {
		
		Bukkit.getScheduler().runTaskAsynchronously(Vulcanth.getInstance(), new BukkitRunnable() {

			@Override
			public void run() {
				loadAccount(p);
			}
			
		});
		
	}
	
	@SuppressWarnings("deprecation")
	public void insertAsync(Player p2, long data, Player p, ItemStack item) {
		
		Bukkit.getScheduler().runTaskAsynchronously(Vulcanth.getInstance(), new BukkitRunnable() {

			@Override
			public void run() {
				insert(p2, data, p, item);
			}
			
		});
	}

	public void loadAccount(Player p) {

		List<Mail> mails = new ArrayList<>();

		Account account = null;
		
		Vulcanth.getInstance().getConnectionManager().getPrincipalConnection().openConnection();

		if (Vulcanth.getInstance().getConnectionManager().getPrincipalConnection().getConnection() != null) {
			try {
				PreparedStatement stm = Vulcanth.getInstance().getConnectionManager().getPrincipalConnection()
						.getConnection()
						.prepareStatement("SELECT * FROM correio WHERE uuid = '" + p.getUniqueId().toString() + "'");
				ResultSet rs = stm.executeQuery();

				while (rs.next()) {

					if (account == null)
						account = new Account(p.getUniqueId(), p.getName());

					long data = rs.getLong("data");
					String owner = rs.getString("owner");
					String items = rs.getString("items");
					

					Mail mail = new Mail(owner, Serialize.fromBase64(items), data);
					mails.add(mail);

				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (account != null) {
					account.setMails(mails);
					register(account);
					System.out.print("Importando " + p.getName() + " da tabela.");
				}
				Vulcanth.getInstance().getConnectionManager().getPrincipalConnection().closeConnection();
			}
		}
	}

	public void insert(Player p2, long data, Player p, ItemStack item) {

        Account account = Vulcanth.getInstance().getAccountManager().get(p2.getUniqueId());
        
		Vulcanth.getInstance().getConnectionManager().getPrincipalConnection().openConnection();

		if (Vulcanth.getInstance().getConnectionManager().getPrincipalConnection().getConnection() != null) {
			try {
				PreparedStatement stms = Vulcanth.getInstance().getConnectionManager().getPrincipalConnection()
						.getConnection()
						.prepareStatement("INSERT INTO correio VALUES ('" + p2.getUniqueId().toString() + "','" + p2.getName() + "','" + data + "','" + p.getName() + "','" + Serialize.toBase64(item) + "');");
					
				stms.executeUpdate();		
				
				if (account != null) {
		    		p.setItemInHand(null);
		    		p.sendMessage("�aVoc� enviou uma encomenda para �f" + p2.getName() + "�a!");
		    		p2.sendMessage(new String[] {"�f" + p.getName() + "�a te enviou uma encomenda!", "�aUtilize /correio para visualiza-la."});
					   account.addMails(new Mail(p.getName(), item, data));
				} else {
					p.setItemInHand(null);
					p.sendMessage("�aVoc� enviou uma encomenda para �f" + p2.getName() + "�a!");
					p2.sendMessage(new String[] {"�f" + p.getName() + "�a te enviou uma encomenda!", "�aUtilize /correio para visualiza-la."});
					account = new Account(p2.getUniqueId(), p2.getName());
					account.addMails(new Mail(p.getName(), item, data));
					Vulcanth.getInstance().getAccountManager().register(account);
				}		
				
				System.out.print("O jogador " + account.getName() + " foi salvo na tabela.");
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				Vulcanth.getInstance().getConnectionManager().getPrincipalConnection().closeConnection();
			}
		}
	}

	public void delete(Player p, long data) {

		Vulcanth.getInstance().getConnectionManager().getPrincipalConnection().openConnection();

		if (Vulcanth.getInstance().getConnectionManager().getPrincipalConnection().getConnection() != null) {
			try {
				PreparedStatement stms = Vulcanth.getInstance().getConnectionManager().getPrincipalConnection()
						.getConnection().prepareStatement("DELETE FROM correio WHERE data = '" + data + "' AND uuid = '"
								+ p.getUniqueId().toString() + "';");
				
				stms.executeUpdate();
				System.out.print("O jogador " + p.getName() + " foi removido da tabela.");
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				Vulcanth.getInstance().getConnectionManager().getPrincipalConnection().closeConnection();
			}
		}
	}
	
	public void deleteAll(Account account) {

		Vulcanth.getInstance().getConnectionManager().getPrincipalConnection().openConnection();

		if (Vulcanth.getInstance().getConnectionManager().getPrincipalConnection().getConnection() != null) {
			try {
				PreparedStatement stms = Vulcanth.getInstance().getConnectionManager().getPrincipalConnection()
						.getConnection().prepareStatement("DELETE FROM correio WHERE uuid = '"
								+ account.getUuid().toString() + "';");
				
				stms.executeUpdate();
				System.out.print("O jogador " + account.getName() + " foi removido da tabela.");
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				remove(account);
				Vulcanth.getInstance().getConnectionManager().getPrincipalConnection().closeConnection();
			}
		}
	}

}
