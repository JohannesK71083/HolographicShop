package org.holographicshop.constants;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.generallib.pluginbase.api.VaultSupport;
import org.holographicshop.main.HShopLanguages;
import org.holographicshop.main.HolographicShop;
import org.holographicshop.main.HolographicShopConfig;
import org.holographicshop.manager.ConfirmManager;

public class AdminShop extends Shop {

	public AdminShop(String ownerName, String shopName, ShopType type, Block chest) {
		super(ownerName, shopName, type, chest, true);
	}

	@Override
	public void sellItemTo(final Player player, boolean canTrade) {
		if(!isOpen){
			//player.sendMessage(ChatColor.RED+"이 상점은 현재 영업중 이 아닙니다.");
			player.sendMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_ShopIsNotOpen));
			return;
		}
		
		final ShopItem item = getItem(currentPos);
		
		if(item.getPrice() <= 0){
			HolographicShop.getInstance().sendMessage(player, HShopLanguages.Shop_InvalidPrice);
			return;
		}
		
		final double priceCalc = item.getPrice() * item.getQuantity();
		
		HolographicShop.getInstance().lang.addString(item.toSimpleString());
		HolographicShop.getInstance().lang.addInteger(item.getQuantity());
		HolographicShop.getInstance().lang.addDouble(priceCalc);
		player.sendMessage(ChatColor.GREEN+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Prompt_BuyingInfo));
		
		Runnable run = new Runnable(){
			@Override
			public void run() {
				VaultSupport vault = HolographicShop.getInstance().APISupport.getAPI("Vault");
				
				EconomyResponse response = vault.economy.withdrawPlayer(player, priceCalc);
				
				if(response.transactionSuccess()){
					for(int i = 0; i < item.getQuantity(); i++)
						player.getInventory().addItem(item.getIS());
					
		/*			player.sendMessage(ChatColor.GREEN+"성공적으로 ["+item.getMat()+"] ["+item.getAmount()+"] 개 를 ["
							+ item.getPrice()*item.getAmount()+"] 원에 구입 했습니다.");*/
					HolographicShop.getInstance().lang.addString(item.toSimpleString());
					HolographicShop.getInstance().lang.addInteger(item.getQuantity());
					HolographicShop.getInstance().lang.addDouble(priceCalc);
					player.sendRawMessage(ChatColor.GREEN+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_ItemBuySuccess));
				}else{
					//player.sendMessage(ChatColor.RED+"돈이 부족합니다.");
					player.sendRawMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_NotEnoughMoney));
				}
			}
		};
		
		ConfirmManager manager = HolographicShop.getInstance().getManager(ConfirmManager.class);
		HolographicShopConfig config = HolographicShop.getInstance().getPluginConfig();
		
		if(config.Shop_Transaction_VerificationRequired)
			manager.initiateConversation(player, run);
		else run.run();
	}

	public void buyItemFrom(final Player player, boolean canTrade) {
		if(!isOpen){
			player.sendMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_ShopIsNotOpen));
			return;
		}
		
		final ShopItem item = getItem(currentPos);
		
		final double priceCalc = item.getPrice() * item.getQuantity();
		
		HolographicShop.getInstance().lang.addString(item.toSimpleString());
		HolographicShop.getInstance().lang.addInteger(item.getQuantity());
		HolographicShop.getInstance().lang.addDouble(priceCalc);
		player.sendMessage(ChatColor.GREEN+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Prompt_SellingInfo));
		
		Runnable run = new Runnable(){
			@Override
			public void run() {
				VaultSupport vault = HolographicShop.getInstance().APISupport.getAPI("Vault");
				
				Inventory inv = player.getInventory();
				
				int totalAmount = item.getQuantity() * item.getIS().getAmount();
				
				int numTotal = 0;
				final Set<Entry<Integer, ? extends ItemStack>> sets = new HashSet<Entry<Integer, ? extends ItemStack>>();
				for(Entry<Integer, ? extends ItemStack> entry : inv.all(item.getIS().getType()).entrySet()){
					//int slotID = entry.getKey();
					ItemStack IS = entry.getValue();

					//not similar
					if(!item.getIS().isSimilar(IS))
						continue;
			
					numTotal += IS.getAmount();
					sets.add(entry);
				}
				
				if(numTotal < totalAmount){
		/*			player.sendMessage(ChatColor.RED+"["+item.getMat()+":"+item.getData()
						+"] 를 ["+item.getAmount()+"]개 나 가지고 있지 않습니다.");
					player.sendMessage(ChatColor.GOLD+"가지고있는 ["
						+item.getMat()+":"+item.getData()+"] 의 수: "+numTotal);*/
					
					HolographicShop.getInstance().lang.addString(item.toSimpleString());
					HolographicShop.getInstance().lang.addInteger(item.getQuantity());
					player.sendRawMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_NotEnoughItemsToSell));
					
					HolographicShop.getInstance().lang.addString(item.getIS().getType()+":"+item.getIS().getDurability());
					HolographicShop.getInstance().lang.addInteger(numTotal);
					player.sendRawMessage(ChatColor.GOLD+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_YouDontHaveThisMuchItem));
					return;
				}
				
				EconomyResponse response = vault.economy.depositPlayer(player, item.getPrice());
				if(!response.transactionSuccess()){
					//max money?
					player.sendRawMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_NotEnoughMoney));
					return;
				}
				
				int collecting = 0;
				for(Entry<Integer, ? extends ItemStack> entry : sets){
					int slotID = entry.getKey();
					ItemStack IS = entry.getValue();
					
					collecting += IS.getAmount();
					
					if(collecting > totalAmount){
						inv.getItem(slotID).setAmount(collecting - totalAmount);
						break;
					}else if(collecting == totalAmount){
						inv.clear(slotID);
						break;
					}else{
						inv.clear(slotID);
					}
				}
				
				/*player.sendMessage(ChatColor.GREEN+"성공적으로 ["+item.getMat()+"] ["+item.getAmount()+"] 개 를 ["
						+ item.getPrice()*item.getAmount()+"] 원에 판매 했습니다.");*/
				
				HolographicShop.getInstance().lang.addString(item.toSimpleString());
				HolographicShop.getInstance().lang.addInteger(item.getQuantity());
				HolographicShop.getInstance().lang.addDouble(item.getPrice());
				player.sendRawMessage(ChatColor.GREEN+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_ItemSellSuccess));
			}
		};
		
		ConfirmManager manager = HolographicShop.getInstance().getManager(ConfirmManager.class);
		HolographicShopConfig config = HolographicShop.getInstance().getPluginConfig();
		
		if(config.Shop_Transaction_VerificationRequired)
			manager.initiateConversation(player, run);
		else
			run.run();
	}

	@Override
	public void onTouch(TouchEvent e) {
		if(!isOpen){
			e.getPlayer().sendMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_ShopIsNotOpen));
			return;
		}
		
		//BTN_LEFT,BTN_RIGHT,BTN_PLUS,BTN_MINUS,BTN_ITEM;
		switch(e.getType()){
		case BTN_LEFT:
			nextItem();
			break;
		case BTN_RIGHT:
			preItem();
			break;
		case BTN_PLUS:
			increaseAmount();
			break;
		case BTN_MINUS:
			decreaseAmount();
			break;
		case BTN_ITEM:
			if(type == ShopType.BUY){
				sellItemTo(e.getPlayer());
				break;
			}else if(type == ShopType.SELL){
				buyItemFrom(e.getPlayer());
				break;
			}else{
				Bukkit.getLogger().info("["+shopName+"] unknown shop type: "+type);
				break;
			}
		}
	}

	@Override
	public boolean onDestroy(BlockBreakEvent e) {
		Location chestLoc = e.getBlock().getLocation();
		
		if(!compare(chestLoc)) return false;
		
		if(!this.isOwner(e.getPlayer())&&
				!e.getPlayer().hasPermission("holoshop.admin")) {
			/*e.getPlayer().sendMessage(ChatColor.RED+"자신이 소유한 상점만 파괴 할 수 있습니다.");*/
			e.getPlayer().sendMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(e.getPlayer(), HShopLanguages.Shop_YouCanBreakOnlyYourOwnShop));
			e.setCancelled(true);
			return false;
		}
		////////////////////////////////////////////////////////////////////////////////////
		
		hologram.removeHolo();
		dataPath.delete();
		
		return true;
	}

}
