import java.util.LinkedList;
import java.util.Queue;

public class MartiniEngine {

    private String name = "Martini-C3260061";
    private int[] currentBoard = new int[42];

    //Values of the board. Higher numbers mean that space is more desirable
    private int[] boardValues = {1,  5, 10, 50,  10, 5, 1,
                                 10, 15, 20, 55,  20, 15, 20,
                                 25, 30, 40, 80, 40, 30, 25,
                                 25, 30, 40, 80, 40, 30, 25,
                                 10, 15, 20, 55,  20, 15, 20,
                                 1,  5, 10, 50,  10, 5, 1};
    private int EMPTY = 0;
    private int MINE = 1;
    private int FIRSTPLAYER = 2;

    //Essential Functions
    //------------------------------------------------------------------------------------------------------------------
    //Response to name
    public String getName(){return name;}

    //Response to isready
    public String ready(){return "readyok";}

    //Response to position startpos <moves>
    public int updateBoard(String input, int player) {

        //Converts last character of game log to integer value representing column number
        int col = Character.getNumericValue(input.charAt(input.length() - 1));
        int finalAddress = findAvailableSpace(col, currentBoard);

        //Ensures finalAddress is a valid index
        if(finalAddress >= 0) {
            currentBoard[finalAddress] = player;
        }

        return finalAddress;
    }

    //Response to go ftime x stime y
    public void findBestMove(int timeRemaining){
        int bestVal = 0;
        int index = 0;
        int score;
        int address;
        //LinkedList<Node> children = initChildren(currentBoardNode, MINE);

        //As we at iterating over the children generated for my moves, negaMax must
        //first run and favour the opponent
        if(FIRSTPLAYER == MINE) {
            bestVal = Integer.MIN_VALUE;
            for (int i = 0; i < 7; i++) {
                address = findAvailableSpace(i, currentBoard);
                if(address < 0) continue;
                makeMove(address, MINE);

                score = mini(currentBoard, calcDepth(timeRemaining), Integer.MIN_VALUE, Integer.MAX_VALUE, (3-FIRSTPLAYER));
                if (score > bestVal) {
                    bestVal = score;
                    index = i;
                }
                undoMove(address);
            }
        }
        else {
            bestVal = Integer.MAX_VALUE;
            for (int i = 0; i < 7; i++) {
                address = findAvailableSpace(i, currentBoard);
                if(address < 0) continue;
                makeMove(address, MINE);
                score = maxi(currentBoard, calcDepth(timeRemaining), Integer.MIN_VALUE, Integer.MAX_VALUE, FIRSTPLAYER);
                if (score < bestVal) {
                    bestVal = score;
                    index = i;
                }
                undoMove(address);
            }
        }

        System.out.println("bestmove " + index +  " " + bestVal);
        updateBoard(Integer.toString(index), 1);
    }

    //Response to perft x
    public int perft(int depth, Node root){
        int nodes = 1;
        if(depth == 0) return 1;
        LinkedList<Node> children = initChildren(root, MINE);
        for(int i = 0; i < children.size(); i++){
            nodes += perft(depth-1, children.get(i));
        }
        return nodes;
    }

    //Response to quit
    public String quit(){return "quitting";}

    //Getters & Setters
    //------------------------------------------------------------------------------------------------------------------
    public int[] getCurrentBoard(){return currentBoard;}
    public void setFirstPlayer(int FIRSTPLAYER){this.FIRSTPLAYER = FIRSTPLAYER;}
    public boolean isFirst(){return FIRSTPLAYER == MINE;}
    public boolean isFull(int colNum){return currentBoard[colNum] != EMPTY;}
    public boolean isAvailable(int colNum){return currentBoard[colNum] == EMPTY;}

