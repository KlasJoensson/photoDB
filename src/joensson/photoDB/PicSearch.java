/*******************************************************************************
 * Copyright (c) 2014  Klas Jönsson <klas.joensson@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package joensson.photoDB;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.io.*;

/**
 * This is the class that are used when a there's a search in the database.
 * 
 *  @author Klas Jönsson
 */
public class PicSearch extends JFrame implements ActionListener{
	
	// Just some variables that are needed
	private int length = 5;
	private String[] alt = {" ", "OCH", "ELLER", "INTE", "DATUM" , "F\u00D6RE", "EFTER", 
			"MELLAN"};
	private String[] alt2 = {" ", "DATUM" , "F\u00D6RE", "EFTER", "MELLAN"};
	private String op1, op2, op3, key1, key2, key3, DB;
	private Boolean searchName = true, contains = false;
	
	// Create the stuff needed for this dialogs
	// The stuff to the search dialog
	private JLabel text1 = new JLabel();
	private JLabel text2 = new JLabel();
	private JTextField opt1 = new JTextField(10);
	private JTextField opt2 = new JTextField(10);
	private JTextField opt3 = new JTextField(10);
	private JButton search = new JButton();
	private JButton exi = new JButton();
	private JCheckBox name = new JCheckBox(" ", false);
	private JCheckBox cont = new JCheckBox(" ", false);
	private JLabel dummie = new JLabel();
	
	private JComboBox option1 = new JComboBox(alt2);
	private JComboBox option2 = new JComboBox(alt);
	private JComboBox option3 = new JComboBox(alt);
	private JTextField noResults = new JTextField("" + length, 3);
	
	private String driverErr, SQLErr, IOErr, noPicFoundTxt, titleTxt;
	private KortDB.language useLanguage;
	private ArrayList<Picture> searchResults;
	
	/**
	 * The constructor. It puts the different components in theirs places. 
	 */
	public PicSearch() {
		// Create something to put stuff in
		Container c = getContentPane();
		c.setLayout(new GridLayout(8,2));

		// Add the stuff to that something
		c.add(text1); c.add(dummie);
		c.add(option1); c.add(opt1); 
		c.add(option2); c.add(opt2); 
		c.add(option3); c.add(opt3); 
		c.add(name); c.add(cont);
		c.add(text2); c.add(noResults);
		c.add(search); c.add(exi);
		
		// Connects the buttons with there listener
		search.addActionListener(this);
		exi.addActionListener(this);
		option1.addActionListener(this);
		option2.addActionListener(this);
		option3.addActionListener(this);
		opt1.addActionListener(this);
		opt2.addActionListener(this);
		opt3.addActionListener(this);
		name.addActionListener(this);
		cont.addActionListener(this);
		noResults.addActionListener(this);
	}
	
	/**
	 * This method set the properties of the components and the window, 
	 * e.g. theirs text and sizes.   
	 */
	public void searchDialog(String useThisDB, KortDB.language l){
		DB = useThisDB;
		useLanguage = l;
		if (useLanguage == KortDB.language.ENGLISH)
			english();
		else if (useLanguage == KortDB.language.GERMAN)
			german();
		else if (useLanguage == KortDB.language.FRENCH)
			french();
		else
			swedish();
		// Set the window properties 
		pack();
		// Center the window
		// Get the default toolkit
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		// Get the current screen size
		Dimension scrnsize = toolkit.getScreenSize();
		// Get the current window size
		Dimension winSize = getSize();
		// Calculate the the coordinates
		int y = (scrnsize.height/2) - (winSize.height/2);
		int x = (scrnsize.width/2) - (winSize.width/2);		
		// Place the window in the center
		setLocation(x, y);
		setTitle(titleTxt);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	} // End of method searchDialog
	
	
	
