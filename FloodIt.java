import java.util.Arrays;
import java.util.ArrayList;
import tester.*;
import java.util.Random;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//Represents a single square of the game area
class Cell {

  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;

  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // convenience constructor 
  Cell(int x, int y, Color color, boolean flooded) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = null;
    this.right = null;
    this.top = null;
    this.bottom = null;
  }

  // runs on a cell and creates the visual rectangle of the cell
  WorldImage drawCell(Color color) {
    return new RectangleImage(FloodItWorld.CELL_SIZE, 
        FloodItWorld.CELL_SIZE, OutlineMode.SOLID, color);
  }

  // list of colors and static allows outside to access it
  static ArrayList<Color> colorList = new ArrayList<Color>(Arrays.asList(
      Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PINK));
  
}

// Representing the class for world
class FloodItWorld extends World {
  ArrayList<ArrayList<Cell>> cells;

  int clicks = 30;

  int time = 100;

  //cell_size variable
  static int CELL_SIZE = 22;

  // Board_size variable
  static int BOARD_SIZE = 20;

  // constructor
  FloodItWorld(ArrayList<ArrayList<Cell>> cells) {
    this.cells = cells; 
  }

  //import random library and create variable for it
  Random rand = new Random(); 

  // the constructor to create the board for me
  FloodItWorld() {
    this.cells = new ArrayList<ArrayList<Cell>>();

    for (int y = 0; y < CELL_SIZE; y++) {
      ArrayList<Cell> temporary = new ArrayList<Cell>();

      // create inner list of cells
      for (int x = 0; x < CELL_SIZE; x++) {
        int randIndex = rand.nextInt(3);
        Cell tempCell = new Cell(x, y, Cell.colorList.get(randIndex), false);
        temporary.add(tempCell);
      }

      // creates the list of list of cells or 2D List
      this.cells.add(temporary);
    }

    this.assignNeighbors(this.cells);

    cells.get(0).get(0).flooded = true;
  }

  // assignment top, left, bottom, right cells to a cell
  public void assignNeighbors(ArrayList<ArrayList<Cell>> cells) {
    for (ArrayList<Cell> CellList: cells) {
      for (Cell cell: CellList) {
        if (cell.x > 0) {
          cell.left = CellList.get(CellList.indexOf(cell) - 1);
        }
        else {
          cell.left = null;
        }
        if (cell.x < CELL_SIZE - 1) {
          cell.right = CellList.get(CellList.indexOf(cell) + 1);
        } 
        else {
          cell.right = null;
        }
        if (cell.y > 0) {
          cell.top = cells.get(cells.indexOf(CellList) - 1).get(CellList.indexOf(cell));
        }
        else {
          cell.top = null;
        }
        if (cell.y < CELL_SIZE - 1) {
          cell.bottom = cells.get(cells.indexOf(CellList) + 1).get(CellList.indexOf(cell));
        }
        else {
          cell.bottom = null;
        }
      }
    }
  }

  // creating the world
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(0, 0);
    for (ArrayList<Cell> c: cells) {
      for (Cell cell: c) {
        scene.placeImageXY(cell.drawCell(cell.color), 
            cell.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
            cell.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
      }
      scene.placeImageXY((new TextImage(Integer.toString(this.numOfClicks()), CELL_SIZE,
          Color.white)), CELL_SIZE * CELL_SIZE / 2, BOARD_SIZE);

      scene.placeImageXY((new TextImage("Clicks left: ", CELL_SIZE, Color.white)), 
          CELL_SIZE * CELL_SIZE / 2 - (3 * CELL_SIZE), BOARD_SIZE); 

      scene.placeImageXY((new TextImage(Integer.toString(this.timeLeft()), 
          CELL_SIZE, Color.white)), CELL_SIZE * CELL_SIZE - (3 * CELL_SIZE), BOARD_SIZE);

      scene.placeImageXY((new TextImage("Time left: ", CELL_SIZE, Color.white)), CELL_SIZE * CELL_SIZE - (6 * CELL_SIZE), BOARD_SIZE);
    }
    return scene;
  }

