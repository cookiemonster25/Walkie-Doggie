package doggie.user.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import doggie.animals.model.AnimalModel;
import doggie.user.model.User;
import doggie.user.model.UserRole;

@Repository
@Transactional
public interface UserDao extends JpaRepository<User, Integer> {

	List<User> findByUserName(String userName);
	
	List<User> findAllByUserRoles(UserRole role);
	
	List<User> findByIdAndUserRoles(int id, UserRole role);
	
	List<User> findByFavourites(AnimalModel animal);
}
