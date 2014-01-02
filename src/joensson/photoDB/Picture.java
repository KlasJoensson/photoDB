package joensson.photoDB;
import java.util.ArrayList;

/**
 * This class represents a picture and contains all the 
 * information about one that are stored in the database.
 * 
 * @author Klas Jönsson
 *
 */
public class Picture {
	private int id;
	private String name, oldName;
	private String path, oldPath;
	private String printedFor = "", oldPrintedFor = "";
	private String fileExtension, oldFileExtension;
	private String fileDate, oldFileDate;
	private Boolean printed, oldPrinted;
	private Boolean rawExists, oldRawExists;
	private Boolean modified, oldModified;
	private Boolean changed = false;
	private Boolean nameChanged = false, pathChanged = false; 
	private Boolean printedChanged = false, printedForChanged = false;
	private Boolean fileDateChanged = false, rawChanged = false, descriptionChanged = false;
	private Boolean modifiedChanged = false, fileExtensionChanged = false;
	private ArrayList<String> descriptions, oldDescriptions;
	// TODO implement methods to reset each of the changed variables?
	/**
	 * The constructor for the picture. The descriptions are added
	 * afterwards with its method, the same if its printed.
	 * 
	 * @param p The file-path
	 * @param n The file-name
	 * @param ext The file extension
	 * @param fd The date related to the file
	 * @param re If a raw-version exist
	 * @param mod If the picture has been modified
	 * @param i The picture id in the database
	 */
	public Picture(String p, String n, String ext, String fd,
			Boolean re, Boolean mod, int i) {
		id = i;
		name = oldName = n;
		path = oldName = p;
		fileExtension = oldFileExtension = ext;
		fileDate = oldFileDate = fd;
		rawExists = oldRawExists = re;
		modified = oldModified = mod;
		printed = oldPrinted = false;
	}
	
	/**
	 * Set the printed status to true, i.e. the picture has 
	 * been printed. This method can also be used to chance 
	 * the reason for printing the picture.
	 * 
	 * @param toWhat reason for printing the picture
	 */
	public void setPrinted(String toWhat) {
		if (!printedChanged) {
			oldPrinted = printed;
			oldPrintedFor = printedFor;
		}
		if (!printed) {
			printed = true;
			printedChanged = true;
		}
		if (toWhat == null)
			printedFor = "";
		else
			printedFor = toWhat;
		changed = true;
		printedForChanged = true;
	}
	
	/**
	 * Use this method to unset the printed variables.
	 */
	public void unsetPrinted() {
		if (!printedChanged) {
			oldPrinted = printed;
			oldPrintedFor = printedFor;
		}
		printed = false;
		printedFor = "";
		changed = true;
		printedChanged = true;
		printedForChanged = true;
	}
	
	/**
	 * A small method to see whether the picture is printed.
	 * 
	 * @return Returns true if the picture is printed.
	 */
	public Boolean isPrinted() {
		return printed;
	}
	
	/**
	 * A method to get the reason for the picture is
	 * printed. Returns "n/a" if no reason i given and 
	 * "Not printed if the picture isn't marked as printed.
	 * 
	 * @return The reason for printing the picture.
	 */
	public String getPrintedFor() {
		if(printed)
			if (printedFor == "")
				return "n/a";
			else
				return printedFor;
		else
			return "Not printed";
	}
	
	/**
	 * This method adds one string as a description.
	 * 
	 * @param d A word that describes the picture.
	 */
	public void addDescription(String d) {
		if (!changed)
			oldDescriptions = descriptions;
		descriptions.add(d);
		changed = true;
		descriptionChanged = true;
	}
	
	/**
	 * This method adds several strings to describe the 
	 * picture.
	 * 
	 * @param d An array of strings that describes the picture.
	 */
	public void addDescriptions(String[] d) {
		if (!changed)
			oldDescriptions = descriptions;
		for (int i=0;i<d.length;i++)
			descriptions.add(d[i]);
		changed = true;
		descriptionChanged = true;
	}
	
	/**
	 * This method returns the words that describes the
	 * picture.
	 * 
	 * @return An array that contains the strings that describes the picture.
	 */
	public ArrayList<String> getDescriptions() {
		return descriptions;
	}
	
	/**
	 * This method check if a a specific word is i the descriptions.
	 * 
	 * @param d The word to find.
	 * @return Returns true if it finds d.
	 */
	public Boolean descriptionContains(String d) {
		return descriptions.contains(d);			
	}
	
	/**
	 * This method removes a description. If the input don't exists
	 * it throws an exception.
	 * 
	 * @param d The word to remove
	 * @throws Exception 
	 */
	public void removeDescription(String d) throws Exception {
		if (!changed)
			oldDescriptions = descriptions;
		Boolean test = descriptions.remove(d);
		if (!test)
			throw new Exception();
		else {
			changed = true;
			descriptionChanged = true;
		}
	}
	
	/**
	 * This method reports if there's unsaved chances.
	 * 
	 * @return Returns true if there's no chances since
	 * 			last time the picture was updated.
	 */
	public Boolean changesSaved() {
		return !changed;
	}
	
	/**
	 * Chances the RAW-status.
	 */
	public void changeRawStatus() {
		if (!rawChanged)
			oldRawExists = rawExists;
		rawExists = !rawExists;
		changed = !changed;
		rawChanged = !rawChanged;
	}
	
	/** 
	 * Check if there's a raw-version of the picture.
	 * 
	 * @return True if there's a RAW-image.
 	 */
	public Boolean hasRaw() {
		return rawExists;
	}
	
