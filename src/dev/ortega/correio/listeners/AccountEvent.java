package dev.ortega.correio.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.ortega.correio.Vulcanth;
import dev.ortega.correio.objects.Account;

public class AccountEvent implements Listener {

	@EventHandler
	public void Join(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		Vulcanth.getInstance().getAccountManager().loadAsync(p);

	}

	@EventHandler
	public void Quit(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		Account account = Vulcanth.getInstance().getAccountManager().get(p.getUniqueId());
		if (account != null)
			Vulcanth.getInstance().getAccountManager().remove(account);

	}	

}
