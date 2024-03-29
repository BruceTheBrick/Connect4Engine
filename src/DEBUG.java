
/*public class DEBUG {

    public static void updateBoard(int[][] board, int index){
        for(int i = 6; i > 0; i--){
            if (board[index][i] != 0)
                board[index][i] = 100;
        }
    }


    public int addMove(String input) {

        //Converts last character of game log to integer value representing column number
        char c = input.charAt(input.length() - 1);

        int col = Character.getNumericValue(c);

        //This value is the element number of the lowest space in the given row
        int bottomRow = col + 35;

        //Sets currentSpace to the largest value of the current column
        int currentSpace = bottomRow;

        //Sets nextSpace to be the element directly above the lowest space of the column
        int nextSpace = currentSpace - 7;

        while(true){

            if(currentBoard[currentSpace] == EMPTY) {
                currentBoard[currentSpace] = currentPlayer;
                break;
            }

            else if(currentBoard[currentSpace] != EMPTY){
                if(nextSpace >= 0){
                    currentSpace = nextSpace;
                    nextSpace = nextSpace - 7;
                }
            }
        }

        return currentSpace;
    }
}

public void bestMove(){
        StringBuilder sb = new StringBuilder();
        if(isFirst){
            isFirst = false;
            int bestCol = 0;
            for(int i = 0; i < columnWeights.length; i++){
                if(columnWeights[i] > columnWeights[bestCol]){
                    bestCol = i;
                }
            }
            System.out.println("bestmove "  + bestCol + " " + columnWeights[bestCol]);
        }

        else{
            Random r = new Random();
            System.out.println("bestmove " + r.nextInt(7) + " 100");
        }
    }

    public int numAdjacent(int currentElement, int direction){

        //Checks to see if the next space in either direction is within the limits of the board
        boolean dValid1 = currentElement + direction >= 0 && currentElement + direction <= 41;
        boolean dValid2 = currentElement + direction*-1 >= 0 && currentElement + direction*-1 <= 41;

        int currentValue = currentBoard[currentElement];

        int currentCol = currentElement % 7;

        //Checking vertically
        if(direction == 7 || direction == -7) {

            //Cannot check above
            if ((currentElement - direction) < 0) {
                System.out.println("WOULD BE GOING OUT OF BOUNDS! NOT CHECKING ABOVE!");
                if(currentBoard[currentElement + (direction*-1)] == currentValue){
                    currentBoard[currentElement + direction*-1] = MINE;
                    return 1 + numAdjacent(currentElement-direction*-1, direction);
                }
            }

            //Cannot check below
            else if((currentElement + direction) > 41){
                System.out.println("WOULD BE GOING OUT OF BOUNDS! NOT CHECKING ABOVE!");
                if(currentBoard[currentElement + (direction*-1)] == currentValue){
                    currentBoard[currentElement + direction*-1] = MINE;
                    return 1 + numAdjacent(currentElement + direction*-1, direction);
                }
            }

            else{
                if(currentBoard[currentElement + (direction)] == currentValue) {
                    currentBoard[currentElement + direction] = MINE;
                    return 1 + numAdjacent(currentElement + direction, direction);
                }
                if(currentBoard[currentElement + (direction*-1)] == currentValue) {
                    currentBoard[currentElement + direction*-1] = MINE;
                    return 1 + numAdjacent(currentElement + direction * -1, direction);
                }

            }
        }

        //If checking any other direction
        else{

        }

        return 10;
    }

    public boolean checkHorizontal(int inputElement){

        int currentValue = currentBoard[inputElement];

        //Checks to see if the currentElement is closer to the left or right
        int closestEdge = (inputElement % 7 <= 3) ? 0 : 6;
        int boundaryElement = inputElement;

        if(closestEdge == 0){
            while(boundaryElement % 7 != closestEdge){
                boundaryElement--;
            }

            for(int i = boundaryElement; i < boundaryElement + 4; i++){
                if(currentBoard[i] != currentValue){
                    return false;
                }

            }
        }
        else{
            while(boundaryElement % 7 != closestEdge){
                boundaryElement++;
            }

            for(int i = boundaryElement; i > boundaryElement - 4; i--){
                if(currentBoard[i] != currentValue){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkWin(int inputElement){

        if(currentBoardNode.getState()[inputElement] == 0) return false;

        else if(checkVertical(inputElement))
            return true;
        else if (checkHorizontal(inputElement))
            return true;
        else if(checkDiagonals(inputElement))
            return true;
        else
            return false;
    }

    public boolean checkVertical(int inputElement) {
        int currentValue = currentBoard[inputElement];
        int spaceAbove = inputElement;
        int spacesToCheck = 3;

        //Finds the space above currentElement that is still the same token as currentElement
        while (spaceAbove - 7 >= 0 && currentBoard[spaceAbove - 7] == currentValue) {
            spaceAbove = spaceAbove - 7;
            spacesToCheck--;
        }

        //If there are no more spaces to check (i.e. currentElement was the bottom of a connect 4 vertical line)
        if (spacesToCheck == 0) {
            return true;
        }

        else{
            int spaceBelow = inputElement;
            for (int i = 0; i < spacesToCheck; i++) {

                //If there is a valid space below the current element
                if(spaceBelow + 7 <= 41)
                    spaceBelow = spaceBelow + 7;

                    //If there is no valid space below the current element
                else if(spaceBelow + 7 > 41) {
                    return false;
                }

                //If there is a valid space below the current element, but it does not
                //contain the same value as the current element
                if (currentBoard[spaceBelow] != currentValue) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkDiagonals(int inputElement){

        //Determines which column the inputElement is in
        int initialModClass = inputElement % 7;
        int currentValue = currentBoard[inputElement];

        int leftMostDiag = inputElement;
        int currentDiagModClass = leftMostDiag % 7;
        int spacesLeft = 3;

        //Checks diagonally up right
        while(spacesLeft > 0){

            //Ensures the element diagonally up and left of the inputElement is still a valid element on the board
            //Ensures the mod class of that element is only 1 less than the currentModClass
            if(leftMostDiag - 8 >= 0 && (leftMostDiag - 8) % 7 == currentDiagModClass-1 && currentBoard[leftMostDiag-8] == currentValue) {
                leftMostDiag = leftMostDiag - 8;
                currentDiagModClass = leftMostDiag % 7;
                spacesLeft--;
            }
            else{
                break;
            }
        }

        if(spacesLeft == 0){
            return true;
        }

        //Checks diagonally up left
        else{
            int rightMostDiag = inputElement;
            currentDiagModClass = rightMostDiag % 7;
            spacesLeft = 3;
            while(spacesLeft > 0){

                if(rightMostDiag - 6 >= 0 && (rightMostDiag - 6) % 7 == currentDiagModClass-1 && currentBoard[rightMostDiag-6] == currentValue){
                    rightMostDiag = rightMostDiag - 6;
                    currentDiagModClass = rightMostDiag % 7;
                    spacesLeft--;
                }
                else{
                    break;
                }
            }
        }

        if(spacesLeft == 0){
            return true;
        }

        //Checks Diagonally down right
        else{
            int rightMostDiag = inputElement;
            currentDiagModClass = rightMostDiag % 7;
            spacesLeft = 3;
            while(spacesLeft > 0){

                if(rightMostDiag + 8 <= 41 && (rightMostDiag + 8) % 7 == currentDiagModClass+1 && currentBoard[rightMostDiag+8] == currentValue){
                    rightMostDiag = rightMostDiag + 8;
                    currentDiagModClass = rightMostDiag % 7;
                    spacesLeft--;
                }
                else{
                    break;
                }
            }
        }

        if(spacesLeft == 0){
            return true;
        }

        //Checks diagonally down left
        else{
            leftMostDiag = inputElement;
            currentDiagModClass = leftMostDiag % 7;
            spacesLeft = 3;
            while(spacesLeft > 0){

                if(leftMostDiag + 6 <= 41 && (leftMostDiag + 6) % 7 == currentDiagModClass-1 && currentBoard[leftMostDiag+6] == currentValue){
                    leftMostDiag = leftMostDiag + 6;
                    currentDiagModClass = leftMostDiag % 7;
                    spacesLeft--;
                }
                else{
                    break;
                }
            }

            if(spacesLeft == 0){
                return true;
            }
            return false;
        }
    }

public void evaluation(Node root){
        WinPair result = checkWin(root.getState());

        //Makes this node extremely desirable
        if(result.hasWin() && result.getWinner() == MINE){
            root.setValue(Integer.MAX_VALUE);
        }

        //Makes this node extremely undesirable
        else if(result.hasWin() && result.getWinner() == OPPONENT){
            root.setValue(Integer.MIN_VALUE);
        }

        else{
            int sum = 0;
            for(int i = 0; i < root.getState().length; i++){

                if(root.getState()[i] == MINE) sum += boardValues[i];
                else if (root.getState()[i] == OPPONENT) sum -= boardValues[i];
            }
            root.setValue(sum);
        }
    }

     public int maxi(Node root, int depth){
        //if(depth == 0) return eval(root);
        int max = Integer.MIN_VALUE;
        int score = 0;
        for(int i = 0; i < root.getChildren().size(); i++){
            score = mini(root.getChildren().get(i),depth-1);
            if(score > max) max = score;
        }
        return max;
    }
    public int mini(Node root, int depth){
        //if(depth == 0) return eval(root);
        int min = Integer.MAX_VALUE;
        int score = 0;
        for(int i = 0; i < root.getChildren().size(); i++){
            score = maxi(root.getChildren().get(i), depth-1);
            if(score < min) min = score;
        }
        return min;
    }

    public void genGameTree(Node root, int depth){
        Queue<Node> q = new LinkedList<>();
        q.add(root);

        while(!q.isEmpty() && q.size() < 9){
            Node temp = q.poll();
            int currentPlayer;
            if(temp != null){
                currentPlayer = temp.getPlayer() == MINE ? OPPONENT : MINE;
                initChildren(temp, currentPlayer);
                q.addAll(temp.getChildren());
            }
        }
    }
    public int genGameTreeOld(Node root, int depth){
        if(depth == 0){
            //eval(root);
            return 1;
        }

        if(depth == 1) initChildren(root, MINE);
        else if(depth % 2 == 1) initChildren(root, MINE);
        else initChildren(root, OPPONENT);

        for(int i = 0; i < root.getChildren().size(); i++){
            genGameTree(root.getChildren().get(i), depth - 1);
        }
        return depth;
    }
    public void initGameTree(Node root, int depth){
        root.deleteChildren();
        root.setPlayer(OPPONENT);
        genGameTree(root, depth);
    }

    public void toggleCurrentPlayer(){
        this.currentPlayer = (this.currentPlayer == OPPONENT) ? MINE : OPPONENT;
    }

    public void isFirst(boolean isFirst){
        this.isFirst = isFirst;
        currentPlayer = this.isFirst ? MINE : OPPONENT;
    }

    public boolean isFull(int colNum){
        return currentBoardNode.getState()[colNum] != EMPTY;
    }

    public int getTreeDepth(){return currentBoardNode.getDepth(currentBoardNode);}

    public int negaMax(Node root, int depth, int player){
        if(depth == 0) return evaluation(root, player);

        LinkedList<Node> children = initChildren(root, player);
        if(children.size() == 0) return evaluation(root, player);

        int score;
        int max = Integer.MIN_VALUE;
        for(int i = 0; i < children.size(); i++){
            score = -negaMax(children.get(i), depth - 1, 3 - player);
            if(score > max) max = score;
        }
        return max;
    }
    public int evaluation(Node root, int maximisingPlayer) {
        WinPair result = checkWin(root.getState());

        int sum = 0;

        //Increase board values if maximisingPlayer can connect 2 or 3 in a row
        sum += (numOfTwos(root, maximisingPlayer) * 50);
        sum += (numOfThrees(root, maximisingPlayer) * 200);

        //Decrease board values if other player will connect 2 or 3 in a row
        //This acts as maximising player attempting to block the other player
        sum -= (numOfTwos(root, 3-maximisingPlayer) * 40);
        sum -= (numOfThrees(root, 3-maximisingPlayer) * 190);

        if(result.hasWin()){
            if(result.getWinner() == maximisingPlayer){
                sum += 1000000;
            }
            else{
                sum -= 1000000;
            }
        }

        for(int i = 0; i < root.getState().length; i++) {
            if (root.getState()[i] == maximisingPlayer) sum += boardValues[i];
            else if (root.getState()[i] != maximisingPlayer && root.getState()[i] != EMPTY) sum -= boardValues[i];
        }

        return sum * ((-2*(maximisingPlayer - 1)) + 1);
    }
*/