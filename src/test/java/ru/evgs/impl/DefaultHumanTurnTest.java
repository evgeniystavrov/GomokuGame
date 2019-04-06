package ru.evgs.impl;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.evgs.Cell;
import ru.evgs.CellValue;
import ru.evgs.GameTable;
import ru.evgs.HumanTurn;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DefaultHumanTurnTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    private HumanTurn humanTurn;
    @Before
    public void before(){
        humanTurn = new DefaultHumanTurn();
    }
    @Test
    public void testSetGameTableNotNull(){
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(new IsEqual<String>("Game table can't be null"));
        humanTurn.setGameTable(null);
    }
    @Test
    public void testMakeTurn(){
        GameTable gameTable = mock(GameTable.class);
        humanTurn.setGameTable(gameTable);
        Cell cell = humanTurn.makeTurn(0, 1);
        assertEquals(0, cell.getRowIndex());
        assertEquals(1, cell.getColIndex());
        verify(gameTable).setValue(0, 1, CellValue.HUMAN);
    }
}
