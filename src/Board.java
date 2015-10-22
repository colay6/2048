import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Board extends Group{
    private int boardSize;
    private static final int CELL_PADDING = 10;
    private static final Color BOARD_COLOR = Color.TAN;
    private static final Color EMPTY_CELL_COLOR = Color.BEIGE;

    private int[][] pieces;
    private List<Piece> pieceObjects;
    private double cellSize;

    private Scoreboard scoreboard;
    private BooleanProperty lost;
    private IntegerProperty moveCount;
    private IntegerProperty highestValue;
    
    public Board(int side, int size, Scoreboard sb){
        boardSize = size;
        pieces = new int[boardSize][boardSize];
        pieceObjects = new ArrayList<>();
        scoreboard = sb;
        lost = new SimpleBooleanProperty(false);
        moveCount = new SimpleIntegerProperty(0);
        highestValue = new SimpleIntegerProperty(0);
        highestValue.addListener((obs, oldValue, newValue) -> {
            scoreboard.checkBestPiece((int) newValue);
        });

        Rectangle boardPlaceholder = new Rectangle(side, side, BOARD_COLOR);
        boardPlaceholder.setArcWidth(10);
        boardPlaceholder.setArcHeight(10);
        this.getChildren().add(boardPlaceholder);

        cellSize = (side - (boardSize+1)*CELL_PADDING + 0.0)/boardSize;
        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                Rectangle cell = new Rectangle(cellSize, cellSize, EMPTY_CELL_COLOR);
                cell.setArcWidth(10);
                cell.setArcHeight(10);
                cell.setTranslateX(CELL_PADDING + i*(cellSize + CELL_PADDING));
                cell.setTranslateY(CELL_PADDING + j*(cellSize + CELL_PADDING));
                this.getChildren().add(cell);
            }
        }

        randomSpawn();
