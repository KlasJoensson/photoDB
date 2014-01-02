package joensson.photoDB;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.*;

/**
 * This class implements the window that show the image information and the methods that 
 * handle changes in the data, except for the keywords which got it's own class.
 * 
 * @author Klas Jönsson 
 */
public class ShowInfo extends JFrame implements ActionListener {
	
	// Some labels for presenting the information
	private JLabel name1 = new JLabel();
	private JLabel RAW1 = new JLabel();
	private JLabel modif = new JLabel();
	private JLabel path1 = new JLabel();
	private JLabel printed1 = new JLabel();
	private JLabel description = new JLabel();
	private JLabel dated = new JLabel();
	// Just some dummies to get the layout right
	private JLabel dummie1 = new JLabel();
	private JLabel dummie2 = new JLabel();
	private JLabel dummie3 = new JLabel();
	private JLabel dummie4 = new JLabel();
	private JLabel dummie5 = new JLabel();
	private JLabel dummie6 = new JLabel();
	
	// Some buttons are needed as well
	private JButton ok = new JButton();
	private JButton modifyRAW = new JButton();
	private JButton modifyModif = new JButton();
	private JButton modifyPrinted = new JButton();
	private JButton modifyDescription = new JButton();
	private JButton modifyDate = new JButton();
	private String imageId, DB, driverErr, SQLErr, IOErr, printTxt1, printTxt2, printTxt3;
	private String rawConfirmTxt, rawTxt1, rawTxt2, modifTxt1, modifTxt2, imagePath ;
	private KortDB.language useLanguage;
	private Picture picture;
	/**
	 * The constructor for the ShowInfo class. 
	 * 
	 * @param image the input containing the info about the image
	 * 			stored in a Picture-object
	 * @param useThisDB The name of the database that is used
	 * @param l The language that is used
	 */
	public ShowInfo(Picture image, String useThisDB, KortDB.language l) {
		picture = image;
		DB=useThisDB;
		useLanguage = l;
		if (useLanguage == KortDB.language.ENGLISH)
			english();
		else if (useLanguage == KortDB.language.GERMAN)
			german();
		else if (useLanguage == KortDB.language.FRENCH)
			french();
		else
			swedish();
		// A container to put all the stuff in
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		// Some panels to get a nice layout
		Panel p1 = new Panel();
		Panel p2 = new Panel();
		Panel p3 = new Panel();
		
		p3.setLayout(new GridLayout(7,1));
		p1.setLayout(new GridLayout(7,1));
		p2.setLayout(new GridLayout(1,5));
				
		// Place the text in one panel
		p1.add(name1);
		p1.add(path1);
		p1.add(dated);
		p1.add(RAW1); 
		p1.add(printed1); 
		p1.add(description);
		p1.add(modif);
		
		//  Place the ok-button in an other panel
		p2.add(dummie1);
		p2.add(dummie2);
		p2.add(ok);
		p2.add(dummie5);
		p2.add(dummie6);
		
		//  And then the modify-buttons in a 3rd panel
		p3.add(dummie3);
		p3.add(dummie4);
		p3.add(modifyDate);
		p3.add(modifyRAW);
		p3.add(modifyPrinted);
		p3.add(modifyDescription);
		p3.add(modifyModif);
		
		// Putting it all together
		c.add(p1, BorderLayout.WEST);
		c.add(p3, BorderLayout.EAST);
		c.add(p2, BorderLayout.SOUTH);
		
		getInfo();		
			
		ok.addActionListener(this);
		modifyRAW.addActionListener(this);
		modifyPrinted.addActionListener(this);
		modifyDescription.addActionListener(this);
		modifyDate.addActionListener(this);
		modifyModif.addActionListener(this);
		
		// Set the windows properties 
		pack();
		// Center the window
		// Get the default toolkit
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		// Get the current screen size
		Dimension scrnsize = toolkit.getScreenSize();
		// Get the current window size
		Dimension winSize = getSize();
		// Calculate the the coordinates
		int y = (scrnsize.height/4)*3 - (winSize.height/2);
		int x = (scrnsize.width/4)*3 - (winSize.width/2);
		// Place the window in the centre
		setLocation(x, y);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	} //End constructor ShowInfo

