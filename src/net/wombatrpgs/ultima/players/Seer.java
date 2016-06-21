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
	
	private static final float INVESTIGATION_PERCENT_TO_FLIP = 0.5f;
	
	private List<Player> investigatedPlayers;
	
	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Seer(Simulation simulation) {
		super(simulation);
		investigatedPlayers = new ArrayList<Player>();
		investigatedPlayers.add(this);
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
		
		if (toInvestigate.size() > 0) {
			Player player = Simulation.randomIn(toInvestigate);
			investigatedPlayers.add(player);
			toInvestigate.remove(player);
			if (player.getFaction() == Faction.MAFIA) {
				simulation.prioritizeDaykill(player);
				simulation.prioritizeNightkill(this);
			}
		}
		
		float uninvestigated = (float)toInvestigate.size() / (float)simulation.getPlayers().size();
		if (uninvestigated <= INVESTIGATION_PERCENT_TO_FLIP) {
			for (Player player : toInvestigate) {
				simulation.prioritizeDaykill(player);
				simulation.prioritizeNightkill(this);
			}
		}
	}
}
