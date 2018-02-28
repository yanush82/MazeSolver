import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
21-02-2018 - Janusz Ryn - Maze task
*/

public class Maze {

    private char[][] maze = null;
    private BufferedReader br = null;
    private File file;
    private static int startX = 0, startY = 0 ;
    private static int endX = 0, endY = 0;
    private int height = 0;
    private int width = 0;
    final static char C=' ', X='#', S='S', E='E', V='x';

    public static void main(String[] args) {
        Maze mz = new Maze();
        String fileName = "input.txt";
        mz.buildMaze(fileName);
        System.out.println(" ");
        System.out.println("Generated maze: ");
        mz.print();

        System.out.println("\n\nPath in the maze: ");
        mz.solveMaze();
    }

    // Getting a Maze parameters from input file
    private void buildMaze(String inputFile) {
        char value;
        try {

            //Reading an input file
            file = new File(inputFile);
            br = new BufferedReader(new FileReader(inputFile));

            //Getting width and height of maze
            String dim = br.readLine();
            width = Integer.parseInt(dim.substring(0, dim.indexOf(' ')));
            height = Integer.parseInt((dim.substring(dim.indexOf(' ')+1)));
            maze = new char[height][width];

            //Getting start point
            String start = br.readLine();
            value = start.charAt(0);
            startY = Character.getNumericValue(value);
            value = start.charAt(2);
            startX = Character.getNumericValue(value);

            //Getting finish point
            String finish = br.readLine();
            endY = Integer.parseInt(finish.substring(0, finish.indexOf(' ')));
            endX = Integer.parseInt((finish.substring(finish.indexOf(' ') +1 )));

            //Populating maze array
            int heightCounter = -1;
            String line;
            while((line = br.readLine()) != null) {
                int lineNum = 0;
                heightCounter++;
                for (int i = 0; i < line.length(); i++){
                    if (line.charAt(i) != ' '){
                        maze[heightCounter][lineNum] = line.charAt(i);
                        lineNum++;
                    }
                }
            }
            //Replacing values to text chars
            for (int i=0; i<sizeI(); i++) {
                for (int j=0; j<sizeJ(); j++) {
                    if(maze[i][j]=='0')
                        maze[i][j]=C;
                    if(maze[i][j]=='1')
                        maze[i][j]=X;
                    if(i==startX && j==startY)
                        maze[i][j]=S;
                    if(i==endX && j==endY)
                        maze[i][j]=E;
                }
            }
        }
        //Exceptions
        catch(FileNotFoundException fnfe) {
            System.out.println("The file : " + file.getName() + " does not exist" );
            fnfe.printStackTrace();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        catch(ArrayIndexOutOfBoundsException aioob){
            aioob.printStackTrace();
        }
    }

    public int sizeJ() { return width; }
    public int sizeI() { return height; }

    public void print() {
        for (int i=0; i<sizeI(); i++) {
            for (int j=0; j<sizeJ(); j++) {
                System.out.print(maze[i][j]);
                System.out.print(' ');
            }
            System.out.println();
        }
    }

    public char mark(int i, int j, char value) {
        assert(isInMaze(i,j));
        char tmp = maze[i][j];
        maze[i][j] = value;
        return tmp;
    }

    public char mark (moveDirection move, char value) {
        return mark(move.i(), move.j(), value);
    }

    public boolean isMarked(int i, int j) {
        assert(isInMaze(i,j));
        return (maze[i][j] == V);
    }

    public boolean isMarked(moveDirection move) {
        return isMarked(move.i(), move.j());
    }

    public boolean isClear(int i, int j) {
        assert(isInMaze(i,j));
        return (maze[i][j] != X && maze[i][j] != V);
    }

    public boolean isClear(moveDirection move) {
        return isClear(move.i(), move.j());
    }

    //true if cell is within maze
    public boolean isInMaze(int i, int j) {
        if (i >= 0 && i<sizeI() && j>= 0 && j<sizeJ()) return true;
        else return false;
    }

    //true if cell is within maze
    public boolean isInMaze(moveDirection move) {
        return isInMaze(move.i(), move.j());
    }
    public boolean isFinal( int i, int j) {
        return (i== Maze.endX && j == Maze.endY);
    }
    public boolean isFinal(moveDirection move) {
        return isFinal(move.i(), move.j());
    }

    public char[][] clone() {
        char[][] mazeCopy = new char[sizeI()][sizeJ()];
        for (int i=0; i< maze.length; i++)
            for (int j=0; j<maze[i].length; j++)
                mazeCopy[i][j] = maze[i][j];
        return mazeCopy;
    }

    public void solveMaze() {
        if (solve(new moveDirection(startX, startY))){
            if (maze[startX][startY] == V){
                maze[startX][startY] = S;}
            System.out.println("From S to E marked with x's");
        }
        else
        {
            if (maze[startX][startY] == C){
                maze[startX][startY] = S;}
            System.out.println("Not possible to reach point E");
        }
        print();
    }

    public boolean solve(moveDirection move) {

        //base case
        if (!isInMaze(move)) return false;
        if (isFinal(move)) return true;
        if (!isClear(move)) return false;

        //current position must be clear
        assert(isClear(move));

        //first mark this  location
        mark(move, V);

        //try to go south
        if (solve(move.south())) { return true; }

        //else west
        if (solve(move.west())) { return true; }

        //else north
        if (solve(move.north())) { return true; }

        //else east
        if (solve(move.east())) { return true; }

        //have been clear
        mark(move, C);

        //if none of the above returned, then there is no solution
        return false;
    }
}

class moveDirection {
    int i, j;

    public moveDirection(int i, int j) {
        this.i = i;
        this.j = j;
    };
    public int i() { return i;}

    public int j() { return j;}

    public void print() {
        System.out.println("(" + i + "," + j + ")");
    }
    public moveDirection north() {
        return new moveDirection(i-1, j);
    }
    public moveDirection south() {
        return new moveDirection(i+1, j);
    }
    public moveDirection east() {
        return new moveDirection(i, j+1);
    }
    public moveDirection west() {
        return new moveDirection(i, j-1);
    }
};
