/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abalone;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Furkan ATAK
 */
public class LoginFrame extends JFrame {
    JButton start;

    public static void main(String[] args) {
        LoginFrame frame = new LoginFrame();
    }

    LoginFrame() {
        setTitle("Abalone Game");

        JLabel imageLabel = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("img/abalone1.jpg")));


        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new FlowLayout());
        colorPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        
        Dimension buttonDimens = new Dimension(100, 100);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setSize(300, 300);
        buttonPanel.setPreferredSize(buttonDimens);
        buttonPanel.setMaximumSize(buttonDimens);
        buttonPanel.setMinimumSize(buttonDimens);
        
        Dimension titlePaneDimens = new Dimension(30, 30);
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setSize(titlePaneDimens);
        titlePanel.setPreferredSize(titlePaneDimens);
        titlePanel.setMaximumSize(titlePaneDimens);
        titlePanel.setMinimumSize(titlePaneDimens);
        
        start = new JButton("Let's Start!");
        start.setFont(new Font("Serif", Font.PLAIN, 20));
        JLabel title = new JLabel("                          "
           + " Abalone Game");
        title.setFont(new Font("Serif", Font.PLAIN, 40));
        
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
                Game game = new Game(Rival.yellow);
            }
        });
        
        titlePanel.add(title);
        buttonPanel.add(start);
        add(titlePanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(imageLabel, BorderLayout.NORTH);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        pack();
        setVisible(true);
    }
    
}