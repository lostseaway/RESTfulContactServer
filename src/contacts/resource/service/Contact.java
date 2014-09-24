package contacts.resource.service;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contact class
 * @author Thunyathon Jaruchotrattanasakul 55105469782
 *
 */
@XmlRootElement(name="contact")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Contact {
	private static long nextId = 1_000_000_000L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@XmlAttribute
	private String id;
	private String title;
	private String name;
	private String email;
	private String phoneNumber;
	
	

	public Contact(){
		this("","","","");
	}
	public Contact(String title,String name,String email,String phone){
		this.title = title;
		this.name = name;
		this.email = email;
		this.phoneNumber = phone;
		this.id = Long.toString(getNextId());
	}
	private long getNextId() {
		return nextId++;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * update detail by resiving id.
	 * @param contact
	 */
	public void applyUpdate(Contact contact){
		this.name = contact.getName();
		this.title = contact.getTitle();
		this.email = contact.getEmail();
		this.phoneNumber = contact.getPhoneNumber();
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
