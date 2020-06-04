/**
 *  Doctor.java
 *  Created on Jun 20, 2016 10:35:28 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

import net.wombatrpgs.ultima.Simulation;

/**
 * Prevents nightkills, but can't target same player twice in a row
 */
public class DoctorNoDoubles extends TownPlayer {
	
	// if we survive to this day, claim to provide info
	private Player lastTarget;

	/**
	 * Inherited constructor.
	 * @param	simulation		The simulation this player is a part of
	 */
	public DoctorNoDoubles(Simulation simulation) {
		super(simulation);
	}

	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPreNightkill()
	 */
	@Override
	public void onPreNightkill() {
		super.onPreNightkill();
		
		// if a PR is exposed, protect them
		// otherwise, physician, heal thyself
		Player target;
		if (simulation.getPrioritizedNightkills().size() > 0) {
			target = Simulation.randomIn(simulation.getPrioritizedNightkills());
		} else {
			target = this;
		}
		
		// switch to a secondary target if this would be a duplicate tonight
		if (target == lastTarget) {
			if (target != this) {
				target = this;
			} else if (simulation.getPrioritizedNightkills().size() > 0 &&
					!simulation.getPrioritizedNightkills().contains(target)) {
				target = Simulation.randomIn(simulation.getPrioritizedNightkills());
			} else {
				target = Simulation.randomIn(simulation.getPlayers());
			}
		}
		
		lastTarget = target;
		target.protect();
	}
	
	/**
	 * @see net.wombatrpgs.ultima.players.Player#onPostNightkill()
	 */
	@Override public void onPostNightkill() {
		super.onPostNightkill();
		
		if (Simulation.CLAIM_DAY > -1 &&
				simulation.getDay() >= Simulation.CLAIM_DAY - 1 && 
				simulation.getAllPlayersWithRole(SpecialRole.PARITY_COP).size() > 0) {
			// we claim, scum will rail us
			simulation.storyLog(this + " claimed.");
			simulation.prioritizeNightkill(this);
			simulation.exoneratePlayer(this);
		}
	}
	
	/**
	 * @see net.wombatrpgs.ultima.players.Player#friendlyName()
	 */
	@Override
	protected String friendlyName() {
		return "the doc";
	}
	
	/**
	 * @see net.wombatrpgs.ultima.players.TownPlayer#role()
	 */
	@Override
	protected SpecialRole role() {
		return SpecialRole.DOCTOR_NO_DOUBLES;
	}
}
