package joensson.photoDB;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import javax.swing.*;

/**
 * This class present statistics about a specific database.
 * 
 * @author Klas Jönsson
 *
 */
/* TODO If requested: make a search for the newest
 * or oldest pictures, i.e. use the search-class etc
 */
public class Statistics  extends JFrame implements ActionListener {
	private JLabel noOfPic = new JLabel();
	private JLabel percentRaw = new JLabel();
	private JLabel percentPrinted = new JLabel();
	private JLabel percentModif = new JLabel();
	private JLabel wordCount = new JLabel();
	private JLabel oldest = new JLabel();
	private JLabel youngest = new JLabel();
	private JLabel albumName = new JLabel();
	private JButton ok = new JButton();
	
	private String DB, driverErr, SQLErr, IOErr;
	private KortDB.language useLanguage;
	
	/**
	 * The constructor.
	 *   
	 * @param useThisDB
	 * @param language
	 */
	public Statistics(String useThisDB, KortDB.language l) {
		DB = useThisDB;
		useLanguage = l;
		// A container to put all the stuff in and the layout of the components
		Container c = getContentPane();
		GridBagLayout m = new GridBagLayout();
		c.setLayout(m);
		GridBagConstraints con = new GridBagConstraints();
		con.gridy = 1; con.gridx = 0; con.anchor = GridBagConstraints.WEST;
		m.setConstraints(albumName, con);
		c.add(albumName);
		con.gridy = 2; con.gridx = 0; con.anchor = GridBagConstraints.WEST;
		m.setConstraints(noOfPic, con);
		c.add(noOfPic);
		con.gridy = 3; con.gridx = 0; con.anchor = GridBagConstraints.WEST;
		m.setConstraints(percentRaw, con);
		c.add(percentRaw);
		con.gridy = 4; con.gridx = 0; con.anchor = GridBagConstraints.WEST;
		m.setConstraints(percentPrinted, con);
		c.add(percentPrinted);
		con.gridy = 5; con.gridx = 0; con.anchor = GridBagConstraints.WEST;
		m.setConstraints(percentModif, con);
		c.add(percentModif);
		con.gridy = 6; con.gridx = 0; con.anchor = GridBagConstraints.WEST;
		m.setConstraints(wordCount, con);
		c.add(wordCount);
		con.gridy = 7; con.gridx = 0; con.anchor = GridBagConstraints.WEST;
		m.setConstraints(oldest, con);
		c.add(oldest);
		con.gridy = 8; con.gridx = 0; con.anchor = GridBagConstraints.WEST;
		m.setConstraints(youngest, con);
		c.add(youngest);
		con.gridy = 9; con.gridx = 0; con.anchor = GridBagConstraints.CENTER;
		m.setConstraints(ok, con);
		c.add(ok);
		// Sets the texts
		if (useLanguage == KortDB.language.ENGLISH)
			english();
		else if (useLanguage == KortDB.language.GERMAN)
			german();
		else if (useLanguage == KortDB.language.FRENCH)
			french();
		else
			swedish();
		
		try {
			getStatistics();
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
		
		ok.addActionListener(this);
			
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
		// Place the window in the center
		setLocation(x, y);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	} // End constructor statistics
	
	/**
	 * This method controls what happens when an event happens
	 * 
	 * @param ActionEvent
	 */
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == ok)
			dispose();
	} // End method actionPerformed
	
	/**
	 * This method controls what happens when an event happens
	 * @param ActionEvent
	 */
	private void getStatistics() throws SQLException, IOException, ClassNotFoundException {
		int pics = 0, i = 0;
		String date = "", temp = "";
		String [] name;
		double percent = 0, count = 0;
		DecimalFormat percentFormat = new DecimalFormat("#0.00");
		
		// Loading the JDBC driver
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB);
		conn.setAutoCommit(false);
		Statement s = conn.createStatement();
		ResultSet r;
		
		r = s.executeQuery("SELECT COUNT(*) FROM picture;");
		while (r.next()){
			pics = r.getInt(1);
		}
		noOfPic.setText(noOfPic.getText() + pics);
		
		r = s.executeQuery("SELECT SUM(RAWexists) FROM picture;");
		while (r.next()){
			count = r.getInt(1);
		}
		percent = (count/pics)*100;
		percentRaw.setText(percentRaw.getText() + percentFormat.format(percent) + " %");
		
		r = s.executeQuery("SELECT COUNT(*) FROM printed;");
		while (r.next()){
			count = r.getInt(1);
		}
		percent = (count/pics)*100;
		percentPrinted.setText(percentPrinted.getText() + percentFormat.format(percent) +" %");
		
		r = s.executeQuery("SELECT SUM(Modified) FROM picture;");
		while (r.next()){
			count = r.getInt(1);
		}
		percent = (count/pics)*100;
		percentModif.setText(percentModif.getText() + percentFormat.format(percent) + " %");
		
		r = s.executeQuery("SELECT COUNT(*) FROM description;");
		while (r.next()){
			count = r.getInt(1);
		}
		percent = count/pics;
		wordCount.setText(wordCount.getText() + percentFormat.format(percent));
		
		r = s.executeQuery("SELECT MIN(date) FROM picture;");
		while (r.next()){
			date = r.getString(1);
		}
		r = s.executeQuery("SELECT COUNT(*) FROM picture WHERE date='"+ date +"';");
		while (r.next()){
			pics = r.getInt(1);
		}
		name = new String[pics];
		r = s.executeQuery("SELECT name FROM picture WHERE date='"+ date +"';");
		while (r.next()){
			name[i] = r.getString(1);
			i++;
		}
		for (int c = 0; c == pics-1; c++)
			temp += name[c] +"\n";
		oldest.setText(oldest.getText() + date +"): "+temp);
		
		r = s.executeQuery("SELECT MAX(date) FROM picture;");
		while (r.next()){
			date = r.getString(1);
		}
		r = s.executeQuery("SELECT COUNT(*) FROM picture WHERE date='"+ date +"';");
		while (r.next()){
			pics = r.getInt(1);
		}
		name = new String[pics];
		r = s.executeQuery("SELECT name FROM picture WHERE date='"+ date +"';");
		i=0;
		while (r.next()){
			name[i] = r.getString(1);
			i++;
		}
		temp = "";
		for (int c = 0; c == pics-1; c++)
			temp += name[c] +"\n";
		youngest.setText(youngest.getText() + date + "): " + temp);
		
		// Commit and close the connections
		conn.commit();
		conn.setAutoCommit(true);
	    r.close();
	    s.close();
		conn.close();
	} // End method getStatistics
	
	/**
	 * This method sets all texts in Swedish
	 */
	private void swedish() {
		setTitle("Statistik");
		albumName.setText("Album namn: " + DB);
		noOfPic.setText("Antal bilder: ");
		percentRaw.setText("Andel som har en RAW-bild: ");
		percentPrinted.setText("Andel som \u00E4r utskrivna: ");
		percentModif.setText("Andel som \u00E4r modifierade: ");
		wordCount.setText("S\u00F6kord per bild: ");
		oldest.setText("\u00C4lsta bilden (");
		youngest.setText("Nyaste bilden (");
		ok.setText("St\u00E4ng");
		driverErr = "Hittar inte JDBC drivrutinen";
		SQLErr = "Har st\u00F6tt p\u00E5 ngt kul SQL-fel\n:";
		IOErr = "Har st\u00F6tt p\u00E5 ngt kul I/O-fel\n:";
		pack();
	}// End method swedish
	
	/**
	 * This method sets all texts in English
	 */
	private void english() {
		setTitle("Statistics");
		albumName.setText("Album name: " + DB);
		noOfPic.setText("Numbers of pictures: ");
		percentRaw.setText("Percent with a RAW-picture: ");
		percentPrinted.setText("Percent printed: ");
		percentModif.setText("Percent modified: ");
		wordCount.setText("Average keywords per picture: ");
		oldest.setText("Oldest picture (");
		youngest.setText("Youngest picture (");
		ok.setText("Close");
		driverErr = "Can't find the JDBC-driver";
		SQLErr = "Have found some fun SQL-problem\n:" ;
		IOErr = "Have found some fun I/O-problem:";
		pack();
	} // End method english
	
	/**
	 * This method sets all texts in German
	 */
	private void german() {
		setTitle("Statistik");
		albumName.setText("Albumname: " + DB);
		noOfPic.setText("Anzahl der Bilder: ");
		percentRaw.setText("Prozentsatz, der ein RAW-Bild haben: ");
		percentPrinted.setText("Prozentsatz, dass gedruckt wird: ");
		percentModif.setText("Prozentsatz, der ge\u00E4ndert werden: ");
		wordCount.setText("Stichwort pro Bild: ");
		oldest.setText("\u00C4ltestes Bild (");
		youngest.setText("Neustes Bild (");
		ok.setText("Schlie\u00DFen");
		driverErr = "Kann nicht finden JDBC-Treiber";
		SQLErr = "Gesto\u00DFen Spa\u00DF SQL-Fehler\n:";
		IOErr = "Encountered Spa\u00DF I/O-Error\n:";
		pack();
	}// End method german
	
	/**
	 * This method sets all texts in French
	 */
	private void french() {
		setTitle("Statistiques");
		albumName.setText("Nom de l'album: " + DB);
		noOfPic.setText("Nombre d'images: ");
		percentRaw.setText("Pourcentage qui ont une image RAW: ");
		percentPrinted.setText("Partager ce qui est imprim\u00E9: ");
		percentModif.setText("Pourcentage qui sont modifi\u00E9s: ");
		wordCount.setText("Mots-cl\u00E9s par image: ");
		oldest.setText("Première photo (");
		youngest.setText("Dernière image (");
		ok.setText("Fermer");
		driverErr = "Vous ne trouvez pas de pilote JDBC";
		SQLErr = "Ont rencontr\u00E9 une erreur SQL fun\n:" ;
		IOErr = "Ont rencontr\u00E9 des fun I/O-Error:";
		pack();
	} // End method french
	
} // End class statistics