	/**
	 * Chances the modified-status, i.e. if the picture 
	 * has been modified or not.
	 */
	public void chanceModifiedStatus() {
		if (!modifiedChanged)
			oldModified = modified;
		modified = !modified;
		changed = !changed;
		modifiedChanged = !modifiedChanged;
	}
	
	/**
	 * Returns the modified-status.
	 * 
	 * @return Returns true if the image is modified.
	 */
	public Boolean isModified() {
		return modified;
	}
	
	/**
	 * Chances the pictures name.
	 * 
	 * @param newName The new name
	 */
	public void setName(String newName) {
		if (!nameChanged)
			oldName  = name;
		name = newName;
		changed = true;
		nameChanged = true;
	}
	
	/**
	 * Returns the name of the picture.
	 * 
	 * @return The picture-name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Chances the file-path.
	 * 
	 * @param newPath The new file path
	 */
	public void setPath(String newPath){
		if (!pathChanged)
			oldPath = path;
		path = newPath;
		changed = true;
		pathChanged = true;
	}
	
	/**
	 * Returns the path to the picture.
	 * 
	 * @return The file-path
	 */
	public String getPath(){
		return path;
	}
	
	/**
	 * Chances the file extension of the file.
	 * 
	 * @param ext The new file-extension
	 */
	public void setFileExtension(String ext) {
		if (!fileExtensionChanged)
			oldFileExtension = fileExtension;
		fileExtension =ext;
		changed = true;
		fileExtensionChanged = true;
	}
	
	/**
	 * Returns the file extension, i.e. the type of the file.
	 * 
	 * @return The file Extension
	 */
	public String getFileExtension() {
		return fileExtension;
	}
	
	/**
	 * Chances the date related to the file.
	 * 
	 * @param newDate The new date
	 */
	public void setFileDate(String newDate) {
		if (!fileDateChanged)
			oldFileDate = fileDate;
		fileDate = newDate;
		changed = true;
		fileDateChanged = true;
	}
	
	/**
	 * Returns the date related to the file.
	 * 
	 * @return The file-date
	 */
	public String getFileDate() {
		return fileDate;
	}
	
	/**
	 * Returns the pictures id used in the database
	 * @return The pictures id-number
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * When the new values has been successfully updated
	 * use this method to tell the picture-object this. 
	 */
	public void updated() {
		oldName = name;
	    oldPath = path;
	    oldPrintedFor = printedFor;
		oldPrinted = printed;
		oldRawExists = rawExists;
		oldModified = modified;
		oldDescriptions = descriptions;
		oldFileExtension = fileExtension;
		oldFileDate = fileDate;
		changed = false;
		nameChanged = false; pathChanged = false; 
		printedChanged = false; printedForChanged = false;
		fileDateChanged = false; rawChanged = false;
		descriptionChanged = false;
		modifiedChanged = false;
		fileExtensionChanged = false;
	}
	
	/**
	 * Restores the chanced values to the last saved values.
	 */
	public void reset(){
		name = oldName;
	    path = oldPath;
	    printedFor = oldPrintedFor;
		printed = oldPrinted;
		rawExists = oldRawExists;
		modified = oldModified;
		descriptions = oldDescriptions;
		fileExtension = oldFileExtension;
		fileDate = oldFileDate;
		changed = false;
		nameChanged = false; pathChanged = false; 
		printedChanged = false; printedForChanged = false;
		fileDateChanged = false; rawChanged = false;
		descriptionChanged = false;
		modifiedChanged = false;
		fileExtensionChanged = false;	
	}
	
	/**
	 * A method to check if the file-name has been changed.
	 * 
	 * @return True if it has been changed
	 */
	public Boolean hasNameChanged() {
		return nameChanged;
	}
	
	/**
	 * A method to check if the file-extension has 
	 * been changed.
	 * 
	 * @return True if it has been changed
	 */
	public Boolean hasFileExtensionChanged() {
		return fileExtensionChanged;
	}
	
	/**
	 * A method to check if the file-path has been changed.
	 * 
	 * @return True if it has been changed
	 */
	public Boolean hasPathChanged() {
		return pathChanged;
	}
	
	/**
	 * A method to check if the printed-value has 
	 * been changed.
	 * 
	 * @return True if it has been changed
	 */
	public Boolean hasPrintedChanged() {
		return printedChanged;
	}
	
	/**
	 * A method to check if the reason for printing
	 * the picture has been changed.
	 * 
	 * @return True if it has been changed
	 */
	public Boolean hasPrintedForChanged() {
		return printedForChanged;
	}
	
	/**
	 * A method to check if the file-date has been changed.
	 * 
	 * @return True if it has been changed
	 */
	public Boolean hasFileDateChanged() {
		return fileDateChanged;
	}
	
	/**
	 * A method to check if the raw-value has been changed.
	 * 
	 * @return True if it has been changed
	 */
	public Boolean hasRawChanged() {
		return rawChanged;
	}
	
	/**
	 * A method to check if the value of modified 
	 * has been changed.
	 * 
	 * @return True if it has been changed
	 */
	public Boolean hasModifiedChanged() {
		return modifiedChanged;
	}
	
	/**
	 * A method to check if some of the words describing the
	 * picture has been changed.
	 * 
	 * @return True if it has been changed
	 */
	public Boolean hasDescriptionChanged() {
		return descriptionChanged;
	}
	
	/**
	 * The toString-method, returns info on the file in the format:
	 * name.extension		date
	 * 
	 * e.g. (if the date is implemented as yy-mm-dd:
	 * Picture.jpg	11-12-01
	 */
	public String toString() {
		return name + "." + fileExtension + "\t" + fileDate;
	}
}
