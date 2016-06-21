/**
 *  Simulation.java
 *  Created on Jun 20, 2016 8:22:47 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.wombatrpgs.ultima.players.Faction;
import net.wombatrpgs.ultima.players.MafiaPlayer;
import net.wombatrpgs.ultima.players.Player;
import net.wombatrpgs.ultima.players.TownPlayer;

/**
 * One game simulation.
 */
public class Simulation {
	
	private static Random rand = new Random();
	
	private ArrayList<Player> players;
	private ArrayList<MafiaPlayer> mafia;
	private ArrayList<TownPlayer> town;
	
	private int turnCount;
	
	/**
	 * Creates a game with the given number of players. Autoassigns roles.
	 * @param	playerCount		The total number of players to create
	 * @param	mafiaCount		The number of those players that are scum
	 */
	public Simulation(int playerCount, int mafiaCount) {
		players = new ArrayList<Player>();
		mafia = new ArrayList<MafiaPlayer>();
		town = new ArrayList<TownPlayer>();
		
		turnCount = 0;
		
		for (int i = 0; i < playerCount - mafiaCount; i += 1) {
			TownPlayer townie = new TownPlayer(this);
			players.add(townie);
			town.add(townie);
		}
		
		for (int i = 0; i < mafiaCount; i += 1) {
			MafiaPlayer mafioso = new MafiaPlayer(this);
			mafia.add(mafioso);
			players.add(mafioso);
		}
	}
	
	/**
	 * Simulates the full run of the game.
	 * @return					The result of the game
	 */
	public SimulationResult simulate() {
		SimulationResult result = null;
		while (result == null) {
			result = simulateTurn();
		}
		return result;
	}
	
	/**
	 * Call this from players when that player dies.
	 * @param	player			The player that died
	 */
	public void onPlayerDeath(Player player) {
		players.remove(player);
		if (mafia.contains(player)) mafia.remove(player);
		if (town.contains(player)) town.remove(player);
	}
	
	/**
	 * Simulates one day. Returns the result, if any.
	 * @return					The final result of the game if it resolved, else false
	 */
	private SimulationResult simulateTurn() {
		SimulationResult result;
		
		Player daykillTarget = getDaykillTarget();
		daykillTarget.attemptDaykill();
		result = checkForResult(false);
		if (result != null) {
			return result;
		}
		
		Player nightkillTarget = getNightkillTarget();
		nightkillTarget.attemptNightkill();
		result = checkForResult(true);
		if (result != null) {
			return result;
		}
		
		turnCount += 1;
		return null;
	}
	
	/**@return The player that town chooses to daykill next */
	private Player getDaykillTarget() {
		return randomIn(players);
	}
	
	/** @return The poor slob that mafia chooses to nightkill */
	private Player getNightkillTarget() {
		return randomIn(town);
	}
	
	/**
	 * Checks if the game is done, and if so, returns the result.
	 * @param	isNight			True if game is in night phase
	 * @return					The result of the game, or null if not over
	 */
	private SimulationResult checkForResult(boolean isNight) {
		if (mafia.size() > town.size()) {
			return new SimulationResult(Faction.MAFIA, turnCount, isNight);
		} else if (mafia.size() == 0) {
			return new SimulationResult(Faction.TOWN, turnCount, isNight);
		} else {
			return null;
		}
	}

	/**
	 * Returns a random element of the list.
	 * @param	collection		The collection to select from
	 * @return					A random element from the list or null if empty
	 */
	private static <T> T randomIn(List<T> collection) {
		if (collection.size() == 0) {
			return null;
		} else if (collection.size() == 1) {
			return collection.get(0);
		} else {
			return collection.get(rand.nextInt(collection.size()));
		}
	}
}
