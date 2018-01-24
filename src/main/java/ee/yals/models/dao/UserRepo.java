package ee.yals.models.dao;

import ee.yals.models.User;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

interface UserRepo extends Repository<User, Long> {
    Optional<User> findSingleByAlias(String alias);

    Optional<User> findSingleById(Long id);

    User save(User userToSave);

    List<User> findAll();
}
