package ru.evgs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.evgs.impl.DefaultComputerTurn;
import ru.evgs.impl.DefaultGameTable;
import ru.evgs.impl.DefaultHumanTurn;
import ru.evgs.impl.DefaultWinnerChecker;

public class GUIGomoku extends JFrame {
	private static final Logger LOGGER = LoggerFactory.getLogger(GUIGomoku.class); 
	private static final long serialVersionUID = 1714372457079337160L;
	private final JLabel cells[][];
	// creating game objects
	private final GameTable gameTable;
	private final HumanTurn humanTurn;
	private final ComputerTurn computerTurn;
	private final WinnerChecker winnerChecker;
	// flag for checking witch turn is first
	private boolean isHumanFirstTurn;

	public GUIGomoku() throws HeadlessException {
		super("Gomoku");
		//config section
		gameTable = new DefaultGameTable();
		humanTurn = new DefaultHumanTurn();
		computerTurn = new DefaultComputerTurn();
		winnerChecker = new DefaultWinnerChecker();
		//end config section
		initGameComponents();
		// array for display cells
		cells = new JLabel[gameTable.getSize()][gameTable.getSize()];
		// human makes a turn first on game start, always
		isHumanFirstTurn = true;
		createGameUITable();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				LOGGER.info("Game stopped with game table {}x{}", gameTable.getSize(), gameTable.getSize());
				System.exit(0);
			}
		});
	}
	// initialising game components
	protected void initGameComponents(){
		humanTurn.setGameTable(gameTable);
		computerTurn.setGameTable(gameTable);
		winnerChecker.setGameTable(gameTable);
	}

	protected void drawCellValue(Cell cell) {
		CellValue cellValue = gameTable.getValue(cell.getRowIndex(), cell.getColIndex());
		cells[cell.getRowIndex()][cell.getColIndex()].setText(cellValue.getValue());
		if(cellValue == CellValue.COMPUTER) {
			cells[cell.getRowIndex()][cell.getColIndex()].setForeground(Color.RED);
		} else  {
			// human
			cells[cell.getRowIndex()][cell.getColIndex()].setForeground(Color.BLUE);
		}
		
	}

	protected void markWinnerCells(List<Cell> winnerCells) {
		for (int i = 0; i < winnerCells.size(); i++) {
			Cell cell = winnerCells.get(i);
			cells[cell.getRowIndex()][cell.getColIndex()].setForeground(Color.CYAN);
			cells[cell.getRowIndex()][cell.getColIndex()].setFont(new Font(Font.SERIF, Font.BOLD, 35));
		}
	}
	// drawing game table
	protected void createGameUITable() {
		setLayout(new GridLayout(gameTable.getSize(), gameTable.getSize()));
		for (int i = 0; i < gameTable.getSize(); i++) {
			for (int j = 0; j < gameTable.getSize(); j++) {
				final int row = i;
				final int col = j;
				// creating label in cell
				cells[i][j] = new JLabel();
				// setting size of label
				cells[i][j].setPreferredSize(new Dimension(45, 45));
				// setting alignment
				cells[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				cells[i][j].setVerticalAlignment(SwingConstants.CENTER);
				// setting font of label
				cells[i][j].setFont(new Font(Font.SERIF, Font.PLAIN, 35));
				// creating border
				cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
				// adding label to frame
				add(cells[i][j]);
				// adding event listener
				cells[i][j].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						handleHumanTurn(row, col);
					}
				});
			}
		}
	}

	protected void startNewGame() {
		// on a new game computer makes a first turn
		isHumanFirstTurn = !isHumanFirstTurn;
		// clear game field
		gameTable.reInit();
		for (int i = 0; i < gameTable.getSize(); i++) {
			for (int j = 0; j < gameTable.getSize(); j++) {
				cells[i][j].setText(gameTable.getValue(i, j).getValue());
				cells[i][j].setFont(new Font(Font.SERIF, Font.PLAIN, 35));
				cells[i][j].setForeground(Color.BLACK);
			}
		}
		if (!isHumanFirstTurn) {
			Cell compCell = computerTurn.makeFirstTurn();
			drawCellValue(compCell);
		}
		LOGGER.info("------------------------------------------------------");
		LOGGER.info("New game started with game table {}x{} {}", gameTable.getSize(), gameTable.getSize(), isHumanFirstTurn ? "" : CellValue.COMPUTER + " made the first turn");
	}

	protected void stopGame() {
		for (int i = 0; i < gameTable.getSize(); i++) {
			for (int j = 0; j < gameTable.getSize(); j++) {
				cells[i][j].removeMouseListener(cells[i][j].getMouseListeners()[0]);
			}
		}
		LOGGER.info("Game disabled with game table {}x{}", gameTable.getSize(), gameTable.getSize());
	}
	// creating dialog when game is over
	protected void handleGameOver(String message) {
		int option =JOptionPane.showConfirmDialog(this, message);
		if (option == JOptionPane.YES_OPTION) {
			startNewGame();
		} else if (option == JOptionPane.CANCEL_OPTION) {
			System.exit(0);
		} else {
			stopGame();
		}
	}

	protected void handleHumanTurn(int row, int col) {
		try {
			// if cell is empty
			if (gameTable.isCellFree(row, col)) {
				// player making a turn
				Cell humanCell = humanTurn.makeTurn(row, col);
				// drawing a cell
				drawCellValue(humanCell);
				// checking - human win?
				WinnerResult winnerResult = winnerChecker.isWinnerFound(CellValue.HUMAN);
				if (winnerResult.winnerExists()) {
					// marking winner cells with winner color
					markWinnerCells(winnerResult.getWinnerCells());
					LOGGER.info("Human wins: {}", winnerResult.getWinnerCells());
					handleGameOver("Game over: You win!\nNew game?");
					return;
				}
				// checking for draw
				if (!gameTable.emptyCellExists()) {
					LOGGER.info("Nobody wins - draw");
					handleGameOver("Game over: Draw!\nNew game?");
					return;
				}
				// than computer make a turn
				// similarly, we check his victory
				Cell compCell = computerTurn.makeTurn();
				drawCellValue(compCell);
				winnerResult = winnerChecker.isWinnerFound(CellValue.COMPUTER);
				if (winnerResult.winnerExists()) {
					markWinnerCells(winnerResult.getWinnerCells());
					LOGGER.info("Computer wins: {}", winnerResult.getWinnerCells());
					handleGameOver("Game over: Computer wins!\nNew game?");
					return;
				}
				if (!gameTable.emptyCellExists()) {
					LOGGER.info("Nobody wins - draw");
					handleGameOver("Game over: Draw!\nNew game?");
					return;
				}
			} else {
				LOGGER.warn("Cell {}:{} is not empty", row, col);
				JOptionPane.showMessageDialog(this, "Cell is not empty! Click on empty cell!");
			}
		} catch (RuntimeException e) {
			LOGGER.error("Error in the game: "+e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		// creating GUI
		GUIGomoku w = new GUIGomoku();
		// making window not resizable
		w.setResizable(false);
		// making a window display all its elements
		w.pack();
		// getting a screen size
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		// setting to display window in center of the sreen
		w.setLocation(dim.width / 2 - w.getSize().width / 2, dim.height / 2 - w.getSize().height / 2);
		// visible? of course
		w.setVisible(true);
		LOGGER.info("------------------------------------------------------");
		LOGGER.info("New game started with game table {}x{}", w.gameTable.getSize(), w.gameTable.getSize());
	}
}
