package org.holographicshop.manager.prompts;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.holographicshop.main.HShopLanguages;
import org.holographicshop.main.HolographicShop;

public class ConfirmPrompt extends ValidatingPrompt {
	private Runnable future;
	
	public ConfirmPrompt(Runnable future) {
		this.future = future;
	}

	@Override
	public String getPromptText(ConversationContext arg0) {
		if (arg0.getForWhom() instanceof CommandSender)
			return HolographicShop.getInstance().lang.parseFirstString((CommandSender) arg0.getForWhom(),
					HShopLanguages.Prompt_Confirmation);
		else
			return HolographicShop.getInstance().lang.parseFirstString(HShopLanguages.Prompt_Confirmation);
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1) {
		if(arg1.equalsIgnoreCase("ok")){
			future.run();
			return END_OF_CONVERSATION;
		}else if(arg1.equalsIgnoreCase("no")){
			return END_OF_CONVERSATION;
		}else{
			return this;
		}
	}

	@Override
	protected boolean isInputValid(ConversationContext arg0, String arg1) {
		return arg1.equalsIgnoreCase("ok") || arg1.equalsIgnoreCase("no");
	}

}
