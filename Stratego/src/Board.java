import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Board {
    public void startGame(){
        try {
            initializeGameboard();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Scanner sc = new Scanner(System.in);
        while (gameWinner==0){
            displayGameBoard();
            //starting row, starting col, ending row, ending col
            move(sc.nextInt(),sc.nextInt(),sc.nextInt(),sc.nextInt());

        }
        if (gameWinner==1)
            System.out.println("You win!");
        else
            System.out.println("You lose.");
    }

    private void displayGameBoard(){
        for (int i=0; i<10; i++){
            for (int j=0; j<10; j++){
                if (gameboard[i][j].getColor()=='R') {
                    System.out.print((char)27 + "[31m" );
                    System.out.print(gameboard[i][j].getUnit() + " ");
                }
                else if (gameboard[i][j].getColor()=='B') {
                    System.out.print((char)27 + "[34m" );
                    System.out.print(gameboard[i][j].getUnit() + " ");

                }
                else{
                    System.out.print((char)27 + "[37m" );
                    System.out.print(gameboard[i][j].getUnit() + " ");
                }

            }
            System.out.println();
        }
        System.out.print((char)27 + "[37m" );
    }

    BoardPiece[][] gameboard= new BoardPiece[10][10];
    int gameWinner=0; //0 means no winner yet
    private void initializeGameboard() throws FileNotFoundException {
        Scanner sc = new Scanner(new File("./Stratego/resources/board2.txt"));
        //System.out.println(sc.nextLine());
        for (int i=0; i<4; i++){
            for (int j=0; j<10; j++){
               // System.out.println(i+"  "+j);
                gameboard[i][j]=new BoardPiece(sc.next().charAt(0),'R');
            }
        }
        for (int i=4; i<6; i++){
            for (int j=0; j<10; j++){
                if (j==2||j==3||j==6||j==7){
                    gameboard[i][j]=new BoardPiece('W','0');
                }
                else
                    gameboard[i][j]=new BoardPiece('0','0');
            }
        }
         sc = new Scanner(new File("./Stratego/resources/board1.txt"));
        for (int i=6; i<10; i++){
            for (int j=0; j<10; j++){
                gameboard[i][j]=new BoardPiece(sc.next().charAt(0),'B');
            }
        }
    }

    /*Returns false on illegal move, true on legal move.*/
    private boolean isLegalMove(int startingX, int startingY, int endingX, int endingY){
        System.out.println("trying to move " +gameboard[startingX][startingY].getUnit()+" to " +gameboard[endingX][endingY].getUnit());
        if (gameboard[startingX][startingY].getUnit()=='B'||gameboard[startingX][startingY].getUnit()=='F'||
                gameboard[startingX][startingY].getUnit()=='0'||gameboard[startingX][startingY].getUnit()=='X') {
            System.out.println("invalid starting piece");
            return false;
        }
        if (gameboard[startingX][startingY].getColor()!='B') {
            System.out.println("invalid starting color");
            return false;
        }//if moving a piece not owned by player...
        if (gameboard[endingX][endingY].isLake()){
            System.out.println("cant move to lake");
            return false;
        }
        //if moving into lake...
        if (gameboard[endingX][endingY].getColor()==gameboard[startingX][startingY].getColor()) {
            System.out.println("cant capture friendly unit");
            return false;
        }/* if moving
        onto a space occupied by another piece owned by the player...*/
        if ((Math.abs(startingX-endingX)>=1&&Math.abs(startingY-endingY)>=1)) {
            System.out.println("cant move diagonally");
            return false;
        }
        if (!gameboard[startingX][startingY].isScout()&&( (Math.abs(startingX-endingX)>=2||Math.abs(startingY-endingY)>=2)
                )){ //if it moves too far...
            System.out.println("too far, not a scout");
            return false;
        }
        if (gameboard[startingX][startingY].isScout() ){ //if a scout moves through a unit or lake...
            int starting=0;
            int ending =0;
            boolean horizontal=false;
            if (endingX==startingX){
                starting=Math.min(startingY,endingY);
                ending=Math.max(startingY,endingY);
                horizontal=true;
            }
            else{
                starting=Math.min(startingX,endingX);
                ending=Math.max(startingX,endingX);

            }
            for (int i=starting+1; i<ending; i++){
                if (horizontal)
                    if (!gameboard[startingY][i].isEmpty()||gameboard[startingY][i].isLake()) {
                        System.out.println("collision at "+startingY+" "+i+ "; " +gameboard[startingY][i].getUnit());
                        return false;
                    }
                else
                    if (!gameboard[i][startingX].isEmpty()||gameboard[i][startingX].isLake()) {
                        System.out.println("collision at "+i+" "+startingX+ "; " +gameboard[i][startingX].getUnit());
                        return false;
                    }
            }
        }



        return true;
    }
    private boolean isDigit(char s){
        if (s>='0'&&s<='9')
            return true;
        return false;
    }

    /*Returns 0 on 1 wins, 1 on 2 wins, 2 on draw*/
    private int attack(char unit1, char unit2){
        if (unit1=='3'&&unit2=='B') return 0;
        if (unit1=='1'&&unit2=='M') return 0;
        if (unit2=='F') {

            return 3;
        }
        if (unit2=='B') return 1;
        if (unit1=='M'&&unit2=='M') return 2;
        if (unit1=='M') return 0;
        if (unit1==unit2) return 2;
        if (unit1>unit2) return 0;
        return 1;
    }

    /*Dummy method for UI handling of illegal move*/
    private void illegalMove(){
        System.out.println("Illegal move.");
    }

    /*Attempt to move unit to a tile*/
    private void move(int startingX, int startingY, int endingX, int endingY){
        if (!isLegalMove(startingX,startingY,endingX,endingY)) {
            illegalMove();
            return;
        }
        int result=attack(gameboard[startingX][startingY].getUnit(),gameboard[endingX][endingY].getUnit());
        if (result==0){
            gameboard[endingX][endingY].newPiece(gameboard[startingX][startingY]);
            gameboard[startingX][startingY].reset();
        }
        else if (result==1){
            gameboard[startingX][startingY].reset();
        }
        else if (result==2){
            gameboard[startingX][startingY].reset();
            gameboard[endingX][endingY].reset();
        }
        else if (result==3){
            gameWinner=1;
        }


    }


}
