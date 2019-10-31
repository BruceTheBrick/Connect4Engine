/*  Author: Bryce Lynch
    Student Number: 3260061
    Date of Creation: 27/9/19
 */

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Interface {

    public static void main(String args[]) {

        MartiniEngine martini = new MartiniEngine();
        PrecomputedIndexes.init();

        //Setup for reading coordinator input
        Scanner coordinator = new Scanner(System.in);
        String[] input;
        boolean finished = false;

        martini.debug();

        while(!finished){

            input = coordinator.nextLine().split(" ");
            switch(input[0].toLowerCase()){

                case "name":
                    //Determines if Martini is first or not
                    if(Integer.parseInt(input[2]) == 0) martini.setFirstPlayer(1);
                    else martini.setFirstPlayer(2);

                    System.out.println(martini.getName());
                    break;

                case "isready":
                    System.out.println(martini.ready());
                    break;

                case "position":
                    if(input.length == 3)
                        martini.updateBoard(input[2], 2);
                    break;

                case "go":

                    //Determines which time (ftime or stime) is for Martini

                    if(martini.isFirst())
                        martini.findBestMove(Integer.parseInt(input[2]));
                    else
                        martini.findBestMove(Integer.parseInt(input[4]));


                    break;

                case "perft":
                    Node temp = new Node();
                    temp.setState(martini.getCurrentBoard());
                    System.out.println("perft " + input[1] + " " + martini.perft(Integer.parseInt(input[1]), temp));
                    break;

                case "quit":
                    System.out.println(martini.quit());
                    finished = true;
                    break;

                case "easter":
                    martini.getIntro();
                    break;

                case "print":
                    martini.printBoard();
                    break;

                case "joke":
                    System.out.println(martini.initJokes()[ThreadLocalRandom.current().nextInt(0, 6)]);
                    break;
            }
        }
    }
}
