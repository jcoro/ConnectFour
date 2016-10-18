import java.util.ArrayList;
import java.util.Random;

public class MyAgent2 extends Agent
{
    Random r;

    /**
     * Constructs a new agent, giving it the game and telling it whether it is Red or Yellow.
     * 
     * @param game The game the agent will be playing.
     * @param iAmRed True if the agent is Red, False if the agent is Yellow.
     */
    public MyAgent2(Connect4Game game, boolean iAmRed) {
        super(game, iAmRed);
        r = new Random();
    }
    
    /**
     * The move method is run every time it is this agent's turn in the game. You may assume that
     * when move() is called, the game has at least one open slot for a token, and the game has not
     * already been won.
     * 
     * By the end of the move method, the agent should have placed one token into the game at some
     * point.
     * 
     * After the move() method is called, the game engine will check to make sure the move was
     * valid. A move might be invalid if:
     * - No token was place into the game.
     * - More than one token was placed into the game.
     * - A previous token was removed from the game.
     * - The color of a previous token was changed.
     * - There are empty spaces below where the token was placed.
     * 
     * If an invalid move is made, the game engine will announce it and the game will be ended.
     * 
     */
    public void move() {
        if ( iCanWin() > -1 )
        {
            moveOnColumn( iCanWin() );
        }
        else if ( theyCanWin(0, 0, false) > -1)
        {
            moveOnColumn( theyCanWin(0, 0, false) );
        }
        else 
        {
            moveOnColumn( bestMove() );
        }                
    }

    /**
     * Returns the column index in which the best available move can be made.
     * @return returns the column index in which the best available move can be made.
     */
    public int bestMove() {
        int longest = 0; //The longest current sequence of consecutive tokens - updated as we iterate through the columns.
        int bestMove = randomMove();
        int count = 0;
        int column = myGame.getColumnCount();
        ArrayList<Integer> warningList = wouldGiveWinningMove();
        for (int i = 0; i < column; i++) {
            if ( getLowestEmptyIndex(i) > -1 && !warningList.contains(i) ) {

                Integer[] current = consecutiveTokens(i, iAmRed, 0);
                if (current[0] > longest) {
                    longest = current[0];
                    count = current[1];
                    bestMove = i;
                } else if (current[0] == longest && current[1] > count) {
                    count = current[1];
                    bestMove = i;
                } else if (current[0] == longest && current[1] == count && Math.abs(column / 2 - i) < Math.abs(column / 2 - bestMove)) {
                    bestMove = i;
                }
            }
        }

        // if randomMove is necessary, make sure that it doesn't leave the opponent with a winning move.
        // loop included to prevent infinite while loop if giving winning move is inevitable.
        int loop = 0;
        while (loop < 100 && ( warningList.contains(bestMove) || theyCanWin(0, 1, false) == bestMove)) {
            bestMove = randomMove();
            loop++;
        }
        return bestMove;
    }

    /**
     * Placing a token in some columns could leave the opponent with a winning move in the slot above.
     * This method returns an ArrayList of column indexes which would give the opponent a winning move .
     *
     * @return an ArrayList of column indexes which if played, would give a winning move to the opponent.
     * NB we can call the .contains() method on the ArrayList.
     */
    public ArrayList<Integer> wouldGiveWinningMove() {
        ArrayList<Integer> doNotPlace = new ArrayList<>();
        for (int i = 0; i < myGame.getColumnCount(); i++) {
            // Loop the starting column, so that it will check all the columns instead of only the first one.
            if (theyCanWin(i, 1, false) > -1) {
                // If the opponent can win, when the lowest empty slot of the column is one slot higher, then it's dangerous.
                doNotPlace.add(theyCanWin(i, 1, false));
            }
        }
        return doNotPlace;
    }
    
    /**
     * Determines if a center column is available with an open slot at the bottom.
     * @return the column index, -1 if not available.
     */
    public int centerAvailable() {
        if( myGame.getColumnCount() % 2 == 1)
        {
            int column = ( myGame.getColumnCount() / 2 ); 
            int slot = getLowestEmptyIndex(myGame.getColumn(column));            
            if ( slot == myGame.getRowCount()-1 )
            {
                return column;
            }
        }
        return -1;
    }    
    
