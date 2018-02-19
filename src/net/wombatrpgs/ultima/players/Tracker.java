/**
 *  Tracker.java
 *  Created on Feb 18, 2018 9:23:16 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import java.util.HashSet;

import net.wombatrpgs.ultima.Simulation;

/**
 * Checks if a player visits someone at night.
 */
public class Tracker extends TownPlayer {
	
	// when the tracker has info on some percent of remaining players, they role claim
	private static final float INVESTIGATION_PERCENT_BEFORE_CLAIM = 0.65f;
	
	protected HashSet<Player> seenMoving;
	protected HashSet<Player> seenStill;
	
	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Tracker(Simulation simulation) {
		super(simulation);
		seenMoving = new HashSet<>();
		seenStill = new HashSet<>();
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onDawn()
	 */
	@Override
	public void onDawn() {
		super.onDawn();
		
		if (visiting == null) {
			return;
		}
		
		Player visited = visiting.visiting;
		if (visited != null && !visited.isAlive()) {
			simulation.storyLog(this + " saw " + visiting + " at a corpse and claimed.");
			claim(true);
			return;
		}
		
		if (visited == null) {
			seenStill.add(visiting);
		} else if (!visited.nullified) {
			// probably a town PR?
			seenMoving.add(visiting);
		}
		
		// if we're in the endgame, go ahead and roleclaim to save the innocents
		float uninvestigated = (float)toInvestigate().size() / (float)simulation.getPlayers().size();
		float investigated = 1.0f - uninvestigated;
		if (investigated >= INVESTIGATION_PERCENT_BEFORE_CLAIM) {
			simulation.storyLog(this + " roleclaimed as it was the endgame.");
			claim(false);
		}
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPreNightkill()
	 */
	@Override
	public void onPreNightkill() {
		super.onPreNightkill();
		
		if (nullified) {
			return;
		}
		
		visiting = Simulation.randomIn(toInvestigate());
		
		if (visiting == null) {
			simulation.storyLog(this + " found nobody to follow.");
		} else {
			simulation.storyLog(this + " decided to follow " + visiting + ".");
		}
	}

	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "Tracker";
	}

	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#role()
	 */
	@Override
	protected SpecialRole role() {
		return SpecialRole.TRACKER;
	}
	
	/**
	 * Roleclaims and infodumps to town.
	 * @param 	foundKiller		True if we found a killer last night
	 */
	protected void claim(boolean foundKiller) {
		simulation.prioritizeNightkill(this);
		
		for (Player player : seenMoving) {
			simulation.exoneratePlayer(player);
		}
		
		for (Player player : seenStill) {
			simulation.exoneratePlayer(player);
		}
		
		if (foundKiller) {
			simulation.prioritizeDaykill(visiting);
		}
	}
	
	/**
	 * Generates the list of valid investigation targets.
	 * @return					The list of valid investigation targets
	 */
	protected HashSet<Player> toInvestigate() {
		HashSet<Player> targets = new HashSet<>();
		targets.addAll(simulation.getPlayers());
		targets.removeAll(simulation.getPrioritizedDaykills());
		targets.removeAll(simulation.getPrioritizedNightkills());
		targets.remove(this);
		return targets;
	}
}
