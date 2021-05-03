/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abalone;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Furkan ATAK
 */
public class MainGui extends JFrame{
    
	private BoardPanel boardPanel;
 	private CommPanel commPanel;
        
	MainGui(KeyListener theKeyListener, Rival rival, final Game game) { 
		setTitle("Abalone");
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu gameMenu = new JMenu();
		menuBar.add(gameMenu);
		gameMenu.setText("Game");
			
		JMenuItem quitMenuItem = new JMenuItem();
		gameMenu.add(quitMenuItem);
		quitMenuItem.setText("Quit");
		quitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				game.quit();
			}
		});	
		boardPanel = new BoardPanel(theKeyListener);
		commPanel = new CommPanel(rival);
		
		add(boardPanel);
		add(commPanel, BorderLayout.EAST);
		
		addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent e) {
				game.quit();
		  }
		});
		pack();
		setVisible(true);
	}
        // to reset oppponent player stone color
        public void resetComm(Rival r){
            commPanel.setPlayerImg(r);
            SwingUtilities.updateComponentTreeUI(this);
        }
	
	void showKickOut(Rival rival) {
		commPanel.showKickOut(rival);
	}

	void showWinner(String winner) {
		commPanel.showWinner(winner);
	}
	
	void drawPositionState(int boardPosition, Rival team, boolean selected, boolean hover, boolean oponentMoveHover) {
		boardPanel.drawPositionState(boardPosition, team, selected, hover, oponentMoveHover);
	}

	void clearBoard() {
		boardPanel.clearBoard();
	}

	void flushBoard() {
		boardPanel.flush();
	}
}
