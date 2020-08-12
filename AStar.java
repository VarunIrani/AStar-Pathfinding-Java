
// library imports
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import cell.Cell;
import core.AStarCore;

/**
 * AStar - Main class that includes running code. Keyboard and Mouse events to
 * recognize cell placement and initiation of algorithm
 */
public class AStar extends JFrame {
  private static final long serialVersionUID = 1L;
  public int width, height;
  private int rows = 30, cols = 30;
  private Cell[][] grid = new Cell[rows][cols];
  private Cell start, end;
  private int KEYCODE;
  private boolean startCreated = false, endCreated = false, beginAStar = false, randomCells = false;
  private AStarCore aStarCore = new AStarCore();
  private Font largeFont = new Font("Fira Code", Font.BOLD, 36);
  private Font smallFont = new Font("Fira Code", Font.BOLD, 22);

  // setup() - Initializes grid and adds each cell's neighbors
  private void setup() {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        grid[i][j] = new Cell(i, j);
        grid[i][j].color = Color.WHITE;
      }
    }
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        grid[i][j].addNeighbors(grid);
      }
    }
    System.out.println("Setup Complete");
  }

  /**
   * @param canvas
   * @param e
   */
  // drawWall() - makes the cell a wall where the mouse is
  private void drawWall(JPanel canvas, MouseEvent e) {
    int[] cell = new int[2];
    cell = Cell.clickedCell(e.getPoint());
    if (cell != null) {
      int i = cell[0];
      int j = cell[1];
      if (!grid[i][j].wall && !grid[i][j].start && !grid[i][j].end) {
        grid[i][j].wall = true;
        canvas.repaint();
      }
    }
  }

  // reset() - Resets everyting
  private void reset() {
    startCreated = endCreated = beginAStar = randomCells = false;
    new AStar();
  }

  /**
   * @param g
   */
  private void drawInstructions(Graphics g) {
    // Title
    g.setFont(largeFont);
    g.drawString("A* Pathfinding", grid[0][0].w * 31, 50);
    // Legend
    g.setFont(smallFont);
    g.drawString("Legend", grid[0][0].w * 31, 100);

    g.setFont(smallFont.deriveFont(Font.PLAIN, 18));

    // Start
    g.setColor(new Color(120, 178, 179));
    g.fillRect(grid[0][0].w * 31, 130, grid[0][0].w, grid[0][0].w);

    g.setColor(Color.BLACK);
    g.drawString("Start", grid[0][0].w * 33, 150);

    // End
    g.setColor(new Color(255, 94, 90));
    g.fillRect(grid[0][0].w * 37, 130, grid[0][0].w, grid[0][0].w);

    g.setColor(Color.BLACK);
    g.drawString("End", grid[0][0].w * 39, 150);

    // Open Set
    g.setColor(new Color(143, 255, 232));
    g.fillRect(grid[0][0].w * 42, 130, grid[0][0].w, grid[0][0].w);

    g.setColor(Color.BLACK);
    g.drawString("Open Set", grid[0][0].w * 44, 150);

    // Closed Set
    g.setColor(new Color(251, 168, 255));
    g.fillRect(grid[0][0].w * 31, 180, grid[0][0].w, grid[0][0].w);

    g.setColor(Color.BLACK);
    g.drawString("Closed Set", grid[0][0].w * 33, 200);

    // Wall
    g.setColor(new Color(50, 50, 50));
    g.fillRect(grid[0][0].w * 40, 180, grid[0][0].w, grid[0][0].w);

    g.setColor(Color.BLACK);
    g.drawString("Wall", grid[0][0].w * 42, 200);

    g.setFont(smallFont);
    g.drawString("Controls", grid[0][0].w * 31, 250);

    g.setFont(smallFont.deriveFont(Font.PLAIN, 18));

    g.drawString("• Hold S and click on any cell to set start point", grid[0][0].w * 31, 290);
    g.drawString("• Hold E and click on any cell to set end point", grid[0][0].w * 31, 320);
    g.drawString("• Drag on cells to create walls", grid[0][0].w * 31, 350);
    g.drawString("• Press R to generate random walls", grid[0][0].w * 31, 380);
    g.drawString("• Press Enter to start", grid[0][0].w * 31, 410);
    g.drawString("• Press Escape to reset", grid[0][0].w * 31, 440);
  }

  public AStar() {
    super("A Star Pathfinding");

    setup();

    JPanel canvas = new JPanel() {
      private static final long serialVersionUID = 1L;

      public void paint(Graphics g) {
        super.paint(g);
        setBackground(Color.WHITE);

        drawInstructions(g);

        // Drawing all the cells
        for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
            grid[i][j].show(g);
          }
        }
        if (beginAStar) {
          // AStar begins
          beginAStar = aStarCore.startAStar();
          aStarCore.draw(g);
          try {
            Thread.sleep(50);
            this.repaint();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    };
    canvas.setFocusable(true);

    add(canvas);
    // Reading mouse drag to invoke drawWall()
    canvas.addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseDragged(MouseEvent e) {
        drawWall(canvas, e);
      }

      @Override
      public void mouseMoved(MouseEvent e) {
      }
    });

    canvas.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int[] cell = new int[2];
        cell = Cell.clickedCell(e.getPoint());
        if (cell != null) {
          int i = cell[0];
          int j = cell[1];
          if (KEYCODE == KeyEvent.VK_S) {
            // Creating Start
            if (!startCreated) {
              start = grid[i][j];
              grid[i][j].color = new Color(120, 178, 179);
              grid[i][j].start = true;
              startCreated = true;
              aStarCore.start = start;
              aStarCore.openSet.add(start);
              canvas.repaint();
            }
          } else if (KEYCODE == KeyEvent.VK_E) {
            // Creating End
            if (!endCreated) {
              end = grid[i][j];
              grid[i][j].color = new Color(255, 94, 90);
              grid[i][j].end = true;
              aStarCore.end = end;
              endCreated = true;
              canvas.repaint();
            }
          }
        }
      }
    });

    canvas.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {

      }

      @Override
      public void keyPressed(KeyEvent e) {
        KEYCODE = e.getKeyCode();
        // Pressing Enter would begin the algorithm
        if (KEYCODE == KeyEvent.VK_ENTER) {
          if (startCreated && endCreated && !beginAStar) {
            System.out.println("Start A*");
            beginAStar = true;
            canvas.repaint();
          }
        } else if (KEYCODE == KeyEvent.VK_R) {
          // Cells have a 30% chance of being a wall
          if (!randomCells) {
            Random r = new Random();
            for (int i = 0; i < rows; i++) {
              for (int j = 0; j < cols; j++) {
                if (grid[i][j] != start && grid[i][j] != end) {
                  if (r.nextDouble() < 0.3) {
                    grid[i][j].wall = true;
                    canvas.repaint();
                  }
                }
              }
            }
            randomCells = true;
          }
        } else if (KEYCODE == KeyEvent.VK_RIGHT) {
          // canvas.repaint();
        } else if (KEYCODE == KeyEvent.VK_ESCAPE) {
          reset();
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        KEYCODE = -1;
      }
    });

    GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice device = graphics.getDefaultScreenDevice();
    width = device.getDisplayMode().getWidth();
    height = device.getDisplayMode().getHeight();
    setSize(width, height);
    setVisible(true);
    setResizable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
  }

  /**
   * @param args[]
   */
  public static void main(String args[]) {
    new AStar();
  }
}