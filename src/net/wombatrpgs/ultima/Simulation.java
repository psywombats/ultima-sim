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
	
	private Set<Player> players;
	private Set<MafiaPlayer> mafia;
	private Set<TownPlayer> town;
	private Set<Player> serialKillers;
	private Map<SpecialRole, Player> specialists;
	
	private Set<Player> prioritizedNightkillPlayers;
	private Set<Player> prioritizedDaykillPlayers;
	private Set<Player> exoneratedPlayers;
	private Set<Player> onceProtectedPlayers;
	
	private GameRules rules;
	private int turnCount;
	private boolean enhancedNightkill;
	
	/**
	 * Creates a game with the given number of players. Autoassigns roles.
	 * @param	rules			The rules setup
	 */
	public Simulation(GameRules rules) {
		this.rules = rules;
		players = new HashSet<Player>();
		mafia = new HashSet<MafiaPlayer>();
		town = new HashSet<TownPlayer>();
		serialKillers = new HashSet<Player>();
		specialists = new HashMap<SpecialRole, Player>();
		
		prioritizedNightkillPlayers = new HashSet<Player>();
		prioritizedDaykillPlayers = new HashSet<Player>();
		exoneratedPlayers = new HashSet<Player>();
		onceProtectedPlayers = new HashSet<Player>();
		
		turnCount = 0;
		
		// jank as hell role initiation
		
		for (int i = 0; i < rules.playerCount - rules.mafiaCount; i += 1) {
			TownPlayer townie;
			if (rules.enabledRoles.get(SpecialRole.SEER) && !isAlive(SpecialRole.SEER)) {
				townie = new Seer(this);
				specialists.put(SpecialRole.SEER, townie);
			} else if (rules.enabledRoles.get(SpecialRole.DOCTOR) && !isAlive(SpecialRole.DOCTOR)) {
				townie = new Doctor(this);
				specialists.put(SpecialRole.DOCTOR, townie);
			} else if (rules.enabledRoles.get(SpecialRole.SMITH) && !isAlive(SpecialRole.SMITH)) {
				townie = new Smith(this);
				specialists.put(SpecialRole.SMITH, townie);
			} else {
				townie = new TownPlayer(this);
			}
			players.add(townie);
			town.add(townie);
		}
		
		for (int i = 0; i < rules.mafiaCount; i += 1) {
			MafiaPlayer mafioso;
			if (rules.enabledRoles.get(SpecialRole.ASSASSIN) && !isAlive(SpecialRole.ASSASSIN)) {
				mafioso = new Assassin(this);
				specialists.put(SpecialRole.ASSASSIN, mafioso);
			} else if (rules.enabledRoles.get(SpecialRole.BLACK_MAGE) && !isAlive(SpecialRole.BLACK_MAGE)) {
				mafioso = new BlackMage(this);
				specialists.put(SpecialRole.BLACK_MAGE, mafioso);
			} else if (rules.enabledRoles.get(SpecialRole.THIEF) && !isAlive(SpecialRole.THIEF)) {
				mafioso = new Thief(this);
				specialists.put(SpecialRole.THIEF, mafioso);
			} else {
				mafioso = new MafiaPlayer(this);
			}
			mafia.add(mafioso);
			players.add(mafioso);
		}
		
		if (rules.enabledRoles.get(SpecialRole.SERIAL_KILLER)) {
			Player killer = new SerialKiller(this);
			specialists.put(SpecialRole.SERIAL_KILLER, killer);
			players.add(killer);
			serialKillers.add(killer);
		}
		
		if (rules.useSword) {
			randomIn(town).setSword(true);
		}
	}
	
	/** @return The game rules */
	public GameRules rules() { return rules; }
	
	/** @return All players in the game */
	public Set<Player> getPlayers() { return players; }
	
	/** @return All mafia in the game */
	public Set<MafiaPlayer> getMafia() { return mafia; }
	
	/** @return All mafia in the game */
	public Set<TownPlayer> getTown() { return town; }
	
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
	
	/** @return All players that have at one point been affected by the doctor */
	public Set<Player>getOnceProtected() { return onceProtectedPlayers; }
	
	/** Sets assassination mode on */
	public void enhanceNightkill() { this.enhancedNightkill = true; }
	
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
		if (onceProtectedPlayers.contains(player)) onceProtectedPlayers.remove(player);
		if (serialKillers.contains(player)) serialKillers.remove(player);
		
		for (SpecialRole role : SpecialRole.values()) {
			if (specialists.get(role) == player) {
				specialists.put(role, null);
			}
		}
	}
	
	/**
	 * Call this when a doctor miraculously saves a player.
	 * @param	player			The player that didn't quite die
	 */
	public void onPlayerProtected(Player player) {
		onceProtectedPlayers.add(player);
	}
	
	/**
	 * Marks a specialist as having exhausted their role, no longer treated by the logic of the
	 * other specialists as having that role.
	 * @param	role			The role to deregister
	 */
	public void deregisterSpecial(SpecialRole role) {
		specialists.put(role, null);
	}
	
	/**
	 * Marks a player as clearly innocent to town.
	 * @param	player			The player to prevent daykills for
	 */
	public void exoneratePlayer(Player player) {
		exoneratedPlayers.add(player);
		prioritizedDaykillPlayers.remove(player);
	}
	
	/**
	 * Checks if a role is alive.
	 * @param	role			The role to check
	 * @return					True if that role is alive and well
	 */
	public boolean isAlive(SpecialRole role) {
		return specialists.get(role) != null;
	}
	
	/**@return The player that town chooses to daykill next */
	public Player getDaykillTarget() {
		if (prioritizedDaykillPlayers.size() > 0) {
			return randomIn(prioritizedDaykillPlayers);
		}
		
		HashSet<Player> validTargets = new HashSet<Player>(players);
		validTargets.removeAll(exoneratedPlayers);
		if (validTargets.size() > 0) {
			return randomIn(validTargets);
		}
		
		return randomIn(players);
	}
	
	/** @return The poor slob that mafia chooses to nightkill */
	public Player getNightkillTarget() {
		HashSet<Player> woundedPlayers = new HashSet<Player>();
		for (Player player : town) {
			if (player.isWounded()) {
				woundedPlayers.add(player);
			}
		}
		
		prioritizedNightkillPlayers.removeAll(woundedPlayers);
		if (prioritizedNightkillPlayers.size() > 0) {
			if (prioritizedNightkillPlayers.size() > 1 ||
					!isAlive(SpecialRole.DOCTOR) ||
					specialists.get(SpecialRole.DOCTOR).isNullified()  ||
					!this.enhancedNightkill) {
				Player target = randomIn(prioritizedNightkillPlayers);
				if (!target.isWounded() && players.contains(target)) {
					return target;
				}
			}
		}
		
		HashSet<Player> exoneratedTargets = new HashSet<Player>(exoneratedPlayers);
		exoneratedTargets.removeAll(mafia);
		exoneratedTargets.removeAll(woundedPlayers);
		if (exoneratedTargets.size() > 0) {
			return randomIn(exoneratedTargets);
		}
		
		HashSet<Player> legalPlayers = new HashSet<Player>(players);
		legalPlayers.removeAll(woundedPlayers);
		legalPlayers.removeAll(mafia);
		
		return randomIn(legalPlayers);
	}
	
	/**
	 * Checks if the game is done, and if so, returns the result.
	 * @param	isNight			True if game is in night phase
	 * @return					The result of the game, or null if not over
	 */
	private SimulationResult checkForResult(boolean isNight) {
		if (mafia.size() > (town.size() + serialKillers.size())) {
			return new SimulationResult(Faction.MAFIA, turnCount, isNight);
		} else if (mafia.size() == 0 && serialKillers.size() == 0) {
			return new SimulationResult(Faction.TOWN, turnCount, isNight);
		} else if (mafia.size() <= 1 && town.size() <= 1 && serialKillers.size() <= 1 && players.size() <= 2 && !isNight) {
			return new SimulationResult(Faction.TRUE_RNG, turnCount, isNight);
		} else if (serialKillers.size() > 0 && players.size() == 1) {
			return new SimulationResult(Faction.SK, turnCount, isNight);
		} else {
			return null;
		}
	}
	
	/**
	 * Simulates one day. Returns the result, if any.
	 * @return					The final result of the game if it resolved, else false
	 */
	private SimulationResult simulateTurn() {
		SimulationResult result;
		List<Player> iteratingPlayers;
		
		iteratingPlayers = new ArrayList<Player>(players);
		for (Player player : iteratingPlayers) {
			player.onDawn();
		}
		
		result = checkForResult(false);
		if (result != null) {
			return result;
		}
		
		Player daykillTarget = getDaykillTarget();
		daykillTarget.attemptDaykill();
		result = checkForResult(true);
		if (result != null) {
			return result;
		}
		
		iteratingPlayers = new ArrayList<Player>(players);
		for (Player player : iteratingPlayers) {
			if (player.isAlive() && !player.isNullified()) {
				player.onPostDaykill();
			}
		}
		
		iteratingPlayers = new ArrayList<Player>(players);
		for (Player player : iteratingPlayers) {
			if (player.isAlive() && !player.isNullified()) {
				player.onPreNightkill();
			}
		}
		result = checkForResult(true);
		if (result != null) {
			return result;
		}
		
		Player nightkillTarget = getNightkillTarget();
		if (nightkillTarget != null) {
			nightkillTarget.attemptNightkill(this.enhancedNightkill);
		}
		enhancedNightkill = false;
		result = checkForResult(true);
		if (result != null) {
			return result;
		}
		
		iteratingPlayers = new ArrayList<Player>(players);
		for (Player player : iteratingPlayers) {
			if (player.isAlive() && !player.isNullified()) {
				player.onPostNightkill();
			}
		}
		result = checkForResult(true);
		if (result != null) {
			return result;
		}
		
		turnCount += 1;
		return null;
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
	
	/**
	 * Returns true chance percent of the time.
	 * @param	chance			The probability of returning true, from 0.0 to 1.0
	 * @return					True if the chance succeeds
	 */
	public static boolean chance(float chance) {
		return rand.nextFloat() < chance;
	}
}
