package org.holographicshop.constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.holographicshop.main.HShopLanguages;
import org.holographicshop.main.HolographicShop;
import org.holographicshop.main.HolographicShopConfig;

public abstract class Shop{
	static {
		ConfigurationSerialization.registerClass(ShopItem.class);
	}
	
	File rootPath = new File(HolographicShop.getInstance().getDataFolder(), "Shops");
	
	File dataPath;
	FileConfiguration data;
	
	String ownerName;
	String shopName;
	
	/////////////////////////////////////////
	HoloDisplay hologram;
	Block storage;
	Location chestLocation;
	
	ShopType type;
	
	final List<ShopItem> sellingItems = new ArrayList<ShopItem>();
	boolean isAdminShop;
	boolean isOpen;
	int currentPos = 0;
	
	public Shop(String ownerName, String shopName, ShopType type, Block block, boolean isAdminShop){
		this.ownerName = ownerName;
		this.shopName = shopName;
		this.isAdminShop = isAdminShop;
		this.type = type;
		
		if(!rootPath.exists()) rootPath.mkdirs();
		
		File userFolder = new File(rootPath, ownerName);
		if(!userFolder.exists()) userFolder.mkdirs();
		
		dataPath = new File(userFolder, shopName);
		if(!dataPath.exists())
			try {dataPath.createNewFile();
			} catch (IOException e) {e.printStackTrace();}
		
		data = YamlConfiguration.loadConfiguration(dataPath);
		/////////////////////////////////////////////////////////////
		
		storage = block;
		hologram = new HoloDisplay(this, block);
		chestLocation = block.getLocation();
		
		load();
		save();
		
		resetDisplay();
	}
	
	private void load(){
		int size = data.getInt("Shop.sellingItems.total", 0);
		
		for(int index = 0; index < size; index++){
			//old data
			if(data.isConfigurationSection("Shop.sellingItems."+index)){
				ConfigurationSection section = data.getConfigurationSection("Shop.sellingItems."+index);
				for(String key : section.getKeys(false)){
					ShopItem item = (ShopItem) section.get(key);
					sellingItems.add(item);
					
					item.setPrice(item.getPrice() * item.getQuantity());
				}
			}else{
				ShopItem item = (ShopItem) data.get("Shop.sellingItems."+index, null);
				if(item != null){
					sellingItems.add(item);
				}
			}
		}
		
		isOpen = data.getBoolean("Shop.isOpen", false);
		currentPos = data.getInt("Shop.currentPos", 0);
	}
	
