/**
 *  Faction.java
 *  Created on Jun 20, 2016 8:32:15 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

/**
 * Town or scum?
 */
public enum Faction {
	
	TOWN		("town"),
	MAFIA		("mafia"),
	SK			("bad chef"),
	JOKER		("breadcrumbed"),
	TRUE_RNG	("the flip of a coin"),
	RNG			("mafia, provided their luck doesn't suck");
	
	private String displayName;
	
	/**
	 * Internal constructor.
	 * @param	displayName		The human-readable faction name
	 */
	Faction(String displayName) {
		this.displayName = displayName;
	}
	
	/** @return The human-readable name of the faction */
	public String getDisplayName() {
		return displayName;
	}

}
