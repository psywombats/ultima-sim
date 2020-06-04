/**
 *  GameRules.java
 *  Created on Jun 20, 2016 9:32:14 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.rules;

import java.util.HashMap;
import java.util.Map;

import net.wombatrpgs.ultima.players.SpecialRole;

/**
 * Game rules struct.
 */
public class GameRules {
	
	/** The total number of players in the game */
	public int playerCount;
	
	/** The number of players that are mafiosi */
	public int mafiaCount;
	
	/** The number of "critical" players that can't all die */
	public int agentCount;
	
	/** Is the special role enabled? */
	public Map<SpecialRole, Boolean> enabledRoles;
	
	/** Town can lynch by majority vote and else votes nobody */
	public boolean majorityVotesOnly = true;
	
	/**
	 * Initializes an empty ruleset. Set via properties.
	 */
	public GameRules() {
		enabledRoles = new HashMap<SpecialRole, Boolean>();
		for (SpecialRole role : SpecialRole.values()) {
			enabledRoles.put(role, false);
		}
	}
}