    /**
     * Drops a token into a particular column so that it will fall to the bottom of the column.
     * If the column is already full, nothing will change.
     * 
     * @param columnNumber The column into which to drop the token.
     */
    public void moveOnColumn(int columnNumber) {
        int lowestEmptySlotIndex = getLowestEmptyIndex(myGame.getColumn(columnNumber));   // Find the top empty slot in the column
                                                                                          // If the column is full, lowestEmptySlot will be -1
        if (lowestEmptySlotIndex > -1)  // if the column is not full
        {
            Connect4Slot lowestEmptySlot = myGame.getColumn(columnNumber).getSlot(lowestEmptySlotIndex);  // get the slot in this column at this index
            if (iAmRed) // If the current agent is the Red player...
            {
                lowestEmptySlot.addRed(); // Place a red token into the empty slot
            }
            else // If the current agent is the Yellow player (not the Red player)...
            {
                lowestEmptySlot.addYellow(); // Place a yellow token into the empty slot
            }
        }
    }

    /**
     * Returns the index of the top empty slot in a particular column given a Connect4Column.
     * 
     * @param column The column to check.
     * @return the index of the top empty slot in a particular column; -1 if the column is already full.
     */
    public int getLowestEmptyIndex(Connect4Column column) {
        int lowestEmptySlot = -1;
        for  (int i = 0; i < column.getRowCount(); i++)
        {
            if (!column.getSlot(i).getIsFilled())
            {
                lowestEmptySlot = i;
            }
        }
        return lowestEmptySlot;
    }

    /**
     * Returns the index of the top empty slot in a particular column given an Integer
     * representing the index of the column.
     *
     * @param columnIndex The column to check.
     *
     * @return the index of the top empty slot in a particular column
     */
    public int getLowestEmptyIndex(int columnIndex) {
        Connect4Column column = myGame.getColumn(columnIndex);
        return getLowestEmptyIndex(column);
    }

    /**
     * Returns a random valid move. If your agent doesn't know what to do, making a random move
     * can allow the game to go on anyway.
     *
     * @return return the index of the column in which a token would be placed into
     */
    public int randomMove() {
        if (centerAvailable() > -1){
            return centerAvailable();
        } else {
            int i = r.nextInt(myGame.getColumnCount());
            while (getLowestEmptyIndex(myGame.getColumn(i)) == -1) {
                i = r.nextInt(myGame.getColumnCount());
            }
            return i;
        }
    }

    /**
     * Returns the column that would allow the agent to win.
     *
     * You might want your agent to check to see if it has a winning move available to it so that
     * it can go ahead and make that move. Implement this method to return what column would
     * allow the agent to win.
     *
     * @return the column that would allow the agent to win.
     */
     public int iCanWin() {
        return winningMoveAvailable(iAmRed, 0, 0, false);
     }

    /**
     * Returns the column index where the opponent could win.
     *
     * @param columnIndex The column index that is being tested.
     * @param testRow the row being tested, which is passed 0 for lowest and 1 for the slot above it.
     * @param thisMove a boolean to indicate whether to test the current move or the next move.
     * @return returns an int representing the column index where the opponent could win.
     */
    public int theyCanWin(int columnIndex, int testRow, boolean thisMove) {
        return winningMoveAvailable(!iAmRed, columnIndex, testRow, thisMove);
    }

