package spw4.game2048;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.Nested;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameImplTests {

    GameImpl game;
    @Mock
    Random random;

    @Nested
    class defaultStubTests {
        @BeforeEach
        void defaultStubCtor_initializesGameCorrectly() {
            game = new GameImpl();
        }

        @Test
        void board_includesTwoRandomValues_after_initialize() {
            int[][] actual = game.getBoard();
            int count = 0;

            for (int[] row : actual) {
                for (int col : row) {
                    if (col == 2 || col == 4) {
                        count++;
                    }
                }
            }

            assertEquals(2, count);
        }

        @Test
        void board_doesNotChange_ifMoveIsNotPossible() {
            int[][] board = new int[][]{
                    {2, 0, 0, 0},
                    {4, 0, 0, 0},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0}
            };
            game.setBoard(board);
            game.move(Direction.up);
            int[][] actual = game.getBoard();

            assertArrayEquals(board, actual);
        }

        @Test
        void isOver_afterSettingBoardUnplayable_returnsTrue() {
            int[][] board = new int[][]{
                    {2, 4, 2, 4},
                    {4, 2, 4, 2},
                    {2, 4, 2, 4},
                    {4, 2, 4, 2}
            };
            game.setBoard(board);
            assertTrue(game.isOver());
        }

        @Test
        void isOver_afterSettingBoardPlayable_returnsFalse() {
            int[][] board = new int[][]{
                    {0, 4, 2, 4},
                    {4, 2, 4, 2},
                    {2, 4, 2, 4},
                    {4, 2, 4, 2}
            };
            game.setBoard(board);
            assertFalse(game.isOver());
        }
        @Test
        void testHasPossibleMove_AllOROptionsCovered() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            int[][] board = new int[][]{
                    {2, 0, 2, 2},
                    {2, 0, 4, 16},
                    {8, 0, 2, 0},
                    {2, 4, 0, 0}
            };
            game.setBoard(board);

            Method hasPossibleMoveMethod = game.getClass().getDeclaredMethod("hasPossibleMove", int.class, int.class);
            hasPossibleMoveMethod.setAccessible(true);

            // Test all OR options in hasAdjacentSameValue
            assertTrue((boolean) hasPossibleMoveMethod.invoke(game, 0, 0));  // adjacent value below
            assertTrue((boolean) hasPossibleMoveMethod.invoke(game, 1, 0));  // adjacent value above
            assertTrue((boolean) hasPossibleMoveMethod.invoke(game, 0, 2));  // adjacent value right
            assertTrue((boolean) hasPossibleMoveMethod.invoke(game, 0, 3)); // adjacent value left

            // Test all OR options in hasAdjacentZero
            assertTrue((boolean) hasPossibleMoveMethod.invoke(game, 3, 1));  // empty tile above
            assertTrue((boolean) hasPossibleMoveMethod.invoke(game, 2, 2));  // empty tile below
            assertTrue((boolean) hasPossibleMoveMethod.invoke(game, 2, 0));  // empty tile right
            assertTrue((boolean) hasPossibleMoveMethod.invoke(game, 1, 2));  // empty tile left
        }

        @Test
        void toString_after_initialize_returnsCorrectString() {
            assertAll(
                    () -> assertTrue(game.toString().contains("Score: 0")),
                    () -> assertTrue(game.toString().contains("Moves: 0"))
            );
        }
    }

    @Nested
    class randomStubTests {
        @BeforeEach
        void randomStubCtor_initializesGameCorrectly() {
            when(random.nextDouble())
                    .thenReturn(0.0).thenReturn(0.0)
                    .thenReturn(0.8)
                    .thenReturn(0.25).thenReturn(0.0)
                    .thenReturn(0.5)
                    .thenReturn(0.75).thenReturn(0.0)
                    .thenReturn(0.9);
            game = new GameImpl(random);
        }

        @Test
        void getScore_onScore0_returns0() {
            int expected = 0;

            int actual = game.getScore();

            assertEquals(expected, actual);
        }

        @Test
        void getMoves_onScore0_returns0() {
            int expected = 0;

            int actual = game.getMoves();

            assertEquals(expected, actual);
        }

        @Test
        void isWon_onScore0_returnsFalse() {
            boolean actual = game.isWon();

            assertFalse(actual);
        }

        @Test
        void isOver_onScore0_returnsFalse() {
            boolean actual = game.isOver();

            assertFalse(actual);
        }

        @Test
        void getValueAtUnoccupiedTile_after_initialize_returns0() {
            int expected = 0;

            int actual = game.getValueAt(3, 3);

            assertEquals(expected, actual);
        }

        @Test
        void getValueAtOccupiedTile_after_initialize_returns2() {
            int expected = 2;

            int actual = game.getValueAt(0, 0);

            assertEquals(expected, actual);
        }

        @Test
        void setValueAt_setsValueCorrectly() {
            int expected = 4;

            game.setValueAt(0, 0, 4);
            int actual = game.getValueAt(0, 0);

            assertEquals(expected, actual);
        }

        @Test
        void getBoard_after_initialize_returnsCorrectBoard() {
            int[][] expected = new int[][]{
                    {2, 0, 0, 0},
                    {2, 0, 0, 0},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0}
            };

            int[][] actual = game.getBoard();

            assertArrayEquals(expected, actual);
        }

        @Test
        void move_inDirectionUp_mergesExistingValuesCorrectly() {
            game.move(Direction.up);
            int actual = game.getValueAt(0, 0);
            int expected = 4;
            assertEquals(expected, actual);
        }

        @Test
        void move_inDirectionDown_mergesExistingValuesCorrectly() {
            game.move(Direction.down);
            int actual = game.getValueAt(3, 0);
            int expected = 4;
            assertEquals(expected, actual);
        }

        @Test
        void move_inDirectionLeft_mergesExistingValuesCorrectly() {
            game.setValueAt(0, 1, 2);
            game.move(Direction.left);
            int actual = game.getValueAt(0, 0);
            int expected = 4;
            assertEquals(expected, actual);
        }

        @Test
        void move_inDirectionRight_mergesExistingValuesCorrectly() {
            game.setValueAt(0, 1, 2);
            game.move(Direction.right);
            int actual = game.getValueAt(0, 3);
            int expected = 4;
            assertEquals(expected, actual);
        }

        @Test
        void move_inInvalidDirection_throwsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> game.move(Direction.invalid));
        }

        @Test
        void move_inDirectionLeft_withFourEqualValues_mergesCorrectly() {
            game.setValueAt(0, 0, 2);
            game.setValueAt(0, 1, 2);
            game.setValueAt(0, 2, 2);
            game.setValueAt(0, 3, 2);
            game.move(Direction.left);
            int actualFirst = game.getValueAt(0, 0);
            int actualSecond = game.getValueAt(0, 1);
            int expected = 4;
            assertEquals(expected, actualFirst);
            assertEquals(expected, actualSecond);
        }

        @Test
        void move_inDirectionRight_withFourEqualValues_mergesCorrectly() {
            game.setValueAt(0, 0, 2);
            game.setValueAt(0, 1, 2);
            game.setValueAt(0, 2, 2);
            game.setValueAt(0, 3, 2);
            game.move(Direction.right);
            int actualFirst = game.getValueAt(0, 2);
            int actualSecond = game.getValueAt(0, 3);
            int expected = 4;
            assertEquals(expected, actualFirst);
            assertEquals(expected, actualSecond);
        }

        @Test
        void move_inDirectionUp_withFourEqualValues_mergesCorrectly() {
            game.setValueAt(0, 0, 2);
            game.setValueAt(1, 0, 2);
            game.setValueAt(2, 0, 2);
            game.setValueAt(3, 0, 2);
            game.move(Direction.up);
            int actualFirst = game.getValueAt(0, 0);
            int actualSecond = game.getValueAt(1, 0);
            int expected = 4;
            assertEquals(expected, actualFirst);
            assertEquals(expected, actualSecond);
        }

        @Test
        void move_inDirectionDown_withFourEqualValues_mergesCorrectly() {
            game.setValueAt(0, 0, 2);
            game.setValueAt(1, 0, 2);
            game.setValueAt(2, 0, 2);
            game.setValueAt(3, 0, 2);
            game.move(Direction.down);
            int actualFirst = game.getValueAt(2, 0);
            int actualSecond = game.getValueAt(3, 0);
            int expected = 4;
            assertEquals(expected, actualFirst);
            assertEquals(expected, actualSecond);
        }

        @Test
        void move_withTwo1024Tiles_isWon_returnsTrue() {
            game.setValueAt(0, 3, 1024);
            game.setValueAt(1, 3, 1024);
            game.move(Direction.up);
            boolean actual = game.isWon();
            assertTrue(actual);
        }

        @Test
        void move_withTwo1024Tiles_isOver_returnsTrue() {
            game.setValueAt(0, 3, 1024);
            game.setValueAt(1, 3, 1024);
            game.move(Direction.up);
            boolean actual = game.isOver();
            assertTrue(actual);
        }

    }
}