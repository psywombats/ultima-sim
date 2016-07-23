/**
 *  Jester.java
 *  Created on Jul 23, 2016 1:13:42 AM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import java.util.HashSet;

import net.wombatrpgs.ultima.Simulation;

/**
 * Wins when they're the only one left. Shows up as random garbage to seers.
 */
public class Joker extends Player {
	
	/**
	 * Inherited constructor.
	 * @param	simulation			The simulation this player is a part of
	 */
	public Joker(Simulation simulation) {
		super(simulation, Faction.JOKER);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#isDeceitActive()
	 */
	@Override public boolean isDeceitActive() {
		return true;
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#useSword()
	 */
	@Override
	protected void useSword() {
		Player target = simulation.getDaykillTarget();
		if (target != null) {
			target.attemptNightkill(false);
			HashSet<Player> valid = new HashSet<Player>(simulation.getPlayers());
			valid.remove(this);
			setSword(false);
			if (valid.size() > 0) {
				Simulation.randomIn(valid).setSword(true);
			}
		}
	}
}
