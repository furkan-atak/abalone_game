/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abalone;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pc
 */
public class Board implements Serializable{
    
	private Rival[] state = new Rival[Game.POSITIONS];

	void setTeam(Rival team, int position) {
		state[position] = team;
	}
	
	Rival getPlayer(int position) {
		return state[position];
	}
	Rival[] getState(){
            return this.state;
        }
	void setState(Rival[] state) {
		this.state = state;
	}
        private final static int[] rowLengths = new int[] {5, 6, 7, 8, 9, 8, 7, 6, 5};
	private final static int[] rowSums = new int[10];
	
	static {
		rowSums[0] = 0;
		for (int i = 0; i < rowLengths.length; i++) {
			rowSums[i + 1] = rowSums[i] + rowLengths[i];
		}
	}
	
	//to get neighbours
	static List<Integer> getNeighbours(int posArr) {
		List<Integer> neighbours = new ArrayList<Integer>();
		Point coords = getCoords(posArr);

		int increUp = -1;
		int increDown = 0;
		if (coords.y >= 4) {
			increDown = -1;
			if (coords.y > 4) {
				increUp = 0;
			}
		}
		int neighbourX = coords.x + increUp;
		int neighbourY = coords.y - 1;
		neighbours.add(getPosArray(neighbourX, neighbourY));
		
		neighbourX = coords.x + increUp + 1;
		neighbours.add(getPosArray(neighbourX, neighbourY));
		
		neighbourX = coords.x - 1;
		neighbourY = coords.y;
		neighbours.add(getPosArray(neighbourX, neighbourY));
		
		neighbourX = coords.x + 1;
		neighbours.add(getPosArray(neighbourX, neighbourY));
		
		neighbourX = coords.x + increDown;
		neighbourY = coords.y + 1;
		neighbours.add(getPosArray(neighbourX, neighbourY));
		
		neighbourX = coords.x + increDown + 1;
		neighbours.add(getPosArray(neighbourX, neighbourY));
		
		return neighbours;
	}
	
	static int getNeighbour(int posArr, Move move) {

		Point coords = getCoords(posArr);
		int incrUp = -1;
		int incrDown = 0;
		if (coords.y >= 4) {
			incrDown = -1;
			if (coords.y > 4) {
				incrUp = 0;
			}
		}
		
		int neighbourX = -1;
		int neighbourY = -1;
		switch (move) {
			case leftUpper: 
				neighbourX = coords.x + incrUp;
				neighbourY = coords.y - 1;
				break;
			case rightUpper:
				neighbourX = coords.x + incrUp + 1;
				neighbourY = coords.y - 1;
				break;
			case upper:
				neighbourX = coords.x - 1;
				neighbourY = coords.y;
				break;
			case right:
				neighbourX = coords.x + 1;
				neighbourY = coords.y;
				break;
			case leftDown:
				neighbourX = coords.x + incrDown;
				neighbourY = coords.y + 1;
				break;
			case rightDown:
				neighbourX = coords.x + incrDown + 1;
				neighbourY = coords.y + 1;
				break;
			}
		if (checkValidPos(neighbourX, neighbourY)) {
			return getPosArray(neighbourX, neighbourY);
		}
		return -1; 
	}
	
	static Point getCoords(int arrayPos) {
		Point coords = new Point();
		if (arrayPos < rowSums[1]) {
			coords.y = 0;
		} else if (arrayPos < rowSums[2]) {
			coords.y = 1;
		} else if (arrayPos < rowSums[3]) {
			coords.y = 2;
		} else if (arrayPos < rowSums[4]) {
			coords.y = 3;
		} else if (arrayPos < rowSums[5]) {
			coords.y = 4;
		} else if (arrayPos < rowSums[6]) {
			coords.y = 5;
		} else if (arrayPos < rowSums[7]) {
			coords.y = 6;
		} else if (arrayPos < rowSums[8]) {
			coords.y = 7;
		} else { //<= 60
			coords.y = 8;
		}
		coords.x = arrayPos - rowSums[coords.y];
		return coords;
	}
	
	static int getPosArray(int x, int y) {
		int rowsPart = rowSums[y];
		return rowsPart + x;
	}
	
	static List<Integer> getStraightLine(int arrayPos, Move move) {
		List<Integer> line = new ArrayList<Integer>();
		line.add(arrayPos);
		int neighbour = getNeighbour(arrayPos, move);
		while (neighbour != -1) {
			line.add(neighbour);
			neighbour = getNeighbour(neighbour, move);
		}
		return line;
	}
	
	static boolean checkValidPos(int x, int y) {
		if (x < 0 || y < 0 || y > 8 || x >= rowLengths[y]) {
			return false;
		}
		return true;
	}
}
