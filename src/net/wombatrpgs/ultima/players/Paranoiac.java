/**
 *  Paranoiac.java
 *  Created on Feb 20, 2018 12:16:56 AM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import java.util.HashSet;

import net.wombatrpgs.ultima.Simulation;

/**
 * Revenge killer. Negative utility in this setup lol.
 */
public class Paranoiac extends TownPlayer {
	
	protected Player suspect;

	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Paranoiac(Simulation simulation) {
		super(simulation);
	}
	
	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "Paranoiac";
	}

	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#role()
	 */
	@Override
	protected SpecialRole role() {
		return SpecialRole.PARANOIAC;
	}
	
	/**
	 * @see net.wombatrpgs.ultima.players.Player#onDawn()
	 */
	@Override
	public void onDawn() {
		suspect = null;
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPreNightkill()
	 */
	@Override
	public void onPostDaykill() {
		super.onPreNightkill();
		
		if (simulation.getPrioritizedDaykills().size() > 0) {
			suspect = Simulation.randomIn(simulation.getPrioritizedDaykills());
		} else {
			HashSet<Player> targets = new HashSet<>(simulation.getPlayers());
			targets.remove(this);
			suspect = Simulation.randomIn(targets);
		}
	}
	
	

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPreNightkill()
	 */
	@Override
	public void onPreNightkill() {
		super.onPreNightkill();
		
		if (nullified) {
			suspect = null;
		} else {
			simulation.storyLog(this + " suspects " + suspect + " will try to kill them.");
		}
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#attemptNightkill(net.wombatrpgs.ultima.players.Player, boolean)
	 */
	@Override
	public void attemptNightkill(Player inflicter, boolean ignoresProtection) {
		if (ignoresProtection || inflicter != suspect) {
			super.attemptNightkill(inflicter, ignoresProtection);
		} else {
//			simulation.storyLog("...but " + this + " was expecting " + inflicter + "! REVENGE!");
//			inflicter.attemptNightkill(this, false);
			simulation.storyLog("...but " + this + " was expecting company!");
			simulation.prioritizeNightkill(this);
			simulation.prioritizeDaykill(inflicter);
		}
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#visitFrom(net.wombatrpgs.ultima.players.Player)
	 */
	@Override
	protected void visitFrom(Player visitor) {
		// jaaaanky
		if (visitor.getRole() == SpecialRole.GUNMAN && visitor == suspect) {
			simulation.storyLog("...but " + this + " was expecting " + visitor + "! Whoops!");
			visitor.attemptNightkill(this, false);
		} else {
			super.visitFrom(visitor);
		}
	}
}
