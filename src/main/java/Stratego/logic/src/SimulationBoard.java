package Stratego.logic.src;

import Stratego.board.Move;

import java.util.ArrayList;

public class SimulationBoard{

    public static final int[] BOARD_SIZE = new int[]{10,10};

    private BoardPiece[][] gameboard= new BoardPiece[ BOARD_SIZE[0] ][ BOARD_SIZE[1] ];
    private boolean gameEnded = false;
    private char winner = '0';
    private SimulationMove move_ptr = null;

    public SimulationBoard(Board board) {
        for(int i=0;i<BOARD_SIZE[0];i++){
            for(int j=0;j<BOARD_SIZE[1];j++){
                this.gameboard[i][j] = board.getPiece(i,j).clone();

            }
        }
        gameEnded = false;
        winner = '0';
    }
    public SimulationBoard(Board board, ArrayList<Move> moves) {
        for(int i=0;i<BOARD_SIZE[0];i++){
            for(int j=0;j<BOARD_SIZE[1];j++){
                this.gameboard[i][j] = board.getPiece(i,j).clone();

            }
        }
        int size = moves.size();
        if (size>=6){
            //return last 3 moves made by this player
            for(int i=size-6;i<size;i+=2){
                Move move = moves.get(i);
                SimulationMove simulationMove = new SimulationMove(move);
                simulationMove.setPrev(move_ptr);
                move_ptr = simulationMove;
            }
        }
        gameEnded = false;
        winner = '0';
    }
    public BoardPiece[][] getGameboard() {
        return gameboard;
    }
    //public boolean madeLoopMoves(){
        //prev_move = prev_move.getPrev();
    //}




  /*  public void displayGameBoard(){
        for (int i=0; i<10; i++){
            for (int j=0; j<10; j++){
                if (gameboard[i][j].getColor()=='B') {
                    System.out.print((char)27 + "[34m" );
                    System.out.print(gameboard[i][j].getUnit() + " ");
                }
                else if (gameboard[i][j].getColor()=='R') {
                    System.out.print((char)27 + "[31m" );
                    System.out.print(gameboard[i][j].getUnit() + " ");

                }
                else{
                    System.out.print((char)27 + "[37m" );
                    System.out.print(gameboard[i][j].getUnit() + " ");
                }

            }
            System.out.println();
        }
        System.out.println((char)27 + "[37m" );

    }
*/
    /*Returns false on illegal move, true on legal move.*/
    public boolean makingLoops(SimulationMove move)
    {
        //[1,1]->[1,2]      initial
        // [1,2]->[1,1]     second
        // [1,1]-> [1,2] (current attempted move)
        int count = 0;
        SimulationMove ptr = move_ptr;
        while(ptr!=null) //if has previous move
        {
          boolean moved_back = move.movedBack(ptr);
          if (!moved_back) return false;

          // moved back
          move = ptr;
          count++;
          if( count>=3) return true;  //changed from 2-3
          ptr = ptr.getPrev();
        }
        return false;
    }

    public boolean isLegalMove(SimulationMove simulationMove){
        int     startingX = simulationMove.getStart_x(),
                startingY = simulationMove.getStart_y(),
                endingX   = simulationMove.getEnd_x(),
                endingY   = simulationMove.getEnd_y();
        char color = simulationMove.getColor();

        if (gameboard[startingX][startingY].getUnit()==color||gameboard[startingX][startingY].getUnit()=='F'||
                gameboard[startingX][startingY].getUnit()=='0'||gameboard[startingX][startingY].getUnit()=='X') {
            return false;
        }

        if (gameboard[endingX][endingY].isLake()){
            return false;
        }
        //if moving into lake...
        if (gameboard[endingX][endingY].getColor()==gameboard[startingX][startingY].getColor()) {
            return false;
        }/* if moving onto a space occupied by another piece owned by the player...*/
        if ((Math.abs(startingX-endingX)>=1&& Math.abs(startingY-endingY)>=1)) {
            return false;
        }
        if (!gameboard[startingX][startingY].isScout()&&( (Math.abs(startingX-endingX)>=2|| Math.abs(startingY-endingY)>=2)
        )){ //if it moves too far...
            return false;
        }
        if (gameboard[startingX][startingY].isScout() ){ //if a scout moves through a unit or lake...
            int starting=0;
            int ending =0;
            boolean horizontal=false;
            if (endingX==startingX){
                starting= Math.min(startingY,endingY);
                ending= Math.max(startingY,endingY);
                horizontal=true;
            }
            else{
                starting= Math.min(startingX,endingX);
                ending= Math.max(startingX,endingX);

            }
            for (int i=starting+1; i<ending; i++){

                if (horizontal) {
                    if (!gameboard[startingX][i].isEmpty() || gameboard[startingX][i].isLake()) {
                        return false;
                    }
                }
                else {
                    if (!gameboard[i][startingY].isEmpty() || gameboard[i][startingY].isLake()) {
                        return false;
                    }
                }
            }
        }
        if(makingLoops(simulationMove))
            return false;


        return true;
    }

