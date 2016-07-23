/**
 *  SpecialRole.java
 *  Created on Jun 20, 2016 9:48:07 PM for project ultima-sim
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.ultima.players;

/**
 * Specialist positions (for storage)
 */
public enum SpecialRole {
	
	SEER			("seer"),
	ASSASSIN		("assassin"),
	BLACK_MAGE		("black mage"),
	DOCTOR			("doctor"),
	THIEF			("thief"),
	SMITH			("blacksmith"),
	SERIAL_KILLER	("bad chef");
	
	private String displayName;
	
	/**
	 * Internal constructor.
	 * @param	displayName		The human-readable role name
	 */
	SpecialRole(String displayName) {
		this.displayName = displayName;
	}
	
	/** @return The human-readable name of the role */
	public String getDisplayName() {
		return displayName;
	}
}
