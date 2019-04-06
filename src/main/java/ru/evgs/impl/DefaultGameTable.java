package ru.evgs.impl;

import ru.evgs.CellValue;
import ru.evgs.GameTable;

public class DefaultGameTable implements GameTable {
	private final CellValue[][] gameTable;

	public DefaultGameTable() {
		// setting a size of game table by default values
		gameTable = new CellValue[DefaultConstants.SIZE][DefaultConstants.SIZE];
		// init field
		reInit();
	}
	// getting value of cell by row and col
	@Override
	public CellValue getValue(int row, int col) {
		// verifying index
		if (row >= 0 && row < getSize() && col >= 0 && col < getSize()) {
			return gameTable[row][col];
		} else {
			throw new IndexOutOfBoundsException("Invalid row or col indexes: row=" + row + ", col=" + col + ", size=" + getSize());
		}
	}
	// set the cell value with the passed value for the row and column
	@Override
	public void setValue(int row, int col, CellValue cellValue) {
		if (row >= 0 && row < getSize() && col >= 0 && col < getSize()) {
			gameTable[row][col] = cellValue;
		} else {
			throw new IndexOutOfBoundsException("Invalid row or col indexes: row=" + row + ", col=" + col + ", size=" + getSize());
		}
	}
	// getting a size of field
	public int getSize() {
		return gameTable.length;
	}
	// initialization cells of the field by empty values
	@Override
	public void reInit() {
		for (int i = 0; i < getSize(); i++) {
			for (int j = 0; j < getSize(); j++) {
				setValue(i, j, CellValue.EMPTY);
			}
		}
	}
	// checking is cell free
	@Override
	public boolean isCellFree(int row, int col) {
		return getValue(row, col) == CellValue.EMPTY;
	}
	// checking is empty cells exists
	@Override
	public boolean emptyCellExists() {
		for (int i = 0; i < getSize(); i++) {
			for (int j = 0; j < getSize(); j++) {
				if (getValue(i, j) == CellValue.EMPTY) {
					return true;
				}
			}
		}
		return false;
	}
}
