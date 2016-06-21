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
	protected boolean isDoctorProtected;
	protected boolean alive;
	
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
	
	/** @return True if this player is still alive */
	public boolean isAlive() { return alive; }
	
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
		if (!isDoctorProtected) {
			die();
		} else {
			simulation.onPlayerProtected(this);
		}
	}
	
	/**
	 * Immediately kills this player, no questions asked.
	 */
	public void kill() {
		die();
	}
	
	/**
	 * Protects this player as if the doctor had protected them.
	 */
	public void protect() {
		isDoctorProtected = true;
	}
	
	/**
	 * Called once each day.
	 */
	public void onDawn() {
		isDoctorProtected = false;
	}
	
	/**
	 * Called each night before nightkill has been performed.
	 */
	public void onPreNightkill() {
		// nothing by default
	}
	
	/**
	 * Called each night after a nightkill has been performed.
	 */
	public void onPostNightkill() {
		// nothing by default
	}
	
	/**
	 * Whoops. Kills this player from the sim.
	 */
	protected void die() {
		alive = false;
		simulation.onPlayerDeath(this);
	}
}
