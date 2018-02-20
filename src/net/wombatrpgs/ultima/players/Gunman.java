/**
 *  Gunman.java
 *  Created on Feb 18, 2018 12:53:51 AM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import java.util.HashSet;

import net.wombatrpgs.ultima.Simulation;

/**
 * Vigilante but gimped (heh).
 */
public class Gunman extends TownPlayer {
	
	private static HashSet<Player> partialTargets;
	private static int currentOrdinal;
	
	private int ordinal;
	
	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Gunman(Simulation simulation) {
		super(simulation);
		if (partialTargets == null) {
			partialTargets = new HashSet<>();
		}
		currentOrdinal += 1;
		ordinal = currentOrdinal;
	}
	
	/**
	 * @see net.wombatrpgs.ultima.players.Player#onDawn()
	 */
	@Override public void onDawn() {
		super.onDawn();
		partialTargets.clear();
	}

	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "Gunman " + ordinal;
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPreNightkill()
	 */
	@Override
	public void onPreNightkill() {
		if (nullified) {
			return;
		}
		
		if (simulation.getPrioritizedDaykills().size() > 0) {
			Simulation.randomIn(simulation.getPrioritizedNightkills()).visitFrom(this);
			simulation.storyLog(this + " thinks " + visiting + " is scum and targetted them.");
		} else {
			visiting = Simulation.randomIn(simulation.getPlayers());
			if (visiting == this) {
				onPreNightkill();
				return;
			}
			simulation.storyLog(this + " randomly pursued " + visiting + ".");
		}
		
		if (partialTargets.contains(visiting)) {
			simulation.storyLog("Two gunman targetted " + visiting + " so they got shot.");
			visiting.attemptNightkill(this, false);
		} else  {
			partialTargets.add(visiting);
		}
	}

	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#role()
	 */
	@Override
	protected SpecialRole role() {
		return SpecialRole.GUNMAN;
	}

	/** @return The number of gunmen active this game */
	public static int getQuantity() {
		return 3;
	}
}
