package contacts.resource.service;

import java.util.HashMap;


public class ContactsDAO {
	private HashMap<Long, Contact> contacts;
	private Long nextID;
	public ContactsDAO(){
		contacts = new HashMap<Long, Contact>();
		contacts.put(1L, new Contact("Test","test@abc.com"));
		nextID = 100L;
	}
	
	public Contact find(Long id){
		return contacts.get(id);
	}
	public Contact find(String id){
		return contacts.get(Long.parseLong(id));
	}
	
	
	public HashMap<Long, Contact> findAll(){
		return contacts;
	}
	
	public boolean delete(Long id){
		if(contacts.remove(id) != null) return true;
		return false;
	}
	
	public boolean save(Contact contact){
		if(Long.parseLong(contact.getId()) == 0){
			contact.setId(this.getUniqueId()+"");
			contacts.put(Long.parseLong(contact.getId()),contact );
			return true;
		}
		if(find(Long.parseLong(contact.getId()))==null){
			contacts.put(Long.parseLong(contact.getId()),contact );
			return true;
		}
		return false;
	}
	
	public boolean update(Contact update){
		Contact contact = find(Long.parseLong(update.getId()));
		if(contact != null){
			contact.applyUpdate(update);
			return true;
		}
		return false;
	}
	
	private synchronized long getUniqueId() {
		long id = nextID++;
		while(id < Long.MAX_VALUE){
			if(find(id)==null)return id;
			id = nextID++;
		}
		return id;
	}
}
