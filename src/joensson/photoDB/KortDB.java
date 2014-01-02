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
 * The task of this program is to be a simple GUI for a database containing 
 * information about photographs.
 * 
 * @author Klas Jönsson
 * 
 */
public class KortDB extends JFrame implements ActionListener{
	
	// Create the buttons needed for this dialog
	private JButton search = new JButton();
	private JButton newTuple = new JButton();
	private JButton newDB = new JButton();
	private JButton stat = new JButton();
	private JButton exi = new JButton();
	private JMenuBar mb = new JMenuBar();
	private JMenu menu = new JMenu();
	private JMenuItem swe = new JMenuItem();
	private JMenuItem eng = new JMenuItem();
	private JMenuItem ger = new JMenuItem();
	private JMenuItem fre = new JMenuItem();
	private JMenuItem about = new JMenuItem();
	
	private OpenDB db;
	private String unselectedMessage, aboutMessage; //language = "swe"
	private language useLanguage;
	
	/**
	 * The constructor. It puts the different components in theirs places. 
	 */
	public KortDB(){
		// Create something to put the buttons in
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		
		// Add the buttons to that something 
		c.add(newDB); c.add(search); c.add(stat);  
		c.add(newTuple); c.add(exi);
		
		// Connects the buttons with there listener
		search.addActionListener(this);
		newTuple.addActionListener(this);
		stat.addActionListener(this);
		exi.addActionListener(this);
		newDB.addActionListener(this);
		menu.addActionListener(this);
		swe.addActionListener(this);
		eng.addActionListener(this);
		ger.addActionListener(this);
		fre.addActionListener(this);
		about.addActionListener(this);
	}
	
	/**
	 * This method set the properties of the components and the window, 
	 * e.g. theirs text and sizes.   
	 */
	public void mainMenu(){
		if (useLanguage == language.ENGLISH)
			english();
		else if (useLanguage == language.GERMAN)
			german();
		else if (useLanguage == language.FRENCH)
			french();
		else
			swedish();
		// Set the windows properties
		// Create the menus
		swe.setText("Svenska");
		eng.setText("English");
		ger.setText("Deutsch");
		fre.setText("Française");
		setJMenuBar(mb);
		// The first menu
		mb.add(menu);
		menu.add(swe);
		menu.add(eng);
		menu.add(ger);
		menu.add(fre);
		menu.add(about);
		pack();
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

	} // End method mainMeny
	
	/**
	 * This method controls what happens when an event happens
	 * @param ActionEvent
	 */
	public void actionPerformed(ActionEvent e){
		String useThisDB = null;
		Object s = e.getSource();
		if (s == exi)
			System.exit(0);
		else if (s == search) {						
			try {
				useThisDB = db.getDBname();
				PicSearch p = new PicSearch();
				p.searchDialog(useThisDB, useLanguage);
			}
			catch (NullPointerException err)
			{
				JOptionPane.showMessageDialog(null, unselectedMessage);
			}			
		}						
		else if (s == newTuple){
			try {
				useThisDB = db.getDBname();
				AddPic ins = new AddPic();
				ins.addPicDialog(useThisDB, useLanguage);
			}
			catch (NullPointerException err)
			{
				JOptionPane.showMessageDialog(null, unselectedMessage);
			}		
		}
		else if (s == newDB) {
			db = new OpenDB();
			db.open(useLanguage);
		}
		else if (s == stat) {						
			try {
				useThisDB = db.getDBname();
				new Statistics(useThisDB, useLanguage);

			}
			catch (NullPointerException err)
			{
				JOptionPane.showMessageDialog(null, unselectedMessage);
			}
		}
		else if(s == swe) {
			useLanguage = language.SWEDISH;
			swedish();
		}
		else if(s == eng) {
			useLanguage = language.ENGLISH; 
			english();
		}
		else if(s == ger) {
			useLanguage = language.GERMAN; 
			german();
		}
		else if(s == fre) {
			useLanguage = language.FRENCH; 
			french();
		}
		else if(s == about) {
			JOptionPane.showMessageDialog(null, aboutMessage);
		}
		
	} // End method actionPerformed
	
	/**
	 * Just a list of the languages 
	 *
	 * @author Klas Jönsson
	 *
	 */
	public enum language {
		SWEDISH,
		ENGLISH,
		GERMAN,
		FRENCH
	} // End enum language
	
	/**
	 * This method sets all texts in Swedish 
	 */
	private void swedish() {
		search.setText("S\u00F6k");
		newTuple.setText("L\u00E4gg till Bild");
		newDB.setText("\u00D6ppna album");
		stat.setText("Statistik");
		exi.setText("Avsluta");
		setTitle("Mina kort");
		unselectedMessage = "Du m\u00E5ste \u00F6ppna ett album f\u00F6rst.";
		menu.setText("Inst\u00E4llingar");	
		about.setText("Om");
		aboutMessage = "Mina kort 2.0\nAv Klas J\u00F6nsson\n\u00AE 2011";
		pack();
	} // End method swedish
	
	/**
	 * This method sets all texts in English
	 */
	private void english() {
		search.setText("Search");
		newTuple.setText("Add a picture");
		newDB.setText("Chance album");
		stat.setText("Statistics");
		exi.setText("Exit");
		setTitle("My pictures");
		unselectedMessage = "You have to choose an album first.";
		menu.setText("Preferences");
		about.setText("About");
		aboutMessage = "My pictures 2.0\nBy Klas J\u00F6nsson\n\u00AE 2011";
		pack();
	} // End method english
	
	/**
	 * This method sets all texts in German
	 */
	private void german() {
		search.setText("Sucher");
		newTuple.setText("Foto hinzuf\u00FCgen");
		newDB.setText("\u00D6ffnen Alben");
		stat.setText("Statistik");
		exi.setText("Endet");
		setTitle("Meine Karten");
		unselectedMessage = "Sie m\u00FCssen sich \u00F6ffnen zuerst ein Album.";
		menu.setText("Voreinstellungen");
		about.setText("Auf");
		aboutMessage = "Meine Karten 2.0\nAus Klas J\u00F6nsson\n\u00AE 2011";
		pack();
	} // End method german
	
	/**
	 * This method sets all texts in French 
	 */
	private void french() {
		search.setText("Recherche");
		newTuple.setText("Ajouter une photo");
		newDB.setText("Ouvrir l'album ");
		stat.setText("Statistiques");
		exi.setText("Fin");
		setTitle("Mes cartes");
		unselectedMessage = "Vous devez ouvrir un premier album.";
		menu.setText("Pr\u00E9f\u00E9rences");
		about.setText("Sur");
		aboutMessage = "Mes cartes 2.0\nEn Klas J\u00F6nsson\n\u00AE 2011";
		pack();
	} // End method french
	
	/**
	 * The main method just start the program...
	 * @param args
	 */
	public static void main (String[] args){
		KortDB DB = new KortDB();
		DB.mainMenu();
	} // End method main
	
}
