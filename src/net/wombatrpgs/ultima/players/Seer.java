/**
 *  Seer.java
 *  Created on Jun 20, 2016 9:54:09 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.ultima.Simulation;

/**
 * Can detect scum.
 */
public class Seer extends TownPlayer {
	
	private List<Player> investigatedPlayers;
	
	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Seer(Simulation simulation) {
		super(simulation);
		investigatedPlayers = new ArrayList<Player>();
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPostNightkill()
	 */
	@Override public void onPostNightkill() {
		super.onPostNightkill();
		ArrayList<Player> toInvestigate = new ArrayList<Player>(simulation.getPlayers());
		for (Player player : investigatedPlayers) {
			if (toInvestigate.contains(player)) {
				toInvestigate.remove(player);
			}
		}
		
		toInvestigate.remove(this);
		
		if (toInvestigate.size() > 0) {
			Player player = Simulation.randomIn(toInvestigate);
			investigatedPlayers.add(player);
			if (player.getFaction() == Faction.MAFIA) {
				simulation.prioritizeDaykill(player);
				simulation.prioritizeNightkill(this);
			}
		}
	}
}