	private void save(){
		data.set("Shop.sellingItems.total", sellingItems.size());
		
		int index = 0;
		for(ShopItem item : sellingItems){
			data.set("Shop.sellingItems."+index, null);
			data.set("Shop.sellingItems."+index, item);
			index++;
		}
		
		for(int i=index;i<54;i++) data.set("Shop.sellingItems."+i, null);
		
		data.set("Shop.isOpen", isOpen);
		data.set("Shop.currentPos", currentPos);
		
		try {
			data.save(dataPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getOwnerName() {
		return ownerName;
	}

	public String getShopName() {
		return shopName;
	}
	
	public boolean isOwner(Player player){
		if(player.hasPermission("holoshop.admin")) return true;
		
		return player.getName().equals(ownerName);
	}

	private void resetDisplay(){
		if(sellingItems.size() == 0) {
			hologram.emptyScreen();
			return;
		}
		
		ShopItem item = null;
		try{
			item = getItem(currentPos);
		}catch(IndexOutOfBoundsException e){
			this.currentPos = 0;
		}
		item = getItem(currentPos);
			
		hologram.setItem(item.getIS());
		hologram.setAmountAndPrice(item.getQuantity(), item.getPrice());
		hologram.setShopOpen(isOpen);
	}
	
	public void nextItem(){
		if(currentPos + 1 > sellingItems.size() - 1){
			currentPos = 0;
			resetDisplay();
			return;
		}
		
		currentPos++;
		resetDisplay();
	}
	
	public void preItem(){
		if(currentPos - 1 < 0){
			currentPos = sellingItems.size() < 1 ? 0 : sellingItems.size() - 1;
			resetDisplay();
			return;
		}
		
		currentPos--;
		resetDisplay();
	}
	
	public void increaseAmount(){
		ShopItem item = getItem(currentPos);
		if(item.getQuantity() + 1 > 64) return;
		
		item.setQuantity(item.getQuantity() + 1);
		resetDisplay();
	}
	
	public void decreaseAmount(){
		ShopItem item = getItem(currentPos);
		if(item.getQuantity() - 1 < 1) return;
		
		item.setQuantity(item.getQuantity() - 1);
		resetDisplay();
	}
	
	public void addItem(ShopItem item){
		if(sellingItems.add(item))
			save();
	}
	
	public void removeItem(int pos) throws IndexOutOfBoundsException{
		sellingItems.remove(pos);
		
		if(sellingItems.size() < 1){
			hologram.emptyScreen();
			isOpen = false;
		}else{
			currentPos--;
			resetDisplay();
		}
		
		save();
	}
	
	public void deleteHolo(){
		hologram.removeHolo();
	}
	
	public void toggleShopStat(){
		if(isOpen) isOpen = false;
		else isOpen = true;
		
		resetDisplay();
		
		save();
	}
	
	public ShopItem getItem(int pos) throws IndexOutOfBoundsException{
		return sellingItems.get(pos);
	}
	public void changeItemPrice(int pos, double price) throws IndexOutOfBoundsException{
		getItem(pos).setPrice(price);
		
		resetDisplay();
		
		save();
	}
	
	public final List<String> getAllItems(){
		List<String> names = new ArrayList<String>();
		for(ShopItem item : sellingItems){
			names.add(item.toString());
		}
		return names;
	}
	
	public boolean isOpen() {
		return isOpen;
	}

	public Location getChestLocation() {
		return chestLocation;
	}
	
	public ShopType getType() {
		return type;
	}

	public void setType(ShopType type) {
		this.type = type;
	}

	public boolean compare(Location chestLoc){
		if(!chestLocation.getWorld().getName().equals(chestLoc.getWorld().getName())) return false;
		if(chestLocation.getBlockX() != chestLoc.getBlockX()) return false;
		if(chestLocation.getBlockY() != chestLoc.getBlockY()) return false;
		if(chestLocation.getBlockZ() != chestLoc.getBlockZ()) return false;
		
		return true;
	}

	public void buyItemFrom(final Player player){
		HolographicShopConfig config = HolographicShop.getInstance().getPluginConfig();
		if(config.Shop_Transaction_RefuseCreative 
				&& player.getGameMode() == GameMode.CREATIVE
				&& !player.hasPermission("holographicshop.creativeexempt")){
			HolographicShop.getInstance().sendMessage(player, HShopLanguages.Shop_CreativeRefused);
			return;
		}
		
		buyItemFrom(player, true);
	}
	public abstract void buyItemFrom(final Player player, boolean canTrade);
	public void sellItemTo(Player player){
		HolographicShopConfig config = HolographicShop.getInstance().getPluginConfig();
		if(config.Shop_Transaction_RefuseCreative
				&& player.getGameMode() == GameMode.CREATIVE
				&& !player.hasPermission("holographicshop.creativeexempt")){
			HolographicShop.getInstance().sendMessage(player, HShopLanguages.Shop_CreativeRefused);
			return;
		}
		
		sellItemTo(player, true);
	}
	public abstract void sellItemTo(Player player, boolean canTrade);
	
	public abstract void onTouch(TouchEvent e);
	public abstract boolean onDestroy(BlockBreakEvent e);

	public static enum ShopType{
		SELL,BUY;
	}
}
