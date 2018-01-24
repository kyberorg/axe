package ee.yals.models;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Objects;

/**
 * Links table mapped Java Object
 *
 * @since 1.0
 */
@Entity
@Table(name = "links")
public class Link {

    private static final String IDENT_COLUMN = "ident";
    private static final String LINK_COLUMN = "link";
    private static final String OWNER_COLUMN = "owner";

    @Id
    @GeneratedValue
    private long id;

    @Column(name = IDENT_COLUMN, nullable = false)
    private String ident;

    @Column(name = LINK_COLUMN, nullable = false)
    private String link;

    @JoinColumn(name = OWNER_COLUMN, nullable = false)
    @ManyToOne
    private User owner;

    public long getId() {
        return id;
    }

    public String getIdent() {
        return ident;
    }

    public String getLink() {
        return link;
    }

    public User getOwner() {
        return owner;
    }

    public static Builder store(String link) {
        return new Builder(link);
    }

    public static class Builder {
        private String ident;
        private String link;
        private User owner;

        Builder(String link) {
            this.link = link;
        }

        public Builder withIdent(String ident) {
            this.ident = ident;
            return this;
        }

        public Builder andOwner(User owner) {
            this.owner = owner;
            return this;
        }

        public Link please() throws IllegalStateException {
            if (StringUtils.isBlank(ident)) {
                throw new IllegalStateException("Ident for this link is not set. Cannot create " + Link.class.getSimpleName() +
                        "Did you use withIdent() method?");
            }
            if (StringUtils.isBlank(link)) {
                throw new IllegalStateException("Link is NULL or empty. Cannot create " + Link.class.getSimpleName());
            }
            if (Objects.isNull(owner)) {
                throw new IllegalStateException("Owner for this link is not set. Cannot create " + Link.class.getSimpleName() +
                        "Did you use andOwner() method?");
            }

            Link linkObject = new Link();
            linkObject.ident = ident;
            linkObject.link = link;
            linkObject.owner = owner;

            return linkObject;
        }
    }
}
