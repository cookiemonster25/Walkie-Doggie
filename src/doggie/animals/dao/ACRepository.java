package doggie.animals.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import doggie.animals.model.AC;
import doggie.animals.model.AnimalModel;

@Repository
@Transactional
public interface ACRepository extends JpaRepository<AC, Integer> {

	List<AC> findAllByAnimal(AnimalModel animal);
	
}
