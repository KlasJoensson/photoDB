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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;

/**
 * This class implements the dialog for adding a new picture to the database
 * as well as the operations needed to put the data in it. 
 * 
 * @author Klas Jönsson
 *
 */
public class AddPic extends JFrame implements ActionListener{
	
	private JLabel nameText = new JLabel();
	private JLabel fileNameTxt = new JLabel();
	private JLabel picTypeTxt = new JLabel();
	private JLabel picType = new JLabel();
	private JLabel dateTxt = new JLabel();
	private JRadioButton ownDate = new JRadioButton(" ", false);
	private JTextField ownDateTxt = new JTextField(10);
	private JRadioButton toDay = new JRadioButton(" ", true);
	private JLabel toDayTxt = new JLabel();
	private JRadioButton fileDate = new JRadioButton(" ", false);
	private JLabel fileDateTxt = new JLabel();
	private JLabel dummie = new JLabel();
	
	private JCheckBox raw = new JCheckBox();
	private JCheckBox modified = new JCheckBox();
	private JCheckBox picPrinted = new JCheckBox();
	private JTextField printedTo = new JTextField(20);
	private JLabel dummie3 = new JLabel();
	private JLabel dummie4 = new JLabel();
	private JLabel dummie5 = new JLabel();
	private JLabel dummie7= new JLabel();
	private JLabel dummie8 = new JLabel();
	
	
	private JTextField keyTxt = new JTextField(20);
	private JButton addKey = new JButton();
	private JButton removeKey = new JButton();
	private JButton clearKeyField = new JButton();
	private JLabel searchTxt = new JLabel();
	private JLabel dummie2 = new JLabel();
	private DefaultListModel mod = new DefaultListModel();
	private JList keyList = new JList(mod);
	
	private JButton ok = new JButton();
	private JButton open = new JButton();
	private JButton cancel = new JButton();
	
	String fileName = " ", filePath = " ", dateUsed = getToDaysDate(), DB, titleTxt;
	String notSavedTxt, driverErr, SQLErr, IOErr, fileErrTxt, confirmMessage1, confirmMessage2;
	Boolean printed = false, rawExists = false, picMod = false, ownDateUsed = false, saved;
	
	/**
	 * The constructor. It puts the different components in theirs places. 
	 */
	public AddPic() {
		// Create something to put stuff in
		Container c = getContentPane();
		c.setLayout(new GridLayout(4,1));
		Panel top = new Panel();
		top.setLayout(new GridLayout(6,3));
		top.add(nameText);top.add(fileNameTxt); top.add(dummie3);
		top.add(picTypeTxt);top.add(picType); top.add(dummie4);
		top.add(dateTxt); top.add(dummie); top.add(dummie5);
		top.add(fileDate); top.add(fileDateTxt); top.add(dummie7);
		top.add(toDay); top.add(toDayTxt); top.add(dummie8);
		top.add(ownDate); top.add(ownDateTxt);//top.add(dummie6);
		

		ButtonGroup g = new ButtonGroup();
		g.add(fileDate); g.add(toDay); g.add(ownDate);
		
		Panel middleTop = new Panel();
		middleTop.setLayout(new GridLayout(4,1));
		middleTop.add(raw);
		middleTop.add(modified);
		middleTop.add(picPrinted);
		middleTop.add(printedTo);
				
		Panel middleBottom = new Panel();
		middleBottom.setLayout(new FlowLayout());
		Panel middleBottom1Button = new Panel();
		middleBottom1Button.setLayout(new FlowLayout());
		middleBottom1Button.add(clearKeyField);
		Panel middleBottom1 = new Panel();
		middleBottom1.setLayout(new GridLayout(3,1));
		middleBottom1.add(searchTxt); middleBottom1.add(keyTxt);
		middleBottom1.add(middleBottom1Button);
		Panel middleBottom2 = new Panel();
		middleBottom2.setLayout(new GridLayout(3,1));
		middleBottom2.add(addKey);
		middleBottom2.add(dummie2); 
		middleBottom2.add(removeKey);
		
		JScrollPane sp = new JScrollPane(keyList);
		middleBottom.add(middleBottom1); 
		middleBottom.add(middleBottom2);
		middleBottom.add(sp);
				
		Panel bottom = new Panel();
		bottom.setLayout(new FlowLayout());
		bottom.add(ok);
		bottom.add(open);
		bottom.add(cancel);
		
		// Put the four fields in the container
		c.add(top);
		c.add(middleTop);
		c.add(middleBottom);
		c.add(bottom);
		// Connect every thing with there listener
		ownDate.addActionListener(this);
		ownDateTxt.addActionListener(this);
		toDay.addActionListener(this);
		fileDate.addActionListener(this);
		
		raw.addActionListener(this);
		modified.addActionListener(this);
		picPrinted.addActionListener(this);
		printedTo.addActionListener(this);
		
		addKey.addActionListener(this);
		removeKey.addActionListener(this);
		keyTxt.addActionListener(this);
		clearKeyField.addActionListener(this);
		
		ok.addActionListener(this);
		open.addActionListener(this);
		cancel.addActionListener(this);
	}
	
