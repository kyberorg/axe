package ee.yals.models.repo;

import ee.yals.models.User;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends Repository<User, Long> {
    Optional<User> findSingleByAlias(String alias);

    User save(User userToSave);

    List<User> findAll();
}
