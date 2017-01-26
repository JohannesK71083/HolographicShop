package org.holographicshop.constants;

import org.bukkit.entity.Player;
import org.holographicshop.constants.HoloDisplay.TouchType;

public class TouchEvent {
	private Player player;
	private TouchType type;
	public TouchEvent(Player player, TouchType type) {
		this.player = player;
		this.type = type;
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public TouchType getType() {
		return type;
	}
	public void setType(TouchType type) {
		this.type = type;
	}
}
