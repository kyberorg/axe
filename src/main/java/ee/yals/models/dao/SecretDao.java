package ee.yals.models.dao;

import ee.yals.exceptions.ElementAlreadyExistsException;
import ee.yals.exceptions.UpdatePasswordException;
import ee.yals.models.Secret;
import ee.yals.models.User;
import ee.yals.utils.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Component
public class SecretDao {

    @Autowired
    private SecretRepo secretRepo;

    @Autowired
    private Password.Encryptor passwordEncryptor;

    public Optional<Secret> findSingleByUser(User user) {
        return secretRepo.findSingleByUser(user);
    }


    public Secret save(Secret secretToSave) {

        User secretOwner = secretToSave.getUser();
        Optional<Secret> currentSecret = findSingleByUser(secretOwner);
        if (currentSecret.isPresent()) {
            boolean isThisNewSecret = secretToSave.getId() != currentSecret.get().getId();
            if (isThisNewSecret) {
                throw new ElementAlreadyExistsException("Cannot create more that one " + Secret.class.getSimpleName()
                        + " for one " + User.class.getSimpleName());
            }
        }
        encryptPassword(secretToSave);
        return secretRepo.save(secretToSave);
    }

    @SuppressWarnings("unused")
    //Used in tests
    public List<Secret> findAll() {
        return secretRepo.findAll();
    }

    private void encryptPassword(Secret secret) {
        String fieldName = "password";
        try {
            Field passField = secret.getClass().getDeclaredField(fieldName);
            passField.setAccessible(true);
            passField.set(secret, passwordEncryptor.encrypt(secret.getPassword()));
        } catch (NoSuchFieldException e) {
            throw new UpdatePasswordException("No such field with name: '" + fieldName + "'");
        } catch (IllegalAccessException e) {
            throw new UpdatePasswordException("Unable to update password field. Reason: " + e.getMessage(), e);
        }
    }
}
