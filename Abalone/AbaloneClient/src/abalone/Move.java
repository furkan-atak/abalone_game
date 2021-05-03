/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abalone;

/**
 *
 * @author Pc
 */
public enum Move {
    leftUpper, rightUpper, right, rightDown, leftDown, upper;
	
	Move opposite() {
		switch(this) {
			case leftUpper : return Move.rightDown;
			case rightUpper : return Move.leftDown;
			case upper : return Move.right;
			case right: return Move.upper;
			case leftDown: return Move.rightUpper;
			case rightDown: return Move.leftUpper;
		}
	return null;
	}
}
