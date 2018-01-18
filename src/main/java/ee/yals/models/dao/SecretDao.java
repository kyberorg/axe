package ee.yals.models.dao;

import ee.yals.exceptions.ElementAlreadyExistsException;
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

    public Secret save(Secret secretToSave) throws ElementAlreadyExistsException {

        User secretOwner = secretToSave.getUser();
        Optional<Secret> currentSecret = findSingleByUser(secretOwner);
        if (currentSecret.isPresent()) {
            boolean isThisNewSecret = secretToSave.getId() != currentSecret.get().getId();
            if (isThisNewSecret) {
                throw new ElementAlreadyExistsException("Cannot create more that one " + Secret.class.getSimpleName()
                        + " for one " + User.class.getSimpleName());
            }
        }

        return secretRepo.save(secretToSave);
    }

    @SuppressWarnings("unused")
    //Used in tests
    public List<Secret> findAll() {
        return secretRepo.findAll();
    }

}