	/**
	 * This method controls what happens when an event happens
	 * 
	 * @param ActionEvent
	 */
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == ok){
			if (!picture.changesSaved()) {
				try{
					saveChances();
				}
				catch (ClassNotFoundException x){
					JOptionPane.showMessageDialog(null,driverErr , "Error", 2);
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
			dispose();
		}

		if (e.getSource() == modifyRAW)	
			changeRAW();			
		else if (e.getSource() == modifyPrinted)
			changePrinted();				
		else if (e.getSource() == modifyDescription){
			ChangeDescription CD = new ChangeDescription(this, picture, DB, useLanguage);
			Picture temp = CD.getPicture();
			if (!temp.changesSaved()) {
				picture = temp;
			}
		}
		else if (e.getSource() == modifyDate)
			changeDate();
		else if (e.getSource() == modifyModif)	
			changeModified();
	} // End method actionPerformed
	
	
	
	/**
	 * This method sort out the information of the picture that is given from the 
	 * ResultDialog and returns the image id number in the database (as a string).
	 * 
	 * @param imageData
	 * @return
	 */
	private String extractData(String imageData){
		// Get the filename
		int n = 0, n2 = 0, i = 0;
		String imageId;
		
		while(imageData.charAt(n) != ';') n++;
		// Get the name of the image
		name1.setText(name1.getText() + imageData.substring(0, n));
		imagePath = imageData.substring(0, n);
		n++;
		n2=n;
		
		// Get the file path
		while(imageData.charAt(n) != ';') n++;
		// Get the name of the image
		path1.setText(path1.getText() + imageData.substring(n2, n));
		i = imageData.substring(n2, n).length();
		
		// Some OS use "\" instead of "/"  to separate directors 
		i = imageData.substring(n2, n).length()-1;
		while(imageData.substring(n2, n).charAt(i) != '\\' || 
			imageData.substring(n2, n).charAt(i) != '/') {
			i--;
			if (i < 0) {
				// Base case: the file may be in the root director 
				i = 0;
				break;
			}
		}
		if (imageData.substring(n2, n).charAt(i) == '\\')
			imagePath = imageData.substring(n2, n) + "\\" + imagePath;
		else
			imagePath = imageData.substring(n2, n) + "/" + imagePath;
		n++;
		n2=n;
		
		// Getting the image id
		while(imageData.charAt(n) != ';') n++;
		imageId = imageData.substring(n2, n);
		
		return imageId;
	} // End method extractData
	
	
	private void getInfo() {
		name1.setText(name1.getText() + picture.getName());
		path1.setText(path1.getText() + picture.getPath());
		if (picture.isPrinted())
			printed1.setText(printed1.getText() + printTxt1);
		else
			printed1.setText(printed1.getText() + printTxt2 +
					" (" + picture.getPrintedFor() + ").");	
		if (picture.hasRaw())
			RAW1.setText(RAW1.getText() + rawTxt1);
		else
			RAW1.setText(RAW1.getText() + rawTxt2);
		if (picture.isModified())
			modif.setText(modifTxt1);
		else
			modif.setText(modifTxt2);
		dated.setText(dated.getText() + picture.getFileDate());
		ArrayList<String> desc = picture.getDescriptions();
		String descTxt = "";
		for (int i=0;i<desc.size()-1;i++)
			descTxt += desc.get(i) + ", ";
		descTxt += desc.get(desc.size()-1);
		description.setText(description.getText() + descTxt); 
	}
	
	/**
	 * This method lock in the database to see if there're any information about the 
	 * image in the tables printed and description.
	 *  
	 * @param imageId
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void getMoreInfo(String imageId) throws SQLException, IOException, ClassNotFoundException {
		String info = null;
		Connection conn = openConn(); 
		Statement s = conn.createStatement();
		
		// Getting the information from table printed 
		ResultSet r = s.executeQuery("SELECT uses FROM printed WHERE id=" + imageId + ";");
		// Read the results of the query and add one to obtain the id of the next tuple 
		while (r.next()){
			info = r.getString(1);
		}
		if (info == null)
			printed1.setText(printed1.getText() + printTxt1);
		else
			printed1.setText(printed1.getText() + printTxt2 +" (" + info + ").");
		
		// Finding out if there exists a RAW-file as well
		int i = 0;
		r = s.executeQuery("SELECT RAWexists FROM picture WHERE id=" + imageId + ";");
		// Read the results of the query and add one to obtain the id of the next tuple 
		while (r.next()){
			i = r.getInt(1);
		}
		if (i == 1)
			RAW1.setText(RAW1.getText() + rawTxt1);
		else
			RAW1.setText(RAW1.getText() + rawTxt2);
		
		// Finding out if the image has been modified  
		r = s.executeQuery("SELECT modified FROM picture WHERE id=" + imageId + ";");
		// Read the results of the query and add one to obtain the id of the next tuple 
		while (r.next()){
			i = r.getInt(1);
		}
		if (i == 1)
			modif.setText(modifTxt1);
		else
			modif.setText(modifTxt2);
		
		// finding the date of the file
		String date = "";
		r = s.executeQuery("SELECT Date FROM picture WHERE id=" + imageId + ";");
		// Read the results of the query and add one to obtain the id of the next tuple 
		while (r.next()){
			date = r.getString(1);
		}
		dated.setText(dated.getText() + date); 
		
		// Getting the information from table printed
		r = s.executeQuery("SELECT description FROM description WHERE id=" + imageId + ";");
		// Read the results of the query and add one to obtain the id of the next tuple 
		info = "";
		while (r.next()){
			info = info + r.getString(1) + ", ";
		}
		// Set the text on the label description2, without the last ", "
		description.setText(description.getText() + info.substring(0, info.length()-2));
		
		// Closing connections
	    r.close();
	    closeConn(conn);	
	} // End method getMoreInfo
	
	private void changeRAW() {
		if (picture.hasRaw()){
			RAW1.setText("RAW: " + rawTxt2);
		}
		else {
			RAW1.setText("RAW: "+rawTxt1);
		}
		picture.changeRawStatus();
	}
	
	private void changeModified() {
		if (picture.isModified()){
			modif.setText(modifTxt2);
		}
		else {
			modif.setText(modifTxt1);
		}
		picture.chanceModifiedStatus();
	}
	
	private void changeDate() {
		GetDateDialog gdd = new GetDateDialog(this, imagePath, useLanguage);
		if (gdd.ans != -1){
			// When the user has decided the date we collect it
			String date = gdd.getDate();
			picture.setFileDate(date);
			dated.setText("Datum: " + date);
			}
		else{
			// If the dialog is closed without any choice from the user then do nothing
			return;
		}
	}
	
	private void changePrinted() {
		String uses;
		PrintedDialog pd = new PrintedDialog(this, useLanguage);	
		if (pd.ans == 0){
			// If the picture is printed, then we want to know the reason
			uses = pd.getText();
			printed1.setText(printed1.getText() + printTxt2 + " (" + uses + ").");
			picture.setPrinted(uses);
		}
		if (pd.ans == 1){
			// If the was a post before, we remove it
			printed1.setText(printed1.getText() + printTxt1);
			picture.unsetPrinted();
		}
	}
	
	/**
	 * This method make it possible to change whether there's a RAW-copy of the image. 
	 * 
	 * @param imageId
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void changeRAW(String imageId) throws SQLException, IOException, ClassNotFoundException {
		// Declaration of the variables that the method need
		int oldValue = 0, newValue = 0;
		// A dialog asking if the user wants to change the status
		int op = JOptionPane.showConfirmDialog(this, rawConfirmTxt);
		
		if (op == JOptionPane.YES_OPTION) {
			// If the user wants to change do this
		
			Connection conn = openConn(); 
			Statement s = conn.createStatement();
			
			// Check for the old value
			ResultSet r = s.executeQuery("SELECT RAWexists FROM picture WHERE Id=" +imageId + ";");
			// Read the results of the query 
			while (r.next()){
				oldValue = r.getInt(1);
			}
			// Setting the new value depending on the old
			if (oldValue == 0){
				newValue = 1;
				RAW1.setText("RAW: " + rawTxt1);
			}
			else {
				newValue = 0;
				RAW1.setText("RAW: "+rawTxt2);
			}
			
			// Inserting the new value into the database
			s.execute("UPDATE picture SET RAWexists=" + newValue + " WHERE Id="+ imageId +";");
			
			r.close();
			closeConn(conn);
		    return;
		} else
			// If the user do not want to do this then just return.
			return;
	} // End method changeRAW
	
	/**
	 * This method make it possible to change whether the picture has been modified.
	 * 
	 * @param imageId
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void changeModif(String imageId) throws SQLException, IOException, ClassNotFoundException {
		// Declaration of the variables that the method need
		int oldValue = 0, newValue = 0;

		// A dialog asking if the user wants to change the status
		int op = JOptionPane.showConfirmDialog(this, rawConfirmTxt);

		if (op == JOptionPane.YES_OPTION) {
			// If the user wants to change do this
			
			// Loading the JDBC driver
			Connection conn = openConn();
			
			Statement s = conn.createStatement();
			
			// Check for the old value
			ResultSet r = s.executeQuery("SELECT modified FROM picture WHERE Id=" +imageId + ";");
			// Read the results of the query 
			while (r.next()){
				oldValue = r.getInt(1);
			}
			// Setting the new value depending on the old
			if (oldValue == 0){
				newValue = 1;
				modif.setText(modifTxt1);
			}
			else {
				newValue = 0;
				modif.setText(modifTxt2);
			}
			
			// Inserting the new value into the database
			s.execute("UPDATE picture SET modified=" + newValue + " WHERE Id="+ imageId +";");
			
			r.close();
		    closeConn(conn);
		    return;
		} else
			// If the user do not want to do this then just return.
			return;
	} // End method changeModif
	
	/**
	 * This method make it possible to change whether the picture has been printed.  
	 * 
	 * @param imageId
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void changePrinted(String imageId) throws SQLException, IOException, ClassNotFoundException{
		// Idea: read the value of 'uses' first and if it exists put it as a text in the 
		// text box...
		Connection conn = openConn();		
		Statement s = conn.createStatement();
		String uses, oldUses = null;
		
		ResultSet r = s.executeQuery("SELECT uses FROM printed WHERE id=" + imageId + ";");
		
		// Read the results of the query and add one to obtain the id of the next tuple 
		while (r.next()){
			oldUses = r.getString(2);
		}
			
		PrintedDialog pd = new PrintedDialog(this, useLanguage);	
		if (pd.ans == 0){
			// If the picture is printed, then we want to know the reason
			uses = pd.getText();
			printed1.setText(printed1.getText() + printTxt2 + " (" + uses + ").");
			if (oldUses == null){
				s.execute("INSERT INTO printed (id, uses) VALUES (" + imageId + ",'" + uses + "');");
			}else	
			// Inserting the new value into the table
			s.execute("UPDATE printed SET uses='" + uses + "' WHERE Id="+ imageId +";");
			}
		if (pd.ans == 1){
			// If the was a post before, we remove it
			if (oldUses != null)
				s.execute("DELETE FROM printed WHERE Id="+ imageId +";");
			uses = null;
			printed1.setText(printed1.getText() + printTxt1);
		}
				
		// Commit and close the connections
		r.close();
		closeConn(conn);	
	} // End method changePrinted
	
	/**
	 * This method changes the date of the file, with help of the same dialog as used 
	 * when setting the date in PicInsert (the class used to add images to the database).
	 * 
	 * @param imageId
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void changeDate(String imageId) throws SQLException, IOException, ClassNotFoundException{
		String date = "";
		GetDateDialog gdd = new GetDateDialog(this, imagePath, useLanguage);
		if (gdd.ans != -1){
			// When the user has decided the date we collect it
			date = gdd.getDate();
			dated.setText("Datum: " + date);
			}
		else{
			// If the dialog is closed without any choice from the user then do nothing
			return;
		};
		
		Connection conn = openConn();
		Statement s = conn.createStatement();
		
		// Inserting the new value into the table
		s.execute("UPDATE picture SET Date='" + date + "' WHERE Id="+ imageId +";");
		
		closeConn(conn);
	} // End method newDate
	
	private void saveChances() throws SQLException, IOException, ClassNotFoundException {
		int id = picture.getId();
		ArrayList<String> savedDescription = new ArrayList<String>();
		String name = "", word;
		
		Connection conn = openConn();
		Statement s = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement(
				"UPDATE ? SET ? = ? WHERE ID = " + id);
		PreparedStatement psAddPrinted = conn.prepareStatement(
				"INSERT INTO printed (id, uses) VALUES " +
				"(" + id + ", ?)");
		PreparedStatement psAddDesc = conn.prepareStatement(
				"INSERT INTO description (id, description) VALUES " +
				"(" + id + ", ?)");
		PreparedStatement psRm = conn.prepareStatement(
				"DELETE FROM description WHERE id=" 
						+ id + "AND description='?');");
		
		name = picture.getName() +"." + picture.getFileExtension();
		if (picture.hasNameChanged() || picture.hasFileExtensionChanged()) {
			ps.setString(2, "name");
			ps.setString(3, "'" + name + "'");
			ps.executeUpdate();
		}
		if (picture.hasPathChanged()) {
			ps.setString(2, "path");
			ps.setString(3, "'" + picture.getPath() + "'");
			ps.executeUpdate();	
		}
		if (picture.hasFileDateChanged()) {	
			ps.setString(2, "date");
			ps.setString(3, "'" + picture.getFileDate() + "'");
			ps.executeUpdate();	
		}
		if (picture.hasRawChanged()) {
			if (picture.hasRaw()) {
				ps.setString(2, "RAW");
				ps.setInt(3, 0);
				ps.executeUpdate();		
			}else {
				ps.setString(2, "RAW");
				ps.setInt(3, 1);
				ps.executeUpdate();	
			}
		}
		if (picture.hasModifiedChanged()) {
			if (picture.isModified()) {
				ps.setString(2, "modified");
				ps.setInt(3, 0);
				ps.executeUpdate();		
			}else {
				ps.setString(2, "modified");
				ps.setInt(3, 1);
				ps.executeUpdate();	
			}
		}
		if (picture.hasPrintedChanged()) {
			if (picture.isPrinted()) {
				ps.setString(2, "printed");
				ps.setInt(3, 1);
				ps.executeUpdate();
				psAddPrinted.setString(1, picture.getPrintedFor());
				psAddPrinted.execute();
			} else {
				s.executeQuery("DELETE FROM printed WHERE Id="+ id +";");
			}
		}
		if (picture.hasDescriptionChanged()){
			ArrayList<String> picDesc = picture.getDescriptions();
			ResultSet r = s.executeQuery("SELECT d.description FROM description WHERE id=" + id + ";");
			while (r.next()){
				savedDescription.add(r.getString(1));
			}
			for (int i=0;i<picDesc.size();i++) {
				if (savedDescription.contains(picDesc.get(i))){
					word = picDesc.get(i);
					picDesc.remove(i);
					savedDescription.remove(word);
				}
			}
			if (!savedDescription.isEmpty()) {
				for (int i=0;i<savedDescription.size();i++) {
					psRm.setString(1, savedDescription.get(i));
					psRm.execute();
				}
			}
			if (!picDesc.isEmpty()) {
				for (int i=0;i<picDesc.size();i++) {
					psAddDesc.setString(1, picDesc.get(i));
					psAddDesc.execute();
				}
			}
			
		}
		
		closeConn(conn);
	}
	/**
	 * This method opens a connection to a database chosen by he global variable DB 
	 * @return A connection to the chosen database
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Connection openConn() throws SQLException, IOException, ClassNotFoundException {
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
		setTitle("Om bilden");
		name1.setText("Namn: ");
		RAW1.setText("RAW: ");
		path1.setText("Sökväg: ");
		printed1.setText("Utskriven: ");
		description.setText("Sökord: ");
		dated.setText("Datum: ");
		ok.setText("Ok");
		modifyRAW.setText("Ändra");
		modifyModif.setText("Ändra");
		modifyPrinted.setText("Ändra");
		modifyDescription.setText("Ändra");
		modifyDate.setText("Ändra");
		driverErr = "Hittar inte JDBC drivrutinen";
		SQLErr = "Har stött på ngt kul SQL-fel\n:";
		IOErr = "Har stött på ngt kul I/O-fel\n:";
		printTxt1 = "Bilden är inte utskriven.";
		printTxt2 = "Bilden är utskriven.";
		printTxt3 = "Är bilden utskriven och till vad?";
		rawConfirmTxt ="Är du säker på att du vill ändra statusen?";
		rawTxt1 = "Det finns en RAW-bild.";
		rawTxt2 = "Det finns ingen RAW-bild.";
		modifTxt1 = "Bilden är modifierad.";
		modifTxt2 = "Bilden är inte modifierad.";
	} // End method swedish
	
	/**
	 * This method sets all texts in English
	 */
	private void english(){
		setTitle("About the picture");
		name1.setText("Name: ");
		RAW1.setText("RAW: ");
		path1.setText("Path: ");
		printed1.setText("Printed: ");
		description.setText("Keywords: ");
		dated.setText("Date: ");
		ok.setText("Ok");
		modifyRAW.setText("Modify");
		modifyModif.setText("Modify");
		modifyPrinted.setText("Modify");
		modifyDescription.setText("Modify");
		modifyDate.setText("Modify");
		driverErr = "Can't find the JDBC-driver";
		SQLErr = "Have found some fun SQL-problem\n:" ;
		IOErr = "Have found some fun I/O-problem:";
		printTxt1 = "The picture isn't printed.";
		printTxt2 = "The picture is printed.";
		printTxt3 = "Has the picture been printed and to what?";
		rawConfirmTxt ="Are you sure you want to modify it?";
		rawTxt1 = "There's a RAW-image.";
		rawTxt2 = "There's not a RAW-image.";
		modifTxt1 = "The picture has been manipulated.";
		modifTxt2 = "The picture hasn't been manipulated.";
	} // End method english
	
	/**
	 * This method sets all texts in German
	 */
	private void german(){
		setTitle("Wenn das Bild");
		name1.setText("Namen: ");
		RAW1.setText("RAW: ");
		path1.setText("Path: ");
		printed1.setText("Gedruckt: ");
		description.setText("Stichwort: ");
		dated.setText("Datum: ");
		ok.setText("Ok");
		modifyRAW.setText("Ändern");
		modifyModif.setText("Ändern");
		modifyPrinted.setText("Ändern");
		modifyDescription.setText("Ändern");
		modifyDate.setText("Ändern");
		driverErr = "Kann nicht finden JDBC-Treiber";
		SQLErr = "Gesto\u00DFen Spa\u00DF SQL-Fehler\n:";
		IOErr = "Encountered Spaß I/O-Error\n:";
		printTxt1 = "Das Bild wird nicht gedruckt.";
		printTxt2 = "Das Bild wird gedruckt.";
		printTxt3 = "Ist das Bild gedruckt und für was?";
		rawConfirmTxt ="Sie sind sicher, dass Sie den Status ändern?";
		rawTxt1 = "Es ist ein RAW-Bild.";
		rawTxt2 = "Es gibt keine RAW-Bild.";
		modifTxt1 = "Das Bild wird geändert.";
		modifTxt2 = "Das Bild wird nicht geändert.";
	} // End method german
	
	/**
	 * This method sets all texts in French
	 */
	private void french(){
		setTitle("Si l'image");
		name1.setText("Nom: ");
		RAW1.setText("RAW: ");
		path1.setText("Path: ");
		printed1.setText("Imprimé: ");
		description.setText("Keywords: ");
		dated.setText("Date: ");
		ok.setText("Ok");
		modifyRAW.setText("Changement");
		modifyModif.setText("Changement");
		modifyPrinted.setText("Changement");
		modifyDescription.setText("Changement");
		modifyDate.setText("Changement");
		driverErr = "Vous ne trouvez pas de pilote JDBC";
		SQLErr = "Ont rencontré une erreur SQL fun\n:" ;
		IOErr = "Ont rencontré des fun I/O-Error:";
		printTxt1 = "L'image n'est pas imprimée.";
		printTxt2 = "L'image est imprimée.";
		printTxt3 = "Est-ce l'image imprimée et pour quoi?";
		rawConfirmTxt = "Êtes-vous sûr de vouloir changer le statut?";
		rawTxt1 = "Il ya une image RAW.";
		rawTxt2 = "Il n'y a pas d'image RAW.";
		modifTxt1 = "L'image est modifiée.";
		modifTxt2 = "L'image n'est pas modifiée.";
	} // End method french
	
} // End class ShowInfo
