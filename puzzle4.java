//Sree Harrsha Singara
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.PriorityQueue;


public class puzzle4 {

    // scoring constants
    private static final int PACMAN_INIT_SCORE = 50;
    private static final int POINT_DROP_PMV = 1;
    private static final int POINT_DROP_MAGIC_BULLET = 20;
    private static final int POINT_INC_PER_MONSTR_KILL = 5;

    // dungeon elements constantss
    private static final char WALL = '#';
    private static final char EMPTY = ' ';
    private static final char ACT_MAN = 'A';
    private static final char OGRE = 'G';
    private static final char DEMON = 'D';
    private static final char CORPSE = '@';
    private static int score;


    // Initialize Act-Man's position
    public static int pacManRow = -1;
    public static int pacManCol = -1;

    // Create a priority queue
    public static PriorityQueue<QueueItem> prioriQueue = new PriorityQueue<>((item1, item2) -> Integer.compare(item1.prioriValue, item2.prioriValue));;

    

    static StringBuilder actions = new StringBuilder();

    public static int rows = 0;
    public static int cols = 0;

    //creating a class that contains items that should be in a prority queue
    public static class QueueItem {
        int pacManRow;
        int pacManCol;
        char[][] dungeon;
        ArrayList<ArrayList<Integer>> ogreLoc;
        ArrayList<ArrayList<Integer>> demonLoc;
        StringBuilder output;
        boolean fired;
        boolean[] visited;
        int score;
        int prioriValue;

