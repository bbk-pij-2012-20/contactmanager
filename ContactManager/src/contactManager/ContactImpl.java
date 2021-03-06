package contactManager;

import java.io.Serializable;

/**
 * This class implements a contact that has a name, notes and a unique ID
 * and can be added to meetings. 
 * All contacts are stored in the ContactManagerImpl class.
 * 
 * @author Shahin Zibaee 24th Jan 2014
 *
 */
@SuppressWarnings("serial")
public class ContactImpl implements Contact,Serializable {
	private int id;
	private String name;
	private String notes = "";
	
	/**
	 * This constructs a contact with a specified name, note and id.
	 * 	
	 * @param name	the full name of the contact
	 * @param note	the textual note associated with the contact
	 * @param id		the unique id number of the contact 
	 */
	public ContactImpl(String name, String note, int id) {
		this.name = name;
		addNotes(note);
		setId(id);
	}
	
	/**
	 * This constructs a contact with a specified name.
	 * 
	 * @param name	the full name of the contact, e.g. "Shahin Zibaee" 
	 * 
	 * (IS THIS METHOD USED OTHER THAN IN TEST?)
	 * nope! It's used only to show that an IllegalArgumentException is thrown by 
	 * AddFutureMeeting() when a contact has not been properly added via 
	 * AddNewContact(), but rather through this constructor. (..am wondering if it
	 * should not be NullPointerException instead...)
	 */
	public ContactImpl(String name) {
		this.name = name;
		notes = "";
	}
	
	/**
	 * This constructs a contact with a specified id.
	 * 
	 * @param id		the unique id number of the contact
	 * 
	 */	
	public ContactImpl(int id) {
		setId(id);
	}
	
	/**
	 * An empty constructor.
	 */
	public ContactImpl(){}
	
	/**
	 * This private method sets the id number of the contact.
	 * 
	 * @param id		the unique id number of the contact
	 * 	
	 */
	private void setId(int id) {
		this.id = id;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getNotes() {
		return notes;
	}
	
	@Override
	public void addNotes(String note) {
		notes += note; 
	}
}