//        Piece piece = new Piece(CELL_PADDING, CELL_PADDING, cellSize, 2048);
//        this.getChildren().add(piece);
//        pieceObjects.add(piece);
//        pieces[0][0]=2048;
    }

    private void randomSpawn(){
        ArrayList<String> availableSpots = new ArrayList<>();
        for(int i = 0; i < pieces.length; i++){
            for(int j = 0; j < pieces[0].length; j++){
                if(pieces[i][j] == 0){
                    String loc = i + "," + j;
                    availableSpots.add(loc);
                }
            }
        }
        if(availableSpots.size() > 0){
            int index = (int)(Math.random() * availableSpots.size());
            String[] loc = availableSpots.get(index).split(",");

            double[] location = {CELL_PADDING + Integer.parseInt(loc[1])*(cellSize + CELL_PADDING),
                                 CELL_PADDING + Integer.parseInt(loc[0])*(cellSize + CELL_PADDING)};
            double random = Math.random();
            int value = 2;
            if(random >= 0.8){
                value = 4;
            }

            Piece piece = new Piece(location[0], location[1], cellSize, value, 50-(boardSize-3)*8);
            this.getChildren().add(piece);
            pieceObjects.add(piece);

            pieces[Integer.parseInt(loc[0])][Integer.parseInt(loc[1])] = value;
            if(value > highestValue.getValue()){
                highestValue.setValue(value);
            }
            //System.out.println("===== NEW BOARD =====");
            printBoard();
            //System.out.println("==========");
        }
        resetMergeStates();
    }

    private void resetMergeStates(){
        for(Piece p : pieceObjects){
            p.setAlreadyMerged(false);
        }
    }

    private void printBoard(){
        for(int i = 0; i < pieces.length; i++){
            for(int j = 0; j < pieces[0].length; j++){
                //System.out.print(pieces[i][j] + " ");
            }
            //System.out.println();
        }
    }

    private void checkLose(){
        if(pieceObjects.size() == boardSize*boardSize){
            //System.out.println("BOARD FULL");
            for(int i = 0; i < pieces.length; i++){
                for(int j = 0; j < pieces[0].length; j++){
                    for (int xDelt = -1; xDelt <= 1; xDelt++) {
                        for (int yDelt = -1; yDelt <= 1 ; yDelt++) {
                            if ((yDelt!=0 || xDelt != 0) && (Math.abs(xDelt) + Math.abs(yDelt)) <= 1){
                                int[] neighbor = {i + xDelt, j + yDelt};
                                if (neighbor[0] < 0 || neighbor[0] > pieces.length-1) neighbor[0] = -1;
                                if (neighbor[1] < 0 || neighbor[1] > pieces[0].length-1) neighbor[1] = -1;
                                if(neighbor[0]!=-1 && neighbor[1]!=-1){
                                    if(pieces[i][j] == pieces[neighbor[0]][neighbor[1]]){
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            lost.setValue(true);
        }
    }
    
    public void restartGame(){
        this.getChildren().removeAll(pieceObjects);
        pieceObjects = new ArrayList<>();
        pieces = new int[boardSize][boardSize];
        lost.setValue(false);
        moveCount.setValue(0);
        randomSpawn();
    }
    
    public IntegerProperty getMoveCounter(){
        return moveCount;
    }
    
    public BooleanProperty isLost(){
        return lost;
    }

    public void shiftUp(){
        //System.out.println("SHIFT UP");
        boolean moved = false;
        for(int i = 0; i < pieces.length; i++){
            for(int j = 0; j < pieces[0].length; j++){
                if(pieces[i][j] != 0){
                    int newXIndex = 0;
                    int newYIndex = j;
                    boolean merge = false;
                    Piece pieceToRemove = null;
                    for(int a = i-1; a >= 0; a--){
                        if(pieces[a][j] != 0){
                            ////System.out.println("same? " + (a == i));
                            ////System.out.println("contacted: " + a + "," + j);
                            if(pieces[a][j] == pieces[i][j]){
                                ////System.out.println("Add!");
                                newXIndex = a;
                                for(Piece p: pieceObjects){
                                    if(p.isAtLocation(a, j) && a != i){
                                        if(!p.isAlreadyMerged()){
                                            //this.getChildren().remove(p);
                                            pieceToRemove = p;
                                            pieceObjects.remove(p);
                                            merge = true;
                                            break;
                                        }
                                        else{
                                            int newYCoor = a+1;
                                            if(a+1 >= pieces.length)
                                                newYCoor = pieces.length-1;
                                            newXIndex = newYCoor;
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                            else{
                                ////System.out.println("Leave alone!");
                                int newYCoor = a+1;
                                if(a+1 >= pieces.length)
                                    newYCoor = pieces.length-1;
                                newXIndex = newYCoor;
                                break;
                            }
                        }
                    }
                    ////System.out.println("i: " + newXIndex + " j: " + newYIndex);
                    int newValue = pieces[i][j];
                    if(merge){
                        newValue = 2*newValue;
                        if(newValue > highestValue.getValue()){
                            highestValue.setValue(newValue);
                        }
                        scoreboard.updateScore(newValue);
                    }

                    pieces[newXIndex][newYIndex] = newValue;
                    if(newXIndex != i){
                        movePiece(newXIndex, newYIndex, i, j, newValue, merge, pieceToRemove);
                        moved = true;
                    }
                }
            }
        }

        //System.out.println("===== AFTER SHIFT =====");
        printBoard();
        //System.out.println("==========");
        //System.out.println("num pieces: " + pieceObjects.size());

        if(moved){
            randomSpawn();
            moveCount.setValue(moveCount.getValue()+1);
        }
        checkLose();
    }

    public void shiftDown(){
        //System.out.println("SHIFT DOWN");
        boolean moved = false;
        for(int i = pieces.length-1; i >= 0; i--){
            for(int j = 0; j < pieces[0].length; j++){
                if(pieces[i][j] != 0){
                    int newXIndex = pieces.length-1;
                    int newYIndex = j;
                    boolean merge = false;
                    Piece pieceToRemove = null;
                    for(int a = i+1; a <= pieces.length-1; a++){
                        if(pieces[a][j] != 0){
                            ////System.out.println("merge with: " + a + ", " + j);
                            if(pieces[a][j] == pieces[i][j]){
                                ////System.out.println("Add!");
                                newXIndex = a;
                                for(Piece p: pieceObjects){
                                    if(p.isAtLocation(a, j) && a != i){
                                        if(!p.isAlreadyMerged()){
                                            //this.getChildren().remove(p);
                                            pieceToRemove = p;
                                            pieceObjects.remove(p);
                                            merge = true;
                                            break;
                                        }
                                        else{
                                            int newYCoor = a-1;
                                            if(a-1 < 0)
                                                newYCoor = 0;
                                            newXIndex = newYCoor;
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                            else{
                                ////System.out.println("Leave Alone!");
                                int newYCoor = a-1;
                                if(a-1 < 0)
                                    newYCoor = 0;
                                newXIndex = newYCoor;
                                break;
                            }
                        }
                    }
                    ////System.out.println("i: " + newXIndex + " j: " + newYIndex);
                    int newValue = pieces[i][j];
                    if(merge){
                        newValue = 2*newValue;
                        scoreboard.updateScore(newValue);
                        if(newValue > highestValue.getValue()){
                            highestValue.setValue(newValue);
                        }
                    }

                    pieces[newXIndex][newYIndex] = newValue;
                    if(newXIndex != i){
                        movePiece(newXIndex, newYIndex, i, j, newValue, merge, pieceToRemove);
                        moved = true;
                    }
                }
            }
        }

        //System.out.println("===== AFTER SHIFT =====");
        printBoard();
        //System.out.println("==========");
        //System.out.println("num pieces: " + pieceObjects.size());
        if(moved){
            randomSpawn();
            moveCount.setValue(moveCount.getValue()+1);
        }
        checkLose();
    }

    public void shiftLeft(){
        //System.out.println("SHIFT LEFT");
        boolean moved = false;
        for(int i = 0; i < pieces.length; i++){
            for(int j = 0; j < pieces[0].length; j++){
                if(pieces[i][j] != 0){
                    int newXIndex = i;
                    int newYIndex = 0;
                    boolean merge = false;
                    Piece pieceToRemove = null;
                    for(int a = j-1; a >= 0; a--){
                        if(pieces[i][a] != 0){
                            ////System.out.println("same? " + (a == j));
                            ////System.out.println("contacted: " + i + "," + a);
                            if(pieces[i][a] == pieces[i][j]){
                                ////System.out.println("Add!");
                                newYIndex = a;
                                for(Piece p: pieceObjects){
                                    if(p.isAtLocation(i, a) && a != j){
                                        if(!p.isAlreadyMerged()){
                                            //this.getChildren().remove(p);
                                            pieceToRemove = p;
                                            pieceObjects.remove(p);
                                            merge = true;
                                            break;
                                        }
                                        else{
                                            int newXCoor = a+1;
                                            if(a+1 >= pieces.length)
                                                newXCoor = pieces.length-1;
                                            newYIndex = newXCoor;
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                            else{
                                ////System.out.println("Leave alone!");
                                int newXCoor = a+1;
                                if(a+1 >= pieces.length)
                                    newXCoor = pieces.length-1;
                                newYIndex = newXCoor;
                                break;
                            }
                        }
                    }
                    ////System.out.println("i: " + newXIndex + " j: " + newYIndex);
                    int newValue = pieces[i][j];
                    if(merge){
                        newValue = 2*newValue;
                        scoreboard.updateScore(newValue);
                        if(newValue > highestValue.getValue()){
                            highestValue.setValue(newValue);
                        }
                    }

                    pieces[newXIndex][newYIndex] = newValue;
                    if(newYIndex != j){
                        movePiece(newXIndex, newYIndex, i, j, newValue, merge, pieceToRemove);
                        moved = true;
                    }
                }
            }
        }

        //System.out.println("===== AFTER SHIFT =====");
        printBoard();
        //System.out.println("==========");
        //System.out.println("num pieces: " + pieceObjects.size());

        if(moved){
            randomSpawn();
            moveCount.setValue(moveCount.getValue()+1);
        }
        checkLose();
    }

    public void shiftRight(){
        boolean moved = false;
        for(int i = 0; i < pieces.length; i++){
            for(int j = pieces[0].length-1; j >= 0; j--){
                if(pieces[i][j] != 0){
                    int newXIndex = i;
                    int newYIndex = pieces.length-1;
                    boolean merge = false;
                    Piece pieceToRemove = null;
                    for(int a = j+1; a <= pieces.length-1; a++){
                        if(pieces[i][a] != 0){
                            if(pieces[i][a] == pieces[i][j]){
                                ////System.out.println("Add!");
                                newYIndex = a;
                                for(Piece p: pieceObjects){
                                    if(p.isAtLocation(i, a) && a != j){
                                        if(!p.isAlreadyMerged()){
                                            //this.getChildren().remove(p);
                                            pieceToRemove = p;
                                            pieceObjects.remove(p);
                                            merge = true;
                                            break;
                                        }
                                        else{
                                            int newXCoor = a-1;
                                            if(a-1 < 0)
                                                newXCoor = 0;
                                            newYIndex = newXCoor;
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                            else{
                                ////System.out.println("Leave Alone!");
                                int newXCoor = a-1;
                                if(a-1 < 0)
                                    newXCoor = 0;
                                newYIndex = newXCoor;
                                break;
                            }
                        }
                    }
                    //                    //System.out.println("i: " + newXIndex + " j: " + newYIndex);
                    int newValue = pieces[i][j];
                    if(merge){
                        newValue = 2*newValue;
                        scoreboard.updateScore(newValue);
                        if(newValue > highestValue.getValue()){
                            highestValue.setValue(newValue);
                        }
                    }

                    pieces[newXIndex][newYIndex] = newValue;
                    if(newYIndex != j){
                        movePiece(newXIndex, newYIndex, i, j, newValue, merge, pieceToRemove);
                        moved = true;
                    }
                }
            }
        }

        //System.out.println("===== AFTER SHIFT =====");
        printBoard();
        //System.out.println("==========");
        //System.out.println("num pieces: " + pieceObjects.size());
        if(moved){
            randomSpawn();
            moveCount.setValue(moveCount.getValue()+1);
        }
        checkLose();
    }

    private void movePiece(int newI, int newJ, int oldI, int oldJ, int newValue, boolean merge, Piece pieceToRemove){
        pieces[oldI][oldJ] = 0;

        for(Piece piece : pieceObjects){
            if(piece.isAtLocation(oldI, oldJ)){
                double[] location = {CELL_PADDING + newJ*(cellSize + CELL_PADDING),
                                     CELL_PADDING + newI*(cellSize + CELL_PADDING)};
                piece.movePiece(location, newI, newJ).setOnFinished(e -> {
                    if(pieceToRemove != null && merge){
                        this.getChildren().remove(pieceToRemove);
                    }
                    piece.setValue(newValue);
                });
                piece.setAlreadyMerged(merge);
            }
        }
    }
}
