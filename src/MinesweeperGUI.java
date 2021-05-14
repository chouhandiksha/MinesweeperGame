/*
 * *Creates the GUI components for Minesweeper Game
 * **Author ~ dc4454
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;





public class MinesweeperGUI extends JPanel {

    private final int NUM_IMAGES = 13;
    private final int CELL_SIZE = 15;

    private final int COVER_FOR_CELL = 10;
    private final int MARK_FOR_CELL = 10;
    private final int nullValCell = 0;
    private final int MINE_CELL = 9;
    private final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    private final int DRAW_MINE = 9;
    private final int DRAW_COVER = 10;
    private final int DRAW_MARK = 11;
    private final int DRAW_WRONG_MARK = 12;

    private final int N_MINES = 40;
    private final int N_ROWS = 16;
    private final int N_COLS = 16;

    private final int BOARD_WIDTH = N_COLS * CELL_SIZE + 1;
    private final int BOARD_HEIGHT = N_ROWS * CELL_SIZE + 1;

    private int[] field;
    private boolean inGame;
    private int minesLeft;
    
    private Image[] img;

    private int allCells;
    
    private final JLabel statusbar;
    private final JLabel timerbar;
   
    private boolean gameWon = false;
    
    
    //Menubar variables
    private JMenuBar menubar;
	private JMenu menuBarOptions;
	private JMenuItem menuNewGame, menuOpenGame, menuSaveGame, menuExit;
	private ArrayList<SavedGames> prevGames;
	
	//Pael variables
	private JFrame saveFrame, openGameFrame;
	private JPanel savePanel, editPanel, openGamePanel, loadGamePanel;
	private JButton btnSave;
	
	
	//Array of Buttons to retrieve old games
	private JButton[] btnOpenGame;
	
	//Server-client socket variables
	private Socket socket;
	private ObjectOutputStream clientOpStream;
	private ObjectInputStream clientIpStream;
	
	
	private static MinesweeperGame minesweepGameObj = new MinesweeperGame();
	

    public MinesweeperGUI(JLabel statusbar, JMenuBar menubar, JLabel timerbar) {
    	
    	this.timerbar = timerbar;
        this.statusbar = statusbar;
        this.menubar = menubar ;
        initBoard();
    }


	
	

    
    
    private void initBoard() {

    	
    	
    	
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        
        img = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {

            var path = "src/resources/" + i + ".png";
            img[i] = (new ImageIcon(path)).getImage();
        }

        addMouseListener(new MineHandler());
        addMenu();
        startGame();
        
    }
    
    
    
    
private void initBoardLoaded(String fields, String minesLeft, int points) {

    	
    	
    	
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        
        img = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {

            var path = "src/resources/" + i + ".png";
            img[i] = (new ImageIcon(path)).getImage();
        }

        addMouseListener(new MineHandler());
       // addMenu();
        loadedGame(fields, minesLeft);
        
        
    }

    

// Adds menu bar to the screen for various File options such New, Open, Save, Exit

    public void addMenu(){
		menuBarOptions = new JMenu("File");
		menuBarOptions.setMnemonic(KeyEvent.VK_F);
		menuBarOptions.getAccessibleContext().setAccessibleDescription("Game options");
		
		menubar.add(menuBarOptions);
		
		addOptionItems();

	}

    
// Adds menu options to the menu bar button
    
    public void addOptionItems(){

		menuNewGame = new JMenuItem("New");
		menuNewGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK)); // Handles keystroke ctrl+N for New
		menuNewGame.addActionListener(new MenuHandler());
		
		menuOpenGame = new JMenuItem("Open");
		menuOpenGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));  // Handles keystroke ctrl+O for Open
		menuOpenGame.addActionListener(new MenuHandler());
		
		menuSaveGame = new JMenuItem("Save");
		menuSaveGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));   // Handles keystroke ctrl+S for Save
		menuSaveGame.addActionListener(new MenuHandler());
		
		
		menuExit = new JMenuItem("Exit");
		menuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));   // Handles keystroke ctrl+X for Exit
		menuExit.addActionListener(new MenuHandler());

		
		menuBarOptions.add(menuNewGame);
		menuBarOptions.add(menuOpenGame);
		menuBarOptions.add(menuSaveGame);
		menuBarOptions.add(menuExit);
	}

    
    
    
    
    class MenuHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			Object source = event.getSource();

			if(source == menuNewGame){
				repaint();
				startGame();
			} else if(source == menuOpenGame){
				
				openSavedGameWindow();
				
			} else if ( source == menuSaveGame){
				saveGameWindow();
			} else if ( source == menuExit){
				menuExit();
			}
		}
	}
    
    
    
    
    
    private void startGame() {

        int cell;

        var random = new Random();
        inGame = true;
        minesLeft = N_MINES;

        allCells = N_ROWS * N_COLS;
        field = new int[allCells];

        for (int i = 0; i < allCells; i++) {

            field[i] = COVER_FOR_CELL;
        }

        statusbar.setText(Integer.toString(minesLeft));

        int i = 0;

        while (i < N_MINES) {

            int position = (int) (allCells * random.nextDouble());

            if ((position < allCells)
                    && (field[position] != COVERED_MINE_CELL)) {

                int current_col = position % N_COLS;
                field[position] = COVERED_MINE_CELL;
                i++;

                if (current_col > 0) {
                    cell = position - 1 - N_COLS;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position - 1;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }

                    cell = position + N_COLS - 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                }

                cell = position - N_COLS;
                if (cell >= 0) {
                    if (field[cell] != COVERED_MINE_CELL) {
                        field[cell] += 1;
                    }
                }

                cell = position + N_COLS;
                if (cell < allCells) {
                    if (field[cell] != COVERED_MINE_CELL) {
                        field[cell] += 1;
                    }
                }

                if (current_col < (N_COLS - 1)) {
                    cell = position - N_COLS + 1;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + N_COLS + 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                }
            }
        }
    }
    
    
    /*
     * Save Game helper inner class for connecting to socket and saving the game object. 
     */
    
    class SaveGameHelper implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			
			
			
			LocalDateTime currDateObj = LocalDateTime.now();
		    System.out.println("Before formatting: " + currDateObj);
		    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"); 

		    String gameCurrDate = currDateObj.format(dateFormat);
			
			System.out.println("Starting Connection...");
			try{
		          socket = new Socket("127.0.0.1", 5001);
		          clientOpStream = new ObjectOutputStream(socket.getOutputStream());
		          clientIpStream = new ObjectInputStream(socket.getInputStream());
		             
		          System.out.println("Established connection...");
		          int init = clientIpStream.read();
		          System.out.println(init);
		          
		          		         
		          int points=30; //Giving some value for now
		          
		          String  minesLeftArg=statusbar.getText().substring(0,2).trim();
		         
		          
		        
		          
		          
		          //int minesLeft = Integer.parseInt(temp);
		         
		         
		         
		          //minesLeftArg = Integer.parseInt(temp);
		          
		          
		          String f = "";
		          for(int x: field) {
		        	  f=f+x+" ";
		          }
		          
		          
		          
		          GameMinesweepData gmData = new GameMinesweepData(gameCurrDate, minesweepGameObj, points, f , minesLeftArg);
		          
		          //Code 200: to save data
		          clientOpStream.write(200);
		          clientOpStream.flush();
		          clientOpStream.writeObject(gmData);
		          clientOpStream.flush();

		          int res = clientIpStream.read();
		          System.out.println(res);
		          mkSavePopup(res);
		          
		          clientOpStream.close();
		          clientIpStream.close();
	              socket.close();
		          
		      }catch(IOException ex){
		          ex.printStackTrace();
		      }

			saveFrame.setVisible(false);
		}
	}
    
    
    /*
     * Get Timer value for score
     */
    

    
    public void mkSavePopup(int code){
		if (code == 201) {
			JOptionPane.showMessageDialog(new JPanel(), "Minesweeper game saved!");
		} else {
			JOptionPane.showMessageDialog(new JPanel(), "Some error occured, please try again");
		}
		
	}
    
    
 
    public void openSavedGameWindow(){
		openGameFrame = new JFrame();
		openGameFrame.setTitle("Open Minesweeper Game");
		openGameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		openGameFrame.setSize(400,750);
		openGameFrame.setResizable(true);

		openGamePanel = new JPanel(new BorderLayout());
		openGameFrame.getContentPane().add(openGamePanel);

		loadGamePanel = new JPanel();
		openGamePanel.add(loadGamePanel, BorderLayout.NORTH);
		loadGamePanel.setLayout(new BoxLayout(loadGamePanel,BoxLayout.Y_AXIS));
		
		prevGames = getPrevGames();
		btnOpenGame = new JButton[prevGames.size()];
		
		
		for (int i = 0; i < prevGames.size(); i++) {
			
			btnOpenGame[i] = new JButton(prevGames.get(i).getSavedGame());
			btnOpenGame[i].setHorizontalAlignment(SwingConstants.CENTER);
			btnOpenGame[i].setPreferredSize(new Dimension(400,30));
			btnOpenGame[i].setMaximumSize(new Dimension(400,30));
			btnOpenGame[i].addActionListener(new LoadHelper());
			
			loadGamePanel.add(btnOpenGame[i]);
		}

		openGameFrame.setVisible(true);
	}
    
   
    
    class LoadHelper implements ActionListener {

    	
		public void actionPerformed(ActionEvent event) {
			
			
			GameMinesweepData data = new GameMinesweepData();
			
			Object source = event.getSource();
			
			int id = -1;
			for(int i = 0; i < btnOpenGame.length; i++){
				if(source == btnOpenGame[i]){
					id = prevGames.get(i).getId();
					break;
				}
			}
			
			
			//Connect to server and get data
			System.out.println("Establishing Connection");
			try{
		          socket = new Socket("127.0.0.1", 5001);
		          clientOpStream = new ObjectOutputStream(socket.getOutputStream());
		          clientIpStream = new ObjectInputStream(socket.getInputStream());
		             
		          System.out.println("Connected...");
		          int init = clientIpStream.read();
		          System.out.println(init);
		          
		          //Code 204: to get particular saved data
		          clientOpStream.write(204);
		          clientOpStream.flush();
		          
		          clientOpStream.write(id);
		          clientOpStream.flush();
		          
		          data = (GameMinesweepData) clientIpStream.readObject();
		          System.out.println(data);
		          
		          clientOpStream.close();
		          clientIpStream.close();
	              socket.close();
		          
		      }catch(ClassNotFoundException | IOException ex){
		          ex.printStackTrace();
		      }
			
			openGameFrame.setVisible(false);
			
			minesweepGameObj = data.getGm();
			repaint();
			initBoardLoaded(data.getFields(),data.getMinesLeft(),23);
			
		}
		
		
	}
    
    
    
    public ArrayList<SavedGames> getPrevGames() {
    	
		ArrayList<SavedGames> lists = new ArrayList<>();
		
		
		System.out.println("Starting Connection...");
		try{
	          socket = new Socket("127.0.0.1", 5001);
	          clientOpStream = new ObjectOutputStream(socket.getOutputStream());
	          clientIpStream = new ObjectInputStream(socket.getInputStream());
	             
	          System.out.println("Connected...");
	          int init = clientIpStream.read();
	          System.out.println(init);
	          
	          //Code 202: to get list of saved data
	          clientOpStream.write(202);
	          clientOpStream.flush();
	          
	          lists = (ArrayList<SavedGames>) clientIpStream.readObject();
	          
	          clientOpStream.close();
	          clientIpStream.close();
              socket.close();
	          
	      }catch(ClassNotFoundException | IOException ex){
	          ex.printStackTrace();
	      }
		
		return lists;
	}
    
    
    
    public void menuExit(){

		int selection = JOptionPane.showConfirmDialog(new JPanel() ,"Would you like to exit the game?", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (selection == JOptionPane.YES_OPTION){
			System.exit(0);
		}
	}
    
    
    public void saveGameWindow(){
		 saveFrame = new JFrame();
		 saveFrame.setTitle("Save Minesweeper Game");
		 saveFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		 saveFrame.setSize(300,350);
		 saveFrame.setResizable(false);

		editPanel = new JPanel(new BorderLayout());
		saveFrame.getContentPane().add(editPanel);

		savePanel = new JPanel();
		editPanel.add(savePanel, BorderLayout.CENTER);
		savePanel.setLayout(new BoxLayout(savePanel,BoxLayout.X_AXIS));
		
		btnSave = new JButton("Save Game");
		btnSave.setHorizontalAlignment(SwingConstants.CENTER);
		btnSave.setPreferredSize(new Dimension(150,30));
		btnSave.setMaximumSize(new Dimension(150,30));
		btnSave.addActionListener(new SaveGameHelper());
		
		savePanel.add(btnSave);

		saveFrame.setVisible(true);
	}
    
    
    
    private void find_nullValCells(int j) {

        int current_col = j % N_COLS;
        int cell;

        if (current_col > 0) {
            cell = j - N_COLS - 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == nullValCell) {
                        find_nullValCells(cell);
                    }
                }
            }

            cell = j - 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == nullValCell) {
                        find_nullValCells(cell);
                    }
                }
            }

            cell = j + N_COLS - 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == nullValCell) {
                        find_nullValCells(cell);
                    }
                }
            }
        }

        cell = j - N_COLS;
        if (cell >= 0) {
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == nullValCell) {
                    find_nullValCells(cell);
                }
            }
        }

        cell = j + N_COLS;
        if (cell < allCells) {
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == nullValCell) {
                    find_nullValCells(cell);
                }
            }
        }

        if (current_col < (N_COLS - 1)) {
            cell = j - N_COLS + 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == nullValCell) {
                        find_nullValCells(cell);
                    }
                }
            }

            cell = j + N_COLS + 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == nullValCell) {
                        find_nullValCells(cell);
                    }
                }
            }

            cell = j + 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == nullValCell) {
                        find_nullValCells(cell);
                    }
                }
            }
        }

    }

    
    
    
    
    private void loadedGame(String fieldsString, String minesLeftArg) {

        int cell;

        var random = new Random();
        inGame = true;
        

        allCells = N_ROWS * N_COLS;
        field = new int[allCells];
        
        String[] fieldSplit = fieldsString.substring(0,fieldsString.length()-1).split(" ");
        int j = 0;
        for(String x:fieldSplit) {
        	field[j] = Integer.parseInt(x);
        	j += 1;
        }
        	
        minesLeft = Integer.parseInt(minesLeftArg);
        
        statusbar.setText(Integer.toString(minesLeft));
        
        int i = 0;

        while (i < minesLeft) {

            int position = (int) (allCells * random.nextDouble());

            if ((position < allCells)
                    && (field[position] != COVERED_MINE_CELL)) {

                int current_col = position % N_COLS;
                field[position] = COVERED_MINE_CELL;
                i++;

                if (current_col > 0) {
                    cell = position - 1 - N_COLS;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position - 1;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }

                    cell = position + N_COLS - 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                }

                cell = position - N_COLS;
                if (cell >= 0) {
                    if (field[cell] != COVERED_MINE_CELL) {
                        field[cell] += 1;
                    }
                }

                cell = position + N_COLS;
                if (cell < allCells) {
                    if (field[cell] != COVERED_MINE_CELL) {
                        field[cell] += 1;
                    }
                }

                if (current_col < (N_COLS - 1)) {
                    cell = position - N_COLS + 1;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + N_COLS + 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                }
            }
        }
    }
    
    
    
    @Override
    public void paintComponent(Graphics g) {

        int uncover = 0;

        for (int i = 0; i < N_ROWS; i++) {

            for (int j = 0; j < N_COLS; j++) {

                int cell = field[(i * N_COLS) + j];

                if (inGame && cell == MINE_CELL) {

                    inGame = false;
                }

                if (!inGame) {

                    if (cell == COVERED_MINE_CELL) {
                        cell = DRAW_MINE;
                    } else if (cell == MARKED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_WRONG_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                    }

                } else {

                    if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                        uncover++;
                    }
                }

                g.drawImage(img[cell], (j * CELL_SIZE),
                        (i * CELL_SIZE), this);
            }
        }

        if (uncover == 0 && inGame) {

            inGame = false;
            statusbar.setText("Game won");
            gameWon = true;

        } else if (!inGame) {
            statusbar.setText("Game lost");
            gameWon = false;
        }
    }
    
    
    
    
    
    

    private class MineHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {

            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            boolean doRepaint = false;

            if (!inGame) {

            	startGame();
                repaint();
            }

            if ((x < N_COLS * CELL_SIZE) && (y < N_ROWS * CELL_SIZE)) {

                if (e.getButton() == MouseEvent.BUTTON3) {

                    if (field[(cRow * N_COLS) + cCol] > MINE_CELL) {

                        doRepaint = true;

                        if (field[(cRow * N_COLS) + cCol] <= COVERED_MINE_CELL) {

                            if (minesLeft > 0) {
                                field[(cRow * N_COLS) + cCol] += MARK_FOR_CELL;
                                minesLeft--;
                                String msg = Integer.toString(minesLeft);
                                statusbar.setText(msg+" Mines Left");
                            } else {
                                statusbar.setText("No marks left");
                            }
                        } else {

                            field[(cRow * N_COLS) + cCol] -= MARK_FOR_CELL;
                            minesLeft++;
                            String msg = Integer.toString(minesLeft);
                            statusbar.setText(msg);
                        }
                    }

                } else {

                    if (field[(cRow * N_COLS) + cCol] > COVERED_MINE_CELL) {

                        return;
                    }

                    if ((field[(cRow * N_COLS) + cCol] > MINE_CELL)
                            && (field[(cRow * N_COLS) + cCol] < MARKED_MINE_CELL)) {

                        field[(cRow * N_COLS) + cCol] -= COVER_FOR_CELL;
                        doRepaint = true;

                        if (field[(cRow * N_COLS) + cCol] == MINE_CELL) {
                            inGame = false;
                        }

                        if (field[(cRow * N_COLS) + cCol] == nullValCell) {
                            find_nullValCells((cRow * N_COLS) + cCol);
                        }
                    }
                }

                if (doRepaint) {
                    repaint();
                }
            }
        }
    }
}
