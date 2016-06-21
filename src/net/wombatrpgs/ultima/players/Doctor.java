/**
 *  Doctor.java
 *  Created on Jun 20, 2016 10:35:28 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import net.wombatrpgs.ultima.Simulation;

/**
 * Prevents nightkills.
 */
public class Doctor extends TownPlayer {

	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Doctor(Simulation simulation) {
		super(simulation);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPreNightkill()
	 */
	@Override
	public void onPreNightkill() {
		super.onPreNightkill();
		
		if (simulation.getPrioritizedDaykills().size() > 0) {
			Simulation.randomIn(simulation.getPrioritizedDaykills()).protect();
		} else {
			protect();
		}
	}
}
