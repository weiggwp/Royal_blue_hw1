package Stratego.board;


import Stratego.logic.src.BoardPiece;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

//shuffles and gives positions of pieces
public class arrangement {
    private static ArrayList<BoardPiece> comp;
    private static ArrayList<BoardPiece> user;
    private static BoardPiece compFlag;
    private static BoardPiece userFlag;
    private static HashMap<Character, HashMap<Character,Integer>> remaining_pieces;

    //a hashmap that tracks remaining pieces from both sides


    public ArrayList getPieceList(char color)
    {
        if(color=='R')
            return comp;
        return user;
    }
    public void printLocationOfPieces(char color)
    {
        int count = 0;
        System.out.println();System.out.println();
        if(color=='R')
        {
            //print computer pieces
            System.out.println("Computer pieces");
            for (BoardPiece piece : comp)
            {

                System.out.print(piece.getUnit()+": ("+piece.getX()+","+piece.getY()+")\t");
                count++;
                if((count)%10==0)
                    System.out.println();
            }
        }
        else
        {
            System.out.println("User pieces");
            for(BoardPiece piece: user)
            {
                if((count+1)%10==0)
                    System.out.println();
                System.out.print(piece.getUnit()+": ("+piece.getX()+","+piece.getY()+")\t");
                count++;
            }
        }
        System.out.println();System.out.println();
    }
    //the capturing will take over the captured space
    //lost, win
    public void capturePiece(char color,BoardPiece captured,BoardPiece capturing)
    //blue, 6, B
    {
        System.out.println("color: "+color+" "+captured.getUnit()+capturing.getUnit());
        //update the hashmap of remaining pieces
        char opponent_color = (color=='R')?'B':'R';
        //int current_remaining = remaining_pieces.get(opponent_color).get(captured.getUnit());
        //remaining_pieces.get(opponent_color).replace(captured.getUnit(),current_remaining--);
        DecrementRemaining(opponent_color,captured);

        if(color=='R')  //if computer captured user piece
        {

            user.remove(captured);   //remove pieces from user set of pieces
            comp.remove(capturing); //the capturing piece clears out and copies info to captured
            comp.add(captured);

        }
        else
        {
            comp.remove(captured);  //remove captured piece from enemy pieces
            user.remove(capturing);
            user.add(captured);
        }
    }
    public void DecrementRemaining(char color,BoardPiece piece)
    {
        System.out.println("decrementing count for "+color+" "+piece.getUnit());
        int current = remaining_pieces.get(color).get(piece.getUnit());
        remaining_pieces.get(color).replace(piece.getUnit(),--current);
    }
    public void tiedMove(char color,BoardPiece you,BoardPiece opponent)
    {
        char opponent_color = (color=='R')?'B':'R';
        //int current_remaining = remaining_pieces.get(opponent_color).get(captured.getUnit());
        //remaining_pieces.get(opponent_color).replace(captured.getUnit(),current_remaining--);
        DecrementRemaining(color,you);
        DecrementRemaining(opponent_color,opponent);
        //decrement count for both yours and the opponent's piece
        if(color=='R')  //remove blue piece
        {
            comp.remove(you);
            user.remove(opponent);
        }
        else
        {
            user.remove(you);
            comp.remove(opponent);

        }
    }
    public void MovedPiece(char color,BoardPiece oldLocation,BoardPiece newLocation)//moving to an empty space
    {
        //do not affect hashmap remaining pieces count
        if(color=='R')//moved a computer piece
        {
            comp.remove(oldLocation);
            comp.add(newLocation);
        }
        else
        {
            user.remove(oldLocation);
            user.add(newLocation);
        }
    }
    public void printRemainingPieces()
    {
        remaining_pieces.entrySet().forEach(entry->{

            String result = (entry.getKey()=='R')?"Computer":"User";
            System.out.println(result); //red or blue
            entry.getValue().entrySet().forEach(num->
            {
                System.out.print(num.getKey()+": "+num.getValue()+"\t");
            });
            System.out.println();
        });
    }
    public arrangement()
    {
        remaining_pieces = new HashMap<>();
        remaining_pieces.put('R',new HashMap<>());
        remaining_pieces.put('B',new HashMap<>());
        comp = create_pieces(false,'R');
        user = create_pieces(true,'B');

        //rintRemainingPieces();


    }

