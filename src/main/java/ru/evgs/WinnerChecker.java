package ru.evgs;
// interface defining methods for checking is winner found
public interface WinnerChecker {
	// remember a game table
	void setGameTable(GameTable gameTable);
	// checks if there is a winning combination by value of cell
	WinnerResult isWinnerFound(CellValue cellValue);
}
