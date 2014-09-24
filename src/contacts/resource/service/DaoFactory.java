package contacts.resource.service;

import contacts.resource.service.jpa.JpaDaoFactory;
import contacts.resource.service.mem.MemDaoFactory;

public abstract class DaoFactory {
	private static DaoFactory factory;
	protected DaoFactory(){
		
	}
	public static DaoFactory getInstance() {
		if (factory == null) factory = new MemDaoFactory();
		return factory;
	}
	public abstract ContactDao getContactDao();
	public abstract void shutdown();
}
