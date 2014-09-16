package contacts.resource.service;

/**
 * Factory of ContactsDAO
 * @author Thunyathon Jaruchotrattanasakul 55105469782
 *
 */
public class DaoFactory {
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