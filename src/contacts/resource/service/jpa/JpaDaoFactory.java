package contacts.resource.service.jpa;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


import contacts.resource.service.ContactDao;
import contacts.resource.service.DaoFactory;

/**
 * DAO Factory
 * @author Thunyathon Jaruchotrattanasakul 55105469782
 *
 */
public class JpaDaoFactory extends DaoFactory
{	
	private static final String PERSISTENCE_UNIT = "contacts";
	private static JpaDaoFactory factory;
	private JpaContactsDao dao;
	private final EntityManagerFactory emf;
	private EntityManager em;
	private static Logger logger;
	
	static {
		logger = Logger.getLogger(JpaDaoFactory.class.getName());
	}
	
	public JpaDaoFactory(){
		emf = Persistence.createEntityManagerFactory("contacts");
		System.out.println(emf.toString());
		em = emf.createEntityManager();
		dao = new JpaContactsDao(em);
	}
	@Override
	public ContactDao getContactDao() {
		return dao;
	}
	@Override
	public void shutdown() {
		try {
			if ( em != null && em.isOpen() ) {
				em.close();
			}
			if ( emf != null && emf.isOpen() ) {
				emf.close();
			}
		} catch ( IllegalStateException ex ) {
			logger.log( Level.SEVERE, ex.toString() );
		}
		
	}
	

}
