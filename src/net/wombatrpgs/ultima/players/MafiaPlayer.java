/**
 *  MafiaPlayer.java
 *  Created on Jun 20, 2016 8:25:03 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import net.wombatrpgs.ultima.Simulation;

/**
 * A mafia participant. Scum!!
 */
public class MafiaPlayer extends Player {

	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public MafiaPlayer(Simulation simulation) {
		super(simulation, Faction.MAFIA);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "Mafioso";
	}
}
