package spw4.game2048;

import java.util.Random;

public class GameImpl implements Game {

    private static final int ROWS = 4;
    private static final int COLS = 4;
    private static final int WINNING_TILE = 2048;

    private int[][] board;
    private int score = 0;
    private int moves = 0;
    private int highestTile = 0;
    private final Random random;


    public GameImpl() {
        random = new Random();
        initialize();
    }

    public GameImpl(Random random) {
        this.random = random;
        initialize();
    }

    public int getMoves() {
        return moves;
    }

    public int getScore() {
        return score;
    }

    public int getValueAt(int row, int col) {
        return board[row][col];
    }

    public void setValueAt(int row, int col, int value) {
        board[row][col] = value;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int[][] getBoard() {
        return board;
    }

    public boolean isOver() {
        if (isWon()) {
            return true;
        } else {
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (board[row][col] == 0 || hasPossibleMove(row, col)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isWon() {
        return highestTile == WINNING_TILE;
    }

    @Override
    public String toString() {
        StringBuilder result;
        result = new StringBuilder("Score: " + getScore() + "\n");
        result.append("Moves: ").append(getMoves()).append("\n");
        result.append("---------------------\n");
        for (int row = 0; row < ROWS; row++) {
            result.append("|");
            for (int col = 0; col < COLS; col++) {
                int value = getValueAt(row, col);
                if (value == 0) {
                    result.append("    |");
                } else {
                    result.append(String.format("%4d|", value));
                }
            }
            result.append("\n");
            result.append("---------------------\n");
        }

        return result.toString();
    }

    public void initialize() {
        board = new int[ROWS][COLS];
        // init board with null values
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                board[row][col] = 0;
            }
        }

        for (int i = 0; i < 2; i++) {
            spawnRandomTile();
        }
    }

    public void move(Direction direction) {
        int[][] oldBoard;
        oldBoard = new int[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            System.arraycopy(board[row], 0, oldBoard[row], 0, COLS);
        }

        if (direction.equals(Direction.up)) {
            for (int row = 1; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    int rowHelper = row - 1;
                    boolean thisRowMerged = false;
                    while (rowHelper >= 0) {
                        if (board[rowHelper][col] == 0) {
                            board[rowHelper][col] = board[rowHelper + 1][col];
                            board[rowHelper + 1][col] = 0;
                        } else if (!thisRowMerged) {
                            checkMergeRow(rowHelper, col);
                            thisRowMerged = true;
                        }
                        rowHelper--;
                    }
                }
            }
        } else if (direction.equals(Direction.left)) {
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 1; col < COLS; ++col) {
                    boolean thisColMerged = false;
                    int colHelper = col - 1;
                    while (colHelper >= 0) {
                        if (board[row][colHelper] == 0) {
                            board[row][colHelper] = board[row][colHelper + 1];
                            board[row][colHelper + 1] = 0;
                        } else if (!thisColMerged) {
                            checkMergeCol(row, colHelper);
                            thisColMerged = true;
                        }
                        colHelper--;
                    }
                }
            }
        } else if (direction.equals(Direction.down)) {
            for (int row = ROWS - 1; row >= 0; --row) {
                for (int col = 0; col < COLS; ++col) {
                    boolean thisRowMerged = false;
                    int rowHelper = row + 1;
                    while (rowHelper < ROWS) {
                        if (board[rowHelper][col] == 0) {
                            board[rowHelper][col] = board[rowHelper - 1][col];
                            board[rowHelper - 1][col] = 0;
                        } else if (!thisRowMerged) {
                            checkMergeRowDown(rowHelper, col);
                            thisRowMerged = true;
                        }
                        rowHelper++;
                    }
                }
            }
        } else if (direction.equals(Direction.right)) {
            for (int row = 0; row < ROWS; ++row) {
                for (int col = COLS - 1; col >= 0; --col) {
                    boolean thisColMerged = false;
                    int colHelper = col + 1;
                    while (colHelper < COLS) {
                        if (board[row][colHelper] == 0) {
                            board[row][colHelper] = board[row][colHelper - 1];
                            board[row][colHelper - 1] = 0;
                        } else if (!thisColMerged) {
                            checkMergeColRight(row, colHelper);
                            thisColMerged = true;
                        }
                        colHelper++;
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid direction");
        }

        if (changesDetected(oldBoard)) {
            spawnRandomTile();
            updateHighestTile();
            moves++;
        }
    }

    private boolean changesDetected(int[][] oldBoard) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (oldBoard[i][j] != board[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateHighestTile() {
        int highestTile = 0;
        for (int[] row : board) {
            for (int tile : row) {
                if (tile > highestTile) {
                    highestTile = tile;
                }
            }
        }
        this.highestTile = highestTile;
    }

    private void checkMergeColRight(int row, int colHelper) {
        if (board[row][colHelper] == board[row][colHelper - 1]) {
            board[row][colHelper] *= 2;
            board[row][colHelper - 1] = 0;
            score += board[row][colHelper];
        }
    }

    private void checkMergeRowDown(int rowHelper, int col) {
        if (board[rowHelper][col] == board[rowHelper - 1][col]) {
            board[rowHelper][col] *= 2;
            board[rowHelper - 1][col] = 0;
            score += board[rowHelper][col];
        }
    }

    private void checkMergeCol(int row, int colHelper) {
        if (board[row][colHelper] == board[row][colHelper + 1]) {
            board[row][colHelper] *= 2;
            board[row][colHelper + 1] = 0;
            score += board[row][colHelper];
        }
    }

    private void checkMergeRow(int rowHelper, int col) {
        if (board[rowHelper][col] == board[rowHelper + 1][col]) {
            board[rowHelper][col] *= 2;
            board[rowHelper + 1][col] = 0;
            score += board[rowHelper][col];
        }
    }

    private void spawnRandomTile() {
        boolean foundEmpty = false;

        while (!foundEmpty) {

            int row = (int) (random.nextDouble() * 4);
            int col = (int) (random.nextDouble() * 4);

            int value = random.nextDouble() < 0.9 ? 2 : 4;

            if (board[row][col] == 0) {
                board[row][col] = value;
                foundEmpty = true;
            }
        }
    }

    private boolean hasPossibleMove(int row, int col) {
        int currValue = board[row][col];

        boolean hasAdjacentSameValue =
                (row > 0 && board[row - 1][col] == currValue) ||
                        (row < ROWS - 1 && board[row + 1][col] == currValue) ||
                        (col > 0 && board[row][col - 1] == currValue) ||
                        (col < COLS - 1 && board[row][col + 1] == currValue);

        boolean hasAdjacentZero =
                (row > 0 && board[row - 1][col] == 0) ||
                        (row < ROWS - 1 && board[row + 1][col] == 0) ||
                        (col > 0 && board[row][col - 1] == 0) ||
                        (col < COLS - 1 && board[row][col + 1] == 0);

        return hasAdjacentSameValue || hasAdjacentZero;
    }
}