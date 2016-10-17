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
    public MyAgent2(Connect4Game game, boolean iAmRed)
    {
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
    public void move()
    {                       
        if ( iCanWin() > -1 )
        {
            moveOnColumn( iCanWin() );
        }
        else if ( theyCanWin() > -1)
        {
            moveOnColumn( theyCanWin() );
        }
        else 
        {
            moveOnColumn( bestMove() );
        }                
    }
    
    public int bestMove(){ 
        int playerBestColumn = -1;
        int playerLargest = 0; // The largest number of adjacent player's tokens at any open slot on the board.
        int largestOpponent = 0; // The largest number of adjacent opponent's tokens at any open slot on the board (only measured as a "tiebreaker" when player's tokens are equal in more than one spot).
        for (int i = 0; i < myGame.getColumnCount(); i++)  // Iterate through each column            
        {
            int slotIndex = getLowestEmptyIndex( myGame.getColumn(i) );
            if ( getLowestEmptyIndex(myGame.getColumn(i) ) != -1 && !wouldGiveWinningMove(i, slotIndex) ) // If there's an open slot in this column...
            {                                     
                // The total number of adjacent player's tokens for the given slot on the board.
                int totalPlayers = playersBelow( i, slotIndex ) + playersLeftAndRight(i, slotIndex) +  playersUpperLeftLowerRightDiagonal(i, slotIndex) + playersLowerLeftUpperRightDiagonal(i, slotIndex);
                
                // The total number of adjacent opponent's tokens for the given slot on the board.
                int totalOpponents = opponentsBelow( i, slotIndex ) + opponentLeftAndRight(i, slotIndex) +  opponentUpperLeftLowerRightDiagonal(i, slotIndex) + opponentLowerLeftUpperRightDiagonal(i, slotIndex);              

                if (totalPlayers > playerLargest)
                {
                    playerLargest = totalPlayers;
                    largestOpponent = totalOpponents;
                    playerBestColumn = i;
                    // Tiebreaker - if adjacent player's tokens are equal, use the slot that would block the opponent the most.
                } else if (totalPlayers == playerLargest) {
                    if (totalOpponents > largestOpponent)
                    {
                        playerLargest = totalPlayers;
                        largestOpponent = totalOpponents;
                        playerBestColumn = i;
                    }
                    else
                    {
                        continue;
                    }
                } else {
                    continue;
                }                                                                                                    
            }                
        }
        if ( playerBestColumn > -1)
        {
            return playerBestColumn;
        }
        else
        {  
            return randomMove();  
        }
    }

    public boolean wouldGiveWinningMove(int ci, int si){
        if (si > 0){
            int indexAbove = si--;
            int lr = opponentLeftAndRight(ci, indexAbove);
            int ullr = opponentUpperLeftLowerRightDiagonal(ci, indexAbove);
            int llur = opponentLowerLeftUpperRightDiagonal(ci, indexAbove);
            if (lr == 3 || ullr == 3 || llur == 3){
                return true;
            }
            else
            {
                return false;
            }
        } else {
            return false;
        }
    }
    
    
    /**
     * Calculates the number of adjacent player's tokens below the lowest empty index in a column
     * @param columnIndex an integer representing a column index.
     * @param openSlotIndex an integer representing an open slot index.
     * @return an integer representing the number of player's tokens below the lowest empty index in a column
     */
    public int playersBelow(int columnIndex, int openSlotIndex)
    {
        int below = 0;
        if ( openSlotIndex < myGame.getRowCount() - 1 ) // If the available slot is not at the bottom
        {             
            boolean filledBelowArePlayers = true;
            int belowIncrement = 1;
            while( filledBelowArePlayers && belowIncrement < myGame.getRowCount() - openSlotIndex ) // Keep looking as long as there are rows AND tokens are red
            {
                Connect4Slot belowSlot = myGame.getColumn(columnIndex).getSlot(openSlotIndex + belowIncrement);  //Get the slot below the open slot                                
                if ( belowSlot.getIsFilled() && belowSlot.isMine(iAmRed) )
                    {
                        below++;
                        belowIncrement++;
                    }
                    else
                    {
                        filledBelowArePlayers = false;
                    }
            }
        }
        return below;
    }
    
    /**
     * Calculates the number of adjacent opponent's tokens below the lowest empty index in a column
     * @param columnIndex an integer representing a column index.
     * @param slotIndex an integer representing an open slot index.
     * @return an integer representing the number of opponent's tokens below the lowest empty index in a column
     */
    public int opponentsBelow(int columnIndex, int openSlotIndex)
    {
        int below = 0;
        if ( openSlotIndex < myGame.getRowCount() - 1 ) // If the available slot is not at the bottom
        {             
            boolean filledBelowAreOpponents = true;
            int belowIncrement = 1;
            while( filledBelowAreOpponents && belowIncrement < myGame.getRowCount() - ( openSlotIndex ) ) // Keep looking as long as there are rows AND tokens are yellow
            {
                Connect4Slot belowSlot = myGame.getColumn(columnIndex).getSlot(openSlotIndex + belowIncrement);  //Get the slot below the open slot                                
                if ( belowSlot.getIsFilled() && !belowSlot.isMine(iAmRed) )
                {
                    below++;
                    belowIncrement++;
                }
                else
                {
                    filledBelowAreOpponents = false;
                }
            }
        }
        return below;
    }    
    
    /**
     * Calculates the total number of adjacent player's tokens to the left and right of the lowest empty index in a column
     * @param columnIndex an integer representing a column index.
     * @param openSlotIndex an integer representing an open slot index.
     * @return an integer representing the number of player's tokens to the left and right of the lowest empty index in a column
     */
    public int playersLeftAndRight(int columnIndex, int openSlotIndex)
    {
        //CHECK LEFT
        int left = 0;
        if ( columnIndex > 0 ) //If the column is not the leftmost column
        {
            boolean filledLeftArePlayers = true;
            int leftIncrement = 1;
            while( filledLeftArePlayers && leftIncrement < columnIndex + 1  ) // Keep looking as long as there are columns AND tokens are red
            {
                Connect4Slot leftSlot = myGame.getColumn(columnIndex-leftIncrement).getSlot(openSlotIndex);  //Get the slot to the left of the open slot
                if ( leftSlot.getIsFilled() && leftSlot.isMine(iAmRed) )
                {
                    left++;
                    leftIncrement++;
                }
                else
                {
                    filledLeftArePlayers = false;
                }
            }
        }
        
        //CHECK RIGHT
        int right = 0;
        if ( columnIndex < myGame.getColumnCount()-1 ) //If the column is not the rightmost column
        {
            boolean filledRightArePlayers = true;
            int rightIncrement = 1;
            while( filledRightArePlayers && rightIncrement < myGame.getColumnCount() - columnIndex ) // Keep looking as long as there are columns AND tokens are red
            {
                Connect4Slot rightSlot = myGame.getColumn(columnIndex+rightIncrement).getSlot(openSlotIndex);  //Get the slot to the right of the open slot
                if ( rightSlot.getIsFilled() && rightSlot.isMine(iAmRed) )
                {
                    right++;
                    rightIncrement++;
                }
                else
                {
                    filledRightArePlayers = false;
                }
            }
        }                
        return left + right;
        
    }
    
    /**
     * Calculates the total number of opponent's tokens to the left and right of the lowest empty index in a column
     * @param columnIndex an integer representing a column index.
     * @param openSlotIndex an integer representing an open slot index.
     * @return an integer representing the number of opponent's tokens to the left and right of the lowest empty index in a column
     */
    public int opponentLeftAndRight(int columnIndex, int openSlotIndex)
    {
        //CHECK LEFT
        int left = 0;
        if ( columnIndex > 0 ) //If the column is not the leftmost column
        {
            boolean filledLeftAreOpponents = true;
            int leftIncrement = 1;
            while( filledLeftAreOpponents && leftIncrement < columnIndex + 1 ) // Keep looking as long as there are columns AND tokens are yellow
            {
                Connect4Slot leftSlot = myGame.getColumn(columnIndex-leftIncrement).getSlot(openSlotIndex);  //Get the slot to the left of the open slot
                if ( leftSlot.getIsFilled() && !leftSlot.isMine(iAmRed) )
                {
                    left++;
                    leftIncrement++;
                }
                else
                {
                    filledLeftAreOpponents = false;
                }
            }
        }
        
        //CHECK RIGHT
        int right = 0;
        if ( columnIndex < myGame.getColumnCount()-1 ) //If the column is not the rightmost column
        {
            boolean filledRightAreOpponents = true;
            int rightIncrement = 1;
            while( filledRightAreOpponents && rightIncrement < myGame.getColumnCount() - columnIndex ) // Keep looking as long as there are columns AND tokens are yellow
            {
                Connect4Slot rightSlot = myGame.getColumn(columnIndex+rightIncrement).getSlot(openSlotIndex);  //Get the slot to the right of the open slot
                if ( rightSlot.getIsFilled() && !rightSlot.isMine(iAmRed) )
                {
                    right++;
                    rightIncrement++;
                }
                else
                {
                    filledRightAreOpponents = false;
                }
            }
        }
        return left + right;
    }
    
    /**
     * Calculates the total number of player's tokens to the upper left and lower right diagonally of the lowest empty index in a column
     * @param columnIndex an integer representing a column index.
     * @param openSlotIndex an integer representing an open slot index.
     * @return an integer representing the number of player's tokens to the upper left and lower right diagonally of the lowest empty index in a column
     */
    public int playersUpperLeftLowerRightDiagonal(int columnIndex, int openSlotIndex)
    {        
        //CHECK UPPER LEFT DIAGONAL
        int upperLeftDiagonal = 0;
        int lowerRightDiagonal = 0;
        
        if ( columnIndex > 0  ) //If the column is not the leftmost column
        {
            boolean filledUpperLeftDiagonalArePlayers = true;
            int upperLeftDiagonalIncrement = 1;
            while(filledUpperLeftDiagonalArePlayers
                && upperLeftDiagonalIncrement < columnIndex + 1   // don't increment more than there are columns to the left.
                && upperLeftDiagonalIncrement < openSlotIndex + 1 // don't increment more than there are rows above.
                )
            {
                Connect4Slot uldSlot = myGame.getColumn( columnIndex - upperLeftDiagonalIncrement ).getSlot( openSlotIndex - upperLeftDiagonalIncrement );  //Get the slot to the upper left of the open slot
                if ( uldSlot.getIsFilled() && uldSlot.isMine(iAmRed) )
                {
                    upperLeftDiagonal++;
                    upperLeftDiagonalIncrement++;
                }
                else
                {
                    filledUpperLeftDiagonalArePlayers = false;
                }
            }
        }
        else
        {
            upperLeftDiagonal = 0;
        }
        
        //CHECK LOWER RIGHT DIAGONAL        
        if ( columnIndex < myGame.getColumnCount()-1 && openSlotIndex < myGame.getRowCount() - 1  ) //If the column is not the rightmost column AND the slot is not at the bottom
        {
            boolean filledLowerRightDiagonalArePlayers = true;
            int lowerRightDiagonalIncrement = 1;
            while(filledLowerRightDiagonalArePlayers 
                && lowerRightDiagonalIncrement < myGame.getColumnCount() - columnIndex    // don't increment more than there are columns to the right.
                && lowerRightDiagonalIncrement < myGame.getRowCount() - openSlotIndex     // don't increment more than there are rows below.
            )   
            {
                Connect4Slot lrdSlot = myGame.getColumn( columnIndex + lowerRightDiagonalIncrement ).getSlot( openSlotIndex + lowerRightDiagonalIncrement );  //Get the slot to the lower right of the open slot
                if ( lrdSlot.getIsFilled() && lrdSlot.isMine(iAmRed) )
                {
                    lowerRightDiagonal++;
                    lowerRightDiagonalIncrement++;
                }
                else
                {
                    filledLowerRightDiagonalArePlayers = false;
                }
            }
        }
        else
        {
            lowerRightDiagonal = 0;
        }
        return upperLeftDiagonal + lowerRightDiagonal;
    }
    
    /**
     * Calculates the total number of opponent's tokens to the upper left and lower right diagonally of the lowest empty index in a column
     * @param columnIndex an integer representing a column index.
     * @param openSlotIndex an integer representing an open slot index.
     * @return an integer representing the number of opponent's tokens to the upper left and lower right diagonally of the lowest empty index in a column
     */
    public int opponentUpperLeftLowerRightDiagonal(int columnIndex, int openSlotIndex)
    {        
        //CHECK UPPER LEFT DIAGONAL
        int upperLeftDiagonal = 0;
        int lowerRightDiagonal = 0;
        
        if ( columnIndex > 0  ) //If the column is not the leftmost column
        {
            boolean filledUpperLeftDiagonalAreOpponents = true;
            int upperLeftDiagonalIncrement = 1;
            while(filledUpperLeftDiagonalAreOpponents
                && upperLeftDiagonalIncrement < columnIndex + 1   // don't increment more than there are columns to the left.
                && upperLeftDiagonalIncrement < openSlotIndex + 1 // don't increment more than there are rows above.
                )
            {
                Connect4Slot uldSlot = myGame.getColumn( columnIndex - upperLeftDiagonalIncrement ).getSlot( openSlotIndex - upperLeftDiagonalIncrement );  //Get the slot to the upper left of the open slot
                if ( uldSlot.getIsFilled() && !uldSlot.isMine(iAmRed) )
                {
                    upperLeftDiagonal++;
                    upperLeftDiagonalIncrement++;
                }
                else
                {
                    filledUpperLeftDiagonalAreOpponents = false;
                }
            }
        }
        else
        {
            upperLeftDiagonal = 0;
        }
        
        //CHECK LOWER RIGHT DIAGONAL        
        if ( columnIndex < myGame.getColumnCount()-1 && openSlotIndex < myGame.getRowCount() - 1  ) //If the column is not the rightmost column AND the slot is not at the bottom
        {
            boolean filledLowerRightDiagonalAreOpponents = true;
            int lowerRightDiagonalIncrement = 1;
            while(filledLowerRightDiagonalAreOpponents 
                && lowerRightDiagonalIncrement < myGame.getColumnCount() - columnIndex    // don't increment more than there are columns to the right.
                && lowerRightDiagonalIncrement < myGame.getRowCount() - openSlotIndex     // don't increment more than there are rows below.
            )   
            {
                Connect4Slot lrdSlot = myGame.getColumn( columnIndex + lowerRightDiagonalIncrement ).getSlot( openSlotIndex + lowerRightDiagonalIncrement );  //Get the slot to the lower right of the open slot
                if ( lrdSlot.getIsFilled() && !lrdSlot.isMine(iAmRed) )
                {
                    lowerRightDiagonal++;
                    lowerRightDiagonalIncrement++;
                }
                else
                {
                    filledLowerRightDiagonalAreOpponents = false;
                }
            }
        }
        else
        {
            lowerRightDiagonal = 0;
        }
        return upperLeftDiagonal + lowerRightDiagonal;
    }
    
    /**
     * Calculates the total number of player's tokens to the lower left and upper right diagonally of the lowest empty index in a column.
     * @param columnIndex an integer representing a column index.
     * @param openSlotIndex an integer representing an open slot index.
     * @return an integer representing the number of player's tokens to the lower left and upper right diagonally of the lowest empty index in a column.
     */
    public int playersLowerLeftUpperRightDiagonal(int columnIndex, int openSlotIndex)
    {  
        int lowerLeftDiagonal = 0;
        int upperRightDiagonal = 0;        
        
        //CHECK LOWER LEFT DIAGONAL
        
        if ( columnIndex > 0 &&  myGame.getRowCount() - openSlotIndex > 1 ) //If the column is not the leftmost column AND the slot is not at the bottom
        {                  
            boolean filledLowerLeftDiagonalArePlayers = true;
            int lowerLeftDiagonalIncrement = 1;
            while(filledLowerLeftDiagonalArePlayers
                && lowerLeftDiagonalIncrement <  columnIndex + 1                         // don't increment more than there are columns to the left.
                && lowerLeftDiagonalIncrement < myGame.getRowCount() - openSlotIndex  // don't increment more than there are rows below.
            )
            {
                Connect4Slot lldSlot = myGame.getColumn( columnIndex - lowerLeftDiagonalIncrement ).getSlot( openSlotIndex + lowerLeftDiagonalIncrement );  //Get the slot to the lower left of the open slot
                if ( lldSlot.getIsFilled() && lldSlot.isMine(iAmRed) )
                {
                    lowerLeftDiagonal++;
                    lowerLeftDiagonalIncrement++;
                }
                else
                {
                    filledLowerLeftDiagonalArePlayers = false;
                }
            }
        }
        else
        {
            lowerLeftDiagonal = 0;
        }
        
        //CHECK UPPER RIGHT DIAGONAL        
        if ( columnIndex < myGame.getColumnCount()-1 && openSlotIndex > 0 ) //If the column is not the rightmost column && the slot is not at the top.
        {
            boolean filledUpperRightDiagonalArePlayers = true;
            int upperRightDiagonalIncrement = 1;
            while(filledUpperRightDiagonalArePlayers
            && upperRightDiagonalIncrement <  openSlotIndex + 1                       // don't increment more than there are rows above.
            && upperRightDiagonalIncrement < myGame.getColumnCount() - columnIndex // don't increment more than there are columns to the right.
            )
            {
                Connect4Slot urdSlot = myGame.getColumn( columnIndex + upperRightDiagonalIncrement ).getSlot( openSlotIndex - upperRightDiagonalIncrement );  //Get the slot to the upper right of the open slot                
                if ( urdSlot.getIsFilled() && urdSlot.isMine(iAmRed) )
                {
                    upperRightDiagonal++;
                    upperRightDiagonalIncrement++;
                }
                else
                {
                    filledUpperRightDiagonalArePlayers = false;
                }
            }
        }
        else
        {
            upperRightDiagonal = 0;
        }
        return lowerLeftDiagonal + upperRightDiagonal;
    }
    
    /**
     * Calculates the total number of opponent's tokens to the lower left and upper right diagonally of the lowest empty index in a column.
     * @param columnIndex an integer representing a column index.
     * @param openSlotIndex an integer representing an open slot index.
     * @return an integer representing the number of opponent's tokens to the lower left and upper right diagonally of the lowest empty index in a column.
     */
    public int opponentLowerLeftUpperRightDiagonal(int columnIndex, int openSlotIndex)
    {          
        int lowerLeftDiagonal = 0;
        int upperRightDiagonal = 0;        
        
        //CHECK LOWER LEFT DIAGONAL
        if ( columnIndex > 0 &&  myGame.getRowCount() - openSlotIndex > 1 ) //If the column is not the leftmost column AND the slot is not at the bottom
        {               
            boolean filledLowerLeftDiagonalAreOpponents = true;
            int lowerLeftDiagonalIncrement = 1;
            // don't increment more than there are columns to the left & don't increment more than there are rows below.
            while(filledLowerLeftDiagonalAreOpponents && lowerLeftDiagonalIncrement <  columnIndex + 1 && lowerLeftDiagonalIncrement < myGame.getRowCount() - openSlotIndex )
            {
                Connect4Slot lldSlot = myGame.getColumn( columnIndex - lowerLeftDiagonalIncrement ).getSlot( openSlotIndex + lowerLeftDiagonalIncrement );  //Get the slot to the lower left of the open slot
                if ( lldSlot.getIsFilled() && !lldSlot.isMine(iAmRed) )
                {                    
                    lowerLeftDiagonal++;
                    lowerLeftDiagonalIncrement++;
                }
                else
                {
                    filledLowerLeftDiagonalAreOpponents = false;
                }
            }
        }
        else
        {
            lowerLeftDiagonal = 0;
        }
        
        //CHECK UPPER RIGHT DIAGONAL              
        if ( columnIndex < myGame.getColumnCount()-1 && openSlotIndex > 0) //If the column is not the rightmost column && The slot is not at the top
        {            
            boolean filledUpperRightDiagonalAreOpponents = true;
            int upperRightDiagonalIncrement = 1;
            // don't increment more than there are rows above.
            // don't increment more than there are columns to the right.
            while(filledUpperRightDiagonalAreOpponents && upperRightDiagonalIncrement <  openSlotIndex + 1 && upperRightDiagonalIncrement < myGame.getColumnCount() - columnIndex)
            {
                Connect4Slot urdSlot = myGame.getColumn( columnIndex + upperRightDiagonalIncrement ).getSlot( openSlotIndex - upperRightDiagonalIncrement );  //Get the slot to the upper right of the open slot                
                if ( urdSlot.getIsFilled() && !urdSlot.isMine(iAmRed) )
                {
                    upperRightDiagonal++;
                    upperRightDiagonalIncrement++;
                }
                else
                {
                    filledUpperRightDiagonalAreOpponents = false;
                }
            }
        }
        else
        {
            upperRightDiagonal = 0;
        }
        return lowerLeftDiagonal + upperRightDiagonal;
    }
    
    /**
     * Determines if a center column is available with an open slot at the bottom.
     * @return the column index, -1 if not available.
     */
    public int centerAvailable()    
    {
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
    public void moveOnColumn(int columnNumber)
    {
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
     * Returns the index of the top empty slot in a particular column.
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
     * Returns a random valid movethat doesn't allow the opponent to win. If your agent doesn't know what to do, making a random move
     * can allow the game to go on anyway.
     * 
     * @return a random valid move that doesn't allow the opponent to win.
     */
    public int randomMove()
    {
        if ( centerAvailable() > -1) {
            return centerAvailable();
        } else {
            int colIndex = r.nextInt(myGame.getColumnCount());
            int slotIndex = getLowestEmptyIndex(myGame.getColumn(colIndex));
            while (slotIndex == -1 || wouldGiveWinningMove(colIndex, slotIndex))
            {
                colIndex = r.nextInt(myGame.getColumnCount());
            }
            return colIndex;
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
    public int iCanWin()
    {
        for (int i = 0; i < myGame.getColumnCount(); i++)  // Iterate through each column
        {
            if ( getLowestEmptyIndex(myGame.getColumn(i)) != -1 ) // If there's an open slot in this column...
            {
                int slotIndex = getLowestEmptyIndex(myGame.getColumn(i));
                if ( playersBelow(i, slotIndex) == 3 || playersLeftAndRight(i, slotIndex) == 3 || playersUpperLeftLowerRightDiagonal(i, slotIndex) == 3 || playersLowerLeftUpperRightDiagonal(i, slotIndex) == 3   )  // Test is any of the methods return 3 consecutive tokens
                {                    
                    return i;
                }
            }                
        }        
        return -1;
    }

    /**
     * Returns the column that would allow the opponent to win.
     * 
     * You might want your agent to check to see if the opponent would have any winning moves
     * available so your agent can block them. Implement this method to return what column should
     * be blocked to prevent the opponent from winning.
     *
     * @return the column that would allow the opponent to win.
     */
    public int theyCanWin()
    {
        for (int i = 0; i < myGame.getColumnCount(); i++)  // Iterate through each column
        {
            if ( getLowestEmptyIndex(myGame.getColumn(i)) != -1 ) // If there's an open slot in this column...
            {
                int slotIndex = getLowestEmptyIndex(myGame.getColumn(i));
                if ( opponentsBelow(i, slotIndex) == 3 || opponentLeftAndRight(i, slotIndex) == 3 || opponentUpperLeftLowerRightDiagonal(i, slotIndex) == 3 || opponentLowerLeftUpperRightDiagonal(i, slotIndex) == 3   )  // Test if any of the methods return 3 tokens
                {   
                    return i;
                }
            }                
        }        
        return -1;
    }

    /**
     * Returns the name of this agent.
     *
     * @return the agent's name
     */
    public String getName()
    {
        return "My Agent";
    }
}
