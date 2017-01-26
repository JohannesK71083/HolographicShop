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

public class SubCommandRemove extends SubCommand {

	public SubCommandRemove(PluginBase base, String permission) {
		super(base, permission,
				HShopLanguages.Command_Help_RemoveDescription, 
				new HShopLanguages[]{
						HShopLanguages.Command_Help_RemoveUsage,
						HShopLanguages.Command_Help_RemoveUsage2,
				},
				2,
				"remove",
				"rem", "del");
	}

	@Override
	protected boolean executeOp(Player op, String[] args) {
		// TODO Auto-generated method stub
		return executeUser(op, args);
	}

	@Override
	protected boolean executeUser(Player player, String[] args) {
		String shopName = args[0];
		Integer pos = null;
		
		try{
			pos = Integer.parseInt(args[1]);
		}catch(NumberFormatException e){
			player.sendMessage(ChatColor.RED+base.lang.parseFirstString(player, HShopLanguages.Command_ArgumentIsNotANumber));
			return true;
		}
		
		CommandRemove(player, shopName, pos);
		return true;
	}

	private void CommandRemove(Player player, String shopName, int pos){
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
	
		try{
			shop.removeItem(pos);
		}catch(IndexOutOfBoundsException e){
			player.sendMessage(ChatColor.RED+base.lang.parseFirstString(player, HShopLanguages.Command_ArgumentIsOutOfRange));
			return;
		}
		
		player.sendMessage(ChatColor.GREEN+base.lang.parseFirstString(player, HShopLanguages.Command_RemoveItemSuccess));
	}
}
