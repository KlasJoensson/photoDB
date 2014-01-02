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

/**
 * This class show the selected image in a window.
 * 
 * @author Klas Jönsson
 *
 */
public class ShowImage extends JFrame implements ActionListener {
	private JButton ok = new JButton();
	
	/**
	 * Creates the window in which the image is shown. 
	 * 
	 * @param imageData
	 */
	public ShowImage(String path, KortDB.language useLanguage) {
		if (useLanguage == KortDB.language.ENGLISH)
			ok.setText("Close");
		else if (useLanguage == KortDB.language.GERMAN)
			ok.setText("Schlie\u00DFen");
		else if (useLanguage == KortDB.language.FRENCH)
			ok.setText("Fermer");
		else
			ok.setText("St\u00E4ng");
		// Call the method that create the path to the image
		
		// Create the image
		DynamicImageIcon d = new DynamicImageIcon(path);
		JLabel image = new JLabel(d, JLabel.CENTER);
		d.setParent(image);
		
		// Create the window
		Container c = getContentPane(); 
		c.setLayout(new BorderLayout());
		
		// Add the image and the button to the window
		c.add(image, BorderLayout.CENTER);
		c.add(ok, BorderLayout.SOUTH);
		
		// Activate the button
		ok.addActionListener(this);
		
		// Set the windows properties 
		setSize(250, 450);
		// Center the window on the screen
		// Get the default toolkit
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		// Get the current screen size
		Dimension scrnsize = toolkit.getScreenSize();
		// Get the current window size
		Dimension winSize = getSize();
		// Calculate the the coordinates
		int y = (scrnsize.height/4) - (winSize.height/2);
		int x = (scrnsize.width/5) * 4 - (winSize.width/2);
		// Place the window in the center
		setLocation(x, y);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * This method controls what happens when an event happens.
	 * 
	 * @param ActionEvent
	 */
	public void actionPerformed(ActionEvent e){
	if (e.getSource() == ok)
		dispose();	
	}
	
	/**
	 * This method creates (and returns) the path from the image data sent to it
	 * 
	 * @param imageData
	 * @return
	 *
	private String getPath(String imageData){
		String path, name;
		int n = 0, p;
		// Search for the first ";", which marks the end of the name
		while(imageData.charAt(n) != ';') n++;
		// Get the name of the image
		name = imageData.substring(0, n);
		// Setting the title of the window to the image name
		setTitle(name);
		
		n=n+1;
		p = n;
		// After the ';' comes the path to the image
		while(imageData.charAt(p) != ';') p++;
		path = imageData.substring(n, p);
		
		// To make this work on OS that uses '\' rather than '/' for separating directors
		Boolean test = false;
		for (int i = 0; i < path.length(); i++)
			if (path.charAt(i) == '\\'){
				test = true; 
			} 
		if (test)
			path = path + "\\" + name;
		else
			path = path + "/" + name;
		
		return path;
	} // End method getPath
	*/
} // End class ShowImage
