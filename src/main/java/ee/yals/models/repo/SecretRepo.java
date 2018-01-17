package ee.yals.models.repo;

import ee.yals.models.Secret;
import ee.yals.models.User;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface SecretRepo extends Repository<Secret, Long> {
    Optional<Secret> findSingleByUser(User user);

    Secret save(Secret secretToSave);

    @SuppressWarnings("unused")
        //Used in tests
    List<Secret> findAll();

}
