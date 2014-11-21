/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gogame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author ScottieYan
 */
public class GoGame {

    final int SIZE = 19;
    final int[][] BOARD = new int[SIZE][SIZE];
    final int[][] REMOVE = new int[SIZE][SIZE];//remove dead piece
    final int BLACK = 99;
    final int WHITE = 89;
    final int EMPTY = 79;

    final int FILLED = 69;
    final int UNFILLED = 59;

    boolean turn = true; //true (black)

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new GoGame();
    }

    GoGame() {
        JFrame window = new JFrame("GoGame");
        window.setSize(500, 522);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initialize();
        myMouseListener ml = new myMouseListener();

        bP.addMouseListener(ml);
        window.add(bP);
        window.validate();
        window.setVisible(true);
    }
    BoardPanel bP = new BoardPanel();

    public class BoardPanel extends JPanel {

        @Override
        public void paintComponent(Graphics g) {

            this.setSize(500, 500);
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            for (int i = 1; i <= SIZE; i++) {
                g.drawLine(i * 25, 25, i * 25, 475);
                g.drawLine(25, i * 25, 475, i * 25);
            }
            //turn sign
            if (turn) {
                g.setColor(Color.BLACK);
                g.fillOval(0, 0, 10, 10);
            } else {
                g.setColor(Color.WHITE);
                g.fillOval(0, 0, 10, 10);
            }
            //paint piece
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    switch (BOARD[i][j]) {
                        case BLACK:
                            g.setColor(Color.BLACK);
                            g.fillOval(i * 25 + 13, j * 25 + 13, 24, 24);
                            break;
                        case WHITE:
                            g.setColor(Color.WHITE);
                            g.fillOval(i * 25 + 13, j * 25 + 13, 24, 24);
                            break;
                    }
                    //star
                    if (i == 3 || i == 9 || i == 15) {
                        if (j == 3 || j == 9 || j == 15) {
                            if (BOARD[i][j] == EMPTY) {
                                g.setColor(Color.BLACK);
                                g.fillOval(i * 25 + 19, j * 25 + 19, 12, 12);
                            }
                        }
                    }
                }
            }

        }
    }

    void initialize() {
        bP.setBackground(new Color(150, 202, 51));
        //empty map
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                BOARD[i][j] = EMPTY;
            }
        }
    }

    public class myMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            dropPiece(x, y);

            bP.repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        void dropPiece(int x, int y) {
            boolean complete = false;

            if (x > 20 && y > 20 && x < 480 && y < 480) {
                if (turn) {
                    if (x % 25 < 6 || x % 25 > 19) {
                        if (x % 25 > 19) {
                            x /= 25;
                        } else {
                            x = x / 25 - 1;//left or right
                        }
                        if (y % 25 > 19) {
                            y /= 25;
                        } else {
                            y = y / 25 - 1;//left or right
                        }
                        if (BOARD[x][y] == EMPTY) {
                            BOARD[x][y] = BLACK;
                            System.out.println("b" + x + "," + y);
                            complete = true;
                        }
                    }
                } else {
                    if (x % 25 < 6 || x % 25 > 19) {
                        if (x % 25 > 19) {
                            x /= 25;
                        } else {
                            x = x / 25 - 1;//left or right
                        }
                        if (y % 25 > 19) {
                            y /= 25;
                        } else {
                            y = y / 25 - 1;//left or right
                        }
                        if (BOARD[x][y] == EMPTY) {
                            BOARD[x][y] = WHITE;
                            System.out.println("w" + x + "," + y);
                            complete = true;
                        }
                    }
                }
            }

            if (complete) {
                suicide(x,y,turn);
                removeStone(x, y, turn);
                turn = !turn;
            }
        }
        void suicide(int x,int y, boolean color){
            floodFill(x,y,!color);
            if (deadStone()) {
                remove();//remove stone data
                clear();
                turn=!turn;
            }
        }
        void removeStone(int x, int y, boolean color) {
            floodFill(x - 1, y, color);
            if (deadStone()) {
                remove();//remove stone data
                clear();
            }
            floodFill(x + 1, y, color);
            if (deadStone()) {
                remove();//remove stone data
                clear();
            }
            floodFill(x, y - 1, color);
            if (deadStone()) {
                remove();//remove stone data
                clear();
            }
            floodFill(x, y + 1, color);
            if (deadStone()) {
                remove();//remove stone data
                clear();
            }
        }

        void floodFill(int x, int y, boolean color) {
            int stone;
            if (color) {
                stone = BLACK;
            } else {
                stone = WHITE;
            }
            
            if (x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
                if (BOARD[x][y] != stone && REMOVE[x][y]!=FILLED) {
                    REMOVE[x][y] = FILLED;
                    floodFill(x - 1, y, color);
                    floodFill(x + 1, y, color);
                    floodFill(x, y - 1, color);
                    floodFill(x, y + 1, color);
                }
            }
        }

        boolean deadStone() {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (REMOVE[i][j] == FILLED && BOARD[i][j] == EMPTY) {
                        clear();//remove data from REMOVE
                        return false;
                    }
                }
            }
            return true;
        }

        void clear() {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    REMOVE[i][j] = UNFILLED;
                }
            }
        }

        void remove() {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (REMOVE[i][j] == FILLED) {
                        BOARD[i][j] = EMPTY;
                    }
                }
            }
        }
    }
}
