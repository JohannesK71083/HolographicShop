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

public class SubCommandList extends SubCommand {

	public SubCommandList(PluginBase base, String permission) {
		super(base, permission,
				HShopLanguages.Command_Help_ListDescription, 
				new HShopLanguages[]{
						HShopLanguages.Command_Help_ListUsage
				},
				1,
				"list",
				"l", "li");
	}

	
	
	@Override
	protected boolean executeOp(Player op, String[] args) {
		// TODO Auto-generated method stub
		return executeUser(op, args);
	}



	@Override
	protected boolean executeUser(Player player, String[] args) {
		String shopName = args[0];
		
		CommandList(shopName, player);
		
		return true;
	}



	private void CommandList(String shopName, Player player){
		ShopManager manager = base.getManager(ShopManager.class);
		
		Shop shop = manager.getShopByShopname(shopName);
		if(shop == null){
			/*player.sendMessage(ChatColor.RED+"그러한 이름의 상점이 없습니다.");*/
			player.sendMessage(ChatColor.RED+base.lang.parseFirstString(player, HShopLanguages.Command_NoSuchShopWithThatName));
			return;
		}
		
		if(!shop.isOwner(player)){
		/*	player.sendMessage(ChatColor.RED+"그 상점의 주인이 아닙니다.");*/
			player.sendMessage(ChatColor.RED+base.lang.parseFirstString(player, HShopLanguages.Command_YouAreNotTheOwnerOfThatShop));
			return;
		}
		
		player.sendMessage(ChatColor.GRAY+"===========Item List==========");
		int index = 0;
		for(String mat : shop.getAllItems()){
			player.sendMessage(ChatColor.AQUA+""+index+". "+mat);
			index++;
		}
	}
}
