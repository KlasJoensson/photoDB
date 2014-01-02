package joensson.photoDB;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.*;

/**
 * This class implements a dialog that shows and handle the results of a search.
 *  
 * @author Klas Jönsson
 * 
 */
// TODO Add the possibility to change how many of the found pictures that are shown
public class ResultDialog extends Dialog implements ActionListener {
	private DefaultListModel mod = new DefaultListModel();
	private JList results = new JList(mod);
	private JButton showImage = new JButton();
	private JButton showInfo = new JButton();
	private JButton delTuple = new JButton();
	private JButton exi = new JButton();
	private String[] ans;
	private String DB, driverErr, SQLErr, IOErr, Choice1, Choice2, deleteConfirmMesage;
	private String deletedMessage1, deletedMessage2;
	private KortDB.language useLanguage;
	private ArrayList<Picture> searchResults;
	
	/**
	 * Creating the dialog window
	 * 
	 * @param f
	 * @param answer
	 */
	public ResultDialog(Frame f, ArrayList<Picture> answer, String useThisDB, KortDB.language l, int viewNr) {
		super(f, false);
		useLanguage = l;
		if (useLanguage == KortDB.language.ENGLISH)
			english();
		else if (useLanguage == KortDB.language.GERMAN)
			german();
		else if (useLanguage == KortDB.language.FRENCH)
			french();
		else
			swedish();
		DB = useThisDB;
		searchResults = answer; // So that it can be sent to e.g. ShowImage
		
		// create a panel for the buttons to live in
		Panel p = new Panel();
		p.setLayout(new GridLayout(5,1));
		JScrollPane sp = new JScrollPane(results);
		// Put the list and the panel with the buttons in the window
		add(sp, BorderLayout.WEST);
		add(p, BorderLayout.EAST);
		
		// Put the buttons in the panel
		p.add(showImage);
		p.add(showInfo);
		p.add(delTuple);
		p.add(exi);

		// Add the search result to the list	
		int show;
		if (searchResults.size() > viewNr)
			show = viewNr;
		else
			show = searchResults.size()-1;
		for (int i = 0; i<=show;i++)
			mod.addElement(searchResults.get(i).getName());
		
		// Set the list properties
		results.setVisibleRowCount(8);
		results.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Activate the listeners
		showImage.addActionListener(this);
		showInfo.addActionListener(this);
		delTuple.addActionListener(this);
		exi.addActionListener(this);
		
		pack();
		
		// Put the dialog on top of its parent, if it has one
		if (f != null)
			setLocation(f.getLocation().x + f.getWidth()/2-getWidth()/2,
				f.getLocation().y + f.getHeight()/2-getHeight()/2);
		else {
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
		}	
		setVisible(true);
	} // End constructor ResultDialog
	
	/**
	 * This method controls what happens when an event happens.
	 * 
	 * @param ActionEvent
	 */
	public void actionPerformed(ActionEvent e){
		Object s = e.getSource();
		int nr = results.getSelectedIndex(); // Starts counting at zero
		
		if (s == exi){
			dispose();
		} else
		if (s == showImage && nr != -1){
			String path = searchResults.get(nr).getPath() + 
					"/" + searchResults.get(nr).getName() +
					"." + searchResults.get(nr).getFileExtension();
					
			ShowImage SM = new ShowImage(path, useLanguage);
		} else
		if (s == showInfo && nr != -1){
			ShowInfo SI = new ShowInfo(searchResults.get(nr), DB, useLanguage);
		}  else
		if (s == delTuple && nr != -1){
			try{
				deleteTuple(ans[nr]);
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
		} else {
			// If there's no image selected... 
			JOptionPane.showMessageDialog(null, Choice1, Choice2, 3);
			return;
		}
		
	} // End method actionPerformed
	
	/**
	 * This method delete the selected tuple from the database
	 * 
	 * @param imageId
	 */
	private void deleteTuple(String imageInfo) throws SQLException, IOException, ClassNotFoundException{
		int check = JOptionPane.showConfirmDialog(this, deleteConfirmMesage);
		if (check == JOptionPane.YES_OPTION){
			String  imageId, statement;
			// The id number is the last data in the string, so lets begin in the end 
			// Set the start to the second last char in the string
			int i = imageInfo.length() - 2;
			
			while (imageInfo.charAt(i) != ';') i--;
			imageId = imageInfo.substring(i+1, imageInfo.length()-1);
			
			// Loading the JDBC driver
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB);
			Statement s = conn.createStatement();
			// To control when the database commit
			conn.setAutoCommit(false);
			s.execute("DELETE FROM description WHERE Id="+imageId+";");
			s.execute("DELETE FROM printed WHERE Id="+imageId+";");
			s.execute("DELETE FROM picture WHERE Id="+imageId+";");
			
			// Commit and close connection
			conn.commit();
			conn.setAutoCommit(true);
			conn.close();
			
			JOptionPane.showMessageDialog(this, deletedMessage1 ,deletedMessage2, 1);
		}
		
	} // End method deleteTuple
	
