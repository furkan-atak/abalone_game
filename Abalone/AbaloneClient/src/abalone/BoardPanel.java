/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abalone;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
/**
 *
 * @author Pc
 */
public class BoardPanel extends JPanel {

	private static final int spaceBetweenPoses = 5;
	static final int side = 350;
	private int diameter;
	
	//Buffer to calculate mapping boardposition -> screenCoords just 1 time
	private Point[] screenCoords = new Point[61];
	
	private static final Color BG_COLOR = new Color(64,224,208);
	private static final Color BORDER_COLOR = Color.black;
	private static final Color NONE_COLOR = new Color(0.7f, 0.7f, 0.7f);
	private static final Color SELECTED_COLOR = new Color(1f, 0.4f, 0.4f, 0.7f);
	private static final Color HOVER_COLOR = new Color(173,216,230);
	private static final Color ENEMY_MOVE_COLOR = new Color(0,0,128);

	private Image emptyBoardImage;
	private Image gameBoardImage;
	
	private Image pinkStoneImg = new ImageIcon(getClass().getClassLoader().getResource("img/pink.png")).getImage();
	private Image yellowStoneImg = new ImageIcon(getClass().getClassLoader().getResource("img/yellow.png")).getImage();
	
	BoardPanel(KeyListener theKeyListener) {
		
		initEmptyBoardImage();
		initGameBoardImage();
		
		setFocusable(true);
		requestFocus();
		
		Dimension dimens = new Dimension(side, side);
		setPreferredSize(dimens);
		setMinimumSize(dimens);
		setMaximumSize(dimens);
		setSize(dimens);
		
		addKeyListener(theKeyListener);
	}
	
	private void initGameBoardImage() {
		gameBoardImage = createBufferedImage(side, side);
		clearBoard();
	}

	private void initEmptyBoardImage() {

		//total spaces number of longest row
		int nineSpace = spaceBetweenPoses * 10;
		
		//diameter of circles calculation
		diameter = (Math.round((float)(side - nineSpace) / 9));
		
		emptyBoardImage = createBufferedImage(side, side);
		Graphics boardGraphics = emptyBoardImage.getGraphics();
		boardGraphics.setColor(BG_COLOR);
		boardGraphics.fillRect(0, 0, side, side);
		boardGraphics.setColor(BORDER_COLOR);
		
		//x, y coords from each circle to draw
		int x = spaceBetweenPoses;
		int y = spaceBetweenPoses;
		
		//offset for odd rows
		int offset = diameter / 2;
		
		//start row length
		int circlesInRow = 5;
		
		//index in circle buffer
		int circleCount = 0;
		
		//helper vars
		byte incr = -1;
		byte incrCircles = 1;
		int ecount = 2;
		int ocount = 1;
		
		//rows
		for (int j = 0; j < 9; j++) {	
			if (j % 2 == 0) {
				x = ecount * diameter + (ecount + 1) * spaceBetweenPoses;
				ecount = ecount + incr;
			} else {
				x = ocount * diameter + (ocount + 1) * spaceBetweenPoses + offset;
				ocount = ocount + incr;
			}
			
			for (int i = 0; i < circlesInRow; i++) {
				boardGraphics.drawOval(x, y, diameter, diameter);
				screenCoords[circleCount] = new Point(x, y);
				circleCount++;
				x = x + diameter + spaceBetweenPoses;
			}
			
			//decrement circle amount from 4th row
			if (j == 4) {
				incrCircles = -1;
				ecount += 2;
				ocount++;
				incr = 1;
			}
			circlesInRow += incrCircles;
			
			y = y + diameter + spaceBetweenPoses;
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(gameBoardImage, 0, 0, null);
	}

	private Image createBufferedImage(int width, int height) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
		GraphicsDevice gs = ge.getDefaultScreenDevice(); 
		GraphicsConfiguration gc = gs.getDefaultConfiguration(); 
		return gc.createCompatibleImage(width, height, Transparency.OPAQUE); 
	}
	
	private Point getScreenCoords(int boardPosition) {
		return screenCoords[boardPosition];
	}
	
	void drawPositionState(int boardPosition, Rival riv, boolean selected, boolean hover, boolean oponentMoveHover) {
		if (selected && hover) {
			System.out.println("BoardPanel.drawPositionState() warning - selected and hover true");
		}
		Point screenCoords = getScreenCoords(boardPosition);
		Graphics g = gameBoardImage.getGraphics();

		drawPosition(g, screenCoords, riv);
		
		if (selected) {
			g.setColor(SELECTED_COLOR);
			g.fillOval(screenCoords.x, screenCoords.y, diameter, diameter);
		}
		if (hover) {
			g.setColor(HOVER_COLOR);
			g.fillOval(screenCoords.x, screenCoords.y, diameter, diameter);
		}
		if (oponentMoveHover) {
			g.setColor(ENEMY_MOVE_COLOR);
			g.fillOval(screenCoords.x, screenCoords.y, diameter, diameter);
		}
	}
	
	private void drawPosition(Graphics g, Point coords, Rival riv) {
		if (riv == null) {
			g.setColor(NONE_COLOR);
			g.fillOval(coords.x, coords.y, diameter, diameter);
			g.setColor(BORDER_COLOR);
			g.drawOval(coords.x, coords.y, diameter, diameter);
		} else {
			Image marbleImage;
			if (riv == Rival.pink) {
				marbleImage = pinkStoneImg;
			} else {
				marbleImage = yellowStoneImg;
			}
			g.drawImage(marbleImage, coords.x, coords.y, null);
		}
	}

