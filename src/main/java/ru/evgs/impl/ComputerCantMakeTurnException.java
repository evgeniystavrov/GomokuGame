package ru.evgs.impl;
// exception occurs if all fields are filled
public class ComputerCantMakeTurnException extends IllegalStateException {
	private static final long serialVersionUID = -8088634040132432079L;

	public ComputerCantMakeTurnException(String message) {
		super(message);
	}
}
