package ru.evgs;
// interface defining methods for working with the playing field
public interface GameTable {
	// finding cell by row and col
	CellValue getValue(int row, int col);
	// setting value to cell
	void setValue(int row, int col, CellValue cellValue);
	// zeroing values of cells of game field
	void reInit();
	// getting a size of game field
	int getSize();
	// checking that cell is free
	boolean isCellFree(int row, int col);
	// checking the existing of empty cells
	boolean emptyCellExists();
}
