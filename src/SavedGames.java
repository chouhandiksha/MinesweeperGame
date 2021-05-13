import java.io.Serializable;

public class SavedGames implements Serializable{

	private int id, points;
	
	private String gamedate;
	
	SavedGames(int id, String gamedate, int points) {
		this.id = id;
		this.gamedate = gamedate;
		this.points = points;
	}
	
	public String getSavedGame() {
		return "ID: " + id + ", Game Date&Time: " + gamedate + ", Points: " + points;
	}
	
	public int getId() {
		return id;
	}
}
