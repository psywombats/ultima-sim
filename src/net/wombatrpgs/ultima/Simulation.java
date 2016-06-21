/**
 *  Simulation.java
 *  Created on Jun 20, 2016 8:22:47 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.wombatrpgs.ultima.players.*;
import net.wombatrpgs.ultima.rules.GameRules;

/**
 * One game simulation.
 */
public class Simulation {
	
	private static Random rand = new Random();
	
	private ArrayList<Player> players;
	private ArrayList<MafiaPlayer> mafia;
	private ArrayList<TownPlayer> town;
	private Map<SpecialRole, Player> specialists;
	
	private Set<Player> prioritizedNightkillPlayers;
	private Set<Player> prioritizedDaykillPlayers;
	private Set<Player> exoneratedPlayers;
	
	private GameRules rules;
	private int turnCount;
	
	/**
	 * Creates a game with the given number of players. Autoassigns roles.
	 * @param	rules			The rules setup
	 */
	public Simulation(GameRules rules) {
		this.rules = rules;
		players = new ArrayList<Player>();
		mafia = new ArrayList<MafiaPlayer>();
		town = new ArrayList<TownPlayer>();
		specialists = new HashMap<SpecialRole, Player>();
		
		prioritizedNightkillPlayers = new HashSet<Player>();
		prioritizedDaykillPlayers = new HashSet<Player>();
		exoneratedPlayers = new HashSet<Player>();
		
		turnCount = 0;
		
		for (int i = 0; i < rules.playerCount - rules.mafiaCount; i += 1) {
			TownPlayer townie;
			if (rules.enabledRoles.get(SpecialRole.SEER) && !isAlive(SpecialRole.SEER)) {
				townie = new Seer(this);
				specialists.put(SpecialRole.SEER, townie);
			} else if (rules.enabledRoles.get(SpecialRole.DOCTOR) && !isAlive(SpecialRole.DOCTOR)) {
				townie = new Doctor(this);
				specialists.put(SpecialRole.DOCTOR, townie);
			} else {
				townie = new TownPlayer(this);
			}
			players.add(townie);
			town.add(townie);
		}
		
		for (int i = 0; i < rules.mafiaCount; i += 1) {
			MafiaPlayer mafioso = new MafiaPlayer(this);
			mafia.add(mafioso);
			players.add(mafioso);
		}
	}
	
	/** @return The game rules */
	public GameRules rules() { return rules; }
	
	/** @return All players in the game */
	public List<Player> getPlayers() { return players; }
	
	/** @return All mafia in the game */
	public List<MafiaPlayer> getMafia() { return mafia; }
	
	/** @return All mafia in the game */
	public List<TownPlayer> getTown() { return town; }
	
	/** @return Brings a townie to the prioritized mafia hitlist */
	public void prioritizeNightkill(Player player) { prioritizedNightkillPlayers.add(player); }
	
	/** @return Brings a mafioso to the prioritized town lynchlist */
	public void prioritizeDaykill(Player player) { prioritizedDaykillPlayers.add(player); }
	
	/** @return Removes a townie from the prioritized mafia hitlist */
	public void deprioritizeNightkill(Player player) { prioritizedNightkillPlayers.remove(player); }
	
	/** @return Removes a mafioso from the prioritized town lynchlist */
	public void deprioritizeDaykill(Player player) { prioritizedDaykillPlayers.remove(player); }
	
	/** @return All players prioritized for nightkilling */
	public Set<Player>getPrioritizedNightkills() { return prioritizedNightkillPlayers; }
	
	/** @return All players prioritized for daykilling */
	public Set<Player>getPrioritizedDaykills() { return prioritizedDaykillPlayers; }
	
	/**
	 * Marks a player as clearly innocent to town.
	 * @param	player			The player to prevent daykills for
	 */
	public void exoneratePlayer(Player player) {
		exoneratedPlayers.add(player);
		prioritizedDaykillPlayers.remove(player);
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
		if (!players.contains(player)) {
			System.err.println("Player is dead: " + player);
		}
		
		players.remove(player);
		if (mafia.contains(player)) mafia.remove(player);
		if (town.contains(player)) town.remove(player);
		if (prioritizedDaykillPlayers.contains(player)) prioritizedDaykillPlayers.remove(player);
		if (prioritizedNightkillPlayers.contains(player)) prioritizedNightkillPlayers.remove(player);
		if (exoneratedPlayers.contains(player)) exoneratedPlayers.remove(player);
		
		for (SpecialRole role : SpecialRole.values()) {
			if (specialists.get(role) == player) {
				specialists.put(role, null);
			}
		}
	}
	
	/**
	 * Checks if a role is alive.
	 * @param	role			The role to check
	 * @return					True if that role is alive and well
	 */
	public boolean isAlive(SpecialRole role) {
		return specialists.get(role) != null;
	}
	
	/**
	 * Simulates one day. Returns the result, if any.
	 * @return					The final result of the game if it resolved, else false
	 */
	private SimulationResult simulateTurn() {
		SimulationResult result;
		
		for (Player player : players) {
			player.onDawn();
		}
		
		Player daykillTarget = getDaykillTarget();
		daykillTarget.attemptDaykill();
		result = checkForResult(false);
		if (result != null) {
			return result;
		}
		
		for (Player player : players) {
			player.onPreNightkill();
		}
		
		Player nightkillTarget = getNightkillTarget();
		nightkillTarget.attemptNightkill();
		result = checkForResult(true);
		if (result != null) {
			return result;
		}
		
		for (Player player : players) {
			player.onPostNightkill();
		}
		
		turnCount += 1;
		return null;
	}
	
	/**@return The player that town chooses to daykill next */
	private Player getDaykillTarget() {
		if (prioritizedDaykillPlayers.size() > 0) {
			return randomIn(prioritizedDaykillPlayers);
		}
		
		HashSet<Player> validTargets = new HashSet<Player>(players);
		validTargets.remove(exoneratedPlayers);
		return randomIn(validTargets);
	}
	
	/** @return The poor slob that mafia chooses to nightkill */
	private Player getNightkillTarget() {
		if (prioritizedNightkillPlayers.size() > 0) {
			if (prioritizedNightkillPlayers.size() > 1 || !isAlive(SpecialRole.DOCTOR)) {
				return randomIn(prioritizedNightkillPlayers);
			}
		}
		if (exoneratedPlayers.size() > 0) {
			return randomIn(exoneratedPlayers);
		}
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
		} else if (mafia.size() == town.size() && isNight) {
			return new SimulationResult(Faction.RNG, turnCount, true);
		} else {
			return null;
		}
	}

	/**
	 * Returns a random element of the list.
	 * @param	collection		The collection to select from
	 * @return					A random element from the list or null if empty
	 */
	public static <T> T randomIn(List<T> collection) {
		if (collection.size() == 0) {
			return null;
		} else if (collection.size() == 1) {
			return collection.get(0);
		} else {
			return collection.get(rand.nextInt(collection.size()));
		}
	}
	
	/**
	 * Returns a random element of the list.
	 * @param	collection		The collection to select from
	 * @return					A random element from the list or null if empty
	 */
	public static <T> T randomIn(Set<T> collection) {
		return randomIn(new ArrayList<T>(collection));
	}
}
