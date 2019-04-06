package ru.evgs;
// interface defining methods for computer turn
public interface ComputerTurn {
	// remember a game table
	void setGameTable(GameTable gameTable);

	Cell makeTurn();
	
	Cell makeFirstTurn();
}