	/**
	 * This method set the properties of the components and the window, 
	 * e.g. theirs text and sizes.   
	 */
	public void addPicDialog(String useThisDB, KortDB.language useLanguage){
		DB = useThisDB;
		saved = false;
		getFile();
		if (useLanguage == KortDB.language.ENGLISH)
			english();
		if (useLanguage == KortDB.language.GERMAN)
			german();
		if (useLanguage == KortDB.language.FRENCH)
			french();
		else
			swedish();
				
		// Set the list properties
		keyList.setVisibleRowCount(5);
		keyList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		// Set the windows properties 
		pack();
		setTitle(titleTxt);
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
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * This method controls what happens when an event happens
	 * @param ActionEvent
	 */
	public void actionPerformed(ActionEvent e){
		Object s = e.getSource();
		int ans = 0;
		if (s == fileDate){
			dateUsed = getFileDate(filePath + "/" + fileName);
			ownDateUsed = false;
		}
		else if (s == toDay){
			dateUsed = getToDaysDate();
			ownDateUsed = false;
		}
		else if (s == ownDate){;
			ownDateUsed = true;
			ownDateTxt.grabFocus();
		}
		else if (s == raw)
			rawExists = !rawExists;
		else if (s == modified)
			picMod = !picMod;
		else if (s == picPrinted){
			printed = !printed;
			printedTo.grabFocus();
		}
		else if (s == addKey){
			mod.addElement(keyTxt.getText());
			keyTxt.grabFocus();
		}
		else if (s == removeKey){
			Object[] val = keyList.getSelectedValues();
			for (int i = 0; i < val.length; i++)
				mod.removeElement(val[i]);
			keyTxt.grabFocus();
		}
		else if (s == clearKeyField){
			keyTxt.setText("");
			keyTxt.grabFocus();
		}
		else if (s == ok){
			SaveFile();
		}
		else if (s == open){
			if (!saved){
				ans = JOptionPane.showConfirmDialog(null, notSavedTxt);
				if (ans == JOptionPane.YES_OPTION){
					SaveFile();
					getFile();
				}
				else if (ans == JOptionPane.NO_OPTION) {
					getFile();
				}
			
			}else
				getFile();
		}
		else if (s == cancel){
			if (!saved){
				ans = JOptionPane.showConfirmDialog(null, notSavedTxt);
				if (ans == JOptionPane.YES_OPTION){
					SaveFile();
					dispose();
				}
				else if (ans == JOptionPane.NO_OPTION) {
					dispose();
				} 
			}
			else
				dispose();	
		}
						
	}
	
	/**
	 * Just a small wrapper
	 */
	private void SaveFile() {
		try{
			saveInfo();
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
		saved = true;
	}
	
	/**
	 * This method opens a dialog from where a file is picked and then stores the 
	 * filename in the global String fileName and the path in the global String filePath
	 * It also set everything except the date to its default value. 
	 */
	private void getFile() {
		
		/* Uncomment if you want to start in the director that the program are executing 
		 * from, if you want to start in the users home director set the argument to
		 *  "user.home" instead. 
		 * String dir = System.getProperty("user.dir"); 
		 * JFileChooser fc = new JFileChooser(dir);
		 */
		String dir = System.getProperty("user.dir"); 
		JFileChooser fc = new JFileChooser(dir+"/..");
		int res = fc.showOpenDialog(null);
		if (res == JFileChooser.APPROVE_OPTION){
			fileName = fc.getSelectedFile().getName();
			filePath = fc.getSelectedFile().getAbsolutePath();
			//file = path;
			// Cutting of the file name from the path
			int i = filePath.length()-1;
			while(filePath.charAt(i) != '/') {
				i--;
				if (i < 0) break;
			}
			filePath = filePath.substring(0, i);
		}
		else if (res == JFileChooser.ERROR_OPTION){
			JOptionPane.showMessageDialog(null, fileErrTxt,	"Error", 0);
			return;
		}
		saved = false;
		printed = false;
		rawExists = false;
		picMod = false;
		raw.setSelected(false);
		picPrinted.setSelected(false);
		printedTo.setText("");
		picType.setText(getFileType(fileName));
		toDayTxt.setText("(" + getToDaysDate() + ")");
		fileNameTxt.setText(fileName);		
		fileDateTxt.setText("(" + getFileDate(filePath +"/"+ fileName) + ")");
		keyTxt.setText("");
		keyTxt.grabFocus();
		mod.clear();
		if (ownDate.isSelected())
			dateUsed = ownDateTxt.getText();
		else if(fileDate.isSelected())
			dateUsed = getFileDate(filePath +"/"+ fileName);
		else 
			dateUsed = getToDaysDate();
		return;
	} // End method getFile
	
	/**
	 * This method create a date from the system date
	 */
	private String getToDaysDate(){
		Calendar cal = Calendar.getInstance();
		String strMonth, strDay;
		
		int year = cal.get(Calendar.YEAR); 
		
		int Month= cal.get(Calendar.MONTH) + 1;
		if (Month < 10)
			strMonth = "0" + Month;
		else
			strMonth = "" + Month;
		
		int Day = cal.get(Calendar.DAY_OF_MONTH);
		if (Day < 10)
			strDay = "0" + Day;
		else
			strDay = "" + Day;
		return year + "-" + strMonth + "-" + strDay;
	} // End method getToDaysDate
	
	/**
	 * This method gets the date from a file.
	 * 
	 * @param String The filename and the file path
	 */
	private String getFileDate(String fileName){
		// Create an instance of file object. 
		String[] theMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", 
				"Sep", "Oct", "Nov", "Dec"};
		String strMonth, strDay, strDate, strYear;
		int c = 0;
		File file = new File(fileName); 		
		// Get the last modification information. 		
		Long lastModified = file.lastModified();
		// Create a new date object and pass last modified information 		
		// to the date object. 
		Date fileDate = new Date(lastModified);
		strDate = fileDate.toString();
		strMonth = strDate.substring(4, 7);
		while (!strMonth.equals(theMonths[c])) c++;
		c++;
		if (c < 10)
			strMonth = "0" + c;
		else
			strMonth = "" + c;
		strDay = strDate.substring(8, 10);
		strYear = strDate.substring(strDate.length()-4, strDate.length());
		return "" + strYear + "-" + strMonth + "-" + strDay;
	} // End method getFileDate()
	
	/**
	 * This method takes the filename as a string and extract the file extension
	 * @param file The name of the file (with or with out the path)
	 * @return The file extension
	 */
	private String getFileType(String file) {
		int c=0;
		while (file.charAt(c) != '.') c++;
		
		return file.substring(c+1);
	} // End method getFileType
	
	/**
	 * This is the method that creates the SQL-statements and insert the data into the database
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void saveInfo()throws SQLException, IOException, ClassNotFoundException {
		// before we start to inserting we need to know the number of tuples in each table
		int id = 0, raw = 0, modf = 0;
		String query;
		Connection conn = openConn();
		
		// Getting the highest id number 
		Statement s = conn.createStatement();		
		ResultSet r = s.executeQuery("SELECT MAX(id) FROM picture;");
		// Read the results of the query and add one to obtain the id of the next tuple 
		while (r.next()){
			id = r.getInt(1) + 1;
		}
		
		// Table picture
		if (rawExists)
			raw = 1;
		// This is not implemented in the DB, when done then the query needs to implement it as well...
		if (picMod)
			modf = 1;
		// Creating the SQL-statement to insert the new tuple in the table
		if (ownDateUsed)
			dateUsed = ownDateTxt.getText();
		query = "INSERT INTO picture VALUES ('" + dateUsed + "', " + id + ", '" + fileName +
			"','" + filePath + "'," + raw +"," + modf + ");";
		// Inserting the data in to the table
		s.execute(query);
		
	    // Insert the reason in printed, if it's printed.
		if (printed){
			query = "INSERT INTO printed VALUES (" + id + ",'" + printedTo.getText() + "');";
			// Inserting the data in to the table
			s.execute(query);
		}
		for (int i=0; i < mod.size(); i++){
			// Creating the SQL-statement to insert the new tuple in the table
			query = "INSERT INTO description VALUES (" + id + ",'" + mod.get(i).toString() + "');";
			// Inserting the data in to the table
			s.execute(query);
		}
		
		r.close();
	    closeConn(conn);
	    
	    JOptionPane.showMessageDialog(null, confirmMessage1 ,confirmMessage2, 1);
	} //End method saveInfo
	
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
		Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB);
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
		nameText.setText("Namn:");
		picTypeTxt.setText("Format:");
		dateTxt.setText("V\u00E4lj datum:");
		ownDate.setText("Eget datum");
		toDay.setText("Dagens datum");
		fileDate.setText("Filens datum");
		raw.setText("Raw finns");
		modified.setText("Bilden \u00E4r modifierad");
		picPrinted.setText("Bilden \u00E4r utskriven. Till vad?");
		addKey.setText("L\u00E4gg till");
		removeKey.setText("Ta bort");
		clearKeyField.setText("Rensa");
		searchTxt.setText("S\u00F6kord:");
		ok.setText("Spara");
		open.setText("Ny bild");
		cancel.setText("St\u00E4ng");
		titleTxt = "L\u00E4gg till ny bild";
		notSavedTxt = "Bilden \u00E4r inte sparad.\n " +
				"Vill du spara den innan du fots\u00E4tter?";
		driverErr = "Hittar inte JDBC drivrutinen";
		SQLErr = "Har st\u00F6tt p\u00E5 ngt kul SQL-fel\n:" ;
		IOErr = "Har st\u00F6tt p\u00E5 ngt kul IO-fel\n:";
		fileErrTxt = "Nu gjorde du nog ngt dumt.. Fy!";
		confirmMessage1 = "Bild informationen \u00E4r sparad.";
		confirmMessage2 = "F\u00E4rdig";
	} // End method swedish
	
	/**
	 * This method sets all texts in English 
	 */
	private void english(){
		nameText.setText("Name:");
		picTypeTxt.setText("Type:");
		dateTxt.setText("Chose date:");
		ownDate.setText("Use own date");
		toDay.setText("Use todays date");
		fileDate.setText("Use the file date");
		raw.setText("Raw exists");
		modified.setText("The picture is manipulated");
		picPrinted.setText("The picture has been printed, to:");
		addKey.setText("Add");
		removeKey.setText("Remove");
		clearKeyField.setText("Clear");
		searchTxt.setText("Keyword:");
		ok.setText("Save");
		open.setText("New picture");
		cancel.setText("Close");
		titleTxt = "Add new picture";
		notSavedTxt = "The picture isn't saved!\nDo you want to save it?";
		driverErr = "Can't find the JDBC-driver";
		SQLErr = "Have found some fun SQL-problem\n:" ;
		IOErr = "Have found some fun I/O-problem:";
		fileErrTxt = "Now you've probably done something bad...";
		confirmMessage1 = "The information has been saved.";
		confirmMessage2 = "Done";
	} // End method english
	
	/**
	 * This method sets all texts in German 
	 */
	private void german(){
		nameText.setText("Namen:");
		picTypeTxt.setText("Geben:");
		dateTxt.setText("W\u00E4hlen Sie die Daten:");
		ownDate.setText("Benutzerdefiniertes Datum");
		toDay.setText("Das heutige Datum");
		fileDate.setText("Dateidatum");
		raw.setText("Raw ist");
		modified.setText("Das Bild wird ge\u00E4ndert.");
		picPrinted.setText("Das Bild wird gedruckt. F\u00FCr was?");
		addKey.setText("Hinzuf\u00FCgen");
		removeKey.setText("Entfernen");
		clearKeyField.setText("Klare");
		searchTxt.setText("Stichwort:");
		ok.setText("Speichern");
		open.setText("Neues Bild");
		cancel.setText("Schließen");
		titleTxt = "F\u00FCgen Sie ein Bild";
		notSavedTxt = "Das Bild wird nicht gespeichert.\n " +
				"Wollen Sie es vor Fuß setzen zu retten?";
		driverErr = "Kann nicht finden JDBC-Treiber";
		SQLErr = "Gesto\u00DFen Spa\u00DF SQL-Fehler\n:";
		IOErr = "Encountered Spaß I/O-Error\n:";
		fileErrTxt = "Jetzt haben Sie wahrscheinlich etwas dumm .. Oh!";
		confirmMessage1 = "Das Bild wird gespeichert.";
		confirmMessage2 = "Fertig";
	} // End method german
	
	/**
	 * This method sets all texts in French 
	 */
	private void french(){
		nameText.setText("Nom:");
		picTypeTxt.setText("De type:");
		dateTxt.setText("S\u00E9lectionnez les dates:");
		ownDate.setText("Date personnalis\u00E9e");
		toDay.setText("Date du jour");
		fileDate.setText("Fichier Date");
		raw.setText("Raw est");
		modified.setText("L'image est modifi\u00E9e.");
		picPrinted.setText("L'image est imprim\u00E9e. Pour quoi?");
		addKey.setText("Ajouter");
		removeKey.setText("Supprimer");
		clearKeyField.setText("Nettoyer");
		searchTxt.setText("Mots-cl\u00E9s:");
		ok.setText("Enregistrer");
		open.setText("Nouvelle image");
		cancel.setText("Fermer");
		titleTxt = "Ajouter une nouvelle image";
		notSavedTxt = "L'image n'est pas enregistr\u00E9e.\n " +
				"Voulez-vous l'enregistrer avant de mettre les pieds?";
		driverErr = "Vous ne trouvez pas de pilote JDBC";
		SQLErr = "Ont rencontr\u00E9 une erreur SQL fun\n:" ;
		IOErr = "Ont rencontr\u00E9 des fun I/O-Error:";
		fileErrTxt = "Maintenant, vous n'avez probablement quelque chose de stupide .. Oh!";
		confirmMessage1 = "L'image est enregistr\u00E9e.";
		confirmMessage2 = "Finale";
	} // End method french
	
}// End Class AddPic