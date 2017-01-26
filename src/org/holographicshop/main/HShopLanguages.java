package org.holographicshop.main;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.generallib.pluginbase.PluginLanguage.Language;
import org.generallib.serializetools.FileSerialize;
import org.generallib.serializetools.exceptions.FileSerializeException;

public enum HShopLanguages implements Language{
	Holo_Quantity("QTT"),
	Holo_Total("Total"),
	Holo_Unit("$"),
	Holo_Empty("Empty"),
	Holo_Closed("Closed"),
	Holo_Click("Click"),
	Holo_Open("Open"),
	Holo_NoFurthurInformation("&cNo furthur information"), 
	Holo_AmountSelectionDisabled("&cManual amount selection is disabled."), 
	
	Shop_ShopIsNotOpen("Sorry! This shop is not opened yet."),
	Shop_InvalidPrice("Cannot buy item with this price!"),
	Shop_ItemBuySuccess("Successfully bought &6[${string} * ${integer}] &awith &6${double}"),
	Shop_NotEnoughMoney("Not enough money"), 
	Shop_NotEnoughItemsToSell("You don't have &6[${string} * ${integer}]"),
	Shop_YouDontHaveThisMuchItem("Amount of [${string}] you have &c${integer}"),
	Shop_ItemSellSuccess("You sold &6${string} * ${integer} &afor &6${double}"),
	Shop_YouCanBreakOnlyYourOwnShop("You can break only your own shop"),
	Shop_CreativeRefused("&cCannot trade while on creative mode!"),
	
	Shop_NotEnoughItemInChest("Sorry! not enough stock"), 
	Shop_NumOfItemsLeftInChest("Stock left &6${integer}"),
	Shop_Error_OwnerNotFound("Server Error! owner of shop not found."),
	Shop_Error_MoneyTransferUserToOwner("Server Error! could not send money to shop owner"),
	Shop_YouReceiveThisMuchMoney("You've received &6${double} &afor selling &6${string}"), 
	
	Command_Main_HelpDescription("Type /shop help &fto get more informations!"),
	Command_Help_AddItemDescription("Add the item on your hand to the <shopname> with <price>"),
	Command_Help_AddItemUsage("<shopname> <price> to add the item on your hand."),
	Command_Help_AddItemUsage2("<shopname> <price> <quantity> to add the item and set its quantity."),
	Command_Help_RemoveDescription("Remove the item from shop."),
	Command_Help_RemoveUsage("<shopname> <pos> to remove item."),
	Command_Help_RemoveUsage2("<pos> can be achieved by /shop list command."),
	Command_Help_ChangePriceDescription("Change price of item."),
	Command_Help_ChangePriceUsage("<shopname> <pos> <newprice> to change price"),
	Command_Help_ListDescription("Get list of items."), 
	Command_Help_ListUsage("<shopname> to see items with their associated <pos>."),
	Command_Help_ToggleDescription("Toggle the shop to open/close."), 
	Command_Help_ToggleUsage("<shopname> to change the status of the shop."),
	Command_NoSuchShopWithThatName("Could not find the shop with that name"), 
	Command_YouAreNotTheOwnerOfThatShop("You are not the owner of that shop"),
	Command_ArgumentIsNotANumber("Invailed Argument. Argument must be number"),
	Command_HoldAnItemOnHandFirst("I am pretty sure that nobody wants to buy AIR"),
	Command_AddItemSuccess("Successfully added item!"),
	Command_ArgumentIsOutOfRange("The argument you entered is out of bound"),
	Command_RemoveItemSuccess("Successfully removed item!"), 
	Command_ShopToggled("Successfully Toggled the shop!"), 
	Command_ShopState_Open("Open"), 
	Command_ShopState_Closed("Closed"), 
	Command_NothingInTheShop("There is nothing in the shop now"),
	Command_ChangeItemPriceSuccess(""),
	
	Manager_SecondLineIsNull("Second line cannot be null"), 
	Manager_FillSecondLineAndTryAgain("Please fill in the shopname and try again"),
	Manager_ShopNameTooLong("The shopname you entered is too long"),
	Manager_NameLengthMustBeSmallerThanEight("The length of shopname must be less than eight"),
	Manager_ShopNameAlreadyExist("That name is already occupied"),
	Manager_ShopCreateSuccess("Successfully created the new shop"),
	Manager_ThirdLineMustBeShopType("The thrid line must indicate the shoptype (BUY or SELL)"),
	Manager_PutShopTypeAndTryAgain("Please fill in the thired line and try again"),
	Manager_BUY_Description("Users pay money to buy stuff"), 
	Manager_SELL_Description("Users sell items to the shop owner"),
	Manager_ShopNotOwnedByYou("This shop is not owned by you"),
	
	Prompt_Confirmation("&dType &fok &dto &aconfirm &dor &fno &dto &cdecline."), 
	Prompt_SellingInfo("&1Selling &6[${string} * ${integer}] &afor &6[${double}]."),
	Prompt_BuyingInfo("&2Buying &6[${string} * ${integer}] &fwith &6[${double}]."),
	
	;
	
	private String[] defaultSring;
	
	private HShopLanguages(String... defaultString){
		this.defaultSring = defaultString;
	}

	@Override
	public String[] getEngDefault() {
		return defaultSring;
	}
}
