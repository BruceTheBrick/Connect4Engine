import java.util.LinkedList;

public class Node {

    private LinkedList<Node> children;
    private int[] state;
    private int colNum;
    private int player;
    private int lastPiece;


    //Constructors
    public Node(){
        children = new LinkedList<>();
        state = new int[42];
        player = 0;
        lastPiece = 0;
    }
    public Node(Node parent){
        children = new LinkedList<>();
        state = parent.getState().clone();
        player = 0;
        lastPiece = 0;
    }

    //Getters & Setters
    public void setColNum(int colNum){this.colNum = colNum;}
    public int getColNum(){return colNum;}
    public void setPlayer(int player){this.player = player;}
    public int getPlayer(){return player;}
    public LinkedList<Node> getChildren(){return children;}
    public int[] getState(){return state;}
    public void setState(int[] state){this.state = state;}
    public void setLastPiece(int lastPiece){this.lastPiece = lastPiece;}
    public int getLastPiece(){return lastPiece;}
}