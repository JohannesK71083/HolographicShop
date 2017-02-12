package org.holographicshop.constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.holographicshop.constants.Shop.ShopType;
import org.holographicshop.main.HShopLanguages;
import org.holographicshop.main.HolographicShop;
import org.holographicshop.main.HolographicShopConfig;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

public class HoloDisplay {
	final String qtt = HolographicShop.getInstance().lang.parseFirstString(
			HShopLanguages.Holo_Quantity);
	final String total = HolographicShop.getInstance().lang.parseFirstString(
			HShopLanguages.Holo_Total);
	final String unit = HolographicShop.getInstance().lang.parseFirstString(
			HShopLanguages.Holo_Unit);
	
	Shop instance;
	
	Hologram left;
	Hologram right;
	Hologram center;
	
	TextLine leftBtn;
	TextLine rightBtn;
	
	TextLine shopName;
	TextLine itemName;
	TextLine itemDetails;
	TextLine plusBtn;
	ItemLine itemBtn;
	TextLine minusBtn;
	TextLine itemAmountAndPrice;
	TextLine shopStat;
	
	public HoloDisplay(Shop instance, Block chest){
		this.instance = instance;
		
		Location loc = chest.getLocation();

		World w = loc.getWorld();
		double x = loc.getBlockX()+0.5;
		double y = loc.getBlockY()+3.8; 
		double z = loc.getBlockZ()+0.5;
		
		org.bukkit.material.Chest dir = (org.bukkit.material.Chest) chest.getState().getData();
		BlockFace facing = dir.getFacing();
		
		switch(facing){
		case NORTH:
			left = HologramsAPI.createHologram(HolographicShop.getInstance(), new Location(w,x+0.7,y-1.4,z));
			right = HologramsAPI.createHologram(HolographicShop.getInstance(), new Location(w,x-0.7,y-1.4,z));
			break;
		case SOUTH:
			left = HologramsAPI.createHologram(HolographicShop.getInstance(), new Location(w,x-0.7,y-1.4,z));
			right = HologramsAPI.createHologram(HolographicShop.getInstance(), new Location(w,x+0.7,y-1.4,z));
			break;
		case EAST:
			left = HologramsAPI.createHologram(HolographicShop.getInstance(), new Location(w,x,y-1.4,z+0.7));
			right = HologramsAPI.createHologram(HolographicShop.getInstance(), new Location(w,x,y-1.4,z-0.7));
			break;
		case WEST:
			left = HologramsAPI.createHologram(HolographicShop.getInstance(), new Location(w,x,y-1.4,z-0.7));
			right = HologramsAPI.createHologram(HolographicShop.getInstance(), new Location(w,x,y-1.4,z+0.7));
			break;
		default:
			left = HologramsAPI.createHologram(HolographicShop.getInstance(),new Location(w, x, y - 1.4, z - 0.7));
			right = HologramsAPI.createHologram(HolographicShop.getInstance(),new Location(w, x, y - 1.4, z + 0.7));
			break;
		}
		
		center = HologramsAPI.createHologram(HolographicShop.getInstance(), new Location(w,x,y,z));
		
		left.appendTextLine("");
		leftBtn = left.appendTextLine(ChatColor.GOLD+"◀");
		left.appendTextLine("");
		
		right.appendTextLine("");
		rightBtn = right.appendTextLine(ChatColor.GOLD+"▶");
		right.appendTextLine("");
		
		if(instance.isAdminShop){
			shopName = center.appendTextLine(ChatColor.YELLOW+"[OP] "+ChatColor.AQUA+instance.shopName
					+(instance.type == ShopType.BUY ? ChatColor.GOLD : ChatColor.GREEN)+" ("+instance.type+")");
		}else{
			shopName = center.appendTextLine(ChatColor.AQUA+instance.shopName
					+ChatColor.GOLD+" ("+instance.type+")");
		}

		HolographicShopConfig config = HolographicShop.getInstance().getPluginConfig();
		
		itemName = center.appendTextLine(ChatColor.DARK_GRAY+HolographicShop.getInstance().lang.parseFirstString(
				HShopLanguages.Holo_Empty));
		itemDetails = center.appendTextLine(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(
				HShopLanguages.Holo_Empty));
		if(config.Shop_Holo_allowManualAmountSelection)
			plusBtn = center.appendTextLine(ChatColor.GOLD+""+ChatColor.BOLD+"+");
		itemBtn = center.appendItemLine(new ItemStack(Material.BARRIER));
		if(config.Shop_Holo_allowManualAmountSelection)
			minusBtn = center.appendTextLine(ChatColor.GOLD+""+ChatColor.BOLD+"-");
		itemAmountAndPrice = center.appendTextLine(ChatColor.LIGHT_PURPLE+"-1");
		shopStat = center.appendTextLine(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(
				HShopLanguages.Holo_Closed));
		
		itemDetails.setTouchHandler(new itemDetailTouchHandler());
		
		leftBtn.setTouchHandler(new leftTouchHandler());
		rightBtn.setTouchHandler(new rightTouchHandler());
		if(config.Shop_Holo_allowManualAmountSelection){
			plusBtn.setTouchHandler(new plusTouchHandler());
			minusBtn.setTouchHandler(new minusTouchHandler());
		}
		itemBtn.setTouchHandler(new itemTouchHandler());
	}
	
