/**
 *  SerialKiller.java
 *  Created on Jul 23, 2016 12:37:05 AM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import java.util.HashSet;

import net.wombatrpgs.ultima.Simulation;

/**
 * Unaligned player with a nightkill.
 */
public class SerialKiller extends Player {
	
	// nightkill only succeeds at this percent, otherwise noop
	private static float SUCCESS_RATE = 1.0f;

	/**
	 * Inherited constructor.
	 * @param	simulation			The simulation this player is a part of
	 */
	public SerialKiller(Simulation simulation) {
		super(simulation, Faction.SK);
	}
	
	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPostNightkill()
	 */
	@Override public void onPostNightkill() {
		if (Simulation.chance(SUCCESS_RATE)) {
			HashSet<Player> legal = new HashSet<Player>(simulation.getPlayers());
			legal.remove(this);
			Player target = Simulation.randomIn(legal);
			if (target != null) {
				target.attemptNightkill(false);
			}
		}
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "Serial Killer";
	}
}