    /**
     * Returns the Column Index that would allow a player to win on this move or the next move.
     * place the token here unless the move would immediately would allow the opponent to win.
     * @param color boolean to check color.
     * @param colIndex the column index to start counting from.
     * @param slotIndex the slot index, 0 for lowest and 1 for the slot above it.
     * @param thisMove a boolean to indicate whether to test the current move or the next move.
     * @return returns the column index offering a win on this or the subsequent move,
     * -1 if no such move exists.
     */
    public int winningMoveAvailable(boolean color, int colIndex, int slotIndex, boolean thisMove){
        int winningMove = -1;
        for (int i = colIndex; i < myGame.getColumnCount(); i++){
            int j = getLowestEmptyIndex(i) - slotIndex;
            if (j > -1) {
                if (consecutiveTokens(i, color, slotIndex)[0] >= 3) {
                    // if there is a string of consecutive tokens that is three (or two instances of two tokens separated by an empty slot)
                    return i;
                    // If not, check if the move AFTER this one (!thisMove) could set up a winning move
                    // e.g., there's two consecutive tokens with enough empty slots around them to potentially win (and those slots can be filled), OR
                    // there's more than 1 consecutive tokens streak at the same slot, and one of those slots can be filled
                } else if (!thisMove && consecutiveTokens(i, color, slotIndex)[0] == 2 && (consecutiveTokens(i, color, slotIndex)[2] == 2 ||
                        (consecutiveTokens(i, color, slotIndex)[1] > 1 && consecutiveTokens(i, color, slotIndex)[2] == 1))) {
                    winningMove = i;
                }
            }
        }
        if (color == iAmRed && winningMove > -1) {
            ArrayList<Integer> warningList = wouldGiveWinningMove();
            // To make sure that this move (which would make me win next turn) won't make the opponent win immediately
            if (warningList.contains(winningMove) || theyCanWin(0, 0, true) > -1) {
                // If this move will make the opponent win immediately, don't place the token here.
                winningMove = -1;
            }
        }
        return winningMove;
    }

    /**
     * Returns an array of three integers indicating, if a token is placed in a slot:
     * 1. How long the consecutive token streak would be
     * 2. How many consecutive token streaks with this length exist for the particular slot
     * 3. How many open slots adjacent to the consecutive token streak would be fillable.
     * @param columnIndex The column index that is being tested.
     * @param color The color being tested. Red == true, Yellow == false.
     * @param testRow the row being tested, which is passed 0 for lowest and 1 for the slot above it.
     * @return an array of three integers, the first is the length of consecutive tokens, the second is
     * the number of consecutive token streaks of this length, and the third is the number of adjacent
     * slots which are fillable (0, 1, or 2)
     */
    private Integer[] consecutiveTokens(int columnIndex, boolean color, int testRow) {
        int longest = 0; // length of the longest consecutive token streak
        int count = 0; // number of largest consecutive token streak
        int numberFillable = 0; // number of adjacent slots which are fillable (0, 1, or 2)
        for (int i = 1; i <= 4; i++) {
            // count tokens in each of the four directions
            int length = consecutiveTokensLength(columnIndex, color, i, testRow)[0];
            if (length > longest) {
                // if current length is longer, update the longest, count, and numberFillable.
                longest = length;
                count = 1;
                numberFillable = consecutiveTokensLength(columnIndex, color, i, testRow)[1];
            }
            if (longest != 0 && length == longest) {
                // if current length is same as longest, increase count by 1, make the numberFillable be the larger one.
                count++;
                numberFillable = Math.max(consecutiveTokensLength(columnIndex, color, i, testRow)[1], numberFillable);
            }
        }
        return new Integer[]{longest, count, numberFillable};

    }

