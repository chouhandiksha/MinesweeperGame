/*
 * *Creates Server for the Database socket connection
 * **Author ~ dc4454
 */

import java.io.*;
import java.net.*;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class GameServer {

	private Connection connection;

	public static void main(String args[]) {

		new GameServer().execute();
	}

	/*
	 * Connecting to database minesweeperfinaldata.db
	 */

	public void execute() {
		try {

			connection = DriverManager.getConnection("jdbc:sqlite:minesweeperfinaldata.db");

		} catch (SQLException e) {
			System.err.println("Error: " + e);
			System.exit(1);
		}

		System.out.println("Initialising");
		try {
			ServerSocket server = new ServerSocket(5001);
			System.out.println("Server Started ");

			/*
			 * After server started. Take care of output and input streams
			 */

			while (true) {

				Socket clientSocket = server.accept();

				ObjectInputStream serverInputStream = new ObjectInputStream(clientSocket.getInputStream());
				ObjectOutputStream serverOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

				System.out.println("connection established!");

				serverOutputStream.write(100);
				serverOutputStream.flush();

				int code = serverInputStream.read();

				if (code == 200) {
					GameMinesweepData field = (GameMinesweepData) serverInputStream.readObject();

					int retIdCode = saveData(field);

					serverOutputStream.write(retIdCode);
					serverOutputStream.flush();
				}

				if (code == 204) {
					int id = serverInputStream.read();
					GameMinesweepData data = retrieveMineData(id);
					serverOutputStream.writeObject(data);
					serverOutputStream.flush();
				}

				if (code == 202) {
					ArrayList<SavedGames> li = getSavedGameData();
					serverOutputStream.writeObject(li);
					serverOutputStream.flush();
				}

			}
		}
		catch (IOException e) {
			
			System.out.println(e.getCause());
			e.printStackTrace();
			
		} catch (ClassNotFoundException e) {
			
			System.out.println(e.getCause());
			e.printStackTrace();
			
		}
	}


	// Take id to fetch that game state
	
	public GameMinesweepData retrieveMineData(int id) {
		
		GameMinesweepData data = new GameMinesweepData();

		String sql = "Select gamedate, gameobj, points from minesweepertable where id = ?";
		System.out.println(id);
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, id);

			ResultSet resultSet = ps.executeQuery();
			resultSet.next();

			byte[] buf = resultSet.getBytes("gameobj");
			ObjectInputStream objectIn = null;
			if (buf != null)
				objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));

			data = (GameMinesweepData) objectIn.readObject();

			
			
			System.out.println("Game Date:" + data.getGameDate());
			System.out.println("Points:" + data.getPoints());
			resultSet.close();
			ps.close();

		} catch ( ClassNotFoundException | SQLException| IOException e) {
			e.printStackTrace();
		}

		return data;
	}
	
	
	public int saveData(GameMinesweepData data) {
		System.out.println("Date: " + data.getGameDate());
		System.out.println("GameData: " + data.getGm().toString());
		System.out.println("Points: " + data.getPoints());
	

		String sqlInsertQuery = "INSERT INTO minesweepertable(gamedate, gameobj, points) values(?,?,?)";
		try {
			PreparedStatement ps = connection.prepareStatement(sqlInsertQuery);

			ps.setString(1, data.getGameDate());

			ByteArrayOutputStream objByte = new ByteArrayOutputStream();

			ObjectOutputStream ops = new ObjectOutputStream(objByte);

			ops.writeObject(data);
			ops.close();
			ps.setBytes(2, objByte.toByteArray());
			ps.setInt(3, data.getPoints());

			int result = ps.executeUpdate();
			ps.close();

			return result > 0 ? 201 : 404;

		} catch ( SQLException | IOException e ) {
			e.printStackTrace();
			return 404;
		}
	}

	public ArrayList<SavedGames> getSavedGameData() {

		// Get saved game states
		ArrayList<SavedGames> lists = new ArrayList<SavedGames>();
		
		int id, score;
		String datetime;

		String sqlSelectQuery = "Select id, gamedate, points from minesweepertable";
		try {
			
			PreparedStatement ps = connection.prepareStatement(sqlSelectQuery);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {

				id = resultSet.getInt(1);
				datetime = resultSet.getString(2);
				score = resultSet.getInt(3);

				SavedGames savedGames = new SavedGames(id, datetime, score);
				System.out.println("Fetched: " + savedGames.getSavedGame());

				lists.add(savedGames);
			}

			resultSet.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return lists;
	}

	
}
