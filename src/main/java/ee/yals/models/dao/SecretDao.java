package ee.yals.models.dao;

import ee.yals.models.Secret;
import ee.yals.models.User;
import ee.yals.models.repo.SecretRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SecretDao {

    @Autowired
    private SecretRepo secretRepo;

    public Optional<Secret> findSingleByUser(User user) {
        return secretRepo.findSingleByUser(user);
    }

    public Secret save(Secret secretToSave) {
        //TODO check on user
        return secretRepo.save(secretToSave);
    }

    @SuppressWarnings("unused")
    //Used in tests
    public List<Secret> findAll() {
        return secretRepo.findAll();
    }
}
