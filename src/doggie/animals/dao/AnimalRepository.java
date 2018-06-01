package doggie.animals.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import doggie.animals.model.AnimalModel;

@Repository
@Transactional
public interface AnimalRepository extends JpaRepository<AnimalModel, Integer> {

	AnimalModel findById(int searchString);
	
}