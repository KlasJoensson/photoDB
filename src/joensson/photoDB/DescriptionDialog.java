package joensson.photoDB;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class implements a dialog asking for some words that describe the image.
 *  
 * @author Klas Jönsson
 * 
 */
public class DescriptionDialog extends Dialog implements ActionListener {

	private Button Ok = new Button ("Färdig");
	private Button Cancel = new Button ("Avbryt");
	private Button More = new Button ("Fler ord");
	private JLabel text = new JLabel("Skriv ett ord som beskriver bilden");
	private JTextField Desc = new JTextField(20);
	public int ans = -1;
	private String word;
	
	/**
	 * Creating the dialog window
	 * 
	 * @param f
	 * @param question
	 */
	public DescriptionDialog(Frame f, String question) {
		super(f, true);
		Panel p = new Panel();
		p.setLayout(new GridLayout(1,3));
		add(text, BorderLayout.NORTH);
		add(Desc, BorderLayout.CENTER);
		add(p, BorderLayout.SOUTH);
		
		p.add(Ok);
		p.add(More);
		p.add(Cancel);
		
		Ok.addActionListener(this);
		Cancel.addActionListener(this);
		More.addActionListener(this);
		Desc.addActionListener(this);
		
		// Set the window properties
		pack();
		// Put the dialog on top of its parent
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
	}
	
	/**
	 * This method controls what happens when an event happens.
	 * 
	 * @param ActionEvent
	 */
	public void actionPerformed(ActionEvent e){
		Object s = e.getSource();
		if (s == Ok){
			ans = 0;
			word = Desc.getText(); // Get what ever is the reason
			setVisible(false);
		}
		if (s == More){
			ans = 1;
			word = Desc.getText(); // Get what ever is the reason
			setVisible(false);
		}
		if (s == Cancel){
			ans = 2;
			word = null;
			setVisible(false);
		}
		if (s == Desc){
			word = Desc.getText();
		}
	}
	
	/**
	 * Just a get method to be able to read what has been written in the text box.
	 * 
	 * @return String
	 */
	public String getText(){
		return word;
	}
}
