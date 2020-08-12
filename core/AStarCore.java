package core;

import cell.Cell;
import java.util.*;
import java.awt.*;

public class AStarCore {
	public ArrayList<Cell> openSet = new ArrayList<Cell>(), closedSet = new ArrayList<Cell>();

	public Cell start, end, current;

	/**
	 * @param a
	 * @param b
	 * @return int
	 */
	private int heuristic(Cell a, Cell b) {
		Point p = a.getPoint();
		Point q = b.getPoint();
		return (int) Point.distance(p.x, p.y, q.x, q.y);
	}

	/**
	 * @param g
	 */
	public void draw(Graphics g) {
		for (int i = 0; i < openSet.size(); i++) {
			openSet.get(i).color = new Color(143, 255, 232);
			openSet.get(i).show(g);
		}

		for (int i = 0; i < closedSet.size(); i++) {
			closedSet.get(i).color = new Color(251, 168, 255);
			closedSet.get(i).show(g);
		}
		ArrayList<Cell> path = new ArrayList<Cell>();
		Cell temp = current;
		path.add(temp);
		while (temp.previous != null) {
			path.add(temp.previous);
			temp = temp.previous;
		}
		for (int i = 0; i < path.size(); i++) {
			path.get(i).color = new Color(248, 0, 104);
			path.get(i).show(g);
		}
	}

	/**
	 * @return boolean
	 */
	public boolean startAStar() {
		if (openSet.size() > 0) {
			current = null;
			int winner = 0;
			for (int i = 0; i < openSet.size(); i++) {
				Cell spot = openSet.get(i);
				if (spot.fCost < openSet.get(winner).fCost) {
					winner = i;
				}
			}
			current = openSet.get(winner);
			if (current.end && end.end) {
				System.out.println("Done");
				return false;
			}

			closedSet.add(current);
			openSet.remove(current);

			ArrayList<Cell> neighbors = current.neighbors;
			for (int i = 0; i < neighbors.size(); i++) {
				Cell neighbor = neighbors.get(i);
				if (!closedSet.contains(neighbor) && !neighbor.wall) {
					int tempG = current.gCost + 1;
					boolean newPath = false;
					if (openSet.contains(neighbor)) {
						if (tempG < neighbor.gCost) {
							neighbor.gCost = tempG;
							newPath = true;
						}
					} else {
						neighbor.gCost = tempG;
						newPath = true;
						openSet.add(neighbor);
					}

					if (newPath) {
						neighbor.hCost = heuristic(neighbor, end);
						neighbor.fCost = neighbor.gCost + neighbor.hCost;
						neighbor.previous = current;
					}
				}
			}
			return true;
		} else {
			System.out.println("No Solution!");
			return false;
		}
	}
}