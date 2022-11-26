package pm.axe.internal;

import lombok.*;
import pm.axe.db.models.User;
import pm.axe.services.LinkService;

/**
 * Input for {@link LinkService}'s {@link LinkService#createLink(LinkServiceInput)}.
 *
 * @since 3.2.1
 */
@Builder(builderMethodName = "internalBuilder")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class LinkServiceInput {
    @NonNull
    private String link;
    private String customIdent;
    private String sessionID;
    private String description;
    private User linkOwner;

    /**
     * Creates builder with required parameter set.
     *
     * @param link string with link to save
     * @return {@link LinkServiceInputBuilder} with {@link #link} set to continue building {@link LinkServiceInput}.
     */
    public static LinkServiceInputBuilder builder(final String link) {
        return internalBuilder().link(link);
    }

}
