/**
 *  BlackMage.java
 *  Created on Jun 21, 2016 12:17:48 AM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import net.wombatrpgs.ultima.Simulation;

/**
 * Casts debilitating spells on town.
 */
public class BlackMage extends MafiaPlayer {
	
	// if there 2 more town or less than mafia, chance to just sling spells
	private static final int MAFIA_DELTA_BEFORE_CLAIM = 2;
	
	private int remainingDeceits = 1;
	private int remainingSlows = 2;

	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is part of
	 */
	public BlackMage(Simulation simulation) {
		super(simulation);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPostDaykill()
	 */
	@Override public void onPostDaykill() {
		super.onPostDaykill();
		
		// if doctor role is around and specials have been revealed, slow them
		if (remainingSlows > 0 &&
				simulation.isAlive(SpecialRole.DOCTOR) &&
				simulation.getPrioritizedNightkills().size() > 0 &&
				!simulation.isAlive(SpecialRole.ASSASSIN)) {
			slow(Simulation.randomIn(simulation.getPrioritizedNightkills()));
			return;
		}
		
		// if this is endgame territory, fire a random slow
		int mafiaDelta = simulation.getTown().size() - simulation.getMafia().size();
		if (remainingSlows > 0 && mafiaDelta <= MAFIA_DELTA_BEFORE_CLAIM) {
			slow(Simulation.randomIn(simulation.getTown()));
			return;
		}
		
		// fire a deceit if available on a fellow mafia
		if (remainingDeceits > 0) {
			deceit(Simulation.randomIn(simulation.getMafia()));
			return;
		}
	}

	/**
	 * Slows the victim.
	 * @param	target			The player to slow
	 */
	private void slow(Player target) {
		target.nullify();
		remainingSlows -= 1;
		for (Player player : simulation.getPlayers()) {
			player.activateDeceit(false);
		}
	}
	
	/**
	 * Activates deceit cloaking on the target.
	 * @param	player			The player to deceive
	 */
	private void deceit(Player player) {
		player.activateDeceit(true);
		remainingDeceits -= 1;
	}
}
