package ee.yals.models.dao;

import ee.yals.models.Token;
import ee.yals.models.User;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface TokenRepo extends Repository<Token, Long> {
    Optional<Token> findSingleByOwner(User owner);

    Token save(Token tokenToSave);

    List<Token> findAll();

    long count();
}
