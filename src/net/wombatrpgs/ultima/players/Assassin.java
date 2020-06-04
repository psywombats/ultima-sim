/**
 *  Assassin.java
 *  Created on Jun 20, 2016 11:29:49 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import net.wombatrpgs.ultima.Simulation;

/**
 * Can bypass all protection to kill a target.
 */
public class Assassin extends MafiaPlayer {
	
	// if there are only 2 more town that mafia, jump the gun for a good chance at winning
	private static final int MAFIA_DELTA_BEFORE_CLAIM = 1;

	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Assassin(Simulation simulation) {
		super(simulation);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPreNightkill()
	 */
	@Override public void onPreNightkill() {
		super.onPreNightkill();
		
		// are we still armed?
		if (!simulation.isAlive(SpecialRole.ASSASSIN)) {
			return;
		}
		
		// there is a claimed role, let's take 'em out
		if (simulation.getPrioritizedNightkills().size() > 0) {
			assassinate(Simulation.randomIn(simulation.getPrioritizedNightkills()));
			return;
		}
		
		// it's the endgame, no good to die with a loaded gun
		int mafiaDelta = simulation.getTown().size() - simulation.getMafia().size();
		if (mafiaDelta <= MAFIA_DELTA_BEFORE_CLAIM) {
			assassinate(Simulation.randomIn(simulation.getTown()));
			return;
		}
	}
	
	/**
	 * Immediately kills the victim but removes our power.
	 * @param	victim			The victim to kill
	 */
	private void assassinate(Player victim) {
		// hack, skip a wounded victim
		simulation.storyLog("The assassin used there power to defeat any doc protection.");
		simulation.enhanceNightkill();
		simulation.deregisterSpecial(SpecialRole.ASSASSIN);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.MafiaPlayer#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "the assassin";
	}
	
}
