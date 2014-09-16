package contacts.resource.service;

import java.util.HashMap;


//public class ContactsDAO {
//	private HashMap<Long, Contact> contacts;
//	private Long nextID;
//	public ContactsDAO(){
//		contacts = new HashMap<Long, Contact>();
//		contacts.put(1L, new Contact("Test","test@abc.com"));
//		nextID = 100L;
//	}
//	
//	public Contact find(Long id){
//		return contacts.get(id);
//	}
//	public Contact find(String id){
//		return contacts.get(Long.parseLong(id));
//	}
//	
//	
//	public HashMap<Long, Contact> findAll(){
//		return contacts;
//	}
//	
//	public boolean delete(Long id){
//		if(contacts.remove(id) != null) return true;
//		return false;
//	}
//	
//	public boolean save(Contact contact){
//		if(Long.parseLong(contact.getId()) == 0){
//			contact.setId(this.getUniqueId()+"");
//			contacts.put(Long.parseLong(contact.getId()),contact );
//			return true;
//		}
//		if(find(Long.parseLong(contact.getId()))==null){
//			contacts.put(Long.parseLong(contact.getId()),contact );
//			return true;
//		}
//		return false;
//	}
//	
//	public boolean update(Contact update){
//		Contact contact = find(Long.parseLong(update.getId()));
//		if(contact != null){
//			contact.applyUpdate(update);
//			return true;
//		}
//		return false;
//	}
//	
//	private synchronized long getUniqueId() {
//		long id = nextID++;
//		while(id < Long.MAX_VALUE){
//			if(find(id)==null)return id;
//			id = nextID++;
//		}
//		return id;
//	}
//}
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Data access object for saving and retrieving contacts.
 * This DAO uses an in-memory list of person.
 * Use DaoFactory to get an instance of this class, such as:
 * dao = DaoFactory.getInstance().getContactDao()
 * 
 * @author jim
 */
public class ContactsDAO {
	private List<Contact> contacts;
	private AtomicLong nextId;
	
	public ContactsDAO() {
		contacts = new ArrayList<Contact>();
		nextId = new AtomicLong(1000L);
	}
	
	/** Find a contact by ID in contacts.
	 * @param the id of contact to find
	 * @return the matching contact or null if the id is not found
	 */
	public Contact find(long id) {
		for(Contact c : contacts) 
			if (Long.parseLong(c.getId()) == id) return c;
		return null;
	}

	public List<Contact> findAll() {
		return java.util.Collections.unmodifiableList(contacts);
	}

	/**
	 * Delete a saved contact.
	 * @param id the id of contact to delete
	 * @return true if contact is deleted, false otherwise.
	 */
	public boolean delete(long id) {
		for(int k=0; k<contacts.size(); k++) {
			if (Long.parseLong(contacts.get(k).getId()) == id) {
				contacts.remove(k);
				return true;
			}
		}
		return false;
	}
	
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
	public boolean save(Contact contact) {
		if ((Long.parseLong(contact.getId()) == 0)) {
			contact.setId( getUniqueId()+"" );
			return contacts.add(contact);
		}
		// check if this contact is already in persistent storage
		Contact other  = find(Long.parseLong(contact.getId()));
		if (other == contact) return true;
		if ( other != null ) contacts.remove(other);
		return contacts.add(contact);
	}

	/**
	 * Update a Contact.  Only the non-null fields of the
	 * update are applied to the contact.
	 * @param update update info for the contact.
	 * @return true if the update is applied successfully.
	 */
	public boolean update(Contact update) {
		Contact contact = find(Long.parseLong(update.getId()));
		if (contact == null) return false;
		contact.applyUpdate(update);
		save(contact);
		return true;
	}
	
	/**
	 * Get a unique contact ID.
	 * @return unique id not in persistent storage
	 */
	private synchronized long getUniqueId() {
		long id = nextId.getAndAdd(1L);
		while( id < Long.MAX_VALUE ) {	
			if (find(id) == null) return id;
			id = nextId.getAndAdd(1L);
		}
		return id; // this should never happen
	}
	
	public List<Contact> getByQuery(String s){
		List<Contact> out = new ArrayList<Contact>();
		String rex = ".*\\b"+s+"\\b.*";
		for(int i =0;i<contacts.size();i++){
			if(contacts.get(i).getTitle().matches(rex)) out.add(contacts.get(i));
		}
		return out;
	}
}