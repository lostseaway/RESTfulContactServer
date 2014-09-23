package contacts.resource.service.mem;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import contacts.resource.service.Contact;
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

	@Override
	public void shutdown() {
		List<Contact> contacts = daoInstance.findAll();
		Contacts exportContacts = new Contacts();
		exportContacts.setContacts( contacts );

		try {
			JAXBContext context = JAXBContext.newInstance( Contacts.class );
			File outputFile = new File( "tmp/ContactPersistant.xml" );
			Marshaller marshaller = context.createMarshaller();	
			marshaller.marshal( exportContacts, outputFile );
		} catch ( JAXBException e ) {
			e.printStackTrace();
		}
		
	}
}