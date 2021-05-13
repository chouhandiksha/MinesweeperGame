/*
 * *Creates Server for the Database socket connection
 * **Author ~ dc4454
 */

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import javax.swing.JOptionPane;

public class GameServer {
  ArrayList clientOutputStreams;
  
  private Connection conn;
  
  public static void main(String args[]){
      new GameServer().go();
  }
  
  /**
   * server and handle requests
   */
  public void go(){
      clientOutputStreams = new ArrayList();
      
      try {
    	  conn = DriverManager.getConnection("jdbc:sqlite:minesweeperfinaldata.db");

      } catch (SQLException e) {
    	  System.err.println("Connection error: " + e);
    	  System.exit(1);
      }
      
      System.out.println("Starting Server...");
      try{
          ServerSocket server = new ServerSocket(5001);
          System.out.println("Server Started...");
          while(true){
        	  
              Socket clientSocket = server.accept();
              
              ObjectInputStream serverInputStream = new ObjectInputStream(clientSocket.getInputStream());
              ObjectOutputStream serverOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
     
              System.out.println("Client Connected...");
              
              serverOutputStream.write(100);
              serverOutputStream.flush();
              
              int code = serverInputStream.read();
              if (code == 200) {
            	  GameMinesweepData data = (GameMinesweepData) serverInputStream.readObject();
            	  
            	  int returnCode = saveData(data);
            	  
            	  serverOutputStream.write(returnCode);
            	  serverOutputStream.flush();
              }
              
              if (code == 202) {
            	  ArrayList<SavedGames> list = retriveData();
            	  
            	  serverOutputStream.writeObject(list);
            	  serverOutputStream.flush();
              }
              
              if (code == 204) {
            	  int id = serverInputStream.read();
            	  
            	  GameMinesweepData data = retrieveMineData(id);
            	  
            	  serverOutputStream.writeObject(data);
            	  serverOutputStream.flush();
              }
              
          }
      }catch(IOException e){
          System.out.println("Cause1"+e.getCause());
          e.printStackTrace();
      }catch(ClassNotFoundException e){
          System.out.println("Cause2"+e.getCause());
          e.printStackTrace();
      }
  }

  public int saveData(GameMinesweepData data) {
	  System.out.println("Date: "+data.getGameDate());
	  System.out.println("GameData: "+data.getGm().toString());
	  System.out.println("Points: "+data.getPoints());
	  //Add JDBC code to save to database
	  
	  String sql = "INSERT INTO minesweepertable(gamedate, gameobj, points) values(?,?,?)";
	   try {
		   PreparedStatement ps = conn.prepareStatement(sql);
		 
		   ps.setString(1, data.getGameDate());
	
		   
		   //Saving object data
		   
		   ByteArrayOutputStream objByte = new ByteArrayOutputStream();
		   
		   ObjectOutputStream ops = new ObjectOutputStream(objByte);
		   
		   ops.writeObject(data);
		   ops.close();
		   ps.setBytes( 2, objByte.toByteArray());
		   ps.setInt( 3, data.getPoints());
		   
		   
		   
		   
		   int res = ps.executeUpdate();
		   ps.close();
		   
		   return res>0?201:404;
		   
		   
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return 404;
		}
  }
  
  
  public ArrayList<SavedGames> retriveData() {
	  
	  //Get saved game states
	  ArrayList<SavedGames> lists = new ArrayList<SavedGames>();
	  int id, score ;
	  String datetime;
	  
	  String sql = "Select id, gamedate, points from minesweepertable";
	   try {
		   PreparedStatement ps = conn.prepareStatement(sql);
		   ResultSet rset = ps.executeQuery();
		   
		   while (rset.next()) {
			   id = rset.getInt(1);
			   
			   datetime = rset.getString(2);
			   score = rset.getInt(3);
			   
			   
			   SavedGames savedGames = new SavedGames(id, datetime, score);
			   System.out.println("Fetched: " + savedGames.getSavedGame());
			   
			   lists.add(savedGames);
		   }
		   
		   rset.close();
		   ps.close();
		   
		} catch (SQLException e) {
			e.printStackTrace();
		}
	  
	  return lists;
  }
  
  
  public GameMinesweepData retrieveMineData(int id) {
	  //Use id to fetch that game state
	  GameMinesweepData data = new GameMinesweepData();
	  
	  String sql = "Select gamedate, gameobj, points from minesweepertable where id = ?";
	  System.out.println(id);
	   try {
		   PreparedStatement ps = conn.prepareStatement(sql);
		   ps.setInt(1, id);

		   ResultSet rset = ps.executeQuery();
		   rset.next();
		   
		   byte[] buf = rset.getBytes("gameobj");
		   ObjectInputStream objectIn = null;
		   if (buf != null)
			   objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
		   
		   data = (GameMinesweepData) objectIn.readObject();
		   
		   
		   System.out.println("Game Date:"+data.getGameDate());
     	   System.out.println("Points:"+data.getPoints());
		   
		   rset.close();
		   ps.close();
		   
		} catch (IOException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	  
	  return data;
  }
}
