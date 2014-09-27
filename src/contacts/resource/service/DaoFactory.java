package contacts.resource.service;

import contacts.resource.service.jpa.JpaDaoFactory;
import contacts.resource.service.mem.MemDaoFactory;

/**
 * Abstract class for DaoFactory
 * @author Thunyathon Jaruchotrattanasakul 55105469782
 *
 */
public abstract class DaoFactory {
	private static DaoFactory factory;
	protected DaoFactory(){
		
	}
	/**
	 * Get Factory
	 * @return factory
	 */
	public static DaoFactory getInstance() {
		if (factory == null) factory = new MemDaoFactory();
		return factory;
	}
	/**
	 * Get Contact Dao
	 * @return contactDAO
	 */
	public abstract ContactDao getContactDao();
	/**
	 * Shut the factory down
	 */
	public abstract void shutdown();
}
