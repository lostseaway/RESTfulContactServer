package contacts.resource.service.mem;

import contacts.resource.service.DaoFactory;

/**
 * Factory of ContactsDAO
 * @author Thunyathon Jaruchotrattanasakul 55105469782
 *
 */
public class MemDaoFactory extends DaoFactory {
	private static MemDaoFactory factory;
	private MemContactsDAO daoInstance;
	
	public MemDaoFactory() {
		daoInstance = new MemContactsDAO();
	}
	
	public static MemDaoFactory getInstance() {
		if (factory == null) factory = new MemDaoFactory();
		return factory;
	}
	
	public MemContactsDAO getContactDao() {
		return daoInstance;
	}
}