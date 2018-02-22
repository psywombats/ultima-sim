/**
 *  Occultist.java
 *  Created on Feb 21, 2018 8:37:33 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import java.util.HashSet;

import net.wombatrpgs.ultima.Simulation;

/**
 * Suicide bomber type. When nightkilled, takes someone with them. Note this only triggers from
 * scum kills for umineko rules.
 */
public class Vengeful extends TownPlayer {

	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public Vengeful(Simulation simulation) {
		super(simulation);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "Vengeful";
	}

	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#role()
	 */
	@Override
	protected SpecialRole role() {
		return SpecialRole.VENGEFUL;
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPreNightkill()
	 */
	@Override
	public void onPreNightkill() {
		super.onPreNightkill();
		
		if (nullified) {
			return;
		}
		
		Player target = Simulation.randomIn(simulation.getPrioritizedDaykills());
		if (target == null) {
			HashSet<Player> targets = new HashSet<>(simulation.getPlayers());
			targets.remove(simulation.getPrioritizedNightkills());
			target = Simulation.randomIn(targets);
		}
		if (target != null) {
			target.visitFrom(this);
		}
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#attemptNightkill(net.wombatrpgs.ultima.players.Player, boolean)
	 */
	@Override
	public void attemptNightkill(Player inflicter, boolean ignoresProtection) {
		super.attemptNightkill(inflicter, ignoresProtection);
		if (!isAlive() && inflicter.faction == Faction.MAFIA) {
			if (visiting != null && visiting.isAlive()) {
				simulation.storyLog("On death, " + this + " chose to take out " + visiting + ".");
				visiting.attemptNightkill(this, false);
			}
		}
	}
}
