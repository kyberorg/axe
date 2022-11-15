package io.kyberorg.yalsee.mail.letters;

import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.services.mail.LetterType;
import io.kyberorg.yalsee.users.UsernameGenerator;

import java.util.Map;

public interface Letter {
    LetterType getLetterType();

    String getSubject();

    String getLink();

    Map<String, Object> getTemplateVars();

    default String getUsername(final Token token) {
        String username = token.getUser().getUsername();
        return UsernameGenerator.isGenerated(username) ? "New Yalsee User" : username;
    }
}
