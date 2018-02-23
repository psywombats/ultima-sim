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
	 * @see net.wombatrpgs.ultima.players.Player#onPostDaykill()
	 */
	@Override
	public void onPostDaykill() {
		super.onPostDaykill();
		
		// the "umineko rule"
//		if (simulation.getMafia().size() == 1) {
//			return;
//		}
		
		Simulation.randomIn(simulation.getTown()).visitFrom(this);
		visiting.nullify();
		simulation.storyLog(this + " blocked " + visiting + ".");
	}
	
	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#role()
	 */
	@Override
	protected SpecialRole role() {
		return SpecialRole.ROLEBLOCKER;
	}
	
	/**
	 * @see net.wombatrpgs.ultima.players.MafiaPlayer#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "Roleblocker";
	}
}
