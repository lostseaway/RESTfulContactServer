package contacts.resource.service.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import contacts.resource.service.Contact;
import contacts.resource.service.ContactDao;

/**
 * The Contacts DAO that use JPA
 * @author Thunyathon Jaruchotrattanasakul 55105469782
 *
 */
public class JpaContactsDao implements ContactDao {
	private final EntityManager em;
// This class isn't an entity so JAX isn't going to inject anything.
// The @Context is useless here.  uriInfo doesn't seem to be used, either.
	@Context
	UriInfo uriInfo;
	public JpaContactsDao(EntityManager em) {
		this.em = em;
	}
	@Override
	public Contact find(long id) {
		return em.find(Contact.class, id);
	}

	@Override
	public List<Contact> findAll() {
		Query query = em.createQuery("SELECT a FROM Contact a");
		List<Contact> list = query.getResultList();
		return list;
	}

	@Override
	public boolean delete(long id) {
		EntityTransaction et = em.getTransaction();
		Contact contact = em.find(Contact.class, id);
		if(contact==null) return false;
		em.remove(contact);
		et.commit();
		return true;
	}

	@Override
	public boolean save(Contact contact) {
// Must use try catch and rollback
		if (contact == null) throw new IllegalArgumentException("NULL contact");
		Contact con = em.find(Contact.class,contact.getId());
		if(con!=null)return false;
		EntityTransaction et = em.getTransaction();
		et.begin();
		em.persist(contact);
		et.commit();
		return true;
	}

	@Override
	public boolean update(Contact update) {
// Must use try catch and rollback
		EntityTransaction et = em.getTransaction();
		et.begin();
		Contact contact = em.find(Contact.class, update.getId());
		if(contact == null){
			et.commit();
			return false;
		}
// Don't set attributes of contact. Too much coupling.
// Just use em.merge( update )
		if(update.getName()!=null) contact.setName(update.getName());
		if(update.getEmail()!=null) contact.setEmail(update.getEmail());
		if(update.getPhoneNumber()!=null) contact.setPhoneNumber(update.getPhoneNumber());
		if(update.getTitle()!=null) contact.setTitle(update.getTitle());
		et.commit();
		return true;
	}

	@Override
	public List<Contact> getByQuery(String s) {
		Query query = em.createQuery("SELECT c FROM Contacts c WHERE LOWER(c.title) LIKE :title");
		query.setParameter("title", "%"+s.toLowerCase()+"%");
		List<Contact> list = query.getResultList();
		return list;
	}

}