	void flush() {
		repaint();
		revalidate();
	}
	
	void clearBoard() {
		Graphics g = gameBoardImage.getGraphics();
		g.drawImage(emptyBoardImage, 0, 0, null);
	}
}

class CommPanel extends JPanel{
    
	private OutPanel kickedPanel;
	private static final int WIDTH = 125;
	private static final Color BG_COLOR = new java.awt.Color(255,140,0); 
	private JLabel winnerLabel;
	private JPanel playerPanel;
	CommPanel(Rival rival) {
		setBackground(BG_COLOR);
		Dimension infoPanelDimension = new Dimension(WIDTH, BoardPanel.side);
		setPreferredSize(infoPanelDimension);
		BoxLayout boxLayout = new BoxLayout(this, javax.swing.BoxLayout.Y_AXIS);
		setLayout(boxLayout);
		
		Border line = BorderFactory.createMatteBorder(0, 2, 0, 0, new java.awt.Color(255,215,0));
		setBorder(line);
		
		playerPanel = new JPanel();
		playerPanel.setLayout(new FlowLayout());
		playerPanel.setBackground(new java.awt.Color(0,0,139));
		Dimension playerPanelDim = new Dimension(125, 35);
		playerPanel.setSize(playerPanelDim);
		playerPanel.setPreferredSize(playerPanelDim);
		playerPanel.setMaximumSize(playerPanelDim);
		playerPanel.setMinimumSize(playerPanelDim);
                
		Dimension howToDimension = new Dimension(150, 65);
                JPanel howToPanel = new JPanel();
                howToPanel.setLayout(new FlowLayout());
                howToPanel.setSize(howToDimension);
		howToPanel.setPreferredSize(howToDimension);
		howToPanel.setMaximumSize(howToDimension);
		howToPanel.setMinimumSize(howToDimension);
		ImageIcon rivalImg;
		if (rival == Rival.yellow) {
			 rivalImg = new ImageIcon(getClass().getClassLoader().getResource("img/yellow1.png"));
		} else {
			 rivalImg = new ImageIcon(getClass().getClassLoader().getResource("img/pink1.png"));
		}
                JLabel playerStr = new JLabel("Player: ");
                playerStr.setForeground(Color.WHITE);
		playerPanel.add(playerStr);
		playerPanel.add(new JLabel(rivalImg));
                JLabel howTo1 = new JLabel("\"Space\" For Selection");
                JLabel howTo2 = new JLabel("W,A,D,X,C");
                JLabel howTo3 = new JLabel("Is Stand For Move");
                
                howToPanel.add(howTo1);
                howToPanel.add(howTo2);
                howToPanel.add(howTo3);
		
		kickedPanel = new OutPanel();
		
		winnerLabel = new JLabel();
		winnerLabel.setForeground(Color.white);
		winnerLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		add(howToPanel);
		add(playerPanel);
		add(kickedPanel);
		add(winnerLabel);
	}
	public void setPlayerImg(Rival rival){
             playerPanel.remove(1);
             ImageIcon rivalImg;
		if (rival == Rival.yellow) {
			 rivalImg = new ImageIcon(getClass().getClassLoader().getResource("img/yellow1.png"));
		} else {
			 rivalImg = new ImageIcon(getClass().getClassLoader().getResource("img/pink1.png"));
		}
                playerPanel.add(new JLabel(rivalImg), 1);
                playerPanel.revalidate();
                playerPanel.repaint();
        }
	
	void showKickOut(Rival rival) {
		kickedPanel.showOut(rival);
	}

	void showWinner(String theTeam) {
		winnerLabel.setText(theTeam + " wins!");
		repaint();
		revalidate();
	}
}
class OutPanel extends JPanel {
	
	private static final Color BG_COLOR = Color.lightGray;
	
	private final Image yellowOutImg = new ImageIcon(getClass().getClassLoader().getResource("img/yellowOut.png")).getImage();
	private final Image pinkOutImg = new ImageIcon(getClass().getClassLoader().getResource("img/pinkOut.png")).getImage();
	private int yellowOut;
	private int pinkOut;
	
	private int diameter = 15;
	private int spaceX = 5;
	private int spaceY = 7;
	
	OutPanel() {
		setBackground(BG_COLOR);
		Dimension dimension = new Dimension(250, 50);
		setSize(dimension);
		setPreferredSize(dimension);
		setMaximumSize(dimension);
		setMinimumSize(dimension);
	}
	
	public void showOut(Rival rival) {
		if (rival == Rival.yellow) {
			yellowOut++;
		} else {
			pinkOut++;
		}
		repaint();
		revalidate();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int x = spaceX;
		int y = spaceY;
		
		g.setColor(Rival.yellow.color);
		for (int i = 0; i < yellowOut; i++) {
			g.drawImage(yellowOutImg, x, y, null);
			x += diameter + spaceX;
		}
		x = spaceX;
		y += diameter + spaceY;
			
		g.setColor(Rival.pink.color);
		for (int i = 0; i < pinkOut; i++) {
			g.drawImage(pinkOutImg, x, y, null);
			x += diameter + spaceX;
		}
	}

	void refresh() {
		repaint();
		revalidate();
	}
}