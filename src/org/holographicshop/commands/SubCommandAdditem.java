package org.holographicshop.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.generallib.pluginbase.PluginBase;
import org.generallib.pluginbase.PluginLanguage.Language;
import org.generallib.pluginbase.commands.SubCommand;
import org.holographicshop.constants.Shop;
import org.holographicshop.constants.ShopItem;
import org.holographicshop.main.HShopLanguages;
import org.holographicshop.main.HolographicShop;
import org.holographicshop.manager.ShopManager;

public class SubCommandAdditem extends SubCommand {

	public SubCommandAdditem(PluginBase base, String permission) {
		super(base, permission,
				HShopLanguages.Command_Help_AddItemDescription, 
				new HShopLanguages[]{
						HShopLanguages.Command_Help_AddItemUsage,
						HShopLanguages.Command_Help_AddItemUsage2,
				},
				-1,
				"additem",
				"ai", "add");
	}

	@Override
	protected boolean executeOp(Player op, String[] args) {
		return executeUser(op, args);
	}

	@Override
	protected boolean executeUser(Player player, String[] args) {
		if(args.length != 2 && args.length != 3)
			return false;
		
		String shopName = args[0];
		Double price = null;
		Integer quantity = 1;
		
		try{
			price = Double.parseDouble(args[1]);
		}catch(NumberFormatException e){
			player.sendMessage(ChatColor.RED+base.lang.parseFirstString(player, HShopLanguages.Command_ArgumentIsNotANumber));
			return true;
		}
		
		if(args.length == 3){
			try{
				quantity = Integer.parseInt(args[2]);
			}catch(NumberFormatException e){
				quantity = 1;
			}
		}
		
		CommandAddItem(player, shopName, price, quantity);
		return true;
	}

	private void CommandAddItem(Player player, String shopName, double price, int quantity){
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
		
		ItemStack IS = player.getItemInHand();
		if(IS.getType() == Material.AIR){
			player.sendMessage(ChatColor.BLUE+base.lang.parseFirstString(player, HShopLanguages.Command_HoldAnItemOnHandFirst));
			return;
		}
		
		ShopItem item = new ShopItem(IS.clone(), price, quantity);
		shop.addItem(item);
		
		HolographicShop.getInstance().lang.addString(item.toString());
		player.sendMessage(ChatColor.GREEN+base.lang.parseFirstString(player, HShopLanguages.Command_AddItemSuccess));
	}
}
