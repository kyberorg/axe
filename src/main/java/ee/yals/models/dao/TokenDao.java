package ee.yals.models.dao;

import ee.yals.models.Token;
import ee.yals.models.repo.TokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TokenDao {

    @Autowired
    private TokenRepo tokenRepo;

    public Optional<Token> findSingleByToken(String token) {
        return tokenRepo.findSingleByToken(token);
    }

    public Token save(Token tokenToSave) {
        //TODO set expiration
        return tokenRepo.save(tokenToSave);
    }


}
