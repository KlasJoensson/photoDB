package joensson.photoDB;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class implements a dialog asking for what purpose the picture are printed.
 *  
 * @author Klas Jönsson
 * 
 */
public class PrintedDialog extends Dialog implements ActionListener {
	
	private JButton Ok = new JButton ();
	private JButton Cancel = new JButton ();
	private JLabel text = new JLabel();
	private JTextField usedTo = new JTextField(20);
	public int ans = -1;
	private String used;
	
	/**
	 * Creating the dialog window
	 * 
	 * @param f
	 * @param KortDB.language The language that is wanted
	 */
	public PrintedDialog(Frame f, KortDB.language useLanguage) {
		super(f, true);
		if (useLanguage == KortDB.language.ENGLISH)
			english();
		else if (useLanguage == KortDB.language.GERMAN)
			german();
		else if (useLanguage == KortDB.language.FRENCH)
			french();
		else
			swedish();
		
		// Create the dialog
		Panel p = new Panel();
		p.setLayout(new GridLayout(1,2));
		add(text, BorderLayout.NORTH);
		add(usedTo, BorderLayout.CENTER);
		add(p, BorderLayout.SOUTH);

		
		// Adding the buttons to theirs panel
		p.add(Ok);
		p.add(Cancel);
		
		// Activating the listeners
		Ok.addActionListener(this);
		Cancel.addActionListener(this);
		usedTo.addActionListener(this);
		
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
	} // End constructor PrintedDialog
	
	/**
	 * This method controls what happens when an event happens.
	 * 
	 * @param ActionEvent
	 */
	public void actionPerformed(ActionEvent e){
		Object s = e.getSource();
		if (s == Ok){
			ans = 0;
			used = usedTo.getText(); // Get what ever is the reason
			setVisible(false);
		}
		if (s == Cancel){
			ans = 1;
			used = null;
			setVisible(false);
		}
		if (s == usedTo){
			used = usedTo.getText();
		}
	}  // End method actionPerformed
	
	/**
	 * Just a get method to be able to read what has been written in the text box.
	 *  
	 * @return String 
	 */
	public String getText(){
		return used;
	}  // End method getText
	
	/**
	 * This method sets all texts in Swedish
	 */
	private void swedish(){
		Ok.setText("Är utskriven");;
		Cancel.setText("Är inte utskriven");
		text.setText("Till vad skrevs bilden ut?");
	} // End method swedish
	
	/**
	 * This method sets all texts in English
	 */
	private void english(){
		Ok.setText("It's printed");;
		Cancel.setText("It's not printed");
		text.setText("To what was it printed?");		
	} // End method english
	
	/**
	 * This method sets all texts in German
	 */
	private void german(){
		Ok.setText("Wird gedruckt");;
		Cancel.setText("Wird nicht gedruckt");
		text.setText("Denn was war das Bild aussehen?");		
	} // End method german
	
	/**
	 * This method sets all texts in French
	 */
	private void french(){
		Ok.setText("est imprimée");;
		Cancel.setText("N'est pas imprimée");
		text.setText("Pour ce qui était l'image ressemble?");
	} // End method french
	
} // End Class PrintedDialog
