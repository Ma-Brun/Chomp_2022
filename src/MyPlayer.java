import java.awt.*;
import java.util.*;

public class MyPlayer {
    public Chip[][] gameBoard;
    public int[] columns;
    static HashSet<String> losingBoards = new HashSet<>();


    public MyPlayer() {
        columns = new int[10];

        ArrayList<int[]> allBoards = generatePossibilities10x10();
        allBoards.sort(Comparator.comparingInt(MyPlayer::sum));

        generateLosers(allBoards);
    }


    // Game analysis logic
    public Point move (Chip[][]pBoard){
        System.out.println("MyPlayer Move");
        gameBoard = pBoard;
        int[] board = convertChipBoardToIntArray(gameBoard);  // Real board state in a 1d array
        if (isWinner(board)) {
            ArrayList<int[]> winBoards = winner(board);
            if (!winBoards.isEmpty()) {
                int[] nextBoard = winBoards.get(0); // Take first winning move
                for (int col = 0; col < board.length; col++) {
                    if (board[col] != nextBoard[col]) {
                        int row = nextBoard[col];  // The new chip's row index
                        return new Point(row, col);
                    }
                }
            }
            // Fallback if something went wrong:
            return new Point(0, 0);

        }
        else if (!isWinner(board)) {
            int col = board.length - 1;
            int rowIndex = 0;
            for (; col >= 0; col--) {
                int col1 = col;
                if (board[col1] > 0) {
                    rowIndex = board[col] - 1;
                    System.out.printf("Fallback move: column %d, row %d%n", col, rowIndex);
                    col = 0; //fallback move
                    return new Point(rowIndex, col1);
                }
            }
            return new Point(rowIndex, col); // fallback move if the board is losing
        }
        else {
            System.out.println("Board is empty — making default move at (0, 0)");
            return new Point(0, 0); // it should never reach this point
        }
    }
    static ArrayList<int[]> winner(int[] myBoard) {
        ArrayList<int[]> winners = new ArrayList<>();
        System.out.println("It is a winner! Make any of the following moves:");
        for (int[] move : generateOneMoveBoards(myBoard)) {
            if (losingBoards.contains(getX(move))) {
                System.out.println("  -> " + getX(move));
                winners.add(move);
            }
        }
        return winners;
    }
    static void generateLosers(ArrayList<int[]> allBoards) {
        for (int[] board : allBoards) {
            boolean isLosing = true;

            for (int[] move : generateOneMoveBoards(board)) {
                if (losingBoards.contains(getX(move))) {
                    isLosing = false;
                    break;
                }
            }

            if (isLosing) {
                losingBoards.add(getX(board));
            }
        }
    }

    // Generate all possible moves from the current board state
    static ArrayList<int[]> generateOneMoveBoards(int[] board) {
        ArrayList<int[]> possibleBoards = new ArrayList<>();
        int cols = board.length;

        for (int col = 0; col < cols; col++) {
            for (int row = 1; row <= board[col]; row++) {
                if (col == 0 && row == 1) continue; // Skip the losing move
                int[] newBoard = simulateClick(board, col, row);
                possibleBoards.add(newBoard);
            }
        }
        return possibleBoards;
    }

    // Simulate a click on the board
    static int[] simulateClick(int[] board, int clickedCol, int clickedRow) {
        int[] newBoard = new int[board.length];
        for (int i = 0; i < board.length; i++) {
            if (i < clickedCol) {
                newBoard[i] = board[i];
            } else {
                newBoard[i] = Math.min(board[i], clickedRow - 1);
            }
        }
        return newBoard;
    }

    // Convert the board to a string for easy comparison
    static String getX(int[] x) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < x.length; i++) {
            sb.append(x[i]);
            if (i < x.length - 1) sb.append(",");
        }
        return sb.toString();
    }

    // Check if the board configuration is a winning state
    static boolean isWinner(int[] board) {
        for (int[] nextBoard : generateOneMoveBoards(board)) {
            if (losingBoards.contains(getX(nextBoard))) {
                return true;
            }
        }
        return false;
    }

    // Calculate the sum of the board values
    static int sum(int[] board) {
        int s = 0;
        for (int val : board) s += val;
        return s;
    }

    static ArrayList<int[]> generatePossibilities10x10() {
        ArrayList<int[]> results = new ArrayList<>();
        generateBoardsRecursive(results, new int[10], 0, 10);
        return results;
    }

    static void generateBoardsRecursive(ArrayList<int[]> results, int[] current, int index, int maxVal) {
        if (index == current.length) {
            results.add(current.clone());
            return;
        }
        for (int i = maxVal; i >= 0; i--) {
            current[index] = i;
            generateBoardsRecursive(results, current, index + 1, i);
        }
    }
    public static int[] convertChipBoardToIntArray(Chip[][] chipBoard) {
        int cols = chipBoard.length;
        int[] board = new int[cols];

        for (int col = 0; col < cols; col++) {
            int chipcount = 0;
            for(int row = 0; row < 10; row++)
                if(chipBoard[row][col] != null && chipBoard[row][col].isAlive) {
                    chipcount++;
            }
            board[col] = chipcount;
        }

        return board;
    }
}


//