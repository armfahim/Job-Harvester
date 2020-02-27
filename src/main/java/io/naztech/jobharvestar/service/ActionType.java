package io.naztech.jobharvestar.service;

/**
 * @author Md. Kamruzzaman
 */
public enum ActionType {

	LOGIN("LOGIN"),
	NEW("NEW"),
	SELECT("SELECT"),
	UPDATE("UPDATE"),
	EMPTY(""),
	DELETE("DELETE");

	private final String actionType;

	private ActionType(String actionType) {
		this.actionType = actionType;
	}

	@Override
	public String toString() {
		return actionType;
	}

}
