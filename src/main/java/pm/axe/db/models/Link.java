package pm.axe.db.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
public class Link extends BaseModel {

    @Column(name = "ident", nullable = false)
    private String ident;
    @Column(name = "link", nullable = false)
    private String link;

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
}
