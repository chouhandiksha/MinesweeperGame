

/**
 * 
 */

/**
 * @author Admin dc4454
 *
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;






public class MinesweeperGame extends JFrame implements Serializable{

	
	public void initGame() {
		
		
		
        JLabel statusbar = new JLabel("");
        JMenuBar menubar = new JMenuBar();
        add(menubar, BorderLayout.NORTH);
        add(statusbar, BorderLayout.SOUTH);
		
		
		add(new MinesweeperGUI(statusbar, menubar));

	    setResizable(false);
	    pack();

	    setTitle("Minesweeper dc4454");
	    setLocationRelativeTo(null);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		EventQueue.invokeLater(() -> {

            var ex = new MinesweeperGame();
            ex.setVisible(true);
            ex.initGame();
        });

		
		
		
		
		

		
		
	}

}
