/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abalone;

import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Furkan ATAK
 */
public enum Rival implements Serializable {
	yellow(Color.white),
	pink(Color.black);
	
	Color color;	
	Rival(Color color) {
		this.color = color;
	}
}


