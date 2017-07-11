package ee.yals.models;

import javax.persistence.*;

/**
 * Links table mapped Java Object
 *
 * @since 1.0
 */
@Entity
@Table(name = "links")
public class Link {

    public static final String IDENT_COLUMN = "ident";
    public static final String LINK_COLUMN = "link";

    @Id
    @GeneratedValue
    private long id;

    @Column(name = IDENT_COLUMN, nullable = false)
    private String ident;
    @Column(name = LINK_COLUMN, nullable = false)
    private String link;

    public long getId() {
        return id;
    }

    public String getIdent() {
        return ident;
    }

    public String getLink() {
        return link;
    }

    public Link setIdent(String ident) {
        this.ident = ident;
        return this;
    }

    public Link setLink(String link) {
        this.link = link;
        return this;
    }

    public static Link create(String ident, String link) {
        Link linkObject = new Link();
        linkObject.ident = ident;
        linkObject.link = link;
        return linkObject;
    }
}
