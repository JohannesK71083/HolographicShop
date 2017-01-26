package org.holographicshop.manager;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.generallib.pluginbase.PluginBase;
import org.generallib.pluginbase.PluginManager;
import org.holographicshop.main.HolographicShop;
import org.holographicshop.manager.prompts.ConfirmPrompt;

public class ConfirmManager extends PluginManager<HolographicShop> implements ConversationAbandonedListener {

	public ConfirmManager(HolographicShop base, int loadPriority) {
		super(base, loadPriority);
	}

	public void initiateConversation(Player player, Runnable future){
		if(player.isConversing())
			return;
		
		Conversation conv = new ConversationFactory(base)
				.thatExcludesNonPlayersWithMessage("Players only.")
				.withPrefix(new ConversationPrefix(){
					@Override
					public String getPrefix(ConversationContext arg0) {
						return "[Shop]";
					}
				})
				.addConversationAbandonedListener(this)
				.withEscapeSequence("no")
				.withTimeout(30)
				.withFirstPrompt(new ConfirmPrompt(future))
				.buildConversation(player);
		conv.begin();
	}

	@Override
	public void conversationAbandoned(ConversationAbandonedEvent arg0) {
		
	}

	@Override
	protected void onEnable() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDisable() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onReload() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
