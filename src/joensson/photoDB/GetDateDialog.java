package joensson.photoDB;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.io.File; 
import java.util.Date;
/**
 * This class get a date from either from the input in the text field, from the
 * system date or the file it self.
 *   
 * @author Klas Jönsson
 *
 */
public class GetDateDialog extends Dialog implements ActionListener {
	private JButton myDate = new JButton ();
	private JButton toDay = new JButton ();
	private JButton fileDate = new JButton ();
	private JLabel text = new JLabel();
	private JTextField ownDate = new JTextField(20);
	public int ans = -1;
	private String date, fileName;
	
	/**
	 * The constructor that constructs the dialog
	 * @param f
	 * @param file
	 */
	public GetDateDialog(Frame f, String file, KortDB.language useLanguage) {
		super(f, true);
		if (useLanguage == KortDB.language.ENGLISH)
			english();
		else if (useLanguage == KortDB.language.GERMAN)
			german();
		else if (useLanguage == KortDB.language.FRENCH)
			french();
		else
			swedish();
		fileName= file;
		Panel p = new Panel();
		p.setLayout(new GridLayout(1,3));
		add(text, BorderLayout.NORTH);
		add(ownDate, BorderLayout.CENTER);
		add(p, BorderLayout.SOUTH);
		
		p.add(myDate);
		p.add(toDay);
		p.add(fileDate);
		
		myDate.addActionListener(this);
		toDay.addActionListener(this);
		fileDate.addActionListener(this);
		ownDate.addActionListener(this);
		
		pack();
		// Put the dialog on top of its parent
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
	} // End constructor GetDateDialog
	
	/**
	 * This method controls what happens when an event happens.
	 * 
	 * @param ActionEvent
	 */
	public void actionPerformed(ActionEvent e){
		Object s = e.getSource();
		if (s == myDate){
			ans = 0;
			date = ownDate.getText();
			setVisible(false);
		}
		if (s == toDay){
			ans = 1;
			getToDaysDate();
			setVisible(false);
		}
		if (s == fileDate){
			ans = 2;
			getFileDate();
			setVisible(false);
		}
		if (s == ownDate){
			date = ownDate.getText();
			setVisible(false);
		}
	} // End method actionPerformed
	
	/**
	 * This method create a date from the system date
	 */
	private void getToDaysDate(){
		Calendar cal = Calendar.getInstance();
		
		int y = cal.get(Calendar.YEAR); 
		int m = cal.get(Calendar.MONTH) + 1;
		int d = cal.get(Calendar.DAY_OF_MONTH);
		date = y + "-" + m + "-" + d;
	} // End method getToDaysDate
	
	/**
	 * This method create a date from a file sent in to the class in the string "file".  
	 */
	private void getFileDate(){
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
		date = "" + strYear + "-" + strMonth + "-" + strDay;		
	} // End method getFileDate()
	
	/**
	 * Just a get method to be able to read what has been written in the text box.
	 * 
	 * @return the date written in the text box
	 */
	public String getDate(){
		return date;
	} // End method getDate
	/**
	 * This method sets all texts in Swedish
	 */
	private void swedish(){
		myDate.setText("Eget datum");
		toDay.setText("Dagens datum");
		fileDate.setText("Filens datum");
		text.setText("Skriv in ditt eget datum(YYYY-MM-DD):");
	} // End method swedish
	
	/**
	 * This method sets all texts in English
	 */
	private void english(){
		myDate.setText("Own date");
		toDay.setText("To day");
		fileDate.setText("The date of the file");
		text.setText("Write our own date(YYYY-MM-DD):");
	} // End method english
	
	/**
	 * This method sets all texts in German
	 */
	private void german(){
		myDate.setText("Benutzerdefiniertes Datum");
		toDay.setText("Das heutige Datum");
		fileDate.setText("Dateidatum");
		text.setText("Geben Sie hier Ihr eigenes(YYYY-MM-DD):");
	} // End method german
	
	/**
	 * This method sets all texts in French
	 */
	private void french(){
		myDate.setText("Date personnalisée");
		toDay.setText("Date du jour");
		fileDate.setText("Fichier Date");
		text.setText("Entrez votre propre date(YYYY-MM-DD):");
	} // End method french
	
}// End class GetDateDialog
