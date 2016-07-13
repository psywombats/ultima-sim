/**
 *  Thief.java
 *  Created on Jun 22, 2016 1:36:33 AM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import net.wombatrpgs.ultima.Simulation;

/**
 * Steals items from town!
 */
public class Thief extends MafiaPlayer {

	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is part of
	 */
	public Thief(Simulation simulation) {
		super(simulation);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPostDaykill()
	 */
	@Override public void onPostDaykill() {
		
		// if there are prioritized nightkills, make sure they won't be shielded
		if (simulation.getPrioritizedNightkills().size() > 0) {
			stealFrom(Simulation.randomIn(simulation.getPrioritizedNightkills()));
			return;
		}
		
		// otherwise f u town
		stealFrom(Simulation.randomIn(simulation.getTown()));
	}
	
	/**
	 * Takes items from the victim!
	 * @param	player			The player to steal from
	 */
	private void stealFrom(Player player) {
		player.hasShield = false;
		player.hasSword = false;
	}
}
