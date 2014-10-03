package contacts.resource.service;

import java.util.List;

/**
 * ContactDao interface
 * @author Thunyathon Jaruchotrattanasakul 55105469782
 *
 */
public interface ContactDao {
	
	/** Find a contact by ID in contacts.
	 * @param the id of contact to find
	 * @return the matching contact or null if the id is not found
	 */
	public Contact find(long id) ;
	
	
	public Contacts findAll();
	
	/**
	 * Delete a saved contact.
	 * @param id the id of contact to delete
	 * @return true if contact is deleted, false otherwise.
	 */
	public boolean delete(long id);
	
	/**
	 * Save or replace a contact.
	 * If the contact.id is 0 then it is assumed to be a
	 * new (not saved) contact.  In this case a unique id
	 * is assigned to the contact.  
	 * If the contact.id is not zero and the contact already
	 * exists in saved contacts, the old contact is replaced.
	 * @param contact the contact to save or replace.
	 * @return true if saved successfully
	 */
	public boolean save(Contact contact);
	
	/**
	 * Update a Contact.  Only the non-null fields of the
	 * update are applied to the contact.
	 * @param update update info for the contact.
	 * @return true if the update is applied successfully.
	 */
	public boolean update(Contact update);
	
	/**
	 * Get Contact by checking subString 
	 * @param s sub-Srting
	 * @return
	 */
	public Contacts getByQuery(String s);
}