  // make final scene of the game
  public WorldScene makeFinalScene(String s) {
    WorldScene finalScene = this.getEmptyScene();
    finalScene.placeImageXY((new TextImage(s, FloodItWorld.CELL_SIZE, Color.BLACK)), 
        FloodItWorld.CELL_SIZE * FloodItWorld.CELL_SIZE / 2, FloodItWorld.BOARD_SIZE
        * FloodItWorld.BOARD_SIZE / 2);
    return finalScene;
  }

  // code for event when the "r" is pressed on keyboard
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      new FloodItWorld();
      this.cells = new FloodItWorld().cells;
      this.clicks = 30;
      this.time = 100;
      this.makeScene();
    }
  }

  // check if all the cells are Flooded
  public boolean allCellsFlooded() {
    boolean isTrue = true;
    for (ArrayList<Cell> c : cells) {
      for (Cell cell: c) {
        if (!(cell.flooded)) {
          isTrue = false;
        }
      }
    }
    return isTrue;
  }

  // this tracks how many available clicks are left for the player
  public int numOfClicks() {
    if (this.clicks < 0) {
      return 0;
    }
    else {
      return this.clicks;
    }
  }

  // tracks how much time is left for you to complete the game
  public int timeLeft() {
    if(this.time < 0) {
      return 0;
    }
    else {
      return this.time;
    }
  }

  // tracks with clicking 
  public void onMouseClicked(Posn pos) {
    this.makeFloodIt(getClickedCell(pos));
    this.clicks -= 1;
  }

  // tracks with time ticking
  public void onTick() {
    this.time -= 1;
  }
  
  // get the cell that you click on
  public Cell getClickedCell(Posn pos) {
    Cell saveCell = null;
    // create a random cell 
    for (ArrayList<Cell> c: cells) {
      for (Cell cell: c) {
        if (cell.x * FloodItWorld.CELL_SIZE  <= pos.x && pos.x <= cell.x * 
            FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE 
            && cell.y * FloodItWorld.CELL_SIZE <= pos.y && pos.y <=  cell.y * 
            FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE) {
          // update the cell 
          saveCell = cell;
          break;
        }
      }
    }
    return saveCell;
  } 

  //the end of the world
  public WorldEnd worldEnds() {
    if (this.allCellsFlooded() && (this.numOfClicks() > 0)) {
      return new WorldEnd(true, this.makeFinalScene("You won the game! Congrats!"));
    }
    else {
      new WorldEnd(false, this.makeScene());
    }
    if (this.numOfClicks() == 0) {
      return new WorldEnd(true, this.makeFinalScene("You are out of clicks. You lost :("));
    }
    if (this.timeLeft() == 0) {
      return new WorldEnd(true, this.makeFinalScene("You are out of time. You lost :("));
    }
    return new WorldEnd(false, this.makeScene());
  }

  // the method that mainly deals with flood
  public void makeFloodIt(Cell clicked) {
    for (ArrayList<Cell> c : cells) {
      for (Cell cell: c) {
        if (cell.flooded) {
          cell.color = clicked.color;
          if (cell.left != null && cell.left.color.equals(clicked.color)) {
            cell.left.flooded = true;
          }
          if (cell.right != null && cell.right.color.equals(clicked.color)) {
            cell.right.flooded = true;
          }
          if (cell.bottom != null && cell.bottom.color.equals(clicked.color)) {
            cell.bottom.flooded = true;
          }
          if (cell.top != null && cell.top.color.equals(clicked.color)) {
            cell.top.flooded = true;
          }
        }
      }
    }
  } 
}  


// class for examples
class ExamplesFlood {
  ExamplesFlood(){}

  // Cell Examples
  Cell c1;
  Cell c2;
  Cell c3;
  Cell c4;
  Cell c5;
  Cell c6;
  Cell c7;
  Cell c8;
  Cell c9;
  Cell c10;
  Cell c11;
  Cell c12;
  Cell c13;

  // FloodItWorld Example
  FloodItWorld f1;
  FloodItWorld f2;
  FloodItWorld f3;
  FloodItWorld f4;

