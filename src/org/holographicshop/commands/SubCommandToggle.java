package org.holographicshop.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.generallib.pluginbase.PluginBase;
import org.generallib.pluginbase.PluginLanguage.Language;
import org.generallib.pluginbase.commands.SubCommand;
import org.holographicshop.constants.Shop;
import org.holographicshop.main.HShopLanguages;
import org.holographicshop.main.HolographicShop;
import org.holographicshop.manager.ShopManager;

public class SubCommandToggle extends SubCommand {
	
	
	public SubCommandToggle(PluginBase base, String permission){
		super(base, permission,
				HShopLanguages.Command_Help_ToggleDescription, 
				new HShopLanguages[]{
						HShopLanguages.Command_Help_ToggleUsage
				},
				1,
				"toggle",
				"tg");
	}
	
	@Override
	protected boolean executeOp(Player op, String[] args) {
		return executeUser(op, args);
	}

	@Override
	protected boolean executeUser(Player player, String[] args) {
		String shopName = args[0];
		
		CommandToggleShopStat(player, shopName);
		return true;
	}

	private void CommandToggleShopStat(Player player, String shopName){
		ShopManager manager = base.getManager(ShopManager.class);
		
		Shop shop = manager.getShopByShopname(shopName);
		if(shop == null){
			player.sendMessage(ChatColor.RED+base.lang.parseFirstString(player, HShopLanguages.Command_NoSuchShopWithThatName));
			return;
		}
		
		if(!shop.isOwner(player)){
			player.sendMessage(ChatColor.RED+base.lang.parseFirstString(player, HShopLanguages.Command_YouAreNotTheOwnerOfThatShop));
			return;
		}
		
		shop.toggleShopStat();
		
		player.sendMessage(ChatColor.GREEN + base.lang.parseFirstString(player, HShopLanguages.Command_ShopToggled)+" "+(shop.isOpen() ? 
				ChatColor.GREEN+ base.lang.parseFirstString(player, HShopLanguages.Command_ShopState_Open)
				: ChatColor.RED +base.lang.parseFirstString(player, HShopLanguages.Command_ShopState_Closed)));
		
		if(shop.getAllItems().size() < 1){
			if(shop.isOpen()) shop.toggleShopStat();
			player.sendMessage(ChatColor.RED + base.lang.parseFirstString(player, HShopLanguages.Command_NothingInTheShop));
			player.sendMessage(ChatColor.GREEN + base.lang.parseFirstString(player, HShopLanguages.Command_ShopToggled)+" "+(shop.isOpen() ? 
					ChatColor.GREEN+ base.lang.parseFirstString(player, HShopLanguages.Command_ShopState_Open)
					: ChatColor.RED +base.lang.parseFirstString(player, HShopLanguages.Command_ShopState_Closed)));
		}
	}
}