	/**
	 * This method controls what happens when an event happens.
	 * 
	 * @param ActionEvent
	 */
	public void actionPerformed(ActionEvent e){
		
		Object S = e.getSource();
		if (S == exi)
			dispose();
		else if (S == search){
			// I need to get this things here, 'cos otherwise you have to press 'enter'
			// after each change in the different fields 
			key1 = opt1.getText();
			key2 = opt2.getText();
			key3 = opt3.getText();
			length = Integer.parseInt(noResults.getText());
			op1 = (String) option1.getSelectedItem();
			op2 = (String) option2.getSelectedItem();
			op3 = (String) option3.getSelectedItem();
			// And now we can call the method that do the searching
			searchDB();
		}
		else if (S == option1)
			op1 = (String) option1.getSelectedItem();
		else if (S == option2)
			op2 = (String) option2.getSelectedItem();
		else if (S == option3)
			op3 = (String) option3.getSelectedItem();
		else if (S == opt1){
			key1 = opt1.getText();
		}
		else if (S == opt2)
			key2 = opt2.getText();
		else if (S == opt3)
			key3 = opt3.getText();
		else if (S == name)
			searchName = !searchName;
		else if (S == cont)
			contains = !contains;
		else if (S == noResults)
			length = Integer.parseInt(noResults.getText());
	} // End of method actionPerformed
	
	/**
	 * This method handles the search with a little help from three other methods.
	 * The first creates the query, the second ask the database the query and the last 
	 * presents the results of it.
	 */
	private void searchDB(){
		// Call the method that create the query 
		String query = createQuery();
		
		//Uncomment the next line to print the query in the console
		//System.out.println(query);
		
		// Ask the database the query
		//String[] answer = new String[length];
		try{
			askQuery(query, length);
		}
		catch (ClassNotFoundException x){
			JOptionPane.showMessageDialog(null, driverErr, "Error", 2);
			return;
		}
		catch (SQLException x){
			JOptionPane.showMessageDialog(null, SQLErr + x.getMessage(), "Error", 2);
			return;
		}
		catch (IOException x){
			JOptionPane.showMessageDialog(null, IOErr + x.getMessage(), "Error", 2);
			return;
		}
				
		// Call the dialog that view the result
		results();
		
	} // End of method searchDB
	
	/**
	 * This method creates a SQL-statement that search the database.
	 *  
	 * @return A String containing the query  
	 */
	private String createQuery() {
		String query = createSelectStatement(key1, op1);

		if (option2.getSelectedIndex() != 0) {

			if (option2.getSelectedIndex() == 2)
				query += " UNION " + createSelectStatement(key2, op1);
			else if (option2.getSelectedIndex() == 3)
				query += " EXCEPT " + createSelectStatement(key2, op1);
			else 
				query += " INTERSECT " + createSelectStatement(key2, op1);
		}
		
		if (option3.getSelectedIndex() != 0) {
			System.out.println("op3");	
			if (option3.getSelectedIndex() == 2)
				query += " UNION " + createSelectStatement(key3, op2);
			else if (option3.getSelectedIndex() == 3)
				query += " EXCEPT " + createSelectStatement(key3, op2);
			else 
				query += " INTERSECT " + createSelectStatement(key3, op1);
		}
		return query;
	} // End of method createQuery
	
	/**
	 * This method is a help method that creates the select-statement 
	 *  the String array alt is the same as the one used to create the
	 *  menu in the JComboBoxes.
	 * 
	 * @param key Is the key word that we search for or the date
	 * @param arg Is the argument from the JComboBox
	 * @return A String with the select-statement
	 */
	private String createSelectStatement(String key, String arg) {
		String statement = "SELECT DISTINCT * " +
		"FROM picture AS p, description AS d WHERE";

		if (arg.equals(alt[4]))
			statement += " p.date='" + key + "'";
		else if (arg.equals(alt[5]))
			statement += " p.date<'" + key + "'";
		else if (arg.equals(alt[6]))
			statement += " p.date>'" + key + "'";
		else if (arg.equals(alt[7])){
			// In this case the data is on the form <start date>,<end date>
			int c = 0; 
			while (key.charAt(c) != ',') c++;
			statement += " p.date>'" + key.substring(0,c-1) + "' AND p.Date<'" +
				key.substring(c+1) + "'";
		}
		else {
			if (contains)
				statement += " p.Id=d.Id AND d.Description LIKE '%" + key +"%'";
			else
				statement += " p.Id=d.Id AND d.Description='" + key +"'";
		}
		if (!searchName)
			statement += "OR p.Name LIKE '%" + key + "%'";

		return statement;
	} // End of method createSelectStatement
	
