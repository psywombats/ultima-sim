/**
 *  UltimaSim.java
 *  Created on Jun 20, 2016 8:21:58 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wombatrpgs.ultima.players.Faction;
import net.wombatrpgs.ultima.players.SpecialRole;
import net.wombatrpgs.ultima.rules.GameRules;

/**
 * Entry point.
 */
public class UltimaSim {
	
	private static int iterations = 1;

	/**
	 * Entry point.
	 * @param	args			unused for now
	 */
	public static void main(String[] args) {
		List<SimulationResult> results = new ArrayList<SimulationResult>();
		
		GameRules rules = new GameRules();
		rules.playerCount = 11;
		rules.mafiaCount = 2;
		rules.enabledRoles.put(SpecialRole.DETECTIVE, true);
		rules.enabledRoles.put(SpecialRole.ROLEBLOCKER, true);
		rules.enabledRoles.put(SpecialRole.PROTECTOR, true);
		rules.majorityVotesOnly = true;
		
		for (int i = 0; i < iterations; i += 1) {
			Simulation simulation = new Simulation(rules);
			simulation.setDebugOn();
			results.add(simulation.simulate());
		}
		
		int totalTurns = 0;
		int totalNights = 0;
		Map<Faction, Integer> wins = new HashMap<Faction, Integer>();
		for (Faction faction : Faction.values()) {
			wins.put(faction, 0);
		}
		
		for (SimulationResult result : results) {
			totalTurns += result.getTurnCount();
			totalNights += result.endedOnNight() ? 1 : 0;
			wins.put(result.getWinner(), wins.get(result.getWinner()) + 1);
		}
		
		System.out.println("The game was won by:\n");
		for (Faction faction : Faction.values()) {
			String name = faction.getDisplayName();
			float winPercent = (float)wins.get(faction) / (float)iterations;
			if (winPercent > 0) {
				String formattedPercent = String.format("%.1f", winPercent * 100.0f);
				System.out.println(name + ": " + formattedPercent + "%");
			}
		}
		System.out.println();
		
		float averageTurns = (float)totalTurns / (float)iterations;
		String formattedTurns = String.format("%.1f", averageTurns + 1.0f);
		System.out.println("The average game took " + formattedTurns + " days.");
		
		float nightPercent = (float)totalNights / (float)iterations;
		String formattedNights = String.format("%.1f", nightPercent * 100.0f);
		System.out.println("The game ended with a nightkill " + formattedNights + "% of the time.");
	}
}
