/**
 *  Agent.java
 *  Created on Aug 27, 2019 11:51:37 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import net.wombatrpgs.ultima.Simulation;

/**
 * If all agents die, town loses the game.
 */
public class Agent extends TownPlayer {

	/**
	 * Autogenerated constructor
	 * @param	simulation 		The simulation this agent is a part of
	 */
	public Agent(Simulation simulation) {
		super(simulation);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "Agent";
	}
}
