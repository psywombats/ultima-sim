/**
 *  Seer.java
 *  Created on Jun 20, 2016 9:54:09 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import java.util.ArrayList;

import net.wombatrpgs.ultima.Simulation;

/**
 * Can detect scum.
 */
public class Seer extends TownPlayer {
	
	// when the seer has info on 50% of remaining players, they role claim
	private static final float INVESTIGATION_PERCENT_BEFORE_CLAIM = 0.5f;
	
	private ArrayList<Player> investigatedPlayers;
	
	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Seer(Simulation simulation) {
		super(simulation);
		investigatedPlayers = new ArrayList<Player>();
		investigatedPlayers.add(this);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPostNightkill()
	 */
	@Override public void onPostNightkill() {
		super.onPostNightkill();
		
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
			Player player = Simulation.randomIn(toInvestigate);
			investigatedPlayers.add(player);
			toInvestigate.remove(player);
			
			// we found scum! let everyone know! (...scum will probs kill us)
			if (player.getFaction() == Faction.MAFIA) {
				simulation.prioritizeDaykill(player);
				simulation.prioritizeNightkill(this);
			}
		}
		
		// if there's a safe doctor around, seer immediately role claims
		// unfortunately haven't simulated a counter-claim but that's kinda a desperate ploy
		float threshold = INVESTIGATION_PERCENT_BEFORE_CLAIM;
		if (simulation.isAlive(SpecialRole.DOCTOR) && !simulation.isAlive(SpecialRole.ASSASSIN)) {
			threshold = 0.0f;
		}
		
		// if we're in the endgame, go ahead and roleclaim to save the innocents
		float uninvestigated = (float)toInvestigate.size() / (float)simulation.getPlayers().size();
		float investigated = 1.0f - uninvestigated;
		if (investigated >= threshold) {
			simulation.prioritizeNightkill(this);
			for (Player player : investigatedPlayers) {
				if (player.getFaction() == Faction.MAFIA) {
					simulation.prioritizeDaykill(player);
				} else {
					simulation.exoneratePlayer(player);
				}
			}
		}
	}
}
