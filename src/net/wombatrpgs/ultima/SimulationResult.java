/**
 *  SimulationResult.java
 *  Created on Jun 20, 2016 8:31:56 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima;

import net.wombatrpgs.ultima.players.Faction;

/**
 * The result of a single game.
 */
public class SimulationResult {
	
	private Faction winningFaction;
	private int turnCount;
	private boolean nightEnd;
	
	/**
	 * Creates a new result.
	 * @param	winningFaction	The faction that won
	 * @param	turnCount		How many full days it took to win
	 * @param	nightEnd		True if the game ended during night phase
	 */
	public SimulationResult(Faction winningFaction, int turnCount, boolean nightEnd) {
		this.winningFaction = winningFaction;
		this.turnCount = turnCount;
		this.nightEnd = nightEnd;
	}
	
	/** @return The result in human readable format */
	public String formatResult() {
		String result = "The game was won by " + winningFaction.getDisplayName();
		result += " on the " + (nightEnd ? "night" : "day") + " phase";
		result += " of day " + (turnCount+1) + ".";
		return result;
	}
	
	/** @return The total turns this game took */
	public int getTurnCount() { return turnCount; }
	
	/** @return The winning faction */
	public Faction getWinner() { return winningFaction; }
	
	/** @return True if the game ended during the night */
	public boolean endedOnNight() { return nightEnd; }

}
