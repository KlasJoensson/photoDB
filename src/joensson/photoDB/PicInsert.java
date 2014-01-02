/*******************************************************************************
 * Copyright (c) 2014  Klas Jönsson <klas.joensson@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package joensson.photoDB;
import javax.swing.*;
import java.io.IOException;
import java.sql.*;

/**
 * This class collects the data and insert it in the database.
 * 
 * @author Klas Jönsson
 */
public class PicInsert {
	private String fileName;
	private String path;	
	private int RAWexists;
	private String uses;
	private String date;
	private String[] description = new String[5];
	private Boolean more = true;
	private int index = 0;

	/**
	 * This is the main method of the insert function.
	 * To insert it uses two other methods, one to collect the data that are to be inserted
	 * and one to insert it in the database.
	 */
	public void insert() {
		getInfo();
		try{
			saveInfo();
		}
		catch (ClassNotFoundException x){
			JOptionPane.showMessageDialog(null, "Hittar inte JDBC drivrutinen", "Error", 2);
			return;
		}
		catch (SQLException x){
			JOptionPane.showMessageDialog(null, "Har stött på ngt kul SQL-fel\n:" + x.getMessage(), "Error", 2);
			return;
		}
		catch (IOException x){
			JOptionPane.showMessageDialog(null, "Har stött på ngt kul IO-fel\n:" + x.getMessage(), "Error", 2);
			return;
		}
		
	} // End method insert
	
	/**
	 * This is the method that collect the data that are to be inserted in to the database 
	 */
	private void getInfo(){
		String file;
		/* Uncomment if you want to start in the director that the program are executing from
		 * String dir = System.getProperty("user.dir"); 
		 * JFileChooser fc = new JFileChooser(dir);
		 */
		JFileChooser fc = new JFileChooser();
		int res = fc.showOpenDialog(null);
		if (res == JFileChooser.APPROVE_OPTION){
			fileName = fc.getSelectedFile().getName();
			path = fc.getSelectedFile().getAbsolutePath();
			file = path;
			// Cutting of the file name from the path
			int i = path.length()-1;
			//System.out.println(i+", "+path);
			while(path.charAt(i) != '/') {
				i--;
				if (i < 0) break;
			}
			if (i < 0){
				// If i < 0 then the OS may use "\" instead of "/" 
				i = path.length()-1;
				while(path.charAt(i) != '\\') {
					i--;
					if (i < 0) {
						// Base case: the file may be in the root director 
						i = 0;
						break;
					}
				}
			}
			path = path.substring(0, i);
		}
		else if (res == JFileChooser.ERROR_OPTION){
			JOptionPane.showMessageDialog(null, "Nu gjorde du nog ngt dumt.. Fy!", "Error", 0);
			return;
		}
		else
			return;
		
		// Ask if there exists a RAW- version of the picture 
		String[] options =  { "Nej, tyvärr", "Ja, givetvis"};
		RAWexists = JOptionPane.showOptionDialog(null, "Finns det en RAW fil av bilden",
				 "Info om bilden",JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE , null, options, 0);
		
		// Asks if there's a printed version of the image, and if there is what for it was made
		PrintedDialog pd = new PrintedDialog(null, KortDB.language.SWEDISH);	
		if (pd.ans == 0){
			// If the picture is printed, then we want to know the reason
			uses = pd.getText();				
			}
		if (pd.ans == 1){
			uses = null;
		}
		
		// Asks of the pictures date, e.g. when it was taken, inserted in the database...
		GetDateDialog gdd = new GetDateDialog(null, file, KortDB.language.SWEDISH);
		if (gdd.ans != -1){
			// When the user has decided the date we collect it
			date = gdd.getDate();
			}
		else
			// If the dialog is closed before the user has decided the date we set it to null
			date = null;
		
		// Then we want some word(s) to describe the image
		while (more){
			DescriptionDialog dd = new DescriptionDialog(null,"Skriv in ett ord som beskriver bilden!");	
			if (dd.ans == 0){
				// if its the last word we want to use to describe the image
				description[index] = dd.getText();
				more = false;
				}
			if (dd.ans == 1){
				// If we want more words to describe the image
				description[index] = dd.getText();
				more = true;
				index++;
				// If there are need for more words the the array are growing with five elements
				if (index == description.length){
					String[] temp = new String[description.length];
					temp = description;
					description = new String[temp.length + 5];
				}					
				}
			if (dd.ans == 2){
				// If we decide not to use that or any more words to describe the image
				description = null;
				more = false;
			}
		}
	} // End method getInfo
	
	/**
	 * This is the method that creates the SQL-statements and insert the data into the database
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void saveInfo()throws SQLException, IOException, ClassNotFoundException {
		// before we start to inserting we need to know the number of tuples in each table
		int id = 0;
		String query;
		// Loading the JDBC driver
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:Kort");
		
		// To control when the database commits
		conn.setAutoCommit(false);
		
		// Getting the highest id number 
		Statement s = conn.createStatement();		
		ResultSet r = s.executeQuery("SELECT MAX(id) FROM picture;");
		// Read the results of the query and add one to obtain the id of the next tuple 
		while (r.next()){
			id = r.getInt(1) + 1;
		}
		
		// Table picture
		// Creating the SQL-statement to insert the new tuple in the table
		query = "INSERT INTO picture VALUES ('" + date + "', " + id + ", '" + fileName + "','" + path + "'," + RAWexists + ");";
		// Inserting the data in to the table
		s.execute(query);
	    
	    // Table printed
	    // It's unnecessary to do anything if there's no value to insert...
	    if (uses != null){
			// Creating the SQL-statement to insert the new tuple in the table
			query = "INSERT INTO printed VALUES (" + id + ",'" + uses + "');";
			// Inserting the data in to the table
			s.execute(query);
	    }
	    
	    // Table description
	    // First we create the SQL-statements one for each word and
	    // put them in the same string.
	    for (int i = 0; i < description.length; i++){
	    	if (description[i] != null)
	    	s.execute("INSERT INTO description VALUES (" + id + ",'" + description[i] + "');");
	    }
		// And then we insert the data in to the table
	    //s.execute(query);
	    
		// Commit and the close the connections
	    conn.commit();
	    conn.setAutoCommit(true);
	    r.close();
	    conn.close();
	    
	    JOptionPane.showMessageDialog(null, "Datan är sparad i databasen" ,"Färdig", 1);
	} //End method saveInfo
	
} // End class PicInsert
