/**
 *  Protector.java
 *  Created on Feb 17, 2018 10:46:17 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import java.util.HashSet;

import net.wombatrpgs.ultima.Simulation;

/**
 * Protects but needs backup to actually do anything.
 */
public class Protector extends TownPlayer {
	
	private static HashSet<Player> partialTargets;
	private static int currentOrdinal;
	
	private int ordinal;
	
	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Protector(Simulation simulation) {
		super(simulation);
		if (partialTargets == null) {
			partialTargets = new HashSet<>();
		}
		currentOrdinal += 1;
		ordinal = currentOrdinal;
	}
	
	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPreNightkill()
	 */
	@Override
	public void onPreNightkill() {
		if (nullified) {
			return;
		}
		
		if (simulation.getPrioritizedNightkills().size() > 0) {
			visiting = Simulation.randomIn(simulation.getPrioritizedNightkills());
			simulation.storyLog(this + " knows mafia's after " + visiting + " and protected them.");
		} else {
			double roll = Math.random();
			if (roll > .5) {
				visiting = Simulation.randomIn(simulation.getPlayers());
				simulation.storyLog(this + " randomly chose to protect " + visiting + ".");
			} else {
				visiting = this;
				simulation.storyLog(this + " chose to defend themselves.");
			}
		}
		
		if (partialTargets.contains(visiting)) {
			visiting.protect();
		} else {
			partialTargets.add(visiting);
		}
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
		return "Protector " + ordinal;
	}

	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#role()
	 */
	@Override
	protected SpecialRole role() {
		return SpecialRole.PROTECTOR;
	}

	/** @return The number of protectors active this game */
	public static int getQuantity() {
		return 2;
	}
}
