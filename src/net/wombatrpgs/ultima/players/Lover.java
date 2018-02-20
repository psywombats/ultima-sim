/**
 *  Lover.java
 *  Created on Feb 19, 2018 7:11:15 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import net.wombatrpgs.ultima.Simulation;

/**
 * Two players that get killed as one. Negative utility.
 */
public class Lover extends TownPlayer {
	
	private static int currentOrdinal;
	private int ordinal;
	
	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Lover(Simulation simulation) {
		super(simulation);
		currentOrdinal += 1;
		ordinal = currentOrdinal;
	}
	
	/**
	 * @see net.wombatrpgs.ultima.players.Player#die()
	 */
	@Override
	protected void die() {
		super.die();
		for (Player lover : simulation.getAllPlayersWithRole(getRole())) {
			simulation.storyLog("Alas! This causes " + lover + " to commit suicide.");
			lover.kill();
		}
	}

	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "Lover " + ordinal;
	}
	
	/** @return The number of gunmen active this game */
	public static int getQuantity() {
		return 2;
	}

	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#role()
	 */
	@Override
	protected SpecialRole role() {
		return SpecialRole.LOVER;
	}
}
