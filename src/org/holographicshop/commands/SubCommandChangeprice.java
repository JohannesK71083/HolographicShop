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

public class SubCommandChangeprice extends SubCommand {

	
	public SubCommandChangeprice(PluginBase base, String permission) {
		super(base, permission,
				HShopLanguages.Command_Help_ChangePriceUsage, 
				new HShopLanguages[]{
						HShopLanguages.Command_Help_ChangePriceUsage
				},
				3,
				"changeprice",
				"cp", "change");
	}

	@Override
	protected boolean executeOp(Player op, String[] args) {
		return executeUser(op, args);
	}
	
	@Override
	protected boolean executeUser(Player player, String[] args) {
		String shopName = args[0];
		Integer pos = null;
		Double price = null;
		
		try{
			pos = Integer.parseInt(args[1]);
		}catch(NumberFormatException e){
			player.sendMessage(ChatColor.RED+base.lang.parseFirstString(player, HShopLanguages.Command_ArgumentIsNotANumber));
			return true;
		}
		
		try{
			price = Double.parseDouble(args[2]);
		}catch(NumberFormatException e){
			player.sendMessage(ChatColor.RED+base.lang.parseFirstString(player, HShopLanguages.Command_ArgumentIsNotANumber));
			return true;
		}
		
		CommandChangePrice(player, shopName, pos, price);
		return true;
	}

	private void CommandChangePrice(Player player, String shopName, int pos, double price){
		ShopManager manager = HolographicShop.getInstance().getManager(ShopManager.class);
		
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
			shop.changeItemPrice(pos, price);
		}catch(IndexOutOfBoundsException e){
			player.sendMessage(ChatColor.RED+base.lang.parseFirstString(player, HShopLanguages.Command_ArgumentIsOutOfRange));
			return;
		}
		
		player.sendMessage(ChatColor.GREEN+base.lang.parseFirstString(player, HShopLanguages.Command_ChangeItemPriceSuccess));
	}
}
