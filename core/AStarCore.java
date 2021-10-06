package core;

import cell.Cell;

import java.awt.*;
import java.util.ArrayList;

public class AStarCore {
	public ArrayList<Cell> openSet = new ArrayList<>(), closedSet = new ArrayList<>();

	public Cell start, end, current;

	private int heuristic(Cell a, Cell b) {
		Point p = a.getPoint();
		Point q = b.getPoint();
		return (int) Point.distance(p.x, p.y, q.x, q.y);
	}

	public void draw(Graphics g) {
		for (Cell cell : openSet) {
			cell.color = new Color(143, 255, 232);
			cell.show(g);
		}

		for (Cell cell : closedSet) {
			cell.color = new Color(251, 168, 255);
			cell.show(g);
		}
		ArrayList<Cell> path = new ArrayList<>();
		Cell temp = current;
		path.add(temp);
		while (temp.previous != null) {
			path.add(temp.previous);
			temp = temp.previous;
		}
		for (Cell cell : path) {
			cell.color = new Color(248, 0, 104);
			cell.show(g);
		}
	}

	public boolean startAStar() {
		if (openSet.size() > 0) {
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
			for (Cell neighbor : neighbors) {
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
