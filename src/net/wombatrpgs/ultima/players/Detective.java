/**
 *  Detective.java
 *  Created on Feb 15, 2018 11:32:07 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import java.util.ArrayList;

import net.wombatrpgs.ultima.Simulation;

/**
 * Investigates half a dude at a time.
 */
public class Detective extends TownPlayer {
	
	// when the detective has info on 50% of remaining players, they role claim
	private static final float INVESTIGATION_PERCENT_BEFORE_CLAIM = 0.5f;
	
	private ArrayList<Player> investigatedPlayers;

	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Detective(Simulation simulation) {
		super(simulation);
		investigatedPlayers = new ArrayList<Player>();
		investigatedPlayers.add(this);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPostNightkill()
	 */
	@Override public void onPostNightkill() {
		super.onPostNightkill();
		
		if (nullified) {
			return;
		}
		
		// the dead are not considered investigated in terms of claim chance
		ArrayList<Player> deadPlayers = new ArrayList<Player>();
		for (Player player : investigatedPlayers) {
			if (!player.isAlive()) {
				deadPlayers.add(player);
			}
		}
		for (Player deadPlayer : deadPlayers) {
			investigatedPlayers.remove(deadPlayer);
		}
		
		// don't investigate duplicates
		ArrayList<Player> toInvestigate = new ArrayList<Player>(simulation.getPlayers());
		for (Player player : investigatedPlayers) {
			if (toInvestigate.contains(player)) {
				toInvestigate.remove(player);
			}
		}
		
		// if there are unknown players, investigate them
		if (toInvestigate.size() > 0) {
			Player target = Simulation.randomIn(toInvestigate);
			
			// ...but witch gets first say
			if (toInvestigate.size() > 1) {
				toInvestigate.remove(target);
				if (target.getFaction() == Faction.MAFIA) {
					target = Simulation.randomIn(toInvestigate);
				}
			}
			
			investigatedPlayers.add(target);
			toInvestigate.remove(target);
			
			simulation.storyLog(this + " ended up investigating " + target + ".");
			
			if (target.faction == Faction.MAFIA) {
				simulation.storyLog(this + " roleclaimed to implicate " + target + ".");
				simulation.prioritizeDaykill(target);
				simulation.prioritizeNightkill(this);
			}
		}
		
		// if we're in the endgame, go ahead and roleclaim to save the innocents
		float uninvestigated = (float)toInvestigate.size() / (float)simulation.getPlayers().size();
		float investigated = 1.0f - uninvestigated;
		if (investigated >= INVESTIGATION_PERCENT_BEFORE_CLAIM) {
			simulation.storyLog(this + " roleclaimed as it was the endgame.");
			simulation.prioritizeNightkill(this);
			for (Player player : investigatedPlayers) {
				if (player.getFaction() == Faction.MAFIA) {
					simulation.prioritizeDaykill(player);
				} else if (player.getFaction() == Faction.SK) {
					simulation.markKnownUnaligned(player);
				} else if (player.getFaction() == Faction.JOKER) {
					simulation.prioritizeDaykill(player);
				} else {
					simulation.exoneratePlayer(player);
				}
			}
		}
	}

	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "Detective";
	}
}
