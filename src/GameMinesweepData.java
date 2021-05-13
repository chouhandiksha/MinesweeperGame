import java.io.Serializable;



public class GameMinesweepData implements Serializable{

	
	private MinesweeperGame gm;
	private String gameDate;
	private int points;
	
	GameMinesweepData (){
		
	}
	
	GameMinesweepData( String gameDate, MinesweeperGame gm , int points) {
		
		this.gameDate = gameDate;
		this.gm = gm;
		this.points = points;
		
	}
	
	
	
	
	/*
	 * Getter and Setter functions below for GameMinesweepData Class.
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

	
}