    /**
     * Returns an array of two integers indicating, if a token is placed in a slot:
     * 1. How long the consecutive token streak would be
     * 2. How many open slots adjacent to the consecutive token streak would be fillable.
     * @param columnIndex The column index that is being tested.
     * @param color The color being tested. Red == true, Yellow == false.
     * @param direction There are four possible directions in which consecutive tokens can be aligned.
     * @param testRow the row being tested, which is passed 0 for lowest and 1 for the slot above it.
     * @return an array of two integers, the first is the length of consecutive tokens, the second is
     * the number of adjacent slots which are fillable (0, 1, or 2);
     */
    private Integer[] consecutiveTokensLength(int columnIndex, boolean color, int direction, int testRow) {
        int x = 0;
        int y = 0;
        if (direction == 1) {
            // vertical
            y = 1;
        } else if (direction == 2) {
            // horizontal
            x = 1;
        } else if (direction == 3) {
            // upper-left lower-right descending
            x = 1;
            y = 1;
        } else {
            // lower-left upper-right ascending
            x = 1;
            y = -1;
        }

        int length = 0; // how many adjacent slots are the same color
        int numberFillable = 0; // how many open slots adjacent to the consecutive token streak are currently fillable
        Integer[] directionMultiplier = new Integer[]{1, -1}; // multiply by one if we are moving x or y in a positive direction or -1 if negative
        for (int side : directionMultiplier) {
            int i = 1;
            boolean finished = false;
            while (!finished) {
                int deltaX = x * i * side; // horizontal distance
                int deltaY = y * i * side; // vertical distance
                if (!outOfBounds(columnIndex, deltaX, deltaY, testRow) && getIsFilled(columnIndex, deltaX, deltaY, testRow) &&
                        checkColor(columnIndex, deltaX, deltaY, testRow) == color) {
                    // If the index is valid and if the slot is filled and if the token inside the slot is the color I want.
                    length++;
                } else if (!outOfBounds(columnIndex, deltaX, deltaY, testRow) && !getIsFilled(columnIndex, deltaX, deltaY, testRow) &&
                        getLowestEmptyIndex(columnIndex + deltaX) == getLowestEmptyIndex(columnIndex) + deltaY - testRow) {
                    // Else if the index is valid and the slot is empty and the slot is ready to be filled (it is the lowest empty slot).
                    numberFillable++;
                    finished = true; // Finish counting
                }
                else
                {
                    finished = true; //Finish counting
                }
                i = i + 1; // If not finished, then it will check one slot further.
            }
        }
        return new Integer[] {length, numberFillable};
    }

    /**
     * Checks to see if a slot is outside of the board when testing for consecutive tokens
     * CurrentY gets the lowest empty index in the column being tested and subtracts 0 or 1
     * (1 moves up a row) depending upon whether we're looking at this move or the next.
     * The first tests if x is greater than the number of columns on the board
     * The second tests if the row below the row being looked at (current Y) exists
     * The third tests if there's a column to the left of the columnIndex
     * The fourth tests if the row above the row being looked at (current Y) exists
     * The second tests if
     * @param columnIndex The column relative to which we test if adjacent columns exist.
     * @param x the horizontal distance to increment.
     * @param y the vertical distance to increment.
     * @param testRow the row being tested, which is passed 0 for lowest and 1 for the slot above it.
     * @return a boolean indicating if the position on the board is invalid
     */
    private boolean outOfBounds(int columnIndex, int x , int y, int testRow) {
        int currentY = getLowestEmptyIndex(columnIndex) - testRow;
        return columnIndex + x >= myGame.getColumnCount() || currentY + y >= myGame.getRowCount() || columnIndex + x < 0 || currentY + y < 0;
    }

    /**
     * Returns a boolean indicating whether a position on the board is filled.
     * @param columnIndex The column being tested or the column relative to which we're testing the fill.
     * @param x the horizontal distance to increment.
     * @param y the vertical distance to increment.
     * @param testRow the row being tested, which is passed 0 for lowest and 1 for the slot above it.
     * @return a boolean indicating if the position on the board is filled.
     */
    private boolean getIsFilled(int columnIndex, int x, int y, int testRow) {
        int currentY = getLowestEmptyIndex(columnIndex) - testRow;
        return myGame.getColumn(columnIndex + x).getSlot(currentY + y).getIsFilled();
    }

    /**
     * Returns a boolean indicating whether a position on the board is red.
     * @param columnIndex The column being tested or the column relative to which we're testing the color.
     * @param x the horizontal distance to increment.
     * @param y the vertical distance to increment.
     * @param testRow the row being tested, which is passed 0 for lowest and 1 for the slot above it.
     * @return a boolean indicating if the position on the board is red.
     */
    private boolean checkColor(int columnIndex, int x , int y, int testRow) {
        int currentY = getLowestEmptyIndex(columnIndex) - testRow;
        Connect4Slot toCheck = myGame.getColumn(columnIndex + x).getSlot(currentY + y);
        return toCheck.getIsRed();
    }

    /**
     * Returns the name of this agent.
     *
     * @return the agent's name
     */
    public String getName() {
        return "My Agent";
    }
}
