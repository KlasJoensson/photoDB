package joensson.photoDB;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;

/**
 * This class create a dialog that make it possible to change the keywords used to 
 * describe the image.
 * 
 * @author Klas Jönsson
 *
 */
public class ChangeDescription extends Dialog implements ActionListener {
	
	// Create the different elements used in the dialog
	private DefaultListModel mod = new DefaultListModel();
	private JList words = new JList(mod);
	private JButton ok = new JButton();
	private JButton cancel = new JButton();
	private JButton removeWord = new JButton();
	private JButton newWord = new JButton();
	private JButton unmarkWord = new JButton();
	private JLabel noOfWords = new JLabel("");
	private JLabel dummie1 = new JLabel();
	private JLabel dummie2 = new JLabel();
	private JLabel dummie3 = new JLabel();
	private JLabel dummie4 = new JLabel();
	
	// Create two panels for the buttons to live in and one for the design
	private Panel p1 = new Panel(new GridLayout(7,1));
	private Panel p2 = new Panel(new GridLayout(1,5));
	private Panel empty = new Panel(new GridLayout(7,1));

	private String[] wordList = new String[10];
	private String[] oldWordList = new String[10];
	
	private String id, noOfWordsTxt, driverErr, SQLErr,	IOErr, Choice1, Choice2;
	private String	confirmMessage1, confirmMessage2, inputTxt, DB, foundErr;

	private Picture image;
	
