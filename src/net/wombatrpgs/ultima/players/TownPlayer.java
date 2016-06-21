/**
 *  TownPlayer.java
 *  Created on Jun 20, 2016 8:25:33 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import net.wombatrpgs.ultima.Simulation;

/**
 * A helpful townie player. The uninformed majority.
 */
public class TownPlayer extends Player {

	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public TownPlayer(Simulation simulation) {
		super(simulation, Faction.TOWN);
	}
}
