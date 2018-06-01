package doggie.animals.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "animals")
public class AnimalModel implements java.io.Serializable {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false, length = 30)
	private String name;

	@Column(nullable = false, length = 30)
	private String breed;

	@Column(nullable = false, length = 30)
	private String color;

	@Column(nullable = false)
	private int age;

	@Column(nullable = false, length = 20)
	private String gender;

	@Column(nullable = false)
	private boolean castrated;

	@Column(nullable = false)
	private String description;
	
	
	
	//Relationship
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	private Species species;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	private List<Vaccination> vaccinations;
	
	@OneToMany(mappedBy="animal", fetch = FetchType.LAZY)
	private Set<AC> acs;

	@Version
	long version;
	
	
	
	//Constructor

	public AnimalModel() {
	}

	public AnimalModel(String name, String breed, String color, int age, String gender, boolean castrated,
			String description) {
		super();
		this.name = name;
		this.breed = breed;
		this.color = color;
		this.age = age;
		this.gender = gender;
		this.castrated = castrated;
		this.description = description;
	}
	
	
	
	
	//Getter + Setter

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBreed() {
		return breed;
	}

	public void setBreed(String breed) {
		this.breed = breed;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public boolean isCastrated() {
		return castrated;
	}

	public void setCastrated(boolean castrated) {
		this.castrated = castrated;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Species getSpecies() {
		return species;
	}

	public void setSpecies(Species species) {
		this.species = species;
	}

	public List<Vaccination> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(List<Vaccination> vaccinations) {
		this.vaccinations = vaccinations;
	}
	
	public void addVaccination(Vaccination vaccination) {
		if (vaccinations == null) {
			vaccinations = new ArrayList<Vaccination>();
		}
		vaccinations.add(vaccination);
	}

	public Set<AC> getAcs() {
		return acs;
	}

	public void setAcs(Set<AC> acs) {
		this.acs = acs;
	}
}