    //Win Checking Functions
    //------------------------------------------------------------------------------------------------------------------
    //These functions iterate over all elements of the game board and check to see if there is an instance of
    //four consecutive pieces from the same player.
    public WinPair checkWin(int[] boardState){
        WinPair vertWP = checkVertical(boardState);
        WinPair horWP = checkHorizontal(boardState);
        WinPair d1WP = checkDiagOne(boardState);
        WinPair d2WP = checkDiagTwo(boardState);

        if(vertWP.hasWin()) return vertWP;
        else if(horWP.hasWin()) return horWP;
        else if(d1WP.hasWin()) return d1WP;
        else if(d2WP.hasWin()) return d2WP;
        else return new WinPair();
    }
    public WinPair checkHorizontal(int[] boardState){
        WinPair result = new WinPair();

        outerloop:
        for(int i = 0; i < 36; i = i + 7){
            for(int j = 0; j < 4; j++){
                int currentValue = boardState[i+j];

                //Starts the loop again
                if(currentValue == EMPTY) continue;

                int second, third, fourth;
                second = boardState[i+j+1];
                third = boardState[i+j+2];
                fourth = boardState[i+j+3];

                //If any of second, third or fourth are not currentValue, or are not 1, 2 or 3 spaces away from the
                //starting element, the loop will start again. Ensures second, third and fourth are valid elements
                //and are in the correct positions relative to (i+j)
                if((i+j) % 7 != ((i+j+1) % 7) - 1 || currentValue != second) continue;
                if((i+j) % 7 != ((i+j+2) % 7) - 2 || currentValue != third) continue;
                if((i+j) % 7 != ((i+j+3) % 7) - 3 || currentValue != fourth) continue;

                //If execution makes it here, there is a 4 in a row.
                result.setHasWin(true);
                result.setWinnerNumber(currentValue);
                break outerloop;
            }
        }
        return result;
    }
    public WinPair checkVertical(int[] boardState){
        WinPair result = new WinPair();

        outerloop:
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 21; j = j + 7){
                int currentValue = boardState[i+j];

                //Starts the loop again
                if(currentValue == EMPTY) continue;

                int second, third, fourth;
                second = boardState[i+j+7];
                third = boardState[i+j+14];
                fourth = boardState[i+j+21];

                //If any of second, third or fourth are not currentValue, or are not in the same column as (i+j)
                //the loop will start again. Ensures second, third and fourth are valid elements
                //and are in the correct positions relative to (i+j)
                if((i+j) % 7 != ((i+j+7) % 7)  || currentValue != second) continue;
                if((i+j) % 7 != ((i+j+14) % 7) || currentValue != third) continue;
                if((i+j) % 7 != ((i+j+21) % 7) || currentValue != fourth) continue;

                //If execution makes it here, there is a 4 in a row.
                result.setHasWin(true);
                result.setWinnerNumber(currentValue);
            }
        }
        return result;
    }
    public WinPair checkDiagOne(int[] boardState){
        WinPair result = new WinPair();
        int second, third, fourth;
        //Series of loops that are used to check for diagonal wins, starting in the top left of the board
        //towards the bottom right
        //Each loop ensures that second, third and fourth are valid elements and are in the correct positions
        //relative to (i+j). If there are 4 consecutive pieces of the same player, a win is detected
        for(int i = 3; i < 15; i += 11){
            int currentVal = boardState[i];
            if(currentVal == EMPTY) continue;

            second = boardState[i+8];
            third = boardState[i+16];
            fourth = boardState[i+24];
            if((i) % 7 != ((i+8) % 7) - 1 || currentVal != second) continue;
            if((i) % 7 != ((i+16) % 7) - 2 || currentVal != third) continue;
            if((i) % 7 != ((i+24) % 7) - 3 || currentVal != fourth) continue;

            result.setHasWin(true);
            result.setWinnerNumber(currentVal);
        }
        for(int i = 2; i < 8; i += 5){
            int currentVal = boardState[i];
            if(currentVal == EMPTY) continue;

            second = boardState[i+8];
            third = boardState[i+16];
            fourth = boardState[i+24];
            if((i) % 7 != ((i+8) % 7) - 1 || currentVal != second) continue;
            if((i) % 7 != ((i+16) % 7) - 2 || currentVal != third) continue;
            if((i) % 7 != ((i+24) % 7) - 3 || currentVal != fourth) continue;

            result.setHasWin(true);
            result.setWinnerNumber(currentVal);
        }
        for(int i = 10; i < 16; i += 5){
            int currentVal = boardState[i];
            if(currentVal == EMPTY) continue;

            second = boardState[i+8];
            third = boardState[i+16];
            fourth = boardState[i+24];
            if((i) % 7 != ((i+8) % 7) - 1 || currentVal != second) continue;
            if((i) % 7 != ((i+16) % 7) - 2 || currentVal != third) continue;
            if((i) % 7 != ((i+24) % 7) - 3 || currentVal != fourth) continue;

            result.setHasWin(true);
            result.setWinnerNumber(currentVal);
        }
        for(int i = 0; i < 2; i++){
            int currentVal = boardState[i];
            if(currentVal == EMPTY) continue;

            second = boardState[i+8];
            third = boardState[i+16];
            fourth = boardState[i+24];
            if((i) % 7 != ((i+8) % 7) - 1 || currentVal != second) continue;
            if((i) % 7 != ((i+16) % 7) - 2 || currentVal != third) continue;
            if((i) % 7 != ((i+24) % 7) - 3 || currentVal != fourth) continue;

            result.setHasWin(true);
            result.setWinnerNumber(currentVal);
        }
        for(int i = 8; i < 10; i++){
            int currentVal = boardState[i];
            if(currentVal == EMPTY) continue;

            second = boardState[i+8];
            third = boardState[i+16];
            fourth = boardState[i+24];
            if((i) % 7 != ((i+8) % 7) - 1 || currentVal != second) continue;
            if((i) % 7 != ((i+16) % 7) - 2 || currentVal != third) continue;
            if((i) % 7 != ((i+24) % 7) - 3 || currentVal != fourth) continue;

            result.setHasWin(true);
            result.setWinnerNumber(currentVal);
        }
        for(int i = 16; i < 18; i++){
            int currentVal = boardState[i];
            if(currentVal == EMPTY) continue;

            second = boardState[i+8];
            third = boardState[i+16];
            fourth = boardState[i+24];
            if(currentVal != second) continue;
            if(currentVal != third) continue;
            if(currentVal != fourth) continue;

            result.setHasWin(true);
            result.setWinnerNumber(currentVal);
        }
        return result;
    }
    public WinPair checkDiagTwo(int[] boardState){
        WinPair result = new WinPair();
        int second, third, fourth;
        //Series of loops that are used to check for diagonal wins, starting in the top right of the board
        //towards the bottom left
        //Each loop ensures that second, third and fourth are valid elements and are in the correct positions
        //relative to (i+j). If there are 4 consecutive pieces of the same player, a win is detected
        for(int i = 3; i < 21; i += 17){
            int currentVal = boardState[i];
            if(currentVal == EMPTY) continue;

            second = boardState[i+6];
            third = boardState[i+12];
            fourth = boardState[i+18];
            if((i) % 7 != ((i+6) % 7) + 1 || currentVal != second) continue;
            if((i) % 7 != ((i+12) % 7) + 2 || currentVal != third) continue;
            if((i) % 7 != ((i+18) % 7) + 3 || currentVal != fourth) continue;

            result.setHasWin(true);
            result.setWinnerNumber(currentVal);
        }
        for(int i = 4; i < 14; i += 9){
            int currentVal = boardState[i];
            if(currentVal == EMPTY) continue;

            second = boardState[i+6];
            third = boardState[i+12];
            fourth = boardState[i+18];
            if((i) % 7 != ((i+6) % 7) - 1 || currentVal != second) continue;
            if((i) % 7 != ((i+12) % 7) - 2 || currentVal != third) continue;
            if((i) % 7 != ((i+18) % 7) - 3 || currentVal != fourth) continue;

            result.setHasWin(true);
            result.setWinnerNumber(currentVal);
        }
        for(int i = 10; i < 20; i += 9){
            int currentVal = boardState[i];
            if(currentVal == EMPTY) continue;

            second = boardState[i+6];
            third = boardState[i+12];
            fourth = boardState[i+18];
            if((i) % 7 != ((i+6) % 7) - 1 || currentVal != second) continue;
            if((i) % 7 != ((i+12) % 7) - 2 || currentVal != third) continue;
            if((i) % 7 != ((i+18) % 7) - 3 || currentVal != fourth) continue;

            result.setHasWin(true);
            result.setWinnerNumber(currentVal);
        }
        for(int i = 5; i < 7; i++){
            int currentVal = boardState[i];
            if(currentVal == EMPTY) continue;

            second = boardState[i+6];
            third = boardState[i+12];
            fourth = boardState[i+18];
            if((i) % 7 != ((i+6) % 7) + 1 || currentVal != second) continue;
            if((i) % 7 != ((i+12) % 7) + 2 || currentVal != third) continue;
            if((i) % 7 != ((i+18) % 7) + 3 || currentVal != fourth) continue;

            result.setHasWin(true);
            result.setWinnerNumber(currentVal);
        }
        for(int i = 11; i < 13; i++){
            int currentVal = boardState[i];
            if(currentVal == EMPTY) continue;

            second = boardState[i+6];
            third = boardState[i+12];
            fourth = boardState[i+18];
            if((i) % 7 != ((i+6) % 7) + 1 || currentVal != second) continue;
            if((i) % 7 != ((i+12) % 7) + 2 || currentVal != third) continue;
            if((i) % 7 != ((i+18) % 7) + 3 || currentVal != fourth) continue;

            result.setHasWin(true);
            result.setWinnerNumber(currentVal);
        }
        for(int i = 17; i < 19; i++){
            int currentVal = boardState[i];
            if(currentVal == EMPTY) continue;

            second = boardState[i+6];
            third = boardState[i+12];
            fourth = boardState[i+18];
            if(currentVal != second) continue;
            if(currentVal != third) continue;
            if(currentVal != fourth) continue;

            result.setHasWin(true);
            result.setWinnerNumber(currentVal);
        }
        return result;
    }

    //Minimax and Evaluation function implementations
    //------------------------------------------------------------------------------------------------------------------
    public int maxi(int[] root, int depth, int alpha, int beta, int player){
        int score;

        if(depth == 0) return evaluation(root, depth+1);
        //if(checkWin(root).hasWin()) return evaluation(root, depth+1);

        int max = Integer.MIN_VALUE;
        int address;

        //One loop for each column
        for(int i = 0; i < 7; i++){

            //Finds the lowest space of column i
            address = findAvailableSpace(i, root);

            //Ensures the address is in bounds
            if(address < 0) continue;

            //Makes a move
            root[address] = player;

            //score = mini (root, depth - 1, alpha, beta, 3- player);
            //if(score > max) max = score;
            score = mini (root, depth - 1, alpha, beta, 3- player);
            max = Math.max(max, score);
            alpha = Math.max(alpha, max);


            //Undoes the move
            root[address] = EMPTY;
            if(beta <= alpha) break;
        }
        return max;
    }
    public int mini(int[] root, int depth, int alpha, int beta, int player){
        int score;

        if(depth == 0) return evaluation(root, depth+1);
        //if(checkWin(root).hasWin()) return evaluation(root, depth+1);

        int min = Integer.MAX_VALUE;
        int address;

        //One loop for each column
        for(int i = 0; i < 7; i++){

            //Finds the lowest space of column i
            address = findAvailableSpace(i, root);

            //Ensures the address is in bounds
            if(address < 0) continue;

            //Makes a move
            root[address] = player;

            //score = maxi(root, depth - 1, alpha, beta, 3 - player);
            score = maxi(root, depth - 1, alpha, beta, 3-player);
            //if(score < min) min = score;
            min = Math.min(min, score);
            beta = Math.min(beta, min);
            //Undoes the move
            root[address] = EMPTY;
            if(beta <= alpha) break;
        }
        return min;
    }
    public int evaluation(int[] root, int depth){
        WinPair result = checkWin(root);

        int sum = 0;

        //Increase board values if maximisingPlayer can connect 2 or 3 in a row
        sum += (numOfTwos(root, FIRSTPLAYER) * 50);
        sum += (numOfThrees(root, FIRSTPLAYER) * 200);

        //Decrease board values if other player will connect 2 or 3 in a row
        //This acts as maximising player attempting to block the other player
        sum -= (numOfTwos(root, 3-FIRSTPLAYER) * 40);
        sum -= (numOfThrees(root, 3-FIRSTPLAYER) * 190);

        if(result.hasWin()){
            if(result.getWinner() == FIRSTPLAYER){
                sum += 1000000 * depth;
            }
            else{
                sum -= 1000000 * depth;
            }
        }

        for(int i = 0; i < root.length; i++) {
            if (root[i] == FIRSTPLAYER) sum += boardValues[i];
            else if (root[i] == 3-FIRSTPLAYER) sum -= boardValues[i];
        }

        return sum;
    }

    //Misc functions
    //------------------------------------------------------------------------------------------------------------------
    //Given a column number and a board state, this function returns the lowest free space of that column. If the
    //column is full, it returns -1
    public int findAvailableSpace(int colNum, int[] board){
        boolean found = false;

        //Finds the address of the lowest point of the given column
        int currentSpace = colNum + 35;

        int nextSpace = currentSpace - 7;

        while(!found){
            if(board[currentSpace] == EMPTY) {
                found = true;
            }

            //Ensures nextSpace is in-bounds
            else if(nextSpace >= 0){
                currentSpace = nextSpace;
                nextSpace = nextSpace - 7;
            }
            else {
                currentSpace = -1;
                break;
            }
        }
        return currentSpace;
    }

    //Generates children of a given node. These children contain valid game states.
    public LinkedList<Node> initChildren(Node root, int player){
        LinkedList<Node> children = new LinkedList<>();

        //As there are 7 columns, any node can have no more than 7 children
        for(int i = 0; i < 7; i ++){

            //Finds the address of the lowest point of coloumn 'i' within root's gamestate
            int lowestAddress = findAvailableSpace(i, root.getState());

            if(lowestAddress < 0) continue;

            //Initialises a new node that is identical to root, but contains a 'player' piece in
            //column 'i'
            Node temp = new Node(root);
            temp.setPlayer(player);
            temp.getState()[lowestAddress] = player;
            temp.setColNum(lowestAddress % 7);
            children.add(temp);
        }
        return children;
    }

    //Counts the number of 2 consecutive 'currentPlayer' pieces within the current board
    public int numOfTwos(int[] root, int currentPlayer){
        int sum = 0;
        for(int i = 0; i < PrecomputedIndexes.twoInARow.length; i++){

            //Checks to see if the ith space has the currentPlayer's piece in it
            if(root[i] == currentPlayer) {
                for (int j = 0; j < PrecomputedIndexes.twoInARow[i].size(); j++) {
                    if(root[PrecomputedIndexes.twoInARow[i].get(j)] == currentPlayer){
                        sum++;
                    }
                }
            }
        }
        return sum;
    }

    //Counts the number of 3 consecutive 'currentPlayer' pieces within the current baord
    public int numOfThrees(int[] root, int currentPlayer){
        int sum = 0;
        for(int i = 0; i < PrecomputedIndexes.threeInARow.length; i++){

            //Checks to see if the ith space has the currentPlayer's piece in it
            if(root[i] == currentPlayer) {

                for (int j = 0; j < PrecomputedIndexes.threeInARow[i].size() - 1; j = j + 2) {
                    if(root[PrecomputedIndexes.threeInARow[i].get(j)] == currentPlayer && root[PrecomputedIndexes.threeInARow[i].get(j+1)] == currentPlayer){
                        sum++;
                    }
                }
            }
        }
        return sum;
    }

    //Returns a depth to generate moves to based on the timeRemaining of the player
    public int calcDepth(int timeRemaining){
        return 9;
    }

    //Basically just a fancy way of updating the 'address'th element of currentBoard.
    //Used largely for readability sake
    public void makeMove(int address, int player){currentBoard[address] = player;}

    //Virtually the same as makeMove, fancy way of undoing a move given the address.
    //Used largely for readability sake
    public void undoMove(int address){ currentBoard[address] = EMPTY; }

    //Debug functions
    //------------------------------------------------------------------------------------------------------------------
    //Prints out currentBoardNode's gamestate. Useful in debugging
    public void printBoard(){
        StringBuilder sb = new StringBuilder();

        int counter = 0;
        for(int i = 0; i < currentBoard.length; i++) {
            sb.append(currentBoard[i]);
            sb.append("\t");
            counter++;
            if(counter == 7) {
                sb.append("\n");
                counter = 0;
            }
        }
        sb.append("------------------------------------------------------------------------");
        sb.append("\n");
        for(int i = 0; i < 7; i++) {
            sb.append(i);
            sb.append("\t");
        }


        System.out.println(sb.toString());
    }

    public void debug(){
        FIRSTPLAYER = MINE;
    }


    //WinPair class
    //When checking for a win on a given board, this object contains true/false if there is/isnt a win, and if there
    //is a win it contains the winning player's number
    //------------------------------------------------------------------------------------------------------------------
    private class WinPair{
        private int winnerNumber;
        private boolean hasWin;

        public WinPair(){
            winnerNumber = -1;
            hasWin = false;
        }

        public void setWinnerNumber(int winnerNumber){this.winnerNumber = winnerNumber;}

        public void setHasWin(boolean hasWin){this.hasWin = hasWin;}

        public int getWinner(){return winnerNumber;}

        public boolean hasWin(){return hasWin;}
    }

    //Sneaky lil easter eggs maybe
    public String[] initJokes(){
        String[] jokes = new String[6];
        jokes[0] = "Why do birds fly to warmer climates in the winter?\nIts much easier than walking!";
        jokes[1] = "What creature is smarter than a talking parrot?\nA spelling bee!";
        jokes[2] = "Why was the picture sent to jail?\nIt was framed!";
        jokes[3] = "My girlfriend is the square root of -100; Absolutely perfect but purely imaginary";
        jokes[4] = "Did you know that 60 out of 50 people have trouble with fractions?";
        jokes[5] = "What do you call a half-twisted, one-sided nudie bar?\nA Strip club!";
        return jokes;
    }
    public void getIntro(){
        //Fancy logo
        //ASCII art sourced from https://www.asciiart.eu/food-and-drinks/drinks
        String martini = "()   ()      ()    /\n" +
                "  ()      ()  ()  /\n" +
                "   ______________/___\n" +
                "   \\            /   /\n" +
                "    \\^^^^^^^^^^/^^^/\n" +
                "     \\     ___/   /\n" +
                "      \\   (   )  /\n" +
                "       \\  (___) /\n" +
                "        \\ /    /\n" +
                "         \\    /\n" +
                "          \\  /\n" +
                "           \\/\n" +
                "           ||\n" +
                "           ||\n" +
                "     MARTINI ENGINE\n" +
                "         V 1.0\n" +
                "           ||\n" +
                "           ||\n" +
                "           /\\\n" +
                "          /;;\\\n" +
                "     --------------\n";

        char[] chars = martini.toCharArray();
        long t = 30;
        try {
            for(int i = 0; i < chars.length; i++){
                System.out.print(chars[i]);
                Thread.sleep(t);
            }

        }catch(InterruptedException e){
            e.printStackTrace();
        }

    }
}