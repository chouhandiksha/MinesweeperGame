

/**
 * @author Admin dc4454
 * Creates the GUI components for Minesweeper Game
 *  
 *
 */

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
	
	
	private final int CELL_SIZE = 15;
    private final int TOTAL_IMG = 13;
    
    
    private final int COVER_FOR_CELL = 10;
    private final int MARK_FOR_CELL = 10;
    private final int nullValCell = 0;
    private final int MINE_CELL = 9;
    private final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    
  //initialising number of rows and columns 
    private final int TOTAL_NUM_ROWS = 16;
    private final int TOTAL_NUM_COLS = 16;
    
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    
    
    //initialising mines values
    private final int DRAW_MINE = 9;
    private final int TOT_NUM_MINE = 40;
    private final int DRAW_COVER = 10;
    private final int DRAW_MARK = 11;
    private final int DRAW_WRONG_MARK = 12;
    
    
    //Timer Start init value
    private final int INIT_TIME = 1000;   // timer start
   
    
  //Server-client socket variables
  	private Socket socket;
  	private ObjectOutputStream clientOpStream;
  	private ObjectInputStream clientIpStream;
    
    //Initialising board width and height
    private final int GAME_WIDTH = TOTAL_NUM_COLS * CELL_SIZE + 1;
    private final int GAME_HEIGHT = TOTAL_NUM_ROWS * CELL_SIZE + 1;
    
    //Timer variables initialised
    static int totaltime;
    static Timer timer;
    private int points;
    

    private int[] cellVals;
    private int totalNumCell;
    private boolean minesweeperCheck;
    private int minesLeft;
    
    private Image[] img;

    
    
    private final JLabel statusbar;
    private final JLabel timerbar;
   
    private boolean gameWon = false;
    
    
    //Menubar variables
    private JMenuBar menubar;
	private JMenu menuBarOptions;
	private JMenuItem menuNewGame, menuOpenGame, menuSaveGame, menuExit;
	private ArrayList<SavedGames> prevGames;
	
	//Panel variables
	private JFrame saveFrame, openGameFrame;
	private JPanel savePanel, editPanel, openGamePanel, loadGamePanel;
	private JButton btnSave;
	
	
	//Array of Buttons to retrieve old games
	private JButton[] btnOpenGame;
	
	
	
	
	private static MinesweeperGame minesweepGameObj = new MinesweeperGame();
	

    public MinesweeperGUI(JLabel statusbar, JMenuBar menubar, JLabel timerbar) {
    	
    	this.timerbar = timerbar;
        this.statusbar = statusbar;
        this.menubar = menubar ;
        points = INIT_TIME;
        initBoard();
    }


	
	
    /*
     * Initialising Game Layout for New Game
     */
    
    
    private void initBoard() {
    	
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        
        img = new Image[TOTAL_IMG];

        for (int i = 0; i < TOTAL_IMG; i++) {

            var path = "src/resources/" + i + ".png";
            img[i] = (new ImageIcon(path)).getImage();
        }

        addMouseListener(new MineHandler());
        addMenu();
        startGame();
        
        
    }
    
    
    /*
     * Initialising Game Layout for loaded Game
     */
    
    private void initBoardLoaded(String fields, String minesLeft, int prevPoints) {

    	
    	points = prevPoints;
    	totaltime = points;
    	
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        
        img = new Image[TOTAL_IMG];

        for (int i = 0; i < TOTAL_IMG; i++) {

            var path = "src/resources/" + i + ".png";
            img[i] = (new ImageIcon(path)).getImage();
        }

        addMouseListener(new MineHandler());
       
        loadedGame(fields, minesLeft);
        
        
    }

    

// Adds menu bar to the screen for various File options such New, Open, Save, Exit

    public void addMenu(){
		menuBarOptions = new JMenu("File");
		menuBarOptions.setMnemonic(KeyEvent.VK_F);
		menuBarOptions.getAccessibleContext().setAccessibleDescription("Game options");
		
		menubar.add(menuBarOptions);
		
		addOptList();

	}

    
