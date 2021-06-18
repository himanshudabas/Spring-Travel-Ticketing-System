package com.himanshudabas.springboot.travelticketing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Parameter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ResolveInfo {
    @OneToMany(
            mappedBy = "resolveInfo",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<Document> documents = new ArrayList<>();
    @Id
    @Column(name="TICKET_ID")
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign",parameters=@Parameter(name="property", value="ticket"))
    private Long id;
    @OneToOne
    @PrimaryKeyJoinColumn
    private Ticket ticket;

    @Column(length = 1000)
    private String comment = "";
    @OneToOne
    @JoinColumn(name = "ADMIN_ID")
    private Employee admin = null;

    public ResolveInfo(Ticket ticket) {
        this.ticket = ticket;
    }

    public void addDocument(Document document) {
        documents.add(document);
        document.setResolveInfo(this);
    }

    public void removeDocument(Document document) {
        documents.remove(document);
        document.setResolveInfo(null);
    }
}