  // method to initialize
  void initCell() {

    //examples of Cells initialized
    this.c1 = new Cell(0 , 0, Color.red, true);
    this.c2 = new Cell(1, 0, Color.green, true);
    this.c3 = new Cell(0, 1, Color.blue, true);
    this.c4 = new Cell(1, 1, Color.yellow, true);
    this.c5 = new Cell(0, 0, Color.yellow, false);
    this.c6 = new Cell(1, 0, Color.green, false);
    this.c7 = new Cell(2, 0, Color.blue, false);
    this.c8 = new Cell(0, 1, Color.red, false);
    this.c9 = new Cell(1, 1, Color.yellow, false);
    this.c10 = new Cell(2, 1, Color.green, false);
    this.c11 = new Cell(0, 2, Color.pink, false);
    this.c12 = new Cell(1, 2, Color.blue, false);
    this.c13 = new Cell(2, 2, Color.red, false);

    // the world with randomly generated cells 
    this.f1 = new FloodItWorld();

    // creates my own world with customized 2D list of cells and give it to f1

    ArrayList<ArrayList<Cell>> temporary = new ArrayList<ArrayList<Cell>>();
    temporary.add(new ArrayList<Cell>());
    temporary.get(0).add(c1);

    this.f2 = new FloodItWorld(temporary);

    // creates my own world with customized 2D list of cells and give it to f2
    ArrayList<ArrayList<Cell>> temporary2 = new ArrayList<ArrayList<Cell>>();
    temporary2.add(new ArrayList<Cell>());
    temporary2.add(new ArrayList<Cell>());
    temporary2.get(0).add(c1);
    temporary2.get(0).add(c2);
    temporary2.get(1).add(c3);
    temporary2.get(1).add(c4);


    // my own world with 2 X 2 cells
    this.f3 = new FloodItWorld(temporary2);

    // creates my own world with customized 2D list of Cells and give it to f3
    ArrayList<ArrayList<Cell>> temporary3 = new ArrayList<ArrayList<Cell>>();
    temporary3.add(new ArrayList<Cell>());
    temporary3.add(new ArrayList<Cell>());
    temporary3.add(new ArrayList<Cell>());
    temporary3.get(0).add(c5);
    temporary3.get(0).add(c6);
    temporary3.get(0).add(c7);
    temporary3.get(1).add(c8);
    temporary3.get(1).add(c9);
    temporary3.get(1).add(c10);
    temporary3.get(2).add(c11);
    temporary3.get(2).add(c12);
    temporary3.get(2).add(c13);

    // my own world with 3 X 3 cells 
    this.f4 = new FloodItWorld(temporary3);
  }

  // tests for drawCell method
  void testDrawCell(Tester t) {
    initCell();

    t.checkExpect(this.c1.drawCell(c1.color), 
        new RectangleImage(FloodItWorld.CELL_SIZE, 
            FloodItWorld.CELL_SIZE, "solid", Color.red));
    t.checkExpect(this.c2.drawCell(c2.color), 
        new RectangleImage(FloodItWorld.CELL_SIZE, 
            FloodItWorld.CELL_SIZE, "solid", Color.green));
    t.checkExpect(this.c3.drawCell(c3.color), 
        new RectangleImage(FloodItWorld.CELL_SIZE, 
            FloodItWorld.CELL_SIZE, "solid", Color.blue));
    t.checkExpect(this.c4.drawCell(Color.pink), 
        new RectangleImage(FloodItWorld.CELL_SIZE, 
            FloodItWorld.CELL_SIZE, "solid", Color.pink));
  }