	/**
	 * This method sets all texts in Swedish 
	 */
	private void swedish() {
		setTitle("S\u00E4k Resultat");
		showImage.setText("Visa bild");
		showInfo.setText("Visa information");
		delTuple.setText("Ta bort bild");
		exi.setText("St\u00E4ng");
		driverErr = "Hittar inte JDBC drivrutinen";
		SQLErr = "Har st\u00E4tt p\u00E5 ngt kul SQL-fel\n:" ;
		IOErr = "Har st\u00E4tt p\u00E5 ngt kul IO-fel\n:";
		Choice1 = "Du m\u00E5ste v\u00E4lja en bild";
		Choice2 = "Ogiltigt val";
		deleteConfirmMesage = "\u00C4r du s\u00E4ker p\u00E5 att du vill"+"\n"+"ta bort bilden fr\u00E5n albumet?";
		deletedMessage1 = "Bilden raderad";
		deletedMessage2 = "F\u00E4rdig";
	} // End method swedish

	/**
	 * This method sets all texts in English 
	 */
	private void english() {
		setTitle("Search result");
		showImage.setText("Show picture");
		showInfo.setText("Show information");
		delTuple.setText("Remove picture");
		exi.setText("Close");
		driverErr = "Can't find the JDBC-driver";
		SQLErr = "Have found some fun SQL-problem\n:" ;
		IOErr = "Have found some fun I/O-problem:";
		Choice1 = "Please select a picture first.";
		Choice2 = "Not an option";
		deleteConfirmMesage = "Are you sure that you want to remove the picture";
		deletedMessage1 = "Picture erased";
		deletedMessage2 = "Done";
	} // End method english
	
	/**
	 * This method sets all texts in German
	 */
	private void german() {
		setTitle("Suchergebnisse");
		showImage.setText("Bildangaben");
		showInfo.setText("Detailansicht");
		delTuple.setText("Bild entfernen");
		exi.setText("Schlie\u00DFen");
		driverErr = "Kann nicht finden JDBC-Treiber";
		SQLErr = "Gesto\u00DFen Spa\u00DF SQL-Fehler\n:";
		IOErr = "Encountered Spa\u00DF I/O-Error\n:";
		Choice1 = "Sie müssen ein Bild ausw\u00E4hlen.";
		Choice2 = "Ungültige Auswahl";
		deleteConfirmMesage = "Sie sind sicher,\n dass Sie das Bild aus dem Album l\u00E4schen?";
		deletedMessage1 = "Bild gel\u00E4scht";
		deletedMessage2 = "Fertig";
	} // End method german

	/**
	 * This method sets all texts in French 
	 */
	private void french() {
		setTitle("R\u00E9sultats de la recherche");
		showImage.setText("Voir l'image");
		showInfo.setText("Voir les d\u00E9tails");
		delTuple.setText("Supprimer l'image");
		exi.setText("Fermer");
		driverErr = "Vous ne trouvez pas de pilote JDBC";
		SQLErr = "Ont rencontr\u00E9 une erreur SQL fun\n:" ;
		IOErr = "Ont rencontr\u00E9 des fun I/O-Error:";
		Choice1 = "Vous devez s\u00E9lectionner une image.";
		Choice2 = "S\u00E9lection invalide";
		deleteConfirmMesage = "Etes-vous sûr de vouloir supprimer l'image de l'album?";
		deletedMessage1 = "Image supprim\u00E9e";
		deletedMessage2 = "Finale";
	} // End method french
	
} // End class ResultDialog