	/**
	 * This method ask the database the query created in createQuery.
	 * 
	 * @param query; a String containing a SQL statement
	 * @param legth; an integer that decides how many tuples to return
	 * @return; a String array with the answer to the query 
	 * @throws ClassNotFoundException 
	 */
	private void askQuery(String query, int length) 
		throws SQLException, IOException, ClassNotFoundException {
		// Loading the JDBC driver
		Class.forName("org.sqlite.JDBC");				
		String name, name2, path, date, extention;
		int Id;
		int i = 0;
		boolean rawExists, printed;
		Picture ans;
		
		// Connecting to and asking the database the query
		Connection conn = DriverManager.getConnection("jdbc:sqlite:"+DB);
		Statement s = conn.createStatement();	
		ResultSet r = s.executeQuery(query);
		// Read the results of the query into the string array answer		
		while (r.next()){
			date = r.getString(1);
			Id = r.getInt(2);
			name = r.getString(3);
			path = r.getString(4);
			if (r.getInt(5)==1)
				rawExists = true;
			else
				rawExists = false;
			if (r.getInt(6)==1)
				printed = true;
			else
				printed = false;
			while (name.charAt(i) != '.' || i == name.length()) 
				i++;
			name2 = name.substring(0, i);
			if (i == name.length())
				extention = "n/a";
			else
				extention = name.substring(i+1);
			
			ans = new Picture(path, name2, extention, date, rawExists, printed, Id);
			searchResults.add(ans);
			i = 0;
			/*if (i < length) {// length = number of answers to send back
				answer[i] = name + ";" + path + ";" + Id+ ";";	
			}
			i++;*/
		}
		PreparedStatement ps = conn.prepareStatement(
				"SELECT ? FROM ? WHERE Id = ?");
		for (int j=0; j<searchResults.size();j++) {
			int id = searchResults.get(j).getId();
			ps.setString(1, "description");
			ps.setString(2, "description");
			ps.setInt(3, id);
			//query = "SELECT d.description FROM Description AS d " +
			//		"WHERE d.Id="+id;
		
			//r = s.executeQuery(query);
			r = ps.executeQuery();
			while (r.next()) {
				searchResults.get(j).addDescription(r.getString(1));
			}
			if (searchResults.get(j).isPrinted()) {
				ps.setString(1, "Uses");
				ps.setString(2, "Printed");
				r = ps.executeQuery();
				//r = s.executeQuery("SELECT p.Uses FROM Printed AS p " +
				//		"WHERE d.Id=" + id);
				searchResults.get(j).setPrinted(r.getString(1));		
			}
			searchResults.get(j).updated();
		}
		// Closing connections
	    r.close();
	    conn.close();
	  	//return searchResults;
	}// End of method askQuery
	
	/**
	 * This method starts the result dialog if something is found in the database,
	 * otherwise it give a dialog that says that there are no results. 
	 * 
	 * @param answer
	 */
	private void results(){
		if (searchResults.isEmpty())
			JOptionPane.showMessageDialog(null, noPicFoundTxt, "", 1);
		else{
			new ResultDialog(null, searchResults, DB, useLanguage, length);
		}
		
	} // End method results
	
	/**
	 * This method sets all texts in Swedish 
	 */
	private void swedish() {
		text1.setText("S\u00F6k efter:");
		text2.setText("Max antal bilder i resultatet:");
		search.setText("S\u00F6k");
		exi.setText("Avsluta");
		name.setText("S\u00F6k \u00E4ven i bildnamnet.");
		cont.setText("Beskrivningen inneh\u00E5ller.");
		driverErr = "Hittar inte JDBC drivrutinen";
		SQLErr = "Har st\u00F6tt p\u00E5 ngt kul SQL-fel\n:" ;
		IOErr = "Har st\u00F6tt p\u00E5 ngt kul I/O-fel\n:";
		noPicFoundTxt = "Hittade inte n\u00E5gon bild.";
		titleTxt = "S\u00F6k";
	} // End method swedish 
	
