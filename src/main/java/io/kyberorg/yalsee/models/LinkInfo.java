package io.kyberorg.yalsee.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;


/**
 * LinkInfo table mapped Java Object.
 *
 * @since 3.2
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "link_info")
public class LinkInfo extends TimeModel {
    private static final String IDENT_COLUMN = "ident";
    private static final String DESCRIPTION_COLUMN = "description";
    private static final String SESSION_COLUMN = "session";

    @Column(name = IDENT_COLUMN, nullable = false, unique = true)
    private String ident;

    @Column(name = DESCRIPTION_COLUMN)
    private String description;

    @Column(name = SESSION_COLUMN)
    private String session;

    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;
}
