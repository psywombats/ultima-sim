/**
 *  Roleblocker.java
 *  Created on Feb 15, 2018 11:58:49 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import net.wombatrpgs.ultima.Simulation;

/**
 * Blocks town roles. Minus the demon bits.
 */
public class Roleblocker extends MafiaPlayer {
	
	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Roleblocker(Simulation simulation) {
		super(simulation);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPreNightkill()
	 */
	@Override
	public void onPreNightkill() {
		super.onPreNightkill();
		
		// the "umineko rule"
		if (simulation.getMafia().size() == 1) {
			return;
		}
		
		Player target = Simulation.randomIn(simulation.getTown());
		target.nullified = true;
		simulation.storyLog(this + " blocked " + target + ".");
	}

	/**
	 * @see net.wombatrpgs.ultima.players.MafiaPlayer#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "Roleblocker";
	}
}
