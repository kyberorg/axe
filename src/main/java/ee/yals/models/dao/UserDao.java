package ee.yals.models.dao;

import ee.yals.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserDao {

    @Autowired
    private UserRepo userRepo;

    public Optional<User> findSingleByAlias(String alias) {
        return userRepo.findSingleByAlias(alias);
    }

    public User save(User userToSave) {
        Long now = System.currentTimeMillis();
        if (isNewRecord(userToSave)) {
            userToSave.setCreatedAt(now);
        }
        userToSave.updateUpdatedWith(now);
        return userRepo.save(userToSave);
    }

    @SuppressWarnings("unused") //Used in tests
    public List<User> findAll() {
        return userRepo.findAll();
    }

    private boolean isNewRecord(User userToCheck) {
        return !userRepo.findSingleById(userToCheck.getId()).isPresent();
    }
}
