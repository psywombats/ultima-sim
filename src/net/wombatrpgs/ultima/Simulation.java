/**
 *  Simulation.java
 *  Created on Jun 20, 2016 8:22:47 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima;

import java.util.ArrayList;
import java.util.Collections;
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
	
	// if certain survive to this day, claim to provide info; otherwise no-kill
	public static int CLAIM_DAY = -1;
	
	private static Random rand = new Random();
	
	private Set<Player> players;
	private Set<MafiaPlayer> mafia;
	private Set<TownPlayer> town;
	private Set<Player> serialKillers;
	private Set<Player> jokers;
	private Set<Agent> agents;
	private Map<SpecialRole, Player> specialists;
	
	private Set<Player> prioritizedNightkillPlayers;
	private Set<Player> prioritizedDaykillPlayers;
	private Set<Player> exoneratedPlayers;
	private Set<Player> onceProtectedPlayers;
	private Set<Player> knownUnaligned;
	
	private GameRules rules;
	private int turnCount;
	private boolean enhancedNightkill;
	private boolean debugLog;
	
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
		jokers = new HashSet<Player>();
		agents = new HashSet<Agent>();
		specialists = new HashMap<SpecialRole, Player>();
		
		prioritizedNightkillPlayers = new HashSet<Player>();
		prioritizedDaykillPlayers = new HashSet<Player>();
		exoneratedPlayers = new HashSet<Player>();
		onceProtectedPlayers = new HashSet<Player>();
		knownUnaligned = new HashSet<Player>();
		
		turnCount = 0;
		
		// jank as hell role initiation
		
		if (rules.enabledRoles.get(SpecialRole.SERIAL_KILLER)) {
			Player killer = new SerialKiller(this);
			specialists.put(SpecialRole.SERIAL_KILLER, killer);
			players.add(killer);
			serialKillers.add(killer);
		}
		
		if (rules.enabledRoles.get(SpecialRole.JOKER)) {
			Player joker = new Joker(this);
			specialists.put(SpecialRole.JOKER, joker);
			players.add(joker);
			jokers.add(joker);
		}
		
		for (int i = 0; i < rules.mafiaCount; i += 1) {
			MafiaPlayer mafioso;
			if (rules.enabledRoles.get(SpecialRole.ASSASSIN) && !isAlive(SpecialRole.ASSASSIN)) {
				mafioso = new Assassin(this);
				specialists.put(SpecialRole.ASSASSIN, mafioso);
			} else if (rules.enabledRoles.get(SpecialRole.BLACK_MAGE) && !isAlive(SpecialRole.BLACK_MAGE)) {
				mafioso = new BlackMage(this);
				specialists.put(SpecialRole.BLACK_MAGE, mafioso);
			} else if (rules.enabledRoles.get(SpecialRole.ROLEBLOCKER) && !isAlive(SpecialRole.ROLEBLOCKER)) {
				mafioso = new Roleblocker(this);
				specialists.put(SpecialRole.ROLEBLOCKER, mafioso);
			} else if (rules.enabledRoles.get(SpecialRole.THIEF) && !isAlive(SpecialRole.THIEF)) {
				mafioso = new Thief(this);
				specialists.put(SpecialRole.THIEF, mafioso);
			} else {
				mafioso = new MafiaPlayer(this);
			}
			mafia.add(mafioso);
			players.add(mafioso);
		}
		
		for (int i = 0; i < rules.agentCount; i += 1) {
			Agent agent = new Agent(this);
			players.add(agent);
			town.add(agent);
			agents.add(agent);
		}
		
		while (players.size() < rules.playerCount) {
			TownPlayer townie;
			if (rules.enabledRoles.get(SpecialRole.SEER) && !isAlive(SpecialRole.SEER)) {
				townie = new Seer(this);
				specialists.put(SpecialRole.SEER, townie);
			} else if (rules.enabledRoles.get(SpecialRole.DETECTIVE) && !isAlive(SpecialRole.DETECTIVE)) {
				townie = new Detective(this);
				specialists.put(SpecialRole.DETECTIVE, townie);
			} else if (rules.enabledRoles.get(SpecialRole.TRACKER) && !isAlive(SpecialRole.TRACKER)) {
				townie = new Tracker(this);
				specialists.put(SpecialRole.TRACKER, townie);
			} else if (rules.enabledRoles.get(SpecialRole.DOCTOR) && !isAlive(SpecialRole.DOCTOR)) {
				townie = new Doctor(this);
				specialists.put(SpecialRole.DOCTOR, townie);
			} else if (rules.enabledRoles.get(SpecialRole.SMITH) && !isAlive(SpecialRole.SMITH)) {
				townie = new Smith(this);
				specialists.put(SpecialRole.SMITH, townie);
			} else if (rules.enabledRoles.get(SpecialRole.PARANOIAC) && !isAlive(SpecialRole.PARANOIAC)) {
				townie = new Paranoiac(this);
				specialists.put(SpecialRole.PARANOIAC, townie);
			} else if (rules.enabledRoles.get(SpecialRole.VENGEFUL) && !isAlive(SpecialRole.VENGEFUL)) {
				townie = new Vengeful(this);
				specialists.put(SpecialRole.VENGEFUL, townie);
			} else if (rules.enabledRoles.get(SpecialRole.PROTECTOR) && 
					getAllPlayersWithRole(SpecialRole.PROTECTOR).size() < Protector.getQuantity()) {
				townie = new Protector(this);
			} else if (rules.enabledRoles.get(SpecialRole.GUNMAN) && 
					getAllPlayersWithRole(SpecialRole.GUNMAN).size() < Gunman.getQuantity()) {
				townie = new Gunman(this);
			} else if (rules.enabledRoles.get(SpecialRole.LOVER) && 
					getAllPlayersWithRole(SpecialRole.LOVER).size() < Lover.getQuantity()) {
				townie = new Lover(this);
			} else if (rules.enabledRoles.get(SpecialRole.DOCTOR_NO_DOUBLES) && 
					getAllPlayersWithRole(SpecialRole.DOCTOR_NO_DOUBLES).size() < 1) {
				townie = new DoctorNoDoubles(this);
			} else if (rules.enabledRoles.get(SpecialRole.PARITY_COP) && 
					getAllPlayersWithRole(SpecialRole.PARITY_COP).size() < 1) {
				townie = new ParityCop(this);
			} else if (rules.enabledRoles.get(SpecialRole.INNOCENT) && !isAlive(SpecialRole.INNOCENT)) {
				townie = new TownPlayer(this);
				specialists.put(SpecialRole.INNOCENT, townie);
				exoneratedPlayers.add(townie);
			} else {
				townie = new TownPlayer(this);
			}
			players.add(townie);
			town.add(townie);
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
	
	/** @param player A player to mark as known to town as non-town non-mafia, to kill later */
	public void markKnownUnaligned(Player player) { this.knownUnaligned.add(player); }
	
	/** Turns on verbose logging for what happens during the game */
	public void setDebugOn() { this.debugLog = true; }
	
	/** @return the zero-indexed turn count */
	public int getDay() { return this.turnCount; }
	
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
		
		storyLog("And so " + player + " died.");
		
		players.remove(player);
		if (mafia.contains(player)) mafia.remove(player);
		if (town.contains(player)) town.remove(player);
		if (agents.contains(player)) agents.remove(player);
		if (prioritizedDaykillPlayers.contains(player)) prioritizedDaykillPlayers.remove(player);
		if (prioritizedNightkillPlayers.contains(player)) prioritizedNightkillPlayers.remove(player);
		if (exoneratedPlayers.contains(player)) exoneratedPlayers.remove(player);
		if (onceProtectedPlayers.contains(player)) onceProtectedPlayers.remove(player);
		if (serialKillers.contains(player)) serialKillers.remove(player);
		if (knownUnaligned.contains(player)) knownUnaligned.remove(player);
		if (jokers.contains(player)) jokers.remove(player);
		
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
		if (getDay() < CLAIM_DAY) {
			storyLog("Town no-lynched because they were waiting for cops or something");
			return null;
		}
		
		if (rules.majorityVotesOnly && (float)mafia.size() >= ((float)players.size()) / 2.0f) {
			storyLog("The mafia stalemated the vote and no one was lynched.");
			return null;
		}
		
		if (prioritizedDaykillPlayers.size() > 0) {
			Player result = randomIn(prioritizedDaykillPlayers);
			storyLog("Town had strong reason to believe " + result + " was scum and lynched them.");
			return result;
		}
		
		if (mafia.size() == 0 && knownUnaligned.size() > 0) {
			Player result = randomIn(knownUnaligned);
			storyLog("Town had strong reason to believe " + result + " was non-town and lynched.");
			return result;
		}
		
		HashSet<Player> validTargets = new HashSet<Player>(players);
		validTargets.removeAll(exoneratedPlayers);
		if (validTargets.size() > 0) {
			Player result = randomIn(validTargets);
			storyLog("Town randomly chose " + result + " from non-exonerated players and lynched.");
			return result;
		}
		
		Player result = randomIn(players);
		storyLog("Town thought everyone was townish and so randomly lynched " + result + ".");
		return result;
	}
	
	/**
	 * Finds all living players with the given role.
	 * @param	role			The role to check for
	 * @return					A set of all players with that role
	 */
	public Set<Player> getAllPlayersWithRole(SpecialRole role) {
		HashSet<Player> found = new HashSet<>();
		for (Player player : players) {
			if (player.getRole() == role) {
				found.add(player);
			}
		}
		return found;
	}
	
	/**
	 * Determines who the mafia should kill
	 * @param 	inflicter		The mafioso doing the dirty work
	 * @return					The poor slob to nightkill
	 */
	public Player getNightkillTarget(MafiaPlayer inflicter) {
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
				storyLog("Scum thought " + target + " had a role so " + inflicter + " NK'd them.");
				if (!target.isWounded() && players.contains(target)) {
					return target;
				}
			}
		}
		
		HashSet<Player> exoneratedTargets = new HashSet<Player>(exoneratedPlayers);
		exoneratedTargets.removeAll(mafia);
		exoneratedTargets.removeAll(woundedPlayers);
		if (exoneratedTargets.size() > 0) {
			Player result = randomIn(exoneratedTargets);
			storyLog("Town knows " + result + " is innocent so " + inflicter + " NK'd them");
		}
		
		HashSet<Player> legalPlayers = new HashSet<Player>(players);
		legalPlayers.removeAll(woundedPlayers);
		legalPlayers.removeAll(mafia);
		
		Player target = randomIn(legalPlayers);
		storyLog("For the NK, " + inflicter + " randomly targeted " + target + ".");
		return target;
	}
	
	/**
	 * Checks if the game is done, and if so, returns the result.
	 * @param	isNight			True if game is in night phase
	 * @return					The result of the game, or null if not over
	 */
	private SimulationResult checkForResult(boolean isNight) {
		if ((float)mafia.size() >= ((float)town.size())) {
			storyLog("The mafia now outnumber the town. MAFIA WIN.\n\n");
			return new SimulationResult(Faction.MAFIA, turnCount, isNight);
		} else if (jokers.size() > 0 && players.size() == jokers.size()) {
			storyLog("Only jokers remain. JOKER WIN.\n\n");
			return new SimulationResult(Faction.JOKER, turnCount, isNight);
		} else if (mafia.size() == 0) {
			storyLog("The vicious scums are dead. TOWN WIN.\n\n");
			return new SimulationResult(Faction.TOWN, turnCount, isNight);
		} else if (serialKillers.size() > 0 && players.size() == serialKillers.size()) {
			storyLog("Everyone's dead but the killer. SK WIN.\n\n");
			return new SimulationResult(Faction.SK, turnCount, isNight);
		} else if (agents.size() == 0 && rules.agentCount > 0) {
			storyLog("Agents are dead. MAFIA WIN.\n\n");
			return new SimulationResult(Faction.MAFIA, turnCount, isNight);
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
		
		storyLog("\n\n ** Day " + turnCount + " ** ");
		storyLog("Town players alive:  " + town.size());
		storyLog("Mafia players alive: " + mafia.size());
		
		iteratingPlayers = new ArrayList<Player>(players);
		for (Player player : iteratingPlayers) {
			player.onDawn();
		}
		
		result = checkForResult(false);
		if (result != null) {
			return result;
		}
		
		Player daykillTarget = getDaykillTarget();
		if (daykillTarget != null) {
			daykillTarget.attemptDaykill();
		}
		result = checkForResult(false);
		if (result != null) {
			return result;
		}
		
		iteratingPlayers = new ArrayList<Player>(players);
		for (Player player : iteratingPlayers) {
			if (player.isAlive() && !player.isNullified()) {
				player.onPostDaykill();
			}
		}
		
		storyLog("\n ** Night " + turnCount + " ** ");
		
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
		
		MafiaPlayer inflicter = getNightkillInflicter();
		Player nightkillTarget = getNightkillTarget(inflicter);
		if (nightkillTarget != null) {
			nightkillTarget.attemptNightkill(inflicter, this.enhancedNightkill);
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
	 * Finds the mafioso responsible to inflict the nightkill.
	 * @return					The most suitable mafioso
	 */
	private MafiaPlayer getNightkillInflicter() {
		List<MafiaPlayer> shuffled = new ArrayList<>(getMafia());
		Collections.shuffle(shuffled);
		for (MafiaPlayer player : shuffled) {
			if (player.getRole() == null) {
				return player;
			}
		}
		return shuffled.get(0);
	}
	
	/**
	 * Logs an event as having occurred (if debug settings are enabled). Useful to produce narrative
	 * events of the game as it plays out.
	 * @param	text			The text to print if enabled, prose
	 */
	public void storyLog(String text) {
		if (debugLog) {
			System.out.println(text);
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
	
	/**
	 * Returns true chance percent of the time.
	 * @param	chance			The probability of returning true, from 0.0 to 1.0
	 * @return					True if the chance succeeds
	 */
	public static boolean chance(float chance) {
		return rand.nextFloat() < chance;
	}
}