	public void emptyScreen(){
		itemBtn.setItemStack(new ItemStack(Material.BARRIER));
		itemAmountAndPrice.setText(ChatColor.LIGHT_PURPLE+"-1");
		shopStat.setText(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(
				HShopLanguages.Holo_Closed));
	}
	
	public void setItem(ItemStack IS){
		if(IS.hasItemMeta() && IS.getItemMeta().getDisplayName() != null)
			itemName.setText(ChatColor.BLUE+IS.getItemMeta().getDisplayName()+ChatColor.GOLD+"x"+IS.getAmount());
		else
			itemName.setText(ChatColor.BLUE+IS.getType().name()+":"+IS.getDurability()+ChatColor.GOLD+"x"+IS.getAmount());
		itemDetails.setText(ChatColor.LIGHT_PURPLE+"["+HolographicShop.getInstance().lang.parseFirstString(
				HShopLanguages.Holo_Click)+"]");
		details.clear();
		
		if(IS.hasItemMeta()){
			if(IS.getItemMeta().getDisplayName() != null) details.add(IS.getItemMeta().getDisplayName());
			
			if(IS.getItemMeta().getLore() != null)
				for(String str : IS.getItemMeta().getLore()){
					details.add(str);
				}
			
			if(IS.getItemMeta().getEnchants() != null)
				for(Entry<Enchantment, Integer> entry : IS.getItemMeta().getEnchants().entrySet()){
					Enchantment ench = entry.getKey();
					int level = entry.getValue();
					
					details.add("[ench: "+ench.getName()+", lv: "+level+"]");
				}
		}
		
		detailPage = -1;
		itemBtn.setItemStack(IS);
	}
	
	public void setAmountAndPrice(int amount, double price){
		HolographicShop.getInstance().lang.addInteger(amount);
		HolographicShop.getInstance().lang.addDouble(price);
		itemAmountAndPrice.setText(HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Holo_AmountPriceFormat));
	}
	
	public void setShopOpen(boolean stat){
		if(stat) shopStat.setText(ChatColor.GREEN+HolographicShop.getInstance().lang.parseFirstString(
				HShopLanguages.Holo_Open));
		else shopStat.setText(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(
				HShopLanguages.Holo_Closed));
	}
	
	public void removeHolo(){
		this.left.delete();
		this.right.delete();
		this.center.delete();
	}
	
	private class leftTouchHandler implements TouchHandler{
		@Override
		public void onTouch(Player player) {
			instance.onTouch(new TouchEvent(player, TouchType.BTN_LEFT));
		}
	}
	
	private class rightTouchHandler implements TouchHandler{
		@Override
		public void onTouch(Player player) {
			instance.onTouch(new TouchEvent(player, TouchType.BTN_RIGHT));
		}
	}
	
	private class plusTouchHandler implements TouchHandler{
		@Override
		public void onTouch(Player player) {
			HolographicShopConfig config = HolographicShop.getInstance().getPluginConfig();
			if(!config.Shop_Holo_allowManualAmountSelection){
				HolographicShop.getInstance().sendMessage(player, HShopLanguages.Holo_NoFurthurInformation);
				return;
			}
			
			instance.onTouch(new TouchEvent(player, TouchType.BTN_PLUS));
		}
	}
	
	private class minusTouchHandler implements TouchHandler{
		@Override
		public void onTouch(Player player) {
			HolographicShopConfig config = HolographicShop.getInstance().getPluginConfig();
			if(!config.Shop_Holo_allowManualAmountSelection){
				
				return;
			}
			
			instance.onTouch(new TouchEvent(player, TouchType.BTN_MINUS));
		}
	}
	
	private class itemTouchHandler implements TouchHandler{
		@Override
		public void onTouch(Player player) {
			instance.onTouch(new TouchEvent(player, TouchType.BTN_ITEM));
		}
	}
	
	private List<String> details = new ArrayList<String>();
	private int detailPage = -1;
	
	private class itemDetailTouchHandler implements TouchHandler{
		@Override
		public void onTouch(Player player) {
			detailPage++;
			
			if(details.size() == 0) {
				//player.sendMessage(ChatColor.RED+"더 자세한 내용이 없습니다.");
				player.sendMessage(ChatColor.RED+HolographicShop.getInstance().lang.parseFirstString(player, HShopLanguages.Holo_NoFurthurInformation));
				return;
			}
			if(details.size() == detailPage) detailPage = 0;
			
			itemDetails.setText(ChatColor.LIGHT_PURPLE+"["
					+HolographicShop.getInstance().lang.parseFirstString(player, HShopLanguages.Holo_Click)
					+(detailPage+1)+"/"+details.size()+"] "+details.get(detailPage));
		}
	}
	
	enum TouchType{
		BTN_LEFT,BTN_RIGHT,BTN_PLUS,BTN_MINUS,BTN_ITEM;
	}
}
