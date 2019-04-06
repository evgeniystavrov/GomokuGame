package ru.evgs.impl;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.evgs.Cell;
import ru.evgs.CellValue;
import ru.evgs.GameTable;
import ru.evgs.HumanTurn;
// implementation of human turn
public class DefaultHumanTurn implements HumanTurn {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHumanTurn.class); 
	private GameTable gameTable;
	
	@Override
	public void setGameTable(GameTable gameTable) {
		// checking that game table not null
		Objects.requireNonNull(gameTable, "Game table can't be null");
		this.gameTable = gameTable;
	}

	@Override
	public Cell makeTurn(int row, int col) {
		// setting human cell on game table
		gameTable.setValue(row, col, CellValue.HUMAN);
		Cell cell = new Cell(row, col);
		LOGGER.info("Human turn is {}", cell);
		return cell;
	}
}
