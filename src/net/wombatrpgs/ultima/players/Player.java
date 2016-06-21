/**
 *  Player.java
 *  Created on Jun 20, 2016 8:23:03 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import net.wombatrpgs.ultima.Simulation;

/**
 * A participant, either mafia or town or whatever.
 */
public abstract class Player {
	
	protected Faction faction;
	protected Simulation simulation;
	
	/**
	 * Creates a new player.
	 * @param	simulation		The simulation this player is a part of
	 * @param	faction			The faction this player is a part of
	 */
	protected Player(Simulation simulation, Faction faction) {
		this.simulation = simulation;
		this.faction = faction;
	}
	
	/** @return The faction of this player */
	public Faction getFaction() { return faction; }
	
	/**
	 * Attempts to daykill this player. Can be blocked by random junk maybe. Handles removing the
	 * player from the simulation if they die.
	 */
	public void attemptDaykill() {
		die();
	}
	
	/**
	 * Attempts to daykill this player. Can be blocked by random junk maybe. Handles removing the
	 * player from the simulation if they die.
	 */
	public void attemptNightkill() {
		die();
	}
	
	/**
	 * Called after a nightkill has been performed.
	 */
	public void onPostNightkill() {
		// nothing by default
	}
	
	/**
	 * Whoops. Kills this player from the sim.
	 */
	protected void die() {
		simulation.onPlayerDeath(this);
	}
}
