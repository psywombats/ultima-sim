/**
 *  TownPlayer.java
 *  Created on Jun 20, 2016 8:25:33 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import java.util.HashSet;

import net.wombatrpgs.ultima.Simulation;

/**
 * A helpful townie player. The uninformed majority.
 */
public class TownPlayer extends Player {

	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public TownPlayer(Simulation simulation) {
		super(simulation, Faction.TOWN);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#useSword()
	 */
	@Override protected void useSword() {
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
