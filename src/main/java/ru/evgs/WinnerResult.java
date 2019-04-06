package ru.evgs;

import java.util.List;
// interface defining methods to verify the winner
public interface WinnerResult {
	// checking is winner exists
	boolean winnerExists();
	// getting winner cells
	List<Cell> getWinnerCells();
}
