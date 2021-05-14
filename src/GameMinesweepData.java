/*
 * *Initializes details of game parameters such as Data&Time, Game object, Points, etc
 * **Author ~ dc4454
 */

import java.io.Serializable;



public class GameMinesweepData implements Serializable{

	
	private MinesweeperGame gm;
	private String gameDate;
	private int points;
	private String fields;
	private String minesLeft;
	
	
	
	GameMinesweepData( String gameDate, MinesweeperGame gm , int points, String fields, String minesLeft) {
		
		this.gameDate = gameDate;
		this.gm = gm;
		this.points = points;
		this.fields = fields;
		this.minesLeft = minesLeft;
		
	}
	
GameMinesweepData (){
		
	}
	
	
	/*
	 * Added Getter and Setter functions below for GameMinesweepData Class.
	 */


	public MinesweeperGame getGm() {
		return gm;
	}

	public void setGm(MinesweeperGame gm) {
		this.gm = gm;
	}

	public String getGameDate() {
		return gameDate;
	}

	public void setGameDate(String gameDate) {
		this.gameDate = gameDate;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getMinesLeft() {
		return minesLeft;
	}

	public void setMinesLeft(String minesLeft) {
		this.minesLeft = minesLeft;
	}

	
}