        /*Returns 0 on 1 wins, 1 on 2 wins, 2 on draw, 3 on flag capture, 4 on moving to blank space*/
        public static int attack(char unit1, char unit2) {
            if (unit2 == '0') return 4;
            if (unit1 == '3' && unit2 == 'B') return 0;
            if (unit1 == '1' && unit2 == 'M') return 0;
            if (unit2 == 'F') {

                return 3;
            }
            if (unit2 == 'B') return 1;
            if (unit1 == 'M' && unit2 == 'M') return 2;
            if (unit1 == 'M') return 0;
            if (unit1 == unit2) return 2;
            if (unit1 > unit2) return 0;
            return 1;
        }


        public void undo_move() {
            gameboard[move_ptr.getStart_x()][move_ptr.getStart_y()] = move_ptr.getStarting_piece();
            gameboard[move_ptr.getEnd_x()][move_ptr.getEnd_y()] = move_ptr.getDestination_piece();

            // cant undo to an ended game
            gameEnded = false;
            winner = '0';
            move_ptr = move_ptr.getPrev();
        }

    /*Attempt to move unit to a tile*/
    public void move(SimulationMove simulationMove) {
        int     startingX = simulationMove.getStart_x(),
                startingY = simulationMove.getStart_y(),
                endingX   = simulationMove.getEnd_x(),
                endingY   = simulationMove.getEnd_y();
        char color = simulationMove.getColor();

        SimulationMove sm = new SimulationMove(startingX, startingY, endingX, endingY,
                gameboard[startingX][startingY].clone(), gameboard[endingX][endingY].clone(), color);
        sm.setPrev(move_ptr);
        move_ptr = sm;

        int result = attack(gameboard[startingX][startingY].getUnit(), gameboard[endingX][endingY].getUnit());
        if (result == 0) { //win
            char a = gameboard[endingX][endingY].getUnit();
            gameboard[endingX][endingY].newPiece(gameboard[startingX][startingY]);
            gameboard[endingX][endingY].setRevealed(true);
            gameboard[startingX][startingY].reset();

        } else if (result == 1) {// lose
            char a = gameboard[startingX][startingY].getUnit();
            gameboard[endingX][endingY].setRevealed(true);
            gameboard[startingX][startingY].reset();
        } else if (result == 2) {//tie
            char a = gameboard[endingX][endingY].getUnit();
            gameboard[startingX][startingY].reset();
            gameboard[endingX][endingY].reset();
        } else if (result == 3) {//win game
            char a = gameboard[endingX][endingY].getUnit();
            gameboard[endingX][endingY].newPiece(gameboard[startingX][startingY]);
            gameboard[startingX][startingY].reset();
            gameboard[endingX][endingY].setRevealed(true);
            gameEnded = true;
            winner = color;
        } else if (result == 4) {//mover moved to empty space
            gameboard[endingX][endingY].newPiece(gameboard[startingX][startingY]);
            gameboard[startingX][startingY].reset();
        }

    }


        public static int[] getBoardSize() {
            return BOARD_SIZE;
        }

        public boolean isGameEnded() {
            return gameEnded;
        }

        public void setGameEnded(boolean gameEnded) {
            this.gameEnded = gameEnded;
        }

        public char getWinner() {
            return winner;
        }

        public void setWinner(char winner) {
            this.winner = winner;
        }
        public BoardPiece getPiece(int x, int y){
            return gameboard[x][y];
        }


}
