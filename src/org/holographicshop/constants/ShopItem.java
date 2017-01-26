package org.holographicshop.constants;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class ShopItem implements ConfigurationSerializable{
	private ItemStack IS;
	private double price;
	private int quantity;
	
	private ShopItem(){}
	public ShopItem(ItemStack IS, double price, int quantity){
		this.IS = IS.clone();
		this.price = price;
		this.quantity = quantity;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return IS.getType().name()+":"+IS.getDurability()+"x"+IS.getAmount()+" "+IS.getItemMeta();
	}
	
	public String toSimpleString(){
		if(IS.hasItemMeta() && IS.getItemMeta().getDisplayName() != null)
			return IS.getItemMeta().getDisplayName()+"x"+IS.getAmount();
		else
			return IS.getType().name()+":"+IS.getDurability()+"x"+IS.getAmount();
	}
	public ItemStack getIS(){
		return IS;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((IS == null) ? 0 : IS.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShopItem other = (ShopItem) obj;
		if (IS == null) {
			if (other.IS != null)
				return false;
		} else if (!IS.equals(other.IS))
			return false;
		return true;
	}
	
	@Override
	public Map<String, Object> serialize(){
		Map<String, Object> ser = new HashMap<String, Object>();
		
		ser.put("IS", this.IS);
		ser.put("price", this.price);
		ser.put("quantity", this.quantity);
		
		return ser;
	}
	
	public ShopItem(Map<String, Object> ser){
		this.IS = (ItemStack) ser.getOrDefault("IS", new ItemStack(Material.AIR));
		this.price = (double) ser.getOrDefault("price", 0);
		this.quantity = (int) ser.getOrDefault("quantity", 1);
	}
	
}
