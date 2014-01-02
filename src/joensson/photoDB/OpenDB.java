/*******************************************************************************
 * Copyright (c) 2014  Klas Jönsson <klas.joensson@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package joensson.photoDB;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Panel;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class OpenDB extends JFrame implements ActionListener{
	private JLabel text = new JLabel();
	private JButton newDB = new JButton();
	private JButton open = new JButton();
	private JButton delete = new JButton();
	private JButton cancel = new JButton();
	private DefaultListModel mod = new DefaultListModel();
	private JList DBList = new JList(mod);
	
	private String DBname, ListOfDBs = "",inputMessage, driverErr, SQLErr, 
		IOErr, unselectedMessage, delConfirmTxt;
	
	/**
	 * The constructor. It puts the different components in theirs places. 
	 */
	public OpenDB() {
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		GridBagLayout m = new GridBagLayout();
		GridBagConstraints con = new GridBagConstraints();
		
		Panel list = new Panel();
		list.setLayout(m);
		con.gridy = 1; con.gridx = 1; 
		m.setConstraints(text, con);
		list.add(text);
		con.gridy = 2; con.gridx = 1;
		con.fill = GridBagConstraints.HORIZONTAL;
		JScrollPane sp = new JScrollPane(DBList);
		m.setConstraints(sp, con);
		list.add(sp);
		
		Panel buttons = new Panel();
		buttons.setLayout(m);
		con.fill = GridBagConstraints.HORIZONTAL;
		//GridBagConstraints conNewDB = new GridBagConstraints();
		con.gridy = 1; con.gridx = 1; 
		m.setConstraints(newDB, con);
		buttons.add(newDB);
		//GridBagConstraints conOpen = new GridBagConstraints();
		con.gridy = 3; con.gridx = 1;
		m.setConstraints(open, con);
		buttons.add(open);
		//GridBagConstraints conDelete = new GridBagConstraints();
		con.gridy = 5; con.gridx = 1;
		m.setConstraints(delete, con);
		buttons.add(delete);
		//GridBagConstraints conCancel = new GridBagConstraints();
		con.gridy = 7; con.gridx = 1;
		m.setConstraints(cancel, con);
		buttons.add(cancel);

		c.add(list); c.add(buttons);
		
		newDB.addActionListener(this);
		open.addActionListener(this);
		delete.addActionListener(this);
		cancel.addActionListener(this);
	}
	
	/**
	 * This method controls what happens when an event happens
	 * @param ActionEvent
	 */
	public void actionPerformed(ActionEvent e){
		Object s = e.getSource();
		if (s == cancel){
			//DBname = "Cancel";
			dispose();
		}
		else if (s == newDB){
			try{
				createDB();
			}
			catch (ClassNotFoundException x){
				JOptionPane.showMessageDialog(null, driverErr,  "Error", 2);
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
			dispose();
		}
		else if (s == open){
			int i = DBList.getSelectedIndex();
			if (DBList.isSelectionEmpty())
				JOptionPane.showMessageDialog(null, unselectedMessage);
			else {
				DBname = mod.get(i).toString();			
				dispose();
			}
		}
		else if (s == delete){
			int i = DBList.getSelectedIndex();
			if (DBList.isSelectionEmpty())
				JOptionPane.showMessageDialog(null, unselectedMessage);
			else {
				try {
					DBname = mod.get(i).toString();			
					delDB(DBname, i);
				}
				catch (ClassNotFoundException x){
					JOptionPane.showMessageDialog(null, driverErr,  "Error", 2);
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
			}		
		}
	} // End method actionPerformed
	
	/**
	 * This method set the properties of the components and the window, 
	 * e.g. theirs text and sizes. 
	 * 
	 * @param The language that are used. 
	 */
	public void open(KortDB.language useLanguage) {
		if (useLanguage == KortDB.language.ENGLISH)
			english();
		else if (useLanguage == KortDB.language.GERMAN)
			german();
		else if (useLanguage == KortDB.language.FRENCH)
			french();
		else
			swedish();
		DBList.setVisibleRowCount(5);
		DBList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//Object obj = null;
		int c1 = 0, c2= 0;
		String dir = System.getProperty("user.dir");
		try {
			FileInputStream fis = new FileInputStream(dir+"/DBs.dat");
			ObjectInputStream in = new ObjectInputStream(fis);
			Object obj = in.readObject();
			in.close();
			if (obj != null)
				ListOfDBs = obj.toString();
		}
		catch (IOException ie) {
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dir + "/DBs.dat"));
				ListOfDBs = "";
				out.writeObject(ListOfDBs);
				out.close();
				//ie.printStackTrace();
			}
			catch (IOException ie2) {
				ie2.printStackTrace();
			}
		}
		catch (ClassNotFoundException ce) {
			ce.printStackTrace();
		}
		if (ListOfDBs != null){
			while (c1 < ListOfDBs.length()-1) {
				while (ListOfDBs.charAt(c1) != ';' && c1 < ListOfDBs.length()-1)
					c1++;
				mod.addElement(ListOfDBs.substring(c2, c1));
				c2 = c1+1;
				c1++;
			}
		}

		pack();
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
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);		
	} // End method open
	
	/**
	 * Just a simple get-method.
	 * @return The name of the selected database
	 */
	public String getDBname() {
		return DBname;
	}
	
	/**
	 * Creates a database and add it last in the lists of databases
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void createDB() throws SQLException, IOException, ClassNotFoundException {
		DBname = JOptionPane.showInputDialog(inputMessage);		
		String dir = System.getProperty("user.dir");
		
		Connection conn = openConn();
		
		Statement s = conn.createStatement();	
		// The SQL-statements that create the DB...-
		// The main table "picture"
		s.execute("CREATE TABLE PICTURE (" +
				"date VARCHAR(10)," +
				"Id INTEGER CONSTRAINT pk_picture PRIMARY KEY," +
				"Name VARCHAR(20)," +
				"Place VARCHAR(30)," +
				"RAWexists INTEGER," + // Set to 1 if a RAW-version exits, else 0
				"Modified INTEGER" + // Set to 1 if the image has been modified, else 0
				"); ");
		// Then the table "description" that contains the keywords
		s.execute("CREATE TABLE DESCRIPTION (" +
				"Id INTEGER, " +
				"Description VARCHAR(20) " +
				");");
		// Then the table "printed" that contains the pictures that has been
		//  printed and to what 
		s.execute("CREATE TABLE PRINTED (" +
				"Id INTEGER, "+
				"Uses VARCHAR(20) " +
				");");
		// At last we add then foreign keys to description and printed
		// do not work; how to add foreign keys in SQLite?
		//s.execute("ALTER TABLE DESCRIPTION "+
		//		"ADD CONSTRAINT fk_disciption FOREIGN KEY (Id) REFERECES Picture (Id);");
		//s.execute("ALTER TABLE PRINTED "+
		//		"ADD CONSTRAINT fk_printed FOREIGN KEY (Id) REFERECES Picture (Id);");

		closeConn(conn);
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dir + "/DBs.dat"));
		if (ListOfDBs.isEmpty()){
			ListOfDBs = DBname +";";
		}
		else {
			ListOfDBs += DBname + ";";
		}
		out.writeObject(ListOfDBs);
		out.close();
		mod.addElement(DBname);
	} // End method createDB
	
	/**
	 * This method deletes a database
	 * 
	 * @param selected Name of the database to be deleted
	 * @param index The index of the DB to be removed in the list of DB 
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void delDB(String selected, int index) throws SQLException, IOException, ClassNotFoundException{
		int c1=0, c2=0;
		int op = JOptionPane.showConfirmDialog(this, delConfirmTxt);
		String temp = "", dir = System.getProperty("user.dir");
		if (op == JOptionPane.YES_OPTION) {
			Connection conn = openConn();
			
			Statement s = conn.createStatement();	
			s.execute("DROP TABLE PRINTED");		
			s.execute("DROP TABLE DESCRIPTION");
			s.execute("DROP TABLE PICTURE");

			closeConn(conn);
			if (!ListOfDBs.isEmpty()){
				while (c1 < ListOfDBs.length()-1) {
					while (ListOfDBs.charAt(c1) != ';' && c1 < ListOfDBs.length()-1){
						System.out.println(c1);
						c1++;	
					}
					if (!selected.equals(ListOfDBs.substring(c2, c1)))
						temp += ListOfDBs.substring(c2, c1) +";";
					c2=c1+1;
					c1++;
				}
				
			}
			System.out.println("ListOfDBs: "+ListOfDBs);
			System.out.println("temp: "+temp);
			ListOfDBs = temp;
			
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dir + "/DBs.dat"));
				out.writeObject(ListOfDBs);
				out.close();
			}
			catch (IOException ie2) {
				ie2.printStackTrace();
			}
			mod.remove(index);
		}
	    
	} // End method delDB
	
	/**
	 * This method opens a connection to a database chosen by he global variable DB 
	 * @return A connection to the chosen database
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Connection openConn() throws SQLException, IOException, ClassNotFoundException{
		// Loading the JDBC driver
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBname);
		conn.setAutoCommit(false);
		return conn;
	} // End method openConn
	
	/**
	 * This Method closes and commit a connection to a database 
	 * @param conn The connection to be closed
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void closeConn(Connection conn) throws SQLException, IOException, ClassNotFoundException {
		// Commit and close the connections
		conn.commit();
		conn.setAutoCommit(true);
	    conn.close();
	} // End method closeConn
	
	/**
	 * This method sets all texts in Swedish
	 */
	private void swedish(){
		text.setText("V\u00E4lj album:");
		newDB.setText("Ny");
		open.setText("\u00D6ppna");
		delete.setText("Ta bort album");
		cancel.setText("Avbryt");
		setTitle("\u00D6ppna album");
		inputMessage = "Skriv in namnet p\u00E5 albumet";
		driverErr = "Hittar inte JDBC drivrutinen";
		SQLErr = "Har st\u00D6tt p\u00E5 ngt kul SQL-fel\n:" ;
		IOErr = "Har st\u00D6tt p\u00E5 ngt kul IO-fel\n:";
		unselectedMessage = "Du m\u00E5ste v\u00E4lja ett album.";
		delConfirmTxt = "\u00C4r du s\u00E4ker p\u00E5 attt du vil radera albumet?";
	} // End method swedish
	
	/**
	 * This method sets all texts in English
	 */
	private void english(){
		text.setText("Choose an album:");
		newDB.setText("New");
		open.setText("Open");
		delete.setText("Remove album");
		cancel.setText("Cancel");
		setTitle("Open album");
		inputMessage = "Write the name of the album";
		driverErr = "Can't find the JDBC-driver";
		SQLErr = "Have found some fun SQL-problem\n:" ;
		IOErr = "Have found some fun I/O-problem:";
		unselectedMessage = "You have to choose an album.";
		delConfirmTxt = "Are you sure you want to erase the album?";
	} // End method english
	
	/**
	 * This method sets all texts in German
	 */
	private void german(){
		setTitle("\u00D6ffnen Alben");
		text.setText("Album ausw\u00E4hlen:");
		newDB.setText("Neue");
		open.setText("\u00D6ffnen Alben");
		delete.setText("Album l\u00D6schen");
		cancel.setText("Abbrechen");
		inputMessage = "Geben Sie den Namen des Albums";
		driverErr = "Kann nicht finden JDBC-Treiber";
		SQLErr = "Gesto\u00DFen Spa\u00DF SQL-Fehler\n:";
		IOErr = "Encountered Spaß I/O-Error\n:";
		unselectedMessage = "Sie müssen w\u00E4hlen Sie ein Album.";
		delConfirmTxt = "Sie sind sicher, dass Sie das Bild aus dem Album l\u00F6schen?";
	} // End method german
	
	/**
	 * This method sets all texts in French
	 */
	private void french(){
		setTitle("Ouvrir l'album");
		text.setText("S\u00E9lectionnez l'album:");
		newDB.setText("Nouveau");
		open.setText("Ouvrir l'album ");
		delete.setText("Supprimer l'album");
		cancel.setText("Annuler");
		inputMessage = "Entrez le nom de l'album";
		driverErr = "Vous ne trouvez pas de pilote JDBC";
		SQLErr = "Ont rencontr\u00E9 une erreur SQL fun\n:" ;
		IOErr = "Ont rencontr\u00E9 des fun I/O-Error:";
		unselectedMessage = "Vous devez s\u00E9lectionner un album	.";
		delConfirmTxt = "Etes-vous sûr de vouloir supprimer l'image de l'album?";
	} // End method french
	
} // End class OpenDB