	/**
	 * This method sets all texts in English 
	 */
	private void english() {
		// English in the comboBoxes... 
		String[] alt2 = {" ", "AND", "OR", "NOT", "DATE" , "BEFORE", "AFTER", "BETWEEN"}; 
		option1.setEditable(isEnabled());
		for (int i=4; i<alt2.length; i++)
			option1.insertItemAt(alt2[i], i);
		option2.setEditable(isEnabled());
		for (int i=1; i<alt2.length; i++)
			option2.insertItemAt(alt2[i], i);
		option3.setEditable(isEnabled());
		for (int i=1; i<alt2.length; i++)
			option3.insertItemAt(alt2[i], i);
		text1.setText("Search for:");
		text2.setText("Max max pictures in the search result:");
		search.setText("Search");
		exi.setText("Cancel");
		name.setText("Search in the file name.");
		cont.setText("The description contains.");
		driverErr = "Can't find the JDBC-driver";
		SQLErr = "Have found some fun SQL-problem\n:" ;
		IOErr = "Have found some fun I/O-problem:";
		noPicFoundTxt = "No files match the search.";
		titleTxt = "Search";
	} // End method english
	
	/**
	 * This method sets all texts in German 
	 */
	private void german() {
		// English in the comboBoxes... 
		String[] alt2 = {" ", "AND ", "OR ", "nicht ", "Datum ","VOR", "POST", "mittel"}; 
		option1.setEditable(isEnabled());
		for (int i=4; i<alt2.length; i++)
			option1.insertItemAt(alt2[i], i);
		option2.setEditable(isEnabled());
		for (int i=1; i<alt2.length; i++)
			option2.insertItemAt(alt2[i], i);
		option3.setEditable(isEnabled());
		for (int i=1; i<alt2.length; i++)
			option3.insertItemAt(alt2[i], i);
		text1.setText("Suche nach:");
		text2.setText("Max. Anzahl der Bilder in der Folge:");
		search.setText("Suche");
		exi.setText("Endet");
		name.setText("Auch in den Bildnamen.");
		cont.setText("Die Beschreibung enth\u00E4lt");
		driverErr = "Kann nicht finden JDBC-Treiber";
		SQLErr = "Gesto\u00DFen Spa\u00DF SQL-Fehler\n:";
		IOErr = "Encountered Spa\u00DF I/O-Error\n:";
		titleTxt = "Suche";
	} // End method german 
	
	/**
	 * This method sets all texts in French 
	 */
	private void french() {
		// English in the comboBoxes... 
		String[] alt2 = {" ", "ET", "OU", "NON", "DATE", "avant", "POST", "moyen"}; 
		option1.setEditable(isEnabled());
		for (int i=4; i<alt2.length; i++)
			option1.insertItemAt(alt2[i], i);
		option2.setEditable(isEnabled());
		for (int i=1; i<alt2.length; i++)
			option2.insertItemAt(alt2[i], i);
		option3.setEditable(isEnabled());
		for (int i=1; i<alt2.length; i++)
			option3.insertItemAt(alt2[i], i);
		text1.setText("Rechercher:");
		text2.setText("Le nombre maximum d'images dans le r\u00E9sultat:");
		search.setText("Rechercher");
		exi.setText("Fin");
		name.setText("Rechercher aussi dans les nom de l'image.");
		cont.setText("La description contient.");
		driverErr = "Vous ne trouvez pas de pilote JDBC";
		SQLErr = "Ont rencontr\u00E9 une erreur SQL fun\n:" ;
		IOErr = "Ont rencontr\u00E9 des fun I/O-Error:";
		titleTxt = "Rechercher";
	} // End method french
	
} // End of class PicSearch
