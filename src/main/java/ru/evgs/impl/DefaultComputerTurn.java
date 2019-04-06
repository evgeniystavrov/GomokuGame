package ru.evgs.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.evgs.Cell;
import ru.evgs.CellValue;
import ru.evgs.ComputerTurn;
import ru.evgs.GameTable;
// computer turn class
public class DefaultComputerTurn implements ComputerTurn {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultComputerTurn.class);
	private GameTable gameTable;
	private int winCount = DefaultConstants.WIN_COUNT;
	// remember the playing field
	@Override
	public void setGameTable(GameTable gameTable) {
		// checking that game field not null
		Objects.requireNonNull(gameTable, "Game table can't be null");
		// checking that game field size not smaller than winner count
		if(gameTable.getSize() < winCount) {
			throw new IllegalArgumentException("Size of gameTable is small: size=" + gameTable.getSize() + ". Required >= " + winCount);
		}
		this.gameTable = gameTable;
	}
	// making turn
	@Override
	public Cell makeTurn() {
		// remember the field values
		CellValue[] figures = { CellValue.COMPUTER, CellValue.HUMAN };
		/*	At each iteration, the following combinations are searched:
			4 zero and empty cell;
			4 crosses and empty cell;
			3 zero and 2 empty cells;
			3 crosses and 2 empty cells;
			2 zero and 3 empty cells;
			2 crosses and 3 empty cells;
			1 zero and 4 empty cells;
			1 cross and 4 empty cells;
		*/
		for (int i = winCount - 1; i > 0; i--) {
			for (CellValue cellValue : figures) {
				Cell cell = tryMakeTurn(cellValue, i);
				if (cell != null) {
					LOGGER.info("Computer turn is {}", cell);
					return cell;
				}
			}
		}
		// if no combination is present, then a random cell from the set of empty ones is filled.
		return makeRandomTurn();
	}

	@Override
	public Cell makeFirstTurn() {
		// just making a turn in the middle of the field
		Cell cell = new Cell(gameTable.getSize() / 2, gameTable.getSize() / 2);
		gameTable.setValue(cell.getRowIndex(), cell.getColIndex(), CellValue.COMPUTER);
		LOGGER.info("Computer first turn is {}", cell);
		return cell;
	}
	// random turn on set of empty cells
	protected Cell makeRandomTurn() {
		List<Cell> emptyCells = getAllEmptyCells();
		if (emptyCells.size() > 0) {
			Cell randomCell = emptyCells.get(new Random().nextInt(emptyCells.size()));
			gameTable.setValue(randomCell.getRowIndex(), randomCell.getColIndex(), CellValue.COMPUTER);
			LOGGER.info("Computer random turn is {}", randomCell);
			return randomCell;
		} else {
			throw new ComputerCantMakeTurnException("All cells are filled! Have you checked draw state before call of computer turn?");
		}
	}
	// getting set of empty cells
	protected List<Cell> getAllEmptyCells(){
		List<Cell> emptyCells = new ArrayList<>();
		for (int i = 0; i < gameTable.getSize(); i++) {
			for (int j = 0; j < gameTable.getSize(); j++) {
				if (gameTable.isCellFree(i, j)) {
					emptyCells.add(new Cell(i, j));
				}
			}
		}
		return emptyCells;
	}
	// check the opportunity to make a move by the pattern
	// create a pattern of 5 cells and start sequentially moving it from left to right.
	protected Cell tryMakeTurn(CellValue cellValue, int notBlankCount) {
		LOGGER.trace("Try to make turn by row for pattern: {} empty and {} not empty cells for {}", winCount - notBlankCount, notBlankCount, cellValue);
		Cell cell = tryMakeTurnByRow(cellValue, notBlankCount);
		if (cell != null) {
			return cell;
		}
		LOGGER.trace("Try to make turn by col for pattern: {} empty and {} not empty cells for {}", winCount - notBlankCount, notBlankCount, cellValue);
		cell = tryMakeTurnByCol(cellValue, notBlankCount);
		if (cell != null) {
			return cell;
		}
		LOGGER.trace("Try to make turn by main diagonal for pattern: {} empty and {} not empty cells for {}", winCount - notBlankCount, notBlankCount, cellValue);
		cell = tryMakeTurnByMainDiagonal(cellValue, notBlankCount);
		if (cell != null) {
			return cell;
		}
		LOGGER.trace("Try to make turn by not main diagonal for pattern: {} empty and {} not empty cells for {}", winCount - notBlankCount, notBlankCount, cellValue);
		cell = tryMakeTurnByNotMainDiagonal(cellValue, notBlankCount);
		if (cell != null) {
			return cell;
		}
		return null;
	}

	protected Cell tryMakeTurnByRow(CellValue cellValue, int notBlankCount) {
		for (int i = 0; i < gameTable.getSize(); i++) {
			for (int j = 0; j < gameTable.getSize() - winCount - 1; j++) {
				boolean hasEmptyCells = false;
				int count = 0;
				List<Cell> inspectedCells = new ArrayList<>(winCount);
				// pattern
				for (int k = 0; k < winCount; k++) {
					inspectedCells.add(new Cell(i, j + k));
					if (gameTable.getValue(i, j + k) == cellValue) {
						count++;
					} else if (gameTable.getValue(i, j + k) == CellValue.EMPTY) {
						hasEmptyCells = true;
					} else {
						hasEmptyCells = false;
						break;
					}
				}
				// checking that count of found cells equals to transmitted
				if (count == notBlankCount && hasEmptyCells) {
					LOGGER.debug("Found {} empty and {} not empty cells by row: {} {}", winCount - count, count, inspectedCells, new LoggerPattern(inspectedCells));
					return makeTurnToOneCellFromDataSet(inspectedCells);
				}
			}
		}
		return null;
	}

	protected Cell tryMakeTurnByCol(CellValue cellValue, int notBlankCount) {
		for (int i = 0; i < gameTable.getSize(); i++) {
			for (int j = 0; j < gameTable.getSize() - winCount - 1; j++) {
				boolean hasEmptyCells = false;
				int count = 0;
				List<Cell> inspectedCells = new ArrayList<>(winCount);
				for (int k = 0; k < winCount; k++) {
					inspectedCells.add(new Cell(j + k, i));
					if (gameTable.getValue(j + k, i) == cellValue) {
						count++;
					} else if (gameTable.getValue(j + k, i) == CellValue.EMPTY) {
						hasEmptyCells = true;
					} else {
						hasEmptyCells = false;
						break;
					}
				}
				if (count == notBlankCount && hasEmptyCells) {
					LOGGER.debug("Found {} empty and {} not empty cells by col: {} {}", winCount - count, count, inspectedCells, new LoggerPattern(inspectedCells));
					return makeTurnToOneCellFromDataSet(inspectedCells);
				}
			}
		}
		return null;
	}

	protected Cell tryMakeTurnByMainDiagonal(CellValue cellValue, int notBlankCount) {
		for (int i = 0; i < gameTable.getSize() - winCount - 1; i++) {
			for (int j = 0; j < gameTable.getSize() - winCount - 1; j++) {
				boolean hasEmptyCells = false;
				int count = 0;
				List<Cell> inspectedCells = new ArrayList<>(winCount);
				for (int k = 0; k < winCount; k++) {
					inspectedCells.add(new Cell(i + k, j + k));
					if (gameTable.getValue(i + k, j + k) == cellValue) {
						count++;
					} else if (gameTable.getValue(i + k, j + k) == CellValue.EMPTY) {
						hasEmptyCells = true;
					} else {
						hasEmptyCells = false;
						break;
					}
				}
				if (count == notBlankCount && hasEmptyCells) {
					LOGGER.debug("Found {} empty and {} not empty cells by main diagonal: {} {}", winCount - count, count, inspectedCells, new LoggerPattern(inspectedCells));
					return makeTurnToOneCellFromDataSet(inspectedCells);
				}
			}
		}
		return null;
	}

	protected Cell tryMakeTurnByNotMainDiagonal(CellValue cellValue, int notBlankCount) {
		for (int i = 0; i < gameTable.getSize() - winCount - 1; i++) {
			for (int j = winCount - 1; j < gameTable.getSize(); j++) {
				boolean hasEmptyCells = false;
				int count = 0;
				List<Cell> inspectedCells = new ArrayList<>(winCount);
				for (int k = 0; k < winCount; k++) {
					inspectedCells.add(new Cell(i + k, j - k));
					if (gameTable.getValue(i + k, j - k) == cellValue) {
						count++;
					} else if (gameTable.getValue(i + k, j - k) == CellValue.EMPTY) {
						hasEmptyCells = true;
					} else {
						hasEmptyCells = false;
						break;
					}
				}
				if (count == notBlankCount && hasEmptyCells) {
					LOGGER.debug("Found {} empty and {} not empty cells by not main diagonal: {} {}", winCount - count, count, inspectedCells, new LoggerPattern(inspectedCells));
					return makeTurnToOneCellFromDataSet(inspectedCells);
				}
			}
		}
		return null;
	}
	// making turn in found pattern
	protected Cell makeTurnToOneCellFromDataSet(List<Cell> inspectedCells) {
		Cell cell = findEmptyCellForComputerTurn(inspectedCells);
		gameTable.setValue(cell.getRowIndex(), cell.getColIndex(), CellValue.COMPUTER);
		LOGGER.trace("The best cell is {} for pattern {} {}", cell, inspectedCells, new LoggerPattern(inspectedCells));
		return cell;
	}
	// finding the best move in transmitted pattern
	protected Cell findEmptyCellForComputerTurn(List<Cell> cells) {
		LOGGER.trace("Try to find the best turn by pattern {} {}", cells, new LoggerPattern(cells));
		for (int i = 0; i < cells.size(); i++) {
			Cell currentCell = cells.get(i);
			// if current cell is not empty,
			// we must find her neighbour that is not empty to and fill it by computer value
			if (gameTable.getValue(currentCell.getRowIndex(), currentCell.getColIndex()) != CellValue.EMPTY) {
				if (i == 0) {
					// have no neighbour, just fill it
					if (isCellEmpty(cells.get(i + 1))) {
						return cells.get(i + 1);
					}
					// if it last cell in pattern, just fill it
				} else if (i == cells.size() - 1) {
					if(isCellEmpty(cells.get(i - 1))) {
						return cells.get(i - 1);
					}
				} else {
					// if it has next of previous neighbour, randomly choose a neighbour and returning it
					boolean searchDirectionAsc = new Random().nextBoolean();
					int first = searchDirectionAsc ? i + 1 : i - 1;
					int second = searchDirectionAsc ? i - 1 : i + 1;
					if(isCellEmpty(cells.get(first))) {
						return cells.get(first);
					} else if(isCellEmpty(cells.get(second))) {
						return cells.get(second);
					}
				}
			}
		}
		// if we don't find anything it's an error
		throw new ComputerCantMakeTurnException("All cells are filled: "+cells);
	}
	
	protected boolean isCellEmpty(Cell cell) {
		return gameTable.getValue(cell.getRowIndex(), cell.getColIndex()) == CellValue.EMPTY;
	}

	private class LoggerPattern {
		private final List<Cell> cells;
		LoggerPattern(List<Cell> cells) {
			super();
			this.cells = cells;
		}
		@Override
		public String toString() {
			StringBuilder pattern = new StringBuilder("[");
			for(Cell cell : cells) {
				CellValue cellValue = gameTable.getValue(cell.getRowIndex(), cell.getColIndex());
				pattern.append(cellValue == CellValue.EMPTY ? "*" : cellValue.getValue());
			}
			pattern.append("]");
			return pattern.toString();
		}
	}
}
