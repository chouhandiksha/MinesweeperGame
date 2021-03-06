
/**
 * @author Admin dc4454
 * After executing server (GameServer.java)--> Start code run from here.
 *  Main file
 *
 */

import java.awt.BorderLayout;

import java.awt.EventQueue;
import java.io.Serializable;
import javax.swing.JMenuBar;
import javax.swing.JFrame;
import javax.swing.JLabel;




public class MinesweeperGame extends JFrame implements Serializable{

	
	public void initGame() {
		
		
        JLabel statusbar = new JLabel("");
        JLabel timerbar = new JLabel("Timer: 1000");
        JMenuBar menubar = new JMenuBar();
        add(menubar, BorderLayout.NORTH);
        add(timerbar);
        add(statusbar, BorderLayout.SOUTH);
		
		
		add(new MinesweeperGUI(statusbar, menubar , timerbar));

	    setResizable(false);
	    pack();

	    setTitle("Minesweeper dc4454");
	    setLocationRelativeTo(null);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		EventQueue.invokeLater(() -> {

            var mGame = new MinesweeperGame();
            mGame.setVisible(true);
            mGame.initGame();
        });
		
		
	}

}
