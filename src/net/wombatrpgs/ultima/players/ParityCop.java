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
 * Can detect ~parity~ if two players are on the same alignment.
 */
public class ParityCop extends TownPlayer {
	
	private ArrayList<Player> investigatedPlayers;
	private ArrayList<Player> misidentifiedPlayers;
	
	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public ParityCop(Simulation simulation) {
		super(simulation);
		investigatedPlayers = new ArrayList<Player>();
		misidentifiedPlayers = new ArrayList<Player>();
		investigatedPlayers.add(this);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPreNightkill()
	 */
	@Override
	public void onPreNightkill() {
		super.onPreNightkill();
		
		// the dead are not considered investigated
		ArrayList<Player> deadPlayers = new ArrayList<Player>();
		for (Player player : investigatedPlayers) {
			if (!player.isAlive()) {
				deadPlayers.add(player);
			}
		}
		for (Player deadPlayer : deadPlayers) {
			investigatedPlayers.remove(deadPlayer);
		}
		
		deadPlayers.clear();
		for (Player player : misidentifiedPlayers) {
			if (!player.isAlive()) {
				deadPlayers.add(player);
			}
		}
		for (Player deadPlayer : deadPlayers) {
			misidentifiedPlayers.remove(deadPlayer);
		}

		// don't investigate duplicates
		ArrayList<Player> toInvestigate = new ArrayList<Player>(simulation.getPlayers());
		for (Player player : investigatedPlayers) {
			if (toInvestigate.contains(player)) {
				toInvestigate.remove(player);
			}
		}
		
		// if there are unknown players, investigate them
		if (toInvestigate.size() >= 2) {
			Player player1 = Simulation.randomIn(toInvestigate);
			Player player2;
			do {
				player2 = Simulation.randomIn(toInvestigate);
			} while (player1 == player2);
			
			investigatedPlayers.add(player1);
			investigatedPlayers.add(player2);
			toInvestigate.remove(player1);
			toInvestigate.remove(player2);
			
			if (player1.getFaction() == Faction.MAFIA && player2.getFaction() == Faction.MAFIA) {
				// oops, this is the scumteam, town is hosed
				misidentifiedPlayers.add(player1);
				misidentifiedPlayers.add(player2);
			} else if (player1.getFaction() == Faction.MAFIA) {
				misidentifiedPlayers.add(player2);
			} else if (player2.getFaction() == Faction.MAFIA) {
				misidentifiedPlayers.add(player1);
			}
		}
		
		// if there's only one dude left, we can compare him against our alignment and get him 100%
		if (toInvestigate.size() == 1) {
			investigatedPlayers.add(toInvestigate.get(0));
		}
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPostNightkill()
	 */
	@Override public void onPostNightkill() {
		super.onPostNightkill();
		
		float investigationRatio = (float)investigatedPlayers.size() / (float)simulation.getPlayers().size();
		if ((Simulation.CLAIM_DAY > -1 && simulation.getDay() >= Simulation.CLAIM_DAY) ||
				investigationRatio > .5f) {
			// we claim, scum will rail us
			simulation.storyLog(this + " claimed:");
			simulation.prioritizeNightkill(this);
			simulation.exoneratePlayer(this);
			
			for (Player player : investigatedPlayers) {
				if (!player.isAlive()) {
					continue;
				}
				if (player.getFaction() == Faction.MAFIA) {
					if (misidentifiedPlayers.contains(player)) {
						simulation.exoneratePlayer(player);
						simulation.storyLog("While claiming, " + this + " mistakenly exonerated " + player + "!");
					} else {
						simulation.prioritizeDaykill(player);
						simulation.storyLog("While claiming, " + this + " found " + player + " guilty!");
					}
				} else {
					if (misidentifiedPlayers.contains(player)) {
						simulation.prioritizeDaykill(player);
						simulation.storyLog("While claiming, " + this + " mistakenly found " + player + " guilty!");
					} else {
						simulation.exoneratePlayer(player);
						simulation.storyLog("While claiming, " + this + " exonerated " + player + ".");
					}
				}
			}
		}
	}
	
	/**
	 * @see net.wombatrpgs.ultima.players.Player#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "the cop";
	}
	
	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#role()
	 */
	@Override
	protected SpecialRole role() {
		return SpecialRole.PARITY_COP;
	}
}
