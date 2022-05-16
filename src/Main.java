import java.util.Random;
import java.util.Scanner;

public class Main {
  static int SIZE = 30;
  static char[][] board = new char[30][30];

  public static void fillBoard() {
    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {
        board[i][j] = ' ';
      }
    }
  }

  public static void printBoard() {
    for (int i = -1; i <= SIZE; i++) {
      for (int j = -1; j <= SIZE; j++) {
        if ((i == -1 || i == SIZE) || (j == -1 || j == SIZE)) {
          System.out.print("# ");
        } else {
          System.out.print(board[i][j] + " ");
        }
      }
      System.out.println();
    }
  }

  public static boolean placePlayer(int row, int col, int playerId) {
    if (isPlayerSelectionValid(row, col)) {
      char castPlayerIdToChar = (char) ((char) playerId + '0');
      board[row][col] = castPlayerIdToChar;
      return true;
    } else {
      System.out.println("your player placement was invalid");
      return false;
    }
  }

  public static boolean isPlayerSelectionValid(int row, int col) {
    return (row >= 0 && row < SIZE) && (col >= 0 && col < SIZE);
  }

  public static boolean movePlayer(int currentRow, int currentCol, int newRow, int newCol, int playerId) {
    if (placePlayer(newRow, newCol, playerId)) {
      board[currentRow][currentCol] = ' ';
      return true;
    }
    return false;
  }

  public static boolean placeRug(int row, int col, int size) {
    if (isRugPlacementValid(row, col, size)) {
      for (int i = row; i < size + row; i++) {
        for (int j = col; j < size + col; j++) {
          board[i][j] = '*';
        }
      }
      return true;
    } else {
      System.out.println("the rug placement was out of bounds, or the place is taken");
      return false;
    }
  }

  public static boolean isRugPlacementValid(int row, int col, int size) {
    try {
      for (int i = row; i < size + row; i++) {
        for (int j = col; j < size + col; j++) {
          if (board[i][j] != ' ') return false;
        }
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static boolean hasPlayerWon(int row, int col) {
    if (isPlayerSelectionValid(row, col)) {
      return board[row][col] == '*';
    }
    return false;
  }

  public static int getOverAllWinner(int steps1, int steps2, int games1, int games2) {
    if (games1 > games2) return 1;
    if (games2 > games1) return 2;
    if (steps1 > steps2) return 2;
    if (steps2 > steps1) return 1;
    Random r = new Random();
    int result = r.nextInt(3 - 1) + 1;
    return result;
  }

  public static int[][] getRugFrame(int rugRow, int rugCol, int size) {
    int[][] rugFrame = new int[size * 4 - 4][2];
    int rugMatIndex = 0;
    for (int i = rugRow; i < rugRow + size; i++) {
      for (int j = rugCol; j < rugCol + size; j++) {
        if (i == rugRow || i == rugRow + size - 1 || j == rugCol || j == rugCol + size - 1) {
          rugFrame[rugMatIndex][0] = i;
          rugFrame[rugMatIndex++][1] = j;
        }
      }
    }
    return rugFrame;
  }

  public static int[] closestPoint(int row, int col, int[][] rugIndexes) {
    int minRow = 40, minCol = 40;
    int[] closestPoint = new int[2];
    for (int i = 0; i < rugIndexes.length; i++) {
      if (Math.abs(rugIndexes[i][0] - row) < minRow) {
        minRow = Math.abs(rugIndexes[i][0] - row);
        closestPoint[0] = rugIndexes[i][0];
      }

      if (Math.abs(rugIndexes[i][1] - col) < minCol) {
        minCol = Math.abs(rugIndexes[i][1] - col);
        closestPoint[1] = rugIndexes[i][1];
      }
    }
    return closestPoint;
  }

  public static int getWhosCloser(int rugRow, int rugCol, int size, int aRow, int aCol, int bRow, int bCol) {
    int[][] rugFrame = getRugFrame(rugRow, rugCol, size);
    int[] playerAMeetPoint = closestPoint(aRow, aCol, rugFrame);
    int[] playerBMeetPoint = closestPoint(bRow, bCol, rugFrame);
    int playerADistance = Math.abs(playerAMeetPoint[0] - aRow + playerAMeetPoint[1] - aCol);
    int playerBDistance = Math.abs(playerBMeetPoint[0] - bRow + playerBMeetPoint[1] - bCol);
    if (playerADistance > playerBDistance) {
      return 2;
    } else if (playerADistance < playerBDistance) {
      return 1;
    } else return 0;
  }

  public static void main(String[] args) {

    Scanner in = new Scanner(System.in);
    fillBoard();

    boolean matchISGoing = true;
    boolean keepPlaying = true;
    int player1Steps = 0, player2Steps = 0, player1Wins = 0, player2Wins = 0;
    int turnCounter = 0;

    while (keepPlaying) {

      System.out.println("player " + 1 + " enter locations for your starting point");
      int firstRow = in.nextInt();
      int firstCol = in.nextInt();
      while (!placePlayer(firstRow, firstCol, 1)) {
        System.out.println("re-enter location");
        firstRow = in.nextInt();
        firstCol = in.nextInt();
      }

      System.out.println("player " + 2 + " enter locations for your starting point");
      int secondRow = in.nextInt();
      int secondCol = in.nextInt();
      while (!placePlayer(secondRow, secondCol, 2)) {
        System.out.println("re-enter location");
        secondRow = in.nextInt();
        secondCol = in.nextInt();
      }

      System.out.println("enter location for the rug");
      int row = in.nextInt();
      int col = in.nextInt();
      int size = in.nextInt();
      while (!placeRug(row, col, size)) {
        System.out.println("re-enter location");
        row = in.nextInt();
        col = in.nextInt();
        size = in.nextInt();
      }
      printBoard();
      System.out.println(
          getWhosCloser(row, col, size, firstRow, firstCol, secondRow, secondCol)
              + " has a higher chance of winning");

      while (matchISGoing) {

        int turn = (turnCounter % 2) + 1;
        System.out.println("player " + turn + " enter where you wanna go");

        int playerRow, playerCol;
        if (turn == 1) {
          player1Steps++;
          playerRow = firstRow;
          playerCol = firstCol;
        } else {
          player2Steps++;
          playerRow = secondRow;
          playerCol = secondCol;
        }

        int directionSelected = in.nextInt();
        switch (directionSelected) {
          case 2:
            if (hasPlayerWon(playerRow + 1, playerCol)) {
              matchISGoing = false;
            }
            if (movePlayer(playerRow, playerCol, playerRow + 1, playerCol, turn)) {
              if (turn == 1) firstRow++;
              else secondRow++;
            }
            break;
          case 3:
            if (hasPlayerWon(playerRow, playerCol + 1)) {
              matchISGoing = false;
            }
            if (movePlayer(playerRow, playerCol, playerRow, playerCol + 1, turn)) {
              if (turn == 1) firstCol++;
              else secondCol++;
            }
            break;
          case 4:
            if (hasPlayerWon(playerRow, playerCol - 1)) {
              matchISGoing = false;
            }
            if (movePlayer(playerRow, playerCol, playerRow, playerCol - 1, turn)) {
              if (turn == 1) firstCol--;
              else secondCol--;
            }
            break;
          case 1:
            if (hasPlayerWon(playerRow - 1, playerCol)) {
              matchISGoing = false;
            }
            if (movePlayer(playerRow, playerCol, playerRow - 1, playerCol, turn)) {
              if (turn == 1) firstRow--;
              else secondRow--;
            }
            break;
          default:
            System.out.println("you entered invalid input and missed your turn ");
            break;
        }
        printBoard();
        turnCounter++;

        if (!matchISGoing) {
          System.out.println("player " + turn + " has won");
          if (turn == 1) {
            player1Wins++;
          } else {
            player2Wins++;
          }
        }
      }

      System.out.println("keep playing?");
      String choice = in.next();
      if (choice.equals("y")) {
        matchISGoing = true;
        fillBoard();
      } else if (choice.equals("n")) {
        System.out.println(
            "player "
                + getOverAllWinner(player1Steps, player2Steps, player1Wins, player2Wins)
                + " is overall winner");
        keepPlaying = false;
      }
    }
  }
}