        public QueueItem(int pacManRow, int pacManCol, char[][] dungeon, ArrayList<ArrayList<Integer>> ogreLoc,
                         ArrayList<ArrayList<Integer>> demonLoc, StringBuilder output, boolean fired, boolean[] visited, int score, int prioriValue) {
            this.pacManRow = pacManRow;
            this.pacManCol = pacManCol;
            this.dungeon = dungeon;
            this.ogreLoc = ogreLoc;
            this.demonLoc = demonLoc;
            this.output = output;
            this.fired = fired;
            this.visited = visited;
            this.score = score;
            this.prioriValue = prioriValue;
        }
    }



    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java YourClassName inputFile outputFile");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            // Read input file
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String[] dimensions = reader.readLine().split(" ");
            rows = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);

            char[][] dungeon = new char[rows][cols];



            // Initialize monster count

            int count = 0;

            // Create a 2D ArrayList to hold ogre positions
            ArrayList<ArrayList<Integer>> ogreLoc = new ArrayList<>();

            // Create a 2D ArrayList to hold demon positions
            ArrayList<ArrayList<Integer>> demonLoc = new ArrayList<>();

            for (int i = 0; i < rows; i++) {
                String line = reader.readLine();
                for (int j = 0; j < cols; j++) {
                    dungeon[i][j] = line.charAt(j);
                    if (dungeon[i][j] == ACT_MAN) {
                        pacManRow = i;
                        pacManCol = j;
                    } else if (dungeon[i][j] == OGRE){
                        // Add ogre positions
                        addOgrePosition(ogreLoc, i, j);
                    } else if (dungeon[i][j] == DEMON){
                        // Add demon positions
                        addDemonPosition(demonLoc, i, j);
                    }
                }
            }

            reader.close();

            // Initialize Act-Man's score
            score = PACMAN_INIT_SCORE;
            boolean fired = false; //fire flag
            int prioriValue = 0;
            StringBuilder output = new StringBuilder();
            Object[] result = a_Starr(pacManRow, pacManCol, dungeon, ogreLoc, demonLoc, output, fired, prioriValue);

            gameTermination((StringBuilder)result[0], (int)result[1], (char[][])result[2], output, outputFile, pacManRow, pacManCol);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Helper method to perform BFS to find the shortest path to the nearest monster
    
    public static boolean isValidMove(char direction, int pacManRow, int pacManCol, char[][] dungeon, boolean[] visited, boolean fired) {
        int index;
        switch (direction) {
            case '1': index = 0; break;
            case '2': index = 1; break;
            case '3': index = 2; break;
            case '4': index = 3; break;
            case '6': index = 4; break;
            case '7': index = 5; break;
            case '8': index = 6; break;
            case '9': index = 7; break;
            case 'N': index = 8; break;
            case 'E': index = 9; break;
            case 'S': index = 10; break;
            case 'W': index = 11; break;
            default: return false; // Invalid direction
        }

        // Check if the step is valid and not visited
        if (visited[index] || !visited[index]) {
            switch (direction) {
                case '1': return (dungeon[pacManRow + 1][pacManCol - 1] == ' ' && checkNeighbour(pacManRow+1,pacManCol-1,dungeon,direction));
                case '2': return (dungeon[pacManRow + 1][pacManCol] == ' ' && checkNeighbour(pacManRow+1,pacManCol,dungeon,direction));
                case '3': return (dungeon[pacManRow + 1][pacManCol + 1] == ' ' && checkNeighbour(pacManRow+1,pacManCol+1,dungeon,direction));
                case '4': return (dungeon[pacManRow][pacManCol - 1] == ' ' && checkNeighbour(pacManRow,pacManCol-1,dungeon,direction));
                case '6': return (dungeon[pacManRow][pacManCol + 1] == ' ' && checkNeighbour(pacManRow,pacManCol+1,dungeon,direction));
                case '7': return (dungeon[pacManRow - 1][pacManCol - 1] == ' ' && checkNeighbour(pacManRow-1,pacManCol-1,dungeon,direction));
                case '8': return (dungeon[pacManRow - 1][pacManCol] == ' ' && checkNeighbour(pacManRow-1,pacManCol,dungeon,direction));
                case '9': return (dungeon[pacManRow - 1][pacManCol + 1] == ' ' && checkNeighbour(pacManRow-1,pacManCol+1,dungeon,direction));
                case 'N': return (!fired && checkNeighbour(pacManRow,pacManCol,dungeon,direction));
                case 'E': return (!fired && checkNeighbour(pacManRow,pacManCol,dungeon,direction));
                case 'S': return (!fired && checkNeighbour(pacManRow,pacManCol,dungeon,direction));
                case 'W': return (!fired && checkNeighbour(pacManRow,pacManCol,dungeon,direction));
                default: return false; // Should never reach here
            }
        }

        return false;
    }

    private static boolean checkNeighbour(int i, int j, char[][] dungeon, char direction) {
        // TODO Auto-generated method stub
        for(int c=-1;c<=1;c++){
            for(int d=-1;d<=1;d++){
                if(direction == 'N' && (dungeon[i-1][j]==OGRE || dungeon[i-1][j]==DEMON)){continue;}
                else if(direction == 'E' && (dungeon[i][j+1]==OGRE || dungeon[i][j+1]==DEMON)){continue;}
                else if(direction == 'S' && (dungeon[i+1][j]==OGRE || dungeon[i+1][j]==DEMON)){continue;}
                else if(direction == 'W' && (dungeon[i][j-1]==OGRE || dungeon[i][j-1]==DEMON)){continue;}
                else if((dungeon[i+c][j+d]==OGRE || dungeon[i+c][j+d]==DEMON)){
                    return false;
                }
            }
        }
        return true;
    }


    private static Object[] a_Starr(int pacManRow, int pacManCol, char[][] dungeon, ArrayList<ArrayList<Integer>> ogreLoc, ArrayList<ArrayList<Integer>> demonLoc, StringBuilder output, boolean fired, int prioriValue) {

        char[] directions = new char[]{'1','2','3','4','6','7','8','9','N','E','S','W'};
        boolean[] visited = new boolean[12]; //track visisted directions
        score = 50; // astar's score set or reset


        // Add parameters to a queue item
        QueueItem item = new QueueItem(pacManRow, pacManCol, dungeon, ogreLoc, demonLoc, output, fired, visited, score, prioriValue);
        // Add the queue item to the queue
        prioriQueue.add(item);


        while(!prioriQueue.isEmpty()){
            QueueItem currentItem = prioriQueue.poll();  // Getting current item

            for(int i = 0; i < 12; i++){
                if(isValidMove(directions[i], currentItem.pacManRow, currentItem.pacManCol, currentItem.dungeon, currentItem.visited, currentItem.fired)){
                    StringBuilder updatedOutput = new StringBuilder(currentItem.output); // Create a new StringBuilder to avoid mutating the original
                    updatedOutput.append(directions[i]);

                    //if(updatedOutput.toString().equals("21464")){int qsx = 2;}

                    char[][] updateddung = dungCopy(currentItem.dungeon); // Create a copy of dungeon to avoid mutation

                    int revisedRow = currentItem.pacManRow;
                    int revisedCol = currentItem.pacManCol;
                    if(i<8){updateddung[revisedRow][revisedCol] = ' ';}

                    if(i==0){revisedRow = revisedRow+1; revisedCol = revisedCol-1;}
                    if(i==1){revisedRow = revisedRow+1; revisedCol = revisedCol;}
                    if(i==2){revisedRow = revisedRow+1; revisedCol = revisedCol+1;}
                    if(i==3){revisedRow = revisedRow; revisedCol = revisedCol-1;}
                    if(i==4){revisedRow = revisedRow; revisedCol = revisedCol+1;}
                    if(i==5){revisedRow = revisedRow-1; revisedCol = revisedCol-1;}
                    if(i==6){revisedRow = revisedRow-1; revisedCol = revisedCol;}
                    if(i==7){revisedRow = revisedRow-1; revisedCol = revisedCol+1;}


                    updateddung[revisedRow][revisedCol] = ACT_MAN;


                    ArrayList<ArrayList<Integer>> updatedogrePositions;
                    ArrayList<ArrayList<Integer>> updateddemonPositions;
                    int updatedscore;
                    boolean updatedFireUsed;

                    // Create copies of the ogreLoc and demonLoc ArrayLists
                    ArrayList<ArrayList<Integer>> revisedOgrePos = copyPositions(currentItem.ogreLoc);
                    ArrayList<ArrayList<Integer>> revisedDemonPos = copyPositions(currentItem.demonLoc);


                    if(i==8 || i==9 || i==10 || i==11){
                        Object[] changes = fireUpdates(i, currentItem.pacManRow, currentItem.pacManCol, updateddung, revisedOgrePos, revisedDemonPos, currentItem.score);
                        updateddung = (char[][]) changes[0];
                        updatedogrePositions = (ArrayList<ArrayList<Integer>>) changes[1];
                        updateddemonPositions = (ArrayList<ArrayList<Integer>>) changes[2];
                        updatedscore = (int) changes[3] - POINT_DROP_MAGIC_BULLET;
                        updatedFireUsed = true;
                    }
                    else{
                        updatedogrePositions = copyPositions(revisedOgrePos);
                        updateddemonPositions = copyPositions(revisedDemonPos);
                        updatedscore = currentItem.score - POINT_DROP_PMV;
                        updatedFireUsed = currentItem.fired;
                    }

                    if(updatedogrePositions.isEmpty() && updateddemonPositions.isEmpty()) {
                        return new Object[]{updatedOutput, updatedscore, updateddung};
                    }

                    visited = Arrays.copyOf(currentItem.visited, currentItem.visited.length);
                    visited[i] = true;

                    // Create copies of the ogreLoc and demonLoc ArrayLists
                    ArrayList<ArrayList<Integer>> upogrePositions = copyPositions(updatedogrePositions);
                    ArrayList<ArrayList<Integer>> updemonPositions = copyPositions(updateddemonPositions);

                    ArrayList<Object> temporary = advanceMonsters(updateddung, upogrePositions, updemonPositions, revisedRow, revisedCol, updatedscore);
                    updatedogrePositions = (ArrayList<ArrayList<Integer>>) temporary.get(0);
                    updateddemonPositions = (ArrayList<ArrayList<Integer>>) temporary.get(1);
                    char[][] updatedDungeon = (char[][]) temporary.get(2);
                    updatedscore = (int)temporary.get(3);
                    if(updatedDungeon[revisedRow][revisedCol]!='X'){
                        prioriValue = cost(updatedOutput.toString()) + h(updateddemonPositions, updatedogrePositions);
                        prioriQueue.add(new QueueItem(revisedRow, revisedCol, updatedDungeon, updatedogrePositions, updateddemonPositions, updatedOutput, updatedFireUsed, visited, updatedscore, prioriValue));
                        if(updatedOutput.length() == 7 || (updatedogrePositions.isEmpty() && updateddemonPositions.isEmpty())){
                            return new Object[]{updatedOutput, updatedscore, updatedDungeon};
                        }
                    }
                }
            }
        }
        return new Object[]{};
    }

    
    // Method to add ogre position to the 2D ArrayList
    public static void addOgrePosition(ArrayList<ArrayList<Integer>> ogreLoc, int x, int y) {
        ArrayList<Integer> ogre = new ArrayList<>();
        ogre.add(x);
        ogre.add(y);
        ogreLoc.add(ogre);
    }

    // Method to add Demon position to the 2D ArrayList
    public static void addDemonPosition(ArrayList<ArrayList<Integer>> demonLoc, int x, int y) {
        ArrayList<Integer> demon = new ArrayList<>();
        demon.add(x);
        demon.add(y);
        demonLoc.add(demon);
    }


    
    public static Object[] demonMinDist(ArrayList<ArrayList<Integer>> oremoveList, ArrayList<ArrayList<Integer>> dremoveList, char[][] dungeon, int drow, int dcol, int pacManRow, int pacManCol, ArrayList<ArrayList<Integer>> ogreLoc, ArrayList<ArrayList<Integer>> demonLoc, int score) {
        int dist = Integer.MAX_VALUE;
        int targetRow = pacManRow; // Assuming pacManRow and pacManCol are defined globally
        int targetCol = pacManCol;
        int nRow = drow;
        int nCol = dcol;


        // Define the clockwise order of neighboring cells
        int[][] anticlockwiseOrder = {{-1, 0}, {-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}};

        // Iterate over the neighboring cells in clockwise order
        for (int i = 0; i < anticlockwiseOrder.length; i++) {
            // Calculate the row and column of the neighboring cell
            int dr = anticlockwiseOrder[i][0];
            int dc = anticlockwiseOrder[i][1];
            int neRow = drow + dr;
            int neCol = dcol + dc;

            // Check if the neighboring cell is within the dungeon boundaries
            if (neRow >= 0 && neRow < dungeon.length && neCol >= 0 && neCol < dungeon[0].length) {
                // Check if the neighboring cell is not a wall
                if (dungeon[neRow][neCol] != WALL) {
                    // Calculate the distance from the neighboring cell to the target point
                    int nDist = (int) (Math.pow(targetRow - neRow, 2) + Math.pow(targetCol - neCol, 2));
                    // Update the minimum distance and position if the n distance is smaller
                    if (nDist < dist) {
                        dist = nDist;
                        nRow = neRow;
                        nCol = neCol;
                    }
                }
            }
        }

        // Position the demon in the shortest distance neighbor
        dungeon[drow][dcol] = EMPTY; // Clear the current position

        if(dungeon[nRow][nCol] == CORPSE){
            score += POINT_INC_PER_MONSTR_KILL;
        }

        else if(dungeon[nRow][nCol] == DEMON){
            score += 2*POINT_INC_PER_MONSTR_KILL;
            dungeon[nRow][nCol] = CORPSE;
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(nRow);
            temp.add(nCol);
            demonLoc.remove(temp);
        }

        else if(dungeon[nRow][nCol] == OGRE){
            score += 2*POINT_INC_PER_MONSTR_KILL;
            Iterator<ArrayList<Integer>> iterator = ogreLoc.iterator();
            while (iterator.hasNext()) {
                ArrayList<Integer> position = iterator.next();
                if (position.get(0) == nRow && position.get(1) == nCol) {
                    iterator.remove(); // Use iterator's remove method to safely remove the element
                    break; // Exit the loop once the element is removed
                }
            }
            dungeon[nRow][nCol] = CORPSE;
        }

        else if(dungeon[nRow][nCol] == ACT_MAN){
            score = 0;
            dungeon[nRow][nCol] = 'X';
        }

        else{
            dungeon[nRow][nCol] = DEMON; // Position the demon in the shortest distance neighbor

            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(nRow);
            temp.add(nCol);
            demonLoc.add(temp);
        }

        if(dist == 0){
            score = 0;
        }

        // Return dungeon and score in an object array
        Object[] result = {dungeon, score};
        return result;
    }

    
    public static Object[] ogreMinDist(ArrayList<ArrayList<Integer>> oremoveList, ArrayList<ArrayList<Integer>> dremoveList, char[][] dungeon, int orow, int ocol, int pacManRow, int pacManCol, ArrayList<ArrayList<Integer>> ogreLoc, ArrayList<ArrayList<Integer>> demonLoc, int score) {
        int dist = Integer.MAX_VALUE;
        int targetRow = pacManRow;
        int targetCol = pacManCol;
        int nRow = orow;
        int nCol = ocol;

        // Define the clockwise order of neighboring cells
        int[][] clockwiseOrder = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};

        // Iterate over the neighboring cells in clockwise order
        for (int i = 0; i < clockwiseOrder.length; i++) {
            // Calculate the row and column of the neighboring cell
            int dr = clockwiseOrder[i][0];
            int dc = clockwiseOrder[i][1];
            int neRow = orow + dr;
            int neCol = ocol + dc;

            // Check if the neighboring cell is within the dungeon boundaries
            if (neRow >= 0 && neRow < dungeon.length && neCol >= 0 && neCol < dungeon[0].length) {
                // Check if the neighboring cell is not a wall
                if (dungeon[neRow][neCol] != WALL) {
                    // Calculate the distance from the neighboring cell to the target point
                    int nDist = (int) (Math.pow(targetRow - neRow, 2) + Math.pow(targetCol - neCol, 2));
                    // Update the minimum distance and position if the n distance is smaller
                    if (nDist < dist) {
                        dist = nDist;
                        nRow = neRow;
                        nCol = neCol;
                    }
                }
            }
        }

        if(dungeon[nRow][nCol] == CORPSE){
            score += POINT_INC_PER_MONSTR_KILL;
        }

        else if(dungeon[nRow][nCol] == OGRE){
            score += 2*POINT_INC_PER_MONSTR_KILL;
            dungeon[nRow][nCol] = CORPSE;
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(nRow);
            temp.add(nCol);
            // Remove ogre positions which are in oremoveList
            ogreLoc.remove(temp);
        }

        else if(dungeon[nRow][nCol] == DEMON){
            score += 2*POINT_INC_PER_MONSTR_KILL;
            dungeon[nRow][nCol] = CORPSE;
            Iterator<ArrayList<Integer>> iterator = demonLoc.iterator();
            while (iterator.hasNext()) {
                ArrayList<Integer> position = iterator.next();
                if (position.get(0) == nRow && position.get(1) == nCol) {
                    iterator.remove(); // Use iterator's remove method to remove the current element
                    break;
                }
            }
        }

        else if(dungeon[nRow][nCol] == ACT_MAN){
            score = 0;
            dungeon[nRow][nCol] = 'X';
        }

        else{
            dungeon[nRow][nCol] = OGRE; // Position the ogre in the shortest distance neighbor
            // Replacing the values in ogreLoc ArrayList
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(nRow);
            temp.add(nCol);
            ogreLoc.add(temp);
        }

        if(dist == 0){
            score = 0;
        }

        // Return the updated dungeon and score
        return new Object[]{dungeon, score};
    }



    public static ArrayList<Object> advanceMonsters(char[][] dungeon, ArrayList<ArrayList<Integer>> ogreLoc, ArrayList<ArrayList<Integer>> demonLoc, int newRow, int newCol,int score) {
        ArrayList<Object> result = new ArrayList<>();
        ArrayList<ArrayList<Integer>> ocopyList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> dcopyList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> oremoveList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> dremoveList = new ArrayList<>();
        char[][] tempdung = dungCopy(dungeon);
        ocopyList.addAll(ogreLoc);

        //clearing all ogres and demons in the new dungeon. we will place them later. Until then, we just store those positions.
        for (ArrayList<Integer> temporary : ogreLoc) {
            tempdung[temporary.get(0)][temporary.get(1)] = EMPTY;
        }
        for (ArrayList<Integer> temporary : demonLoc) {
            tempdung[temporary.get(0)][temporary.get(1)] = EMPTY;
        }

        ogreLoc.clear();
        Iterator<ArrayList<Integer>> ogreiterator = ocopyList.iterator();
        while (ogreiterator.hasNext()) {
            ArrayList<Integer> position = ogreiterator.next();
            Object[] a = ogreMinDist(oremoveList, dremoveList, tempdung, position.get(0), position.get(1), newRow, newCol, ogreLoc, demonLoc, score);
            tempdung = (char[][]) a[0];
            score = (int) a[1];
        }


        oremoveList.clear();
        ocopyList.clear();

        dcopyList.addAll(demonLoc);
        demonLoc.clear();
        Iterator<ArrayList<Integer>> demonIterator = dcopyList.iterator();
        while (demonIterator.hasNext()) {
            ArrayList<Integer> position = demonIterator.next();
            Object[] a = demonMinDist(oremoveList, dremoveList, tempdung, position.get(0), position.get(1), newRow, newCol, ogreLoc, demonLoc, score);
            tempdung = (char[][]) a[0];
            score = (int) a[1];
        }

        dremoveList.clear();
        dcopyList.clear();

        result.add(ogreLoc);
        result.add(demonLoc);
        result.add(tempdung);
        result.add(score);
        return result;
    }

    
    public static Object[] fireUpdates(int i, int pacManRow, int pacManCol, char[][] dungeon, ArrayList<ArrayList<Integer>> ogreLoc, ArrayList<ArrayList<Integer>> demonLoc, int score) {
        // Check if firing vertically

        if (i == 8) {
            // Update dungeon tiles and positions for ogres and demons
            for (int j = pacManRow - 1; j > 0; j--) {
                if (dungeon[j][pacManCol] == WALL) {
                    break;
                }
                if (dungeon[j][pacManCol] == OGRE) {
                    dungeon[j][pacManCol] = CORPSE;
                    score += POINT_INC_PER_MONSTR_KILL;
                    // Remove the position from ogreLoc
                    removePosition(ogreLoc, j, pacManCol);
                }
                if (dungeon[j][pacManCol] == DEMON) {
                    dungeon[j][pacManCol] = CORPSE;
                    score += POINT_INC_PER_MONSTR_KILL;
                    // Remove the position from demonLoc
                    removePosition(demonLoc, j, pacManCol);
                }
            }
        }

        else if(i==9){
            for(int j=pacManCol+1; j<cols; j++){
                if(dungeon[pacManRow][j] == WALL){break;}
                if(dungeon[pacManRow][j] == OGRE){dungeon[pacManRow][j] = CORPSE;score += POINT_INC_PER_MONSTR_KILL;removePosition(ogreLoc, pacManRow, j);}
                if(dungeon[pacManRow][j] == DEMON){dungeon[pacManRow][j] = CORPSE;score += POINT_INC_PER_MONSTR_KILL;removePosition(demonLoc, pacManRow, j);}
            }
        }
        else if(i==10){
            for(int j=pacManRow+1; j<rows-1; j++){
                if(dungeon[j][pacManCol] == WALL){break;}
                if(dungeon[j][pacManCol] == OGRE){dungeon[j][pacManCol] = CORPSE;score += POINT_INC_PER_MONSTR_KILL;removePosition(ogreLoc, j, pacManCol);}
                if(dungeon[j][pacManCol] == DEMON){dungeon[j][pacManCol] = CORPSE;score += POINT_INC_PER_MONSTR_KILL;removePosition(demonLoc, j, pacManCol);}
            }
        }
        else if(i==11){
            for(int j=pacManCol-1; j>0; j--){
                if(dungeon[pacManRow][j] == WALL){break;}
                if(dungeon[pacManRow][j] == OGRE){dungeon[pacManRow][j] = CORPSE;score += POINT_INC_PER_MONSTR_KILL;removePosition(ogreLoc, pacManRow, j);}
                if(dungeon[pacManRow][j] == DEMON){dungeon[pacManRow][j] = CORPSE;score += POINT_INC_PER_MONSTR_KILL;removePosition(demonLoc, pacManRow, j);}
            }
        }

        // Return the updated dungeon, ogre positions, demon positions, and score
        return new Object[]{dungeon, ogreLoc, demonLoc, score};
    }

    private static int cost(String moves){
        return moves.length();
    }

    private static int h(ArrayList<ArrayList<Integer>> updateddemonPositions, ArrayList<ArrayList<Integer>> updatedogrePositions){
        return updateddemonPositions.size()*5 + updatedogrePositions.size()*5 ;
    }

    // Method to create a copy of a list of positions
    private static ArrayList<ArrayList<Integer>> copyPositions(ArrayList<ArrayList<Integer>> positions) {
        ArrayList<ArrayList<Integer>> copy = new ArrayList<>();
        for (ArrayList<Integer> position : positions) {
            ArrayList<Integer> newPosition = new ArrayList<>(position);
            copy.add(newPosition);
        }
        return copy;
    }


    private static ArrayList<ArrayList<Integer>> removePosition(ArrayList<ArrayList<Integer>> positions, int row, int col) {
        Iterator<ArrayList<Integer>> iterator = positions.iterator();
        while (iterator.hasNext()) {
            ArrayList<Integer> position = iterator.next();
            if (position.get(0) == row && position.get(1) == col) {
                iterator.remove();
                break; // Stop iterating once the position is found and removed
            }
        }
        return positions;
    }

    // Method to copy a 2D char array
    private static char[][] dungCopy(char[][] dungeon) {
        int rows = dungeon.length;
        int cols = dungeon[0].length;
        char[][] copy = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(dungeon[i], 0, copy[i], 0, cols);
        }
        return copy;
    }

    public static void gameTermination(StringBuilder actions, int score, char[][] dungeon, StringBuilder output, String outputFile, int pacManRow, int pacManCol) {
        try {
            // Write output file
            FileWriter writer = new FileWriter(outputFile);
            writer.write(actions + "\n");
            if(score<=0){score = 0;}
            writer.write(score + "\n");
            int rows = dungeon.length;
            int cols = dungeon[0].length;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    writer.write(dungeon[i][j]);
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the  file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
