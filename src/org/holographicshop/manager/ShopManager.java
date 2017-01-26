package org.holographicshop.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.generallib.pluginbase.PluginManager;
import org.holographicshop.constants.AdminShop;
import org.holographicshop.constants.Shop;
import org.holographicshop.constants.UserShop;
import org.holographicshop.constants.Shop.ShopType;
import org.holographicshop.main.HShopLanguages;
import org.holographicshop.main.HolographicShop;

public class ShopManager extends PluginManager<HolographicShop> implements Listener{

	public ShopManager(HolographicShop base, int loadPriority) {
		super(base, loadPriority);

		dataPath = new File(base.getDataFolder(), "registeredShops.yml");
		if(!dataPath.exists())
			try {
				dataPath.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		data = YamlConfiguration.loadConfiguration(dataPath);
	}

	private static Map<String, Shop> registeredShops_shopname = new ConcurrentHashMap<String, Shop>();
	
	private File dataPath;
	private FileConfiguration data;
	
	public void save(){
		for(Map.Entry<String, Shop> entry : registeredShops_shopname.entrySet()){
			try{
				String shopname = entry.getKey();
				String ownername = entry.getValue().getOwnerName();
				String shoptype = entry.getValue().getType().name();
				String location;
				
				Location loc = entry.getValue().getChestLocation();
				location = loc.getWorld().getName()+":"+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
				
				data.set("Shops."+shopname+".classname", entry.getValue().getClass().getName());
				data.set("Shops."+shopname+".ownername", ownername);
				data.set("Shops."+shopname+".location", location);
				data.set("Shops."+shopname+".shoptype", shoptype);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		try {
			data.save(dataPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void load(){
		ConfigurationSection section = data.getConfigurationSection("Shops");
		if(section == null) return;
		
		List<String> shopnameList = new ArrayList<String>();
		for(String key : section.getKeys(false)){
			if(!shopnameList.contains(key))
				shopnameList.add(key);
		}
		
		for(String shopname : shopnameList){
			String className = String.valueOf(section.get(shopname+".classname"));
			String ownername = String.valueOf(section.get(shopname+".ownername"));
			String location = String.valueOf(section.get(shopname+".location"));
			String shoptype = String.valueOf(section.get(shopname+".shoptype"));
			
			String[] data = location.split(":");
			World world = Bukkit.getWorld(data[0]);
			if(world == null) {
				base.getLogger().info("the chest data has invailed world name "+data[0]);
				base.getLogger().info("skipping shop "+shopname);
				continue;
			}
			
			data = data[1].split(",");
			int x = Integer.parseInt(data[0]);
			int y = Integer.parseInt(data[1]);
			int z = Integer.parseInt(data[2]);
			
			Location loc = new Location(world, x, y, z);
			if(loc.getBlock().getType() != Material.CHEST){
				base.getLogger().info("the chest was not found at "+loc);
				base.getLogger().info("skipping shop "+shopname);
				continue;
			}
			
			ShopType type;
			if((type = ShopType.valueOf(shoptype)) == null){
				base.getLogger().info("invailed shoptype "+shoptype);
				base.getLogger().info("skipping shop "+shopname);
				continue;
			}
			
			Block chest = loc.getBlock();
			try{
				if(className.equals(AdminShop.class.getName())){
					base.getLogger().info("AdminShop ["+shopname+"] successfully registered");
					registeredShops_shopname.put(shopname, new AdminShop(ownername, shopname, type, chest));
				}else if(className.equals(UserShop.class.getName())){
					base.getLogger().info("Shop ["+shopname+"] successfully registered");
					registeredShops_shopname.put(shopname, new UserShop(ownername, shopname, type, chest));
				}else{
					base.getLogger().info("Shop ["+shopname+"] has unknown className ["+className+"]");
				}
			}catch(Exception e){
				e.printStackTrace();
			}

		}
	}

	/**
	 * @param player
	 * @param shopname
	 * @param chest
	 * @return <b>true</b> on success or <b>false</b> if shopname already exist
	 */
	public boolean createAdminShop(Player player, String shopname, Shop.ShopType type, Block chest){
		if(registeredShops_shopname.containsKey(shopname)){
			return false;
		}
		
		////HolographicShop.logDebug("loc "+chest.getLocation());
		Shop shop = new AdminShop(player.getName(), shopname, type, chest);

		registeredShops_shopname.put(shopname, shop);
		save();
		return true;
	}
	
	/**
	 * @param player
	 * @param shopname
	 * @param chest
	 * @return <b>true</b> on success or <b>false</b> if shopname already exist
	 */
	public boolean createUserShop(Player player, String shopname, Shop.ShopType type, Block chest){
		if(registeredShops_shopname.containsKey(shopname)){
			return false;
		}
		
		//HolographicShop.logDebug("loc "+chest.getLocation());
		Shop shop = new UserShop(player.getName(), shopname, type, chest);
		
		registeredShops_shopname.put(shopname, shop);
		save();
		return true;
	}
	
	public Shop getShopByShopname(String shopName){
		return registeredShops_shopname.get(shopName);
	}
	
	@EventHandler
	public void onChestBreak(BlockBreakEvent e){
		if(e.getBlock().getType() != Material.CHEST)
			return;
		
		for(Map.Entry<String, Shop> entry : registeredShops_shopname.entrySet()){
			//HolographicShop.logDebug("ondest called");
			
			if(getShopByShopname(entry.getKey()).onDestroy(e)){
				data.set("Shops."+entry.getKey(), null);
				registeredShops_shopname.remove(entry.getKey());
			}
		}
		
		save();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignPlace(SignChangeEvent e){
		
		Block sign = e.getBlock();
		
		Location signLocation = sign.getLocation();
		Location chestLocation = new Location(signLocation.getWorld(),
				signLocation.getBlockX(), 
				signLocation.getBlockY() - 1,
				signLocation.getBlockZ());

		Block chest = chestLocation.getBlock();
		if(chest.getType() != Material.CHEST)
			return;

		if(e.getLine(0) == null) return;

		if(e.getLine(0).equalsIgnoreCase("[shop]")
				&& e.getPlayer().hasPermission("holographicshop.user")){
			
			if(e.getLine(1) == null || e.getLine(1).equals("")){
/*				e.getPlayer().sendMessage(ChatColor.RED+"두번째 줄은 빈 공간이어서는 안됩니다.");
				e.getPlayer().sendMessage(ChatColor.RED+"두번째 줄에 상점 이름을 넣고 다시 시도하세요.");*/
				e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_SecondLineIsNull));
				e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_FillSecondLineAndTryAgain));
				return;
			}
			
			if(e.getLine(1).length() > 8){
				/*e.getPlayer().sendMessage(ChatColor.RED+"상점 이름이 너무 깁니다.");
				e.getPlayer().sendMessage(ChatColor.RED+"8자 까지만 가능합니다.");*/
				e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_ShopNameTooLong));
				e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_NameLengthMustBeSmallerThanEight));
				return;
			}
/*			
			if(e.getLine(2) == null || e.getLine(2).equals("")
					|| (!e.getLine(2).equals("BUY") && !e.getLine(2).equals("SELL"))){
				e.getPlayer().sendMessage(ChatColor.RED+"세번째 줄은 상점의 종류를 지정 해야 합니다..");
				e.getPlayer().sendMessage(ChatColor.RED+"세번째 줄에 상점 종류를 넣고 다시 시도하세요.");
				e.getPlayer().sendMessage(ChatColor.GREEN+"BUY"+ChatColor.GRAY+" - "
						+ChatColor.WHITE+"BUY상점 즉 가게 주인이 물건을 파는 상점 입니다.");
				e.getPlayer().sendMessage(ChatColor.GREEN+"BUY"+ChatColor.GRAY+" - "
						+ChatColor.WHITE+"SELL상점 즉 가게 주인이 물건을 사들이는 상점 입니다.");
				return;
			}*/
			
			ShopType type = ShopType.BUY;
			
			if(!createUserShop(e.getPlayer(), e.getLine(1), type, chest)){
				/*e.getPlayer().sendMessage(ChatColor.RED+"그 상점 이름은 이미 존재 합니다.");*/
				e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_ShopNameAlreadyExist));
			}else{
				/*e.getPlayer().sendMessage(ChatColor.GREEN+"상점이 생성 되었습니다! /shop help 로 명령어를 알아보세요!");*/
				e.getPlayer().sendMessage(ChatColor.GREEN+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_ShopCreateSuccess));
				signLocation.getBlock().setType(Material.AIR);
			}
		}else if(e.getLine(0).equalsIgnoreCase("[adminshop]")
				&& e.getPlayer().hasPermission("holographicshop.admin")){
			
			if(e.getLine(1) == null || e.getLine(1).equals("")){
				e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_SecondLineIsNull));
				e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_FillSecondLineAndTryAgain));
				return;
			}
			
			if(e.getLine(1).length() > 8){
				e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_ShopNameTooLong));
				e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_NameLengthMustBeSmallerThanEight));
				return;
			}
			
			if(e.getLine(2) == null || e.getLine(2).equals("")
					|| (!e.getLine(2).equals("BUY") && !e.getLine(2).equals("SELL"))){
/*				e.getPlayer().sendMessage(ChatColor.RED+"세번째 줄은 상점의 종류를 지정 해야 합니다..");
				e.getPlayer().sendMessage(ChatColor.RED+"세번째 줄에 상점 종류를 넣고 다시 시도하세요.");
				e.getPlayer().sendMessage(ChatColor.GREEN+"BUY"+ChatColor.GRAY+" - "
						+ChatColor.WHITE+"BUY상점 즉 가게 주인이 물건을 파는 상점 입니다.");
				e.getPlayer().sendMessage(ChatColor.GREEN+"BUY"+ChatColor.GRAY+" - "
						+ChatColor.WHITE+"SELL상점 즉 가게 주인이 물건을 사들이는 상점 입니다.");*/
				e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_ThirdLineMustBeShopType));
				e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_PutShopTypeAndTryAgain));
				e.getPlayer().sendMessage(ChatColor.GREEN+"BUY"+ChatColor.GRAY+" - "
						+ChatColor.WHITE+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_BUY_Description));
				e.getPlayer().sendMessage(ChatColor.GREEN+"SELL"+ChatColor.GRAY+" - "
						+ChatColor.WHITE+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_SELL_Description));
				return;
			}
			
			ShopType type = ShopType.valueOf(e.getLine(2));
			
			if(!createAdminShop(e.getPlayer(), e.getLine(1), type, chest)){
				/*e.getPlayer().sendMessage(ChatColor.RED+"그 상점 이름은 이미 존재 합니다.");*/
				e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_ShopNameAlreadyExist));
			}else{
				/*e.getPlayer().sendMessage(ChatColor.GREEN+"상점이 생성 되었습니다! /shop help 로 명령어를 알아보세요!");*/
				e.getPlayer().sendMessage(ChatColor.GREEN+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_ShopCreateSuccess));
				signLocation.getBlock().setType(Material.AIR);
			}
		}else{
			return;
		}

	}
	
	@EventHandler
	public void onChestOpen(InventoryOpenEvent e){
		Inventory inv = e.getInventory();
		InventoryHolder holder = inv.getHolder();
		if(holder instanceof Chest){
			Chest chest = (Chest) holder;
			
			for(Map.Entry<String, Shop> entry : registeredShops_shopname.entrySet()){
				//HolographicShop.logDebug("onOpen called");
				
				Shop shop = getShopByShopname(entry.getKey());
				if(shop.compare(chest.getLocation())){
					//HolographicShop.logDebug("booleans: "+shop.isOwner((Player) e.getPlayer())+" "+e.getPlayer().hasPermission("holographicshop.admin"));
					if(!shop.isOwner((Player) e.getPlayer()) && !e.getPlayer().hasPermission("holographicshop.admin")){
						/*e.getPlayer().sendMessage(ChatColor.RED+"당신 소유의 상점이 아닙니다.");*/
						e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_ShopNotOwnedByYou));
						e.setCancelled(true);
					}
				}
			}
		}else if(holder instanceof DoubleChest){
			DoubleChest dchest = (DoubleChest) holder;
			Chest chestleft = (Chest) dchest.getLeftSide();
			Chest chestright = (Chest) dchest.getRightSide();
			
			for(Map.Entry<String, Shop> entry : registeredShops_shopname.entrySet()){
				//HolographicShop.logDebug("onOpen called (doublechest)");
				
				Shop shop = getShopByShopname(entry.getKey());
				if((shop.compare(chestleft.getLocation()) ||
						shop.compare(chestright.getLocation()))){
					if(!shop.isOwner((Player) e.getPlayer()) && !e.getPlayer().hasPermission("holographicshop.admin")){
						e.getPlayer().sendMessage(ChatColor.RED+base.lang.parseFirstString(e.getPlayer(), HShopLanguages.Manager_ShopNotOwnedByYou));
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler 
	public void onWorldAutoSave(WorldSaveEvent e){
		new BukkitRunnable(){
			@Override
			public void run() {
				save();
			}
		}.runTask(base);
	}

	@Override
	protected void onEnable() throws Exception {
		load();
	}

	@Override
	protected void onDisable() throws Exception {
		save();
	}

	@Override
	protected void onReload() throws Exception {
		save();
		
		synchronized(registeredShops_shopname){
			for(Entry<String, Shop> entry : registeredShops_shopname.entrySet()){
				entry.getValue().deleteHolo();
			}
			registeredShops_shopname.clear();
		}
		
		load();
	}
}