// Adds menu options to the menu bar button
    
    public void addOptList(){

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

    
    
    //Checking option selected and action accordingly
    
    class MenuHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			Object source = event.getSource();

			if(source == menuNewGame){
				
				timer.cancel();
	            points = INIT_TIME;
				startGame();
				repaint();
			} else if(source == menuOpenGame){
				
				openSavedGameWindow();
				
			} else if ( source == menuSaveGame){
				saveGameWindow();
			} else if ( source == menuExit){
				menuExit();
			}
		}
	}
    
    
    
    
    // Start new minesweeper Game
    
    private void startGame() {
    	timer = new Timer();
    	timeHandler();
        int cell;

        var random = new Random();
        minesweeperCheck = true;
        minesLeft = TOT_NUM_MINE;

        totalNumCell = TOTAL_NUM_ROWS*TOTAL_NUM_COLS;
        cellVals = new int[totalNumCell];

        for (int i = 0; i < totalNumCell; i++) {

        	cellVals[i] =COVER_FOR_CELL;
        }

        statusbar.setText(Integer.toString(minesLeft));

        int i = 0;
        // go through while counter is less than 40
        while (i < TOT_NUM_MINE) {

            int indofTile = (int)(totalNumCell * random.nextDouble());

            if ((indofTile < totalNumCell)
                    && (cellVals[indofTile] != COVERED_MINE_CELL)) {

                int current_col = indofTile % TOTAL_NUM_COLS;
                cellVals[indofTile] =COVERED_MINE_CELL;
                
                i = i+1;
                // check for current column
                if (current_col > 0) {
                    cell = indofTile - 1 - TOTAL_NUM_COLS;
                    if (cell >= 0) {
                        if (cellVals[cell]!=COVERED_MINE_CELL) {
                        	cellVals[cell]+=1;
                        }
                    }
                    cell = indofTile - 1;
                    if (cell >= 0) {
                        if (cellVals[cell] != COVERED_MINE_CELL) {
                        	cellVals[cell] += 1;
                        }
                    }

                    cell = indofTile + TOTAL_NUM_COLS-1;
                    if (cell < totalNumCell) {
                        if (cellVals[cell]!=COVERED_MINE_CELL) {
                        	cellVals[cell]+=1;
                        }
                    }
                }

                cell=indofTile-TOTAL_NUM_COLS;
                if (cell >= 0) {
                    if (cellVals[cell]!=COVERED_MINE_CELL) {
                    	cellVals[cell]+=1;
                    }
                }

                cell=indofTile + TOTAL_NUM_COLS;
                if (cell < totalNumCell) {
                    if (cellVals[cell] != COVERED_MINE_CELL) {
                    	cellVals[cell] += 1;
                    }
                }

                if (current_col < (TOTAL_NUM_COLS - 1)) {
                    cell = indofTile - TOTAL_NUM_COLS + 1;
                    if (cell >= 0) {
                        if (cellVals[cell] != COVERED_MINE_CELL) {
                        	cellVals[cell] += 1;
                        }
                    }
                    cell = indofTile + TOTAL_NUM_COLS + 1;
                    if (cell < totalNumCell) {
                        if (cellVals[cell] != COVERED_MINE_CELL) {
                        	cellVals[cell] += 1;
                        }
                    }
                    cell = indofTile + 1;
                    if (cell < totalNumCell) {
                        if (cellVals[cell] != COVERED_MINE_CELL) {
                        	cellVals[cell] += 1;
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
		             
		          System.out.println("Established connection!");
		          int init = clientIpStream.read();
		          System.out.println(init);
		          
		        
		          
		          String  minesLeftArg=statusbar.getText().substring(0,2).trim();
				          
		          String f = "";
		          for(int x: cellVals) {
		        	  f=f+x+" ";
		          }
		          
		          
		          
		          GameMinesweepData gmData = new GameMinesweepData(gameCurrDate, minesweepGameObj, points, f , minesLeftArg);
		          
		          //this tells data is saved
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
     * Display pop up window for saved game
     */
    

    
    public void mkSavePopup(int code){
		if (code == 201) {
			JOptionPane.showMessageDialog(new JPanel(), "Minesweeper game saved!");
		} else {
			JOptionPane.showMessageDialog(new JPanel(), "Some error occured, please try again");
		}
		
	}
    

    
    
    /*
     * Open saved game window
     */
   
    public void openSavedGameWindow(){
		openGameFrame = new JFrame();
		openGameFrame.setTitle("Open Minesweeper Game");
		
		openGameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		openGameFrame.setSize(500,750);
		
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
			
			//Set size of the Panel
			btnOpenGame[i].setPreferredSize(new Dimension(400,30));
			btnOpenGame[i].setMaximumSize(new Dimension(400,30));
			
			//Load the game using load helper class
			btnOpenGame[i].addActionListener(new LoadHelper());
			
			loadGamePanel.add(btnOpenGame[i]);
		}

		openGameFrame.setVisible(true);
	}
    
   
    /*
     * Load Helper class loads the previously saved games instance
     */
    
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
		          
		          //creating client side input and output streams
		          clientOpStream = new ObjectOutputStream(socket.getOutputStream());
		          clientIpStream = new ObjectInputStream(socket.getInputStream());
		             
		          System.out.println("Connection Established!");
		          
		          int init = clientIpStream.read();
		          System.out.println(init);
		          
		          
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
			initBoardLoaded(data.getFields(),data.getMinesLeft(),data.getPoints());
			
		}
		
		
	}
    
    
    
    public ArrayList<SavedGames> getPrevGames() {
    	
		ArrayList<SavedGames> lists = new ArrayList<>();
		
		
		System.out.println("Connecting to the server");
		try{
	          socket = new Socket("127.0.0.1", 5001);
	          //Creating client side streams for input output data
	          clientOpStream = new ObjectOutputStream(socket.getOutputStream());
	          clientIpStream = new ObjectInputStream(socket.getInputStream());
	             
	          System.out.println("Connection Established!");
	          int init = clientIpStream.read();
	          System.out.println(init);
	          
	          // For getting list of data
	          clientOpStream.write(202);
	          clientOpStream.flush();
	          
	          //initialising list of saved games
	          lists = (ArrayList<SavedGames>) clientIpStream.readObject();
	          
	          //closing all stream before closing socket
	          clientOpStream.close();
	          clientIpStream.close();
              socket.close();
	          
	      }catch(ClassNotFoundException | IOException ex){
	          ex.printStackTrace();
	      }
		
		//return games list
		return lists;
	}
    
    /*
     * Exit game option
     */
    
    public void menuExit(){

		int selection = JOptionPane.showConfirmDialog(new JPanel() ,"You really like to exit the game?", "Prompt Exit Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (selection == JOptionPane.YES_OPTION){
			System.exit(0);
		}
	}
    
    
    
    /*
     * Display Game window for saving game.
     */
    public void saveGameWindow(){
		 saveFrame = new JFrame();
		 saveFrame.setTitle("Save Minesweeper Game");
		 saveFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		 saveFrame.setSize(400,350);
		 saveFrame.setResizable(false);

		editPanel = new JPanel(new BorderLayout());
		saveFrame.getContentPane().add(editPanel, BorderLayout.CENTER);

		savePanel = new JPanel();
		editPanel.add(savePanel, BorderLayout.CENTER);
		savePanel.setLayout(new BoxLayout(savePanel,BoxLayout.X_AXIS));
		
		btnSave = new JButton("Save Game!");
		btnSave.setHorizontalAlignment(SwingConstants.CENTER);
		
		btnSave.setPreferredSize(new Dimension(100,30));
		btnSave.setMaximumSize(new Dimension(150,30));
		btnSave.addActionListener(new SaveGameHelper());
		
		savePanel.add(btnSave);

		saveFrame.setVisible(true);
	}
    
    
    
    private void find_nullValCells(int nv) {

        int current_col = nv % TOTAL_NUM_COLS;
        int cell;

        if (current_col > 0) {
            cell = nv - TOTAL_NUM_COLS - 1;
            if (cell >= 0) {
                if (cellVals[cell] > MINE_CELL) {
                	cellVals[cell] -= COVER_FOR_CELL;
                    if (cellVals[cell] == nullValCell) {
                        find_nullValCells(cell);
                    }
                }
            }
            cell = nv - 1;
            if (cell >= 0) {
                if (cellVals[cell] > MINE_CELL) {
                	cellVals[cell] -= COVER_FOR_CELL;
                	
                    if (cellVals[cell] == nullValCell) {
                        find_nullValCells(cell);
                    }
                }
            }

            cell = nv + TOTAL_NUM_COLS - 1;
            if (cell < totalNumCell) {
                if (cellVals[cell] > MINE_CELL) {
                	cellVals[cell] -= COVER_FOR_CELL;
                    if (cellVals[cell] == nullValCell) {
                        find_nullValCells(cell);
                    }
                }
            }
        }

        cell = nv - TOTAL_NUM_COLS;
        if (cell >= 0) {
            if (cellVals[cell] > MINE_CELL) {
            	cellVals[cell] -= COVER_FOR_CELL;
                if (cellVals[cell] == nullValCell) {
                    find_nullValCells(cell);
                }
            }
        }

        cell = nv + TOTAL_NUM_COLS;
        if (cell < totalNumCell) {
            if (cellVals[cell] > MINE_CELL) {
            	cellVals[cell] -= COVER_FOR_CELL;
                if (cellVals[cell] == nullValCell) {
                    find_nullValCells(cell);
                }
            }
        }

        if (current_col < (TOTAL_NUM_COLS - 1)) {
            cell = nv - TOTAL_NUM_COLS + 1;
            if (cell >= 0) {
                if (cellVals[cell] > MINE_CELL) {
                	cellVals[cell] -= COVER_FOR_CELL;
                    if (cellVals[cell] == nullValCell) {
                        find_nullValCells(cell);
                    }
                }
            }

            cell = nv + TOTAL_NUM_COLS + 1;
            if (cell < totalNumCell) {
                if (cellVals[cell] > MINE_CELL) {
                	cellVals[cell] -= COVER_FOR_CELL;
                    if (cellVals[cell] == nullValCell) {
                        find_nullValCells(cell);
                    }
                }
            }
            cell = nv + 1;
            if (cell < totalNumCell) {
                if (cellVals[cell] > MINE_CELL) {
                	cellVals[cell] -= COVER_FOR_CELL;
                    if (cellVals[cell] == nullValCell) {
                        find_nullValCells(cell);
                    }
                }
            }
        }

    }

    
    // Resume game with fields data
    private void loadedGame(String fieldsString, String minesLeftArg) {
    	
    	
        int cell;
       
        // using random function for assigning mines to the tiles
        var random = new Random();
        minesweeperCheck = true;
        

        totalNumCell = TOTAL_NUM_ROWS * TOTAL_NUM_COLS;
        cellVals = new int[totalNumCell];
        
        String[] fieldSplit = fieldsString.substring(0,fieldsString.length()-1).split(" ");
        int j = 0;
        for(String x:fieldSplit) {
        	cellVals[j] = Integer.parseInt(x);
        	j += 1;
        }
        	
        minesLeft = Integer.parseInt(minesLeftArg);
        
        statusbar.setText(Integer.toString(minesLeft));
        
        int i = 0;

        while (i < minesLeft) {

            int indofTile = (int) (totalNumCell * random.nextDouble());

            if ((indofTile < totalNumCell)
                    && (cellVals[indofTile] != COVERED_MINE_CELL)) {

                int current_col = indofTile % TOTAL_NUM_COLS;
                cellVals[indofTile] = COVERED_MINE_CELL;
                i = i + 1;

                if (current_col > 0) {
                    cell = indofTile - 1 - TOTAL_NUM_COLS;
                    if (cell >= 0) {
                        if (cellVals[cell] != COVERED_MINE_CELL) {
                        	cellVals[cell] += 1;
                        }
                    }
                    cell = indofTile - 1;
                    if (cell >= 0) {
                        if (cellVals[cell] != COVERED_MINE_CELL) {
                        	cellVals[cell] += 1;
                        }
                    }

                    cell = indofTile + TOTAL_NUM_COLS - 1;
                    if (cell < totalNumCell) {
                        if (cellVals[cell] != COVERED_MINE_CELL) {
                        	cellVals[cell] += 1;
                        }
                    }
                }

                cell = indofTile - TOTAL_NUM_COLS;
                if (cell >= 0) {
                    if (cellVals[cell] != COVERED_MINE_CELL) {
                    	cellVals[cell] += 1;
                    }
                }

                cell = indofTile + TOTAL_NUM_COLS;
                if (cell < totalNumCell) {
                    if (cellVals[cell] != COVERED_MINE_CELL) {
                    	cellVals[cell] += 1;
                    }
                }

                if (current_col < (TOTAL_NUM_COLS - 1)) {
                    cell = indofTile - TOTAL_NUM_COLS + 1;
                    if (cell >= 0) {
                        if (cellVals[cell] != COVERED_MINE_CELL) {
                        	cellVals[cell] += 1;
                        }
                    }
                    cell = indofTile + TOTAL_NUM_COLS + 1;
                    if (cell < totalNumCell) {
                        if (cellVals[cell] != COVERED_MINE_CELL) {
                        	cellVals[cell] += 1;
                        }
                    }
                    cell = indofTile + 1;
                    if (cell < totalNumCell) {
                        if (cellVals[cell] != COVERED_MINE_CELL) {
                        	cellVals[cell] += 1;
                        }
                    }
                }
            }
        }
    }
    
    
    
    @Override
    public void paintComponent(Graphics g) {

        int openTile = 0;

        for (int i = 0; i < TOTAL_NUM_ROWS; i++) {

            for (int j = 0; j < TOTAL_NUM_COLS; j++) {

                int cell = cellVals[(i * TOTAL_NUM_COLS) + j];

                if (minesweeperCheck && cell == MINE_CELL) {

                    minesweeperCheck = false;
                }

                if (!minesweeperCheck) {

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
                        openTile++;
                    }
                }

                g.drawImage(img[cell], (j * CELL_SIZE),
                        (i * CELL_SIZE), this);
            }
        }

        if (openTile == 0 && minesweeperCheck) {

            minesweeperCheck = false;
            statusbar.setText("Game won");
            points = INIT_TIME;
            gameWon = true;

        } else if (!minesweeperCheck) {
            statusbar.setText("Game lost");
            timer.cancel();
            points = INIT_TIME;
            gameWon = false;
        }
    }
    
    
    /*
     * Handle timer function, to countdown from value 1000 and restart game after value 0 is encountered
     */
    
    public void timeHandler() {
    	
    	int delay = 1000;
        int period = 1000;
        
        totaltime = points;
        
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
           
            	String statusDetails = statusbar.getText().substring(0,2).trim();
            	int currentTimeCount = getTimerValue();
            	if(currentTimeCount <= 1) {
            		statusbar.setText("Game over, start New Game!");
            		points = INIT_TIME;
            		minesweeperCheck = false;
            		repaint();
            		
            	}
            	else {
            		points=currentTimeCount;
                statusbar.setText(statusDetails+" |Time Remaining: "+currentTimeCount);
            	}
            }
        }, delay, period);
        
    	
    }
    

    private static final int getTimerValue() {
        if (totaltime == 1) {
            timer.cancel();
            
        }
        return --totaltime;
    }
    
    
    private class MineHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {

            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            boolean needChange = false;

            if (!minesweeperCheck) {

            	startGame();
                repaint();
            }

            if ((x < TOTAL_NUM_COLS * CELL_SIZE) && (y < TOTAL_NUM_ROWS * CELL_SIZE)) {

                if (e.getButton() == MouseEvent.BUTTON3) {

                    if (cellVals[(cRow * TOTAL_NUM_COLS) + cCol] > MINE_CELL) {

                        needChange = true;

                        if (cellVals[(cRow * TOTAL_NUM_COLS) + cCol] <= COVERED_MINE_CELL) {

                            if (minesLeft > 0) {
                            	cellVals[(cRow * TOTAL_NUM_COLS) + cCol] += MARK_FOR_CELL;
                                minesLeft--;
                                String minesleftVal = Integer.toString(minesLeft);
                                statusbar.setText(minesleftVal+" Mines Left");
                            } else {
                                statusbar.setText("No marks left");
                            }
                        } else {

                        	cellVals[(cRow * TOTAL_NUM_COLS) + cCol] -= MARK_FOR_CELL;
                            minesLeft++;
                            String minesleftVal = Integer.toString(minesLeft);
                            statusbar.setText(minesleftVal);
                        }
                    }

                } else {

                    if (cellVals[(cRow * TOTAL_NUM_COLS) + cCol] > COVERED_MINE_CELL) {

                        return;
                    }

                    if ((cellVals[(cRow * TOTAL_NUM_COLS) + cCol] > MINE_CELL)
                            && (cellVals[(cRow * TOTAL_NUM_COLS) + cCol] < MARKED_MINE_CELL)) {

                    	cellVals[(cRow * TOTAL_NUM_COLS) + cCol] -= COVER_FOR_CELL;
                        needChange = true;

                        if (cellVals[(cRow * TOTAL_NUM_COLS) + cCol] == MINE_CELL) {
                            minesweeperCheck = false;
                        }

                        if (cellVals[(cRow * TOTAL_NUM_COLS) + cCol] == nullValCell) {
                            find_nullValCells((cRow * TOTAL_NUM_COLS) + cCol);
                        }
                    }
                }

                if (needChange) {
                    repaint();
                }
            }
        }
    }
}