  //test for MakeScene method 
  void testMakeScene(Tester t) {

    // initialize for 1 X 1
    initCell();

    // test for 1 X 1 Cell
    WorldScene testScene = new WorldScene(0, 0);
    testScene.placeImageXY(this.c1.drawCell(Color.red), 
        this.c1.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c1.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);

    t.checkExpect(this.f1.makeScene(), testScene);

    // initialize for 2 X 2
    initCell();

    // test for 2 X 2 Cells 
    WorldScene testScene2 =  new WorldScene(0, 0);
    testScene2.placeImageXY(this.c1.drawCell(Color.red), 
        this.c1.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c1.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
    testScene2.placeImageXY(this.c2.drawCell(Color.green), 
        this.c2.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c2.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
    testScene2.placeImageXY(this.c1.drawCell(Color.blue), 
        this.c3.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c3.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
    testScene2.placeImageXY(this.c1.drawCell(Color.yellow), 
        this.c4.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c4.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);

    t.checkExpect(this.f3.makeScene(), testScene2);

    // initialize for 3 X 3
    initCell();

    // test for 3 X 3 Cells 
    WorldScene testScene3 = new WorldScene(0, 0);
    testScene3.placeImageXY(this.c5.drawCell(Color.yellow), 
        this.c5.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c5.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
    testScene3.placeImageXY(this.c6.drawCell(Color.green), 
        this.c6.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c6.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
    testScene3.placeImageXY(this.c7.drawCell(Color.blue), 
        this.c7.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c7.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
    testScene3.placeImageXY(this.c8.drawCell(Color.red), 
        this.c8.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c8.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
    testScene3.placeImageXY(this.c9.drawCell(Color.yellow), 
        this.c9.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c9.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
    testScene3.placeImageXY(this.c10.drawCell(Color.green), 
        this.c10.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c10.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
    testScene3.placeImageXY(this.c11.drawCell(Color.pink), 
        this.c11.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c11.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
    testScene3.placeImageXY(this.c12.drawCell(Color.blue), 
        this.c12.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c12.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
    testScene3.placeImageXY(this.c13.drawCell(Color.red), 
        this.c13.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2, 
        this.c13.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);

    t.checkExpect(this.f4.makeScene(), testScene3);
  }

  //test for timeLeft method 
  void testimeLeft(Tester t) {

    // initialize
    initCell();

    //check the method 
    t.checkExpect(f1.timeLeft(), 100);
    t.checkExpect(f2.timeLeft() - 30, 70);
    t.checkExpect(f3.timeLeft() - 50, 50);
    t.checkExpect(f4.timeLeft() - 100, 0);
  }
  
  // test for numOfClicks method 
  void testNumOfClicks(Tester t) {

    // initialize
    initCell();

    //check the method 
    t.checkExpect(f1.numOfClicks(), 30);
    t.checkExpect(f2.numOfClicks() - 10, 20);
    t.checkExpect(f3.numOfClicks() - 20, 10);
    t.checkExpect(f4.numOfClicks() - 30, 0);
  }

  // test for getClickedCell method
  void testGetClickedCell(Tester t) {

    //initialize
    initCell();

    // check the method 
    t.checkExpect(f2.getClickedCell(new Posn(0 * FloodItWorld.CELL_SIZE + 1, 0 * FloodItWorld.CELL_SIZE + 1)), this.c1);
    t.checkExpect(f3.getClickedCell(new Posn(0 * FloodItWorld.CELL_SIZE + 1, 0 * FloodItWorld.CELL_SIZE + 1)), this.c1);
    t.checkExpect(f4.getClickedCell(new Posn(0 * FloodItWorld.CELL_SIZE + 1, 0 * FloodItWorld.CELL_SIZE + 1)), this.c5);
    t.checkExpect(f3.getClickedCell(new Posn(1 * FloodItWorld.CELL_SIZE + 1, 1 * FloodItWorld.CELL_SIZE + 1)), this.c4);
    t.checkExpect(f4.getClickedCell(new Posn(1 * FloodItWorld.CELL_SIZE + 1, 1 * FloodItWorld.CELL_SIZE + 1)), this.c9);
  }

  //test for the key event method
  void testonKeyEvent(Tester t) {

    //initialize
    initCell();

    // run the method
    f1.onKeyEvent("r");

    // check the effect
    t.checkExpect(f1, f1);
    t.checkExpect(f1.time, 100);
    t.checkExpect(f1.clicks, 30);
    t.checkExpect(f1.cells, f1.cells);
  }
  
  // test for allCellsFlooded method 
  void testAllCellsFlooded(Tester t) {
    
    //initialize
    initCell();
    
    // check the method 
    t.checkExpect(f2.allCellsFlooded(), true);
    t.checkExpect(f3.allCellsFlooded(), true);
    t.checkExpect(f4.allCellsFlooded(), false);
  }

  // Running the world with bigBang
  void testRunTheWorld(Tester t) {
    initCell();

    this.f1.bigBang(FloodItWorld.CELL_SIZE * FloodItWorld.CELL_SIZE, 
        FloodItWorld.CELL_SIZE * FloodItWorld.CELL_SIZE, 1);
  }
}