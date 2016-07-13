/**
 *  Smith.java
 *  Created on Jun 21, 2016 1:38:40 AM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import java.util.HashSet;

import net.wombatrpgs.ultima.Simulation;

/**
 * Blacksmith. Makes the swords and shields.
 */
public class Smith extends TownPlayer {

	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Smith(Simulation simulation) {
		super(simulation);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPreNightkill()
	 */
	@Override
	public void onPreNightkill() {
		super.onPreNightkill();
		
		// first priority is covering the exposed roles
		for (Player player : simulation.getPrioritizedNightkills()) {
			if (!player.hasShield() && !player.isWounded()) {
				player.grantShield();
				return;
			}
		}
		
		// we should probably take cover too
		if (!hasShield()) {
			grantShield();
			return;
		}
		
		// might as well randomly distribute
		HashSet<Player> validTargets = new HashSet<Player>(simulation.getPlayers());
		for (Player player : simulation.getPlayers()) {
			if (player.hasShield() || player.wounded) {
				validTargets.remove(player);
			}
		}
		if (validTargets.size() > 0) {
			Simulation.randomIn(validTargets).grantShield();
		}
	}
}
