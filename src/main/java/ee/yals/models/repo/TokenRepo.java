package ee.yals.models.repo;

import ee.yals.models.Token;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface TokenRepo extends Repository<Token, Long> {
    Optional<Token> findSingleByToken(String token);

    Token save(Token tokenToSave);

}
