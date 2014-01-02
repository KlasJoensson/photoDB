package joensson.photoDB;
import javax.swing.*;
import java.awt.*;
import java.net.*;

/**
 * This class resizes an image.
 * 
 * @author Jan Skansholm
 * This class is from: 
 * "Java direkt med Swing", Jan Skansholm, Studentliteratur 2002, 3rd edition,
 *  pages 520 and 521.
 */
public class DynamicImageIcon extends ImageIcon{
	
	private JComponent p;	//den komponent ikonen ritas på
	
	// Konstuktorer
	public DynamicImageIcon(String filename) {
		super(filename);
	}
	
	public DynamicImageIcon(URL location){
		super(location);
	}
	
	public DynamicImageIcon(Image image){
		super(image);
	}
	
	// Metoder
	public void setParent(JComponent parent) {
		p = parent;
	}
	
	public Dimension getIconSize() {
		// Räkna ut bildens ursprungsstorlek
		int iw = getImage().getWidth(component),
			ih = getImage().getHeight(component);
		if (p == null)
			return new Dimension(iw, ih);	// ingen förälder angiven
		else {
			// räka ut förälderkomponentens storlek
			int pw, ph;
			pw=p.getWidth() -p.getInsets().left-p.getInsets().right;
			ph=p.getHeight() -p.getInsets().top -p.getInsets().bottom;
			if (pw/(double)ph < iw/(double)ih)
				return new Dimension(pw, pw*ih/iw); // brädden avgör
			else
				return new Dimension(pw*ih/iw, ph); //höjden avgör
		}
	}
	
	public int getIconWidth() {
		return getIconSize().width;
	}
	
	public int getIconHeight() {
		return getIconSize().height;
	}
	
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.drawImage(getImage(), x, y, getIconWidth(), getIconHeight(), component);
	}
}
