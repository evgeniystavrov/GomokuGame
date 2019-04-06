package ru.evgs;
// interface defining methods for human turn
public interface HumanTurn {
	// remember a game table
	void setGameTable(GameTable gameTable);
	// making turn by row and col
	Cell makeTurn(int row, int col);
}
