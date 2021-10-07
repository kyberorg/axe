package io.kyberorg.yalsee.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

/**
 * Links table mapped Java Object.
 *
 * @since 1.0
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Link link = (Link) o;
        return Objects.equals(id, link.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