	/**
	 * The constructor that create a dialog that make it possible to change the keywords 
	 * used to describe the image.
	 * 
	 * @param f
	 * @param imageId
	 * @param useLanguage 
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public ChangeDescription(Frame f, Picture im, String useThisDB, KortDB.language useLanguage) 
			//throws SQLException, IOException, ClassNotFoundException
			{
		super(f, true);
		DB = useThisDB;
		JScrollPane sp = new JScrollPane(words);
		//id = imageId;
		image = im;
		if (useLanguage == KortDB.language.ENGLISH)
			english();
		else if (useLanguage == KortDB.language.GERMAN)
			german();
		else if (useLanguage == KortDB.language.FRENCH)
			french();
		else
			swedish();
		p1.add(newWord);
		p1.add(removeWord);
		p1.add(unmarkWord);
		p1.add(dummie4);
		p1.add(noOfWords);
		
		p2.add(dummie1);
		p2.add(ok);
		p2.add(dummie2);
		p2.add(cancel);
		p2.add(dummie3);
		
		add(empty, BorderLayout.WEST);
		add(sp, BorderLayout.CENTER);
		add(p1, BorderLayout.EAST);
		add(p2, BorderLayout.SOUTH);
		
		words.setVisibleRowCount(7);
		words.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//getDescription(imageId);
		
		ok.addActionListener(this);
		cancel.addActionListener(this);
		newWord.addActionListener(this);
		removeWord.addActionListener(this);
		unmarkWord.addActionListener(this);
		
		pack();
		// Put the dialog on top of its parent, if it has one
		if (f != null)
			setLocation(f.getLocation().x + f.getWidth()/2-getWidth()/2,
				f.getLocation().y + f.getHeight()/2-getHeight()/2);
		else{
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
	 } // End of constructor ChangeDescription
	
	/**
	 * This method handles the actions in the dialog
	 */
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		
		if (s == ok){
			/*try{
				addToDB(id);
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
			}*/
			addNewWord();
			dispose();
		}
		if (s == unmarkWord){
			words.clearSelection();
		}
		if (s == cancel)
			dispose();		
		if (s == newWord)
			addNewWord();
		if (s == removeWord)
			if (words.isSelectionEmpty())
				JOptionPane.showMessageDialog(null, Choice1, Choice2, 3);
			else
				removeWord();
	
	} // End of method actionPerformed
	
	/**
	 * This method take the image id in the form as a string as argument, and then ask the
	 * database of the words that describe the image and up load them to the list.
	 *  
	 * @param imageId
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void getDescription(String imageId) throws SQLException, IOException, ClassNotFoundException{
		// Loading the JDBC driver
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:"+ DB);
		Statement s = conn.createStatement();
		
		// Ask the data base the query
		ResultSet r = s.executeQuery("SELECT description FROM description WHERE id=" + imageId + ";");
		
		// Read the results of the query and add to the list and the array 
		int i = 0;
		while (r.next()){
			mod.addElement(r.getString(1));
			if (i == wordList.length){
				String[] temp = new String[wordList.length];
				temp = wordList;
				wordList = new String[wordList.length + 5];
				wordList = temp;
			}
			wordList[i] = r.getString(1);
			i++;
		}
		
		// Set the text of the label noOfWords, i.e. the number if words found
		noOfWords.setText(noOfWordsTxt + i);
		// Close the connections
		r.close();
	    conn.close();
	    for (int j = 0; j < wordList.length; j++)
	    	oldWordList[j] = wordList[j];
	    
	} // End of method getDescription

	/**
	 * This method add a new keyword to the lists, but do not update the database
	 * (thats done when exiting the change dialog with the Ok-button).  
	 */
	private void addNewWord(){
		
		// Get the new word to add to the others
		int i = 0;
		String word = JOptionPane.showInputDialog(inputTxt);
		if (word != null){
			// Add the new word to the list
			mod.addElement(word);
			image.addDescription(word);
			// Find the first unused place in our internal array 
			while (wordList[i] != null)
				i++;
				
			// Just to make shore that the word fits in the array
			if (i == wordList.length){
				String[] temp = new String[wordList.length];
				temp = wordList;
				wordList = new String[wordList.length + 5];
				wordList = temp;
			}
			// Add the word to our internal list array
			wordList[i] = word;
	
			// Update the text label
			i++;
			noOfWords.setText(noOfWordsTxt + i);
		}
		
	} // End of method addNewWord
	
	private void removeWord(){
		// Get the number of the selected word
		int index = words.getSelectedIndex();
		int i = 0; 
		Boolean found = true;
		String hitta = (String) mod.get(index);
		try {
			image.removeDescription(hitta);
		
			// Find the removed word in our internal array, witch may not be in the end of the 
			// array...
			while (wordList[i] != hitta){
				i++;
				// To avoid a eternal loop
				if (i >= wordList.length){
					found = false;
					break;
				}				
			}
			// Remove the word
			if (found){
				wordList[i] = null;
			} else 
				// If the image isn't found thats 'cos its been added earlier (why dosn't i find it then?)
				wordList[index] = null;
		
			// Update the text label			
			i = mod.getSize()-1;
			noOfWords.setText(noOfWordsTxt + i);
		
			// Remove the selected word from the list
			mod.remove(index);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, foundErr, "Error", 2);
		}
	} // End of method removeWord
	
	/**
	 * This method updates the database.
	 *   
	 * @param imageId
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void addToDB(String imageId) throws SQLException, IOException, ClassNotFoundException{

		/* Remove all common words in oldWordList and wordList, so that only new words to be 
		 * added to the database are left in wordList and old words to remove are in oldWordList
		 */ 
		for (int i=0; i < wordList.length; i++)
			if (wordList[i] != null)
				for (int j=0; j < oldWordList.length; j++)
					if (wordList[i] == oldWordList[j]){
						oldWordList[j] = null;
						wordList[i] = null;
					}
		
		// Loading the JDBC driver
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:"+DB);
		Statement s = conn.createStatement();
		
		// To control when the database commits
		conn.setAutoCommit(false);
		
		// Update the database, i.e insert and and delete the tuples in the database
		for (int i=0; i < wordList.length; i++)
			if (wordList[i] != null)			
				s.execute("INSERT INTO description (Id, Description) " +
						"VALUES ( " + imageId + ",'" + wordList[i] +"');");
		for (int i=0; i < oldWordList.length; i++)		
			if (oldWordList[i] != null)	
				s.execute("DELETE FROM description " +
						"WHERE Id=" + imageId + " AND description='" + oldWordList[i] + "';");
		
		JOptionPane.showMessageDialog(null, confirmMessage1,confirmMessage2, 1);
		
		// Commit and close the connection
		conn.commit();
		conn.setAutoCommit(true);
	    conn.close();
	} // End of method addToDB 

	public Picture getPicture() {
		return image;
	}
	
	/**
	 * This method sets all texts in Swedish
	 */
	private void swedish(){
		ok.setText("Ok");
		cancel.setText("Avbryt");
		removeWord.setText("Ta bort");
		newWord.setText("Nytt ord");
		unmarkWord.setText("Avmarkera ordet");
		noOfWordsTxt = "Antal S\u00F6kord: ";
		driverErr = "Hittar inte JDBC drivrutinen";
		SQLErr = "Har st\u00F6tt p\u00E5 ngt kul SQL-fel\n:";
		IOErr = "Har st\u00F6tt p\u00E5 ngt kul IO-fel\n:";
		Choice1 = "Du m\u00E5ste v\u00E4lja en bild";
		Choice2 = "Ogiltigt val";
		confirmMessage1 = "Bild informationen \u00E4r sparad.";
		confirmMessage2 = "F\u00E4rdig";
		inputTxt = "Skriv ett S\u00F6kord:";
		foundErr = "Ordet finns inte...";
	} // End method swedish
	
	/**
	 * This method sets all texts in English
	 */
	private void english(){
		ok.setText("Ok");
		cancel.setText("Cancel");
		removeWord.setText("Remove");
		newWord.setText("New word");
		unmarkWord.setText("Unmark the word");
		noOfWordsTxt = "Number of search words: ";
		driverErr = "Can't find the JDBC-driver";
		SQLErr = "Have found some fun SQL-problem\n:" ;
		IOErr = "Have found some fun I/O-problem:";
		Choice1 = "Please select a picture first.";
		Choice2 = "Not an option";
		confirmMessage1 = "The information has been updated";
		confirmMessage2 = "Done";
		inputTxt = "Write a keyword:";
		foundErr = "Word not found.";
	} // End method english
	
	/**
	 * This method sets all texts in German
	 */
	private void german(){
		ok.setText("Ok");
		cancel.setText("Abbrechen");
		removeWord.setText("Entfernen");
		newWord.setText("New Wort");
		unmarkWord.setText("Deaktivieren Sie das Wort");
		noOfWordsTxt = "Anzahl der Stichworte: ";
		driverErr = "Kann nicht finden JDBC-Treiber";
		SQLErr = "Gesto\u00DFen Spa\u00DF SQL-Fehler\n:";
		IOErr = "Encountered Spa\u00DF I/O-Error\n:";
		Choice1 = "Sie müssen ein Bild ausw\u00E4hlen";
		Choice2 = "Ungültige Auswahl";
		confirmMessage1 = "Das Bild wird gespeichert.";
		confirmMessage2 = "Fertig";
		inputTxt = "Geben Sie ein Stichwort:";
		foundErr = "Das Wort existiert nicht.";
	} // End method german
	
	/**
	 * This method sets all texts in French
	 */
	private void french(){
		ok.setText("Ok");
		cancel.setText("Annuler");
		removeWord.setText("Supprimer");
		newWord.setText("Nouveau mot");
		unmarkWord.setText("Effacer le mot");
		noOfWordsTxt = "nombre de mots cl\u00E9s: ";
		driverErr = "Vous ne trouvez pas de pilote JDBC";
		SQLErr = "Ont rencontr\u00E9 une erreur SQL fun\n:" ;
		IOErr = "Ont rencontr\u00E9 des fun I/O-Error:";
		Choice1 = "Vous devez s\u00E9lectionner une image.";
		Choice2 = "S\u00E9lection invalide";
		confirmMessage1 = "L'image est enregistr\u00E9e";
		confirmMessage2 = "Finale";
		inputTxt = "Entrez un mot cl\u00E9:";
		foundErr = "Le mot n'existe pas.";
	} // End method french
	
} // End of class ChangeDescription
