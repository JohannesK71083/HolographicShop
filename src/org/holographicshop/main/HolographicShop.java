package org.holographicshop.main;

import org.generallib.pluginbase.PluginBase;
import org.generallib.pluginbase.PluginLanguage.Language;
import org.generallib.pluginbase.PluginManager;
import org.holographicshop.commands.SubCommandAdditem;
import org.holographicshop.commands.SubCommandChangeprice;
import org.holographicshop.commands.SubCommandList;
import org.holographicshop.commands.SubCommandRemove;
import org.holographicshop.commands.SubCommandToggle;
import org.holographicshop.manager.ConfirmManager;
import org.holographicshop.manager.ShopManager;

public class HolographicShop extends PluginBase{
	private static HolographicShop instance;
	
	public HolographicShop() {
		super(new HolographicShopConfig(), "hs", "holographicshop.admin");
		
		instance = this;
		
		registerLanguages();
		registerManagers();
		registerCommands();
	}

	public static HolographicShop getInstance() {
		return instance;
	}

	private void registerCommands() {
		this.executor.addCommand(new SubCommandAdditem(this, null));
		this.executor.addCommand(new SubCommandChangeprice(this, null));
		this.executor.addCommand(new SubCommandList(this, null));
		this.executor.addCommand(new SubCommandRemove(this, null));
		this.executor.addCommand(new SubCommandToggle(this, null));
	}

	private void registerManagers() {
		this.registerManager(new ShopManager(this, PluginManager.NORM_PRIORITY));
		this.registerManager(new ConfirmManager(this, PluginManager.NORM_PRIORITY));
	}

	private void registerLanguages() {
		for(Language lang : HShopLanguages.values()){
			this.lang.registerLanguage(lang);
		}
	}
}
