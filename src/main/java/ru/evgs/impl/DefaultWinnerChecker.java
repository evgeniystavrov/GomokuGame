package ru.evgs.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.evgs.*;

public class DefaultWinnerChecker implements WinnerChecker {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWinnerChecker.class); 
	private GameTable gameTable;
	private int winCount = DefaultConstants.WIN_COUNT;
	
	@Override
	public void setGameTable(GameTable gameTable) {
		// checking that game table is not null
		Objects.requireNonNull(gameTable, "Game table can't be null");
		// checking that game table is not smaller than default winner count
		if(gameTable.getSize() < winCount) {
			throw new IllegalArgumentException("Size of gameTable is small: size=" + gameTable.getSize() + ". Required >= " + winCount);
		}
		// setting game table
		this.gameTable = gameTable;
	}
	// searching winner pattern by cell value
	@Override
	public WinnerResult isWinnerFound(CellValue cellValue) {
		// checking that value of cell is not null
		Objects.requireNonNull(cellValue, "cellValue can't be null");
		LOGGER.trace("Try to find winner by row: is {} winner?", cellValue);
		// by row
		List<Cell> result = isWinnerByRow(cellValue);
		if (result != null) {
			LOGGER.debug("Winner is {}. By row: {}", cellValue, result);
			return new DefaultWinnerResult(result);
		}
		LOGGER.trace("Try to find winner by col: is {} winner?", cellValue);
		// by col
		result = isWinnerByCol(cellValue);
		if (result != null) {
			LOGGER.debug("Winner is {}. By col: {}", cellValue, result);
			return new DefaultWinnerResult(result);
		}
		LOGGER.trace("Try to find winner by main diagonal: is {} winner?", cellValue);
		// by main diag
		result = isWinnerByMainDiagonal(cellValue);
		if (result != null) {
			LOGGER.debug("Winner is {}. By main diagonal: {}", cellValue, result);
			return new DefaultWinnerResult(result);
		}
		LOGGER.trace("Try to find winner by not main diagonal: is {} winner?", cellValue);
		// by not main diag
		result = isWinnerByNotMainDiagonal(cellValue);
		if (result != null) {
			LOGGER.debug("Winner is {}. By not main diagonals: {}", cellValue, result);
			return new DefaultWinnerResult(result);
		}
		LOGGER.trace("Winner not found");
		// if pattern not found
		return new DefaultWinnerResult(null);
	}

	protected List<Cell> isWinnerByRow(CellValue cellValue) {
		for (int i = 0; i < gameTable.getSize(); i++) {
			// array to form a winning pattern
			List<Cell> cells = new ArrayList<>(winCount);
			for (int j = 0; j < gameTable.getSize(); j++) {
				if (gameTable.getValue(i, j) == cellValue) {
					cells.add(new Cell(i, j));
					if (cells.size() == winCount) {
						return cells;
					}
				} else {
					cells.clear();
					// whether we are going outside the field
					if (j > gameTable.getSize() - winCount) {
						break;
					}
				}
			}
		}
		return null;
	}

	protected List<Cell> isWinnerByCol(CellValue cellValue) {
		for (int i = 0; i < gameTable.getSize(); i++) {
			List<Cell> cells = new ArrayList<>(winCount);
			for (int j = 0; j < gameTable.getSize(); j++) {
				if (gameTable.getValue(j, i) == cellValue) {
					cells.add(new Cell(j, i));
					if (cells.size() == winCount) {
						return cells;
					}
				} else {
					cells.clear();
					if (j > gameTable.getSize() - winCount) {
						break;
					}
				}
			}
		}
		return null;
	}

	protected List<Cell> isWinnerByMainDiagonal(CellValue cellValue) {
		int winCountMinus1 = winCount - 1;
		// on rows
		for (int i = 0; i < gameTable.getSize() - winCountMinus1; i++) {
			// on cols
			for (int j = 0; j < gameTable.getSize() - winCountMinus1; j++) {
				List<Cell> cells = new ArrayList<>(winCount);
				// on diag
				for (int k = 0; k < winCount; k++) {
					if (gameTable.getValue(i + k, j + k) == cellValue) {
						cells.add(new Cell(i + k, j + k));
						if (cells.size() == winCount) {
							return cells;
						}
					} else {
						break;
					}
				}
			}
		}
		return null;
	}

	protected List<Cell> isWinnerByNotMainDiagonal(CellValue cellValue) {
		int winCountMinus1 = winCount - 1;
		for (int i = 0; i < gameTable.getSize() - winCountMinus1; i++) {
			for (int j = winCountMinus1; j < gameTable.getSize(); j++) {
				List<Cell> cells = new ArrayList<>(winCount);
				for (int k = 0; k < winCount; k++) {
					if (gameTable.getValue(i + k, j - k) == cellValue) {
						cells.add(new Cell(i + k, j - k));
						if (cells.size() == winCount) {
							return cells;
						}
					} else {
						break;
					}
				}
			}
		}
		return null;
	}
	// internal implementation of the winner check
	private static class DefaultWinnerResult implements WinnerResult {
		private final List<Cell> winnerCells;

		DefaultWinnerResult(List<Cell> winnerCells) {
			if (winnerCells != null) {
				this.winnerCells = Collections.unmodifiableList(winnerCells);
			} else {
				this.winnerCells = Collections.emptyList();
			}
		}

		public List<Cell> getWinnerCells() {
			return winnerCells;
		}

		public boolean winnerExists() {
			return winnerCells.size() > 0;
		}
	}
}
