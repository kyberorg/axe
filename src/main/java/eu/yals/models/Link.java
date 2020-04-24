package eu.yals.models;

import lombok.Data;

import javax.persistence.*;

/**
 * Links table mapped Java Object.
 *
 * @since 1.0
 */
@Data
@Entity
@Table(name = "links")
public class Link {

    private static final String IDENT_COLUMN = "ident";
    private static final String LINK_COLUMN = "link";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = IDENT_COLUMN, nullable = false)
    private String ident;
    @Column(name = LINK_COLUMN, nullable = false)
    private String link;

    /**
     * Builder-like. Setter for ident.
     *
     * @param ident string with part which identifies short link
     * @return under-constructor {@link Link} object
     */
    public Link setIdent(final String ident) {
        this.ident = ident;
        return this;
    }

    /**
     * Builder-like. Setter for link.
     *
     * @param link string with long url
     * @return under-constructor {@link Link} object
     */
    public Link setLink(final String link) {
        this.link = link;
        return this;
    }

    /**
     * Static constructor of {@link Link} object.
     *
     * @param ident string with part which identifies short link
     * @param link  string with long url
     * @return {@link Link} object
     */
    public static Link create(final String ident, final String link) {
        Link linkObject = new Link();
        linkObject.ident = ident;
        linkObject.link = link;
        return linkObject;
    }

    /**
     * Shows {@link Link} object as string.
     *
     * @return string containing object name and object fields with their names
     */
    @Override
    public String toString() {
        return String.format("%s [ ident: %s, shortLink: %s ]", Link.class.getSimpleName(), ident, link);
    }
}
