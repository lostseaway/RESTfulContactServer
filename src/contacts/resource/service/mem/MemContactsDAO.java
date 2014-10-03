package contacts.resource.service.mem;

import java.io.File;
import java.util.HashMap;



import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import contacts.resource.service.Contact;
import contacts.resource.service.ContactDao;
import contacts.resource.service.Contacts;


/**
 * Data access object for saving and retrieving contacts.
 * This DAO uses an in-memory list of person.
 * Use DaoFactory to get an instance of this class, such as:
 * dao = DaoFactory.getInstance().getContactDao()
 * 
 * @author jim
 * @edit Thunyathon Jaruchotratanasakul 5510546972
 */
public class MemContactsDAO implements ContactDao {
	private List<Contact> contacts;
	private AtomicLong nextId;
	
	public MemContactsDAO() {
		contacts = new ArrayList<Contact>();
		nextId = new AtomicLong(1000L);
//		this.save(new Contact("Title","Name","email@mail.com","0899999999"));
		this.loadContacts();
	}
	
	
	public Contact find(long id) {
		for(Contact c : contacts) 
			if (Long.parseLong(c.getId()) == id) return c;
		return null;
	}

	public Contacts findAll() {
		Contacts contacts = new Contacts();
		contacts.setContacts(this.contacts);
		return contacts;
	}
	
	private void loadContacts() {
		try {
			Contacts importContacts = new Contacts();
			JAXBContext context = JAXBContext.newInstance( Contacts.class ) ;
			File inputFile = new File("ContactPersistant.xml" );
			if(!inputFile.exists()){
				System.out.println("eiei");
				Marshaller marshaller = context.createMarshaller();	
				marshaller.marshal( new Contacts(), inputFile );
			}
			else{
				Unmarshaller unmarshaller = context.createUnmarshaller();	
				importContacts = (Contacts) unmarshaller.unmarshal( inputFile );
				if(importContacts.getContacts() == null)return;
				for ( Contact contact : importContacts.getContacts() ) {
					contacts.add(contact);
				}
			}
		} catch ( JAXBException e ) {
			e.printStackTrace();
		}
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
	
	/**
	 * Get Contact by checking subString 
	 * @param s sub-Srting
	 * @return
	 */
	public List<Contact> getByQuery(String s){
		List<Contact> out = new ArrayList<Contact>();
//		String rex = ".*\\b"+s+"\\b.*";
		for(int i =0;i<contacts.size();i++){
			if(contacts.get(i).getTitle().contains(s)) out.add(contacts.get(i));
		}
		return out;
	}
}