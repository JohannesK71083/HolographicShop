package org.holographicshop.constants;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.generallib.pluginbase.api.VaultSupport;
import org.holographicshop.main.HShopLanguages;
import org.holographicshop.main.HolographicShop;
import org.holographicshop.main.HolographicShopConfig;
import org.holographicshop.manager.ConfirmManager;

public class UserShop extends Shop {

	public UserShop(String ownerName, String shopName, ShopType type, Block chest) {
		super(ownerName, shopName, type, chest, false);
	}

	@Override
	public void buyItemFrom(Player player, boolean canTrade) {
		
	}
	
	@Override
	public void sellItemTo(final Player player, boolean canTrade) {
		if(!isOpen){
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
		
		Runnable run =  new Runnable(){
			@Override
			public void run() {
				int numTotal = 0;
				Set<Entry<Integer, ? extends ItemStack>> sets = new HashSet<Entry<Integer, ? extends ItemStack>>();
				for(Entry<Integer, ? extends ItemStack> entry : ((Chest)storage.getState()).getInventory().all(item.getIS().getType()).entrySet()){
					//int slotID = entry.getKey();
					ItemStack IS = entry.getValue();

					//not similar
					if(!item.getIS().isSimilar(IS))
						continue;
			
					numTotal += IS.getAmount();
					sets.add(entry);
				}
				
				int totalAmount = item.getQuantity() * item.getIS().getAmount();
				
				if(numTotal < totalAmount){
					/*player.sendMessage(ChatColor.RED+"상점에 물품이 부족 합니다.");
					player.sendMessage(ChatColor.RED+"남은 수량: "+numTotal);*/
					player.sendRawMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_NotEnoughItemInChest));
					
					HolographicShop.getInstance().lang.addInteger(numTotal);
					player.sendRawMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_NumOfItemsLeftInChest));
					return;
				}

				OfflinePlayer shopOwner = Bukkit.getOfflinePlayer(getOwnerName());
				if(shopOwner == null){
					/*player.sendMessage(ChatColor.RED+"치명적인 에러 발생! 어드민에게 문의하세요!");*/
					player.sendRawMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_Error_OwnerNotFound));
					return;
				}
				
				VaultSupport vault = HolographicShop.getInstance().APISupport.getAPI("Vault");
				
				if(!(vault.economy.withdrawPlayer(player, priceCalc).transactionSuccess())){
					player.sendRawMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_NotEnoughMoney));
					return;
				}
				
				if(!(vault.economy.depositPlayer(shopOwner, priceCalc).transactionSuccess())){
		/*			player.sendMessage(ChatColor.RED+"서버에 문제가 발생해 상점 주인의 계좌에 돈을 보내지 못했습니다.");
					player.sendMessage(ChatColor.RED+"어드민에게 알려주세요.");*/
					player.sendRawMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_Error_MoneyTransferUserToOwner));
					vault.economy.depositPlayer(player, priceCalc);
					return;
				}
				
				int collecting = 0;
				for(Map.Entry<Integer, ? extends ItemStack> entry : sets){
					collecting += entry.getValue().getAmount();
					
					if(collecting > totalAmount){
						((Chest)storage.getState()).getInventory().getItem(entry.getKey()).setAmount(collecting - totalAmount);
						break;
					}else if(collecting == totalAmount){
						((Chest)storage.getState()).getInventory().clear(entry.getKey());
						break;
					}else{
						((Chest)storage.getState()).getInventory().clear(entry.getKey());
					}
				}
				
				for(int i = 0; i < item.getQuantity(); i++)
					player.getInventory().addItem(item.getIS());
				
				/*player.sendMessage(ChatColor.GREEN+"성공적으로 ["+item.getMat()+":"+item.getData()+"] ["+amount+"] 개 를 ["
						+ item.getPrice()*amount+"] 원에 구입 했습니다.");*/
				HolographicShop.getInstance().lang.addString(item.toSimpleString());
				HolographicShop.getInstance().lang.addInteger(item.getQuantity());
				HolographicShop.getInstance().lang.addDouble(priceCalc);
				player.sendRawMessage(ChatColor.GREEN+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_ItemBuySuccess));
				
				if(shopOwner.isOnline()){
					/*((Player)shopOwner).sendMessage(ChatColor.GREEN+"성공적으로 ["+item.getMat()+":"+item.getData()+"] "
							+ "를 ["+amount+"] "+ "개 판매 했습니다.");
					((Player)shopOwner).sendMessage(ChatColor.GOLD+"["+amount*item.getPrice()+"]원 "
							+ ChatColor.GREEN+" 을 받았습니다.");*/
					HolographicShop.getInstance().lang.addString(item.toSimpleString());
					HolographicShop.getInstance().lang.addInteger(item.getQuantity());
					HolographicShop.getInstance().lang.addDouble(priceCalc);
					((Player)shopOwner).sendRawMessage(ChatColor.GREEN+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_ItemSellSuccess));
					
					HolographicShop.getInstance().lang.addString(item.toSimpleString());
					HolographicShop.getInstance().lang.addDouble(priceCalc);
					((Player)shopOwner).sendRawMessage(ChatColor.GOLD+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_YouReceiveThisMuchMoney));
				}
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
			sellItemTo(e.getPlayer());
			break;
		}
	}

	@Override
	public boolean onDestroy(BlockBreakEvent e) {
		Location chestLoc = e.getBlock().getLocation();
		
		if(!compare(chestLoc)) return false;
		
		if(!this.isOwner(e.getPlayer())&&
				!e.getPlayer().hasPermission("holoshop.admin")) {
			e.getPlayer().sendMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Shop_YouCanBreakOnlyYourOwnShop));
			
			e.setCancelled(true);
			return false;
		}
		////////////////////////////////////////////////////////////////////////////////////
		
		hologram.removeHolo();
		dataPath.delete();
		
		return true;
	}

}