    public static Boolean compare(int row,int col)
    {

        if((row==5 || row==6) && (col==3 || col==4 || col==7 || col==8))
        {
            return true;
        }
        return false;
    }
    public static Boolean getImage(int row)
    {
        if(!(row==5 || row==6))
        {
            return true;
        }
        else
        {

            return true;
        }


    }
    public static Boolean blue(int row)
    {
        return row<5;
    }
    public static Boolean red(int row)
    {
        return row>6;
    }

    public static Boolean blank(int row)
    {
        return row==5||row==6;

    }
    public static String getId(int row,int col)
    {
        return row+"_"+col;
    }

    public static BoardPiece getPiece(int row,int col)
    {
        if(row<5)//blue
        {
            int pos = (row)*(10) + col;

            return comp.get(pos);
        }
        else
        {
            int ro = row-6;
            int pos = ro*10+col;
            return user.get(pos);
        }
    }
    public static String assign(int row,int col)
    {

        if(row<5)//blue
        {
            int pos = (row-1)*(10) + (col-1);

            return (comp.get(pos)).toString();
        }
        else
        {
            int ro = row-7;
            int pos = ro*10+col-1;
            return (user.get(pos)).toString();
        }

    }
    //Adds pieces with count of 1 to the remaining pieces hashmap
    public void AddPieceToRemaining(char color,char unit)
    {
        remaining_pieces.get(color).put(unit,1);
    }
    /*
     There are twice unique pieces in total for each user
     this method creates them in order with respect to their image src
     do the same as create_pieces except now we populate the arraylist with objects containing value+image_src
     */
    private ArrayList<BoardPiece> create_pieces(boolean user, char color)
    {
        ArrayList<BoardPiece> collect = new ArrayList<BoardPiece>();

        String src="../images/pieces/piece"; // directory
        String start="";
        String ext=".png";

        if(user)   //blue pieces start at offset 21
            start="2";
        //flag goes last
        BoardPiece flag = new BoardPiece('F',src+start+"1"+ext,color);
        collect.add(new BoardPiece('1',src+start+'2'+ext,color));     //spy
        collect.add(new BoardPiece('M',src+start+'3'+ext,color));       //10
        collect.add(new BoardPiece('9',src+start+'4'+ext,color));       //9

        char[] values={'B','2','8','7','3','4','5','6'};
        int[] counts ={6,8,2,3,5,4,4,4};
        if(!user)
            start = "1"; //now beginning to add pieces that repeat more than once
        else
            start = "7";


        for(int i = 1;i<=8;i++)
        {
            int piece_index = i+2;
            int count = counts[i-1];
            char value = values[i-1];

            String img_src= src + start + i + ext;//finish path

            for(int k=0;k<count;k++) {
                collect.add(new BoardPiece(value,img_src,color));  //create new unique objects with same values

            }
            // System.out.println("after adding "+s+" "+collect);
        }
        Collections.shuffle(collect);
        //blue flag goes first row, red flag goes last row

        if(!user)
            collect.add((new Random()).nextInt(10),flag);
        else
            collect.add((new Random()).nextInt(10)+30,flag);


        return collect;
    }
    //TODO: Instead of storing strings encapsulate it in piece class
    //
    //randomly populates the 40 spots, works for both red and blue team
    private static ArrayList populate(int row)
    {
        ArrayList<String> collect = new ArrayList<String>();
        int[] counts ={6,8,2,3,5,4,4,4};
        String src="../images/pieces/piece"; // directory
        String ext=".png";
        String sr; // relative directory

        // red starts at row 7
        if(row==7)
        {
            sr = src+2;
        }
        else
        {
            sr = src;
        }
        // add single cards first
        String flag = sr+1+ext;
        String spy = sr+2+ext;
        String ten = sr+3+ext;
        String nine=sr+4+ext;
        collect.add(spy);collect.add(ten);collect.add(nine);

        src += row;
        //System.out.println("pre:"+collect);
        for(int i = 1;i<=8;i++)
        {
            int count = counts[i-1];

            String s= src + i +ext;//finish path

            for(int k=0;k<count;k++) {
                collect.add(s);

            }
            // System.out.println("after adding "+s+" "+collect);
        }
        Collections.shuffle(collect);
        //blue flag goes first row, red flag goes last row
        if(row==1)
        {
            collect.add((new Random()).nextInt(10), flag);
        }
        else
            collect.add((new Random()).nextInt(10)+30,flag);

        return collect;

    }
    private static String clone(String s)
    {
        return new String(s);
    }
}