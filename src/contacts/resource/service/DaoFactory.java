package contacts.resource.service;

public class DaoFactory {
	// singleton instance of this factory
	private static DaoFactory factory;
	private ContactsDAO daoInstance;
	
	private DaoFactory() {
		daoInstance = new ContactsDAO();
	}
	
	public static DaoFactory getInstance() {
		if (factory == null) factory = new DaoFactory();
		return factory;
	}
	
	public ContactsDAO getContactDao() {
		return daoInstance;
	}
}