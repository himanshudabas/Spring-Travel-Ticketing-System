package com.himanshudabas.springboot.travelticketing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SequenceGenerator(name = "DOCUMENT_SEQUENCE", sequenceName = "document_sequence", allocationSize=1)
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENT_SEQUENCE")
    @Column(name = "DOCUMENT_ID", nullable = false, updatable = false)
    private Long id;
    private String name;
    private String type;
    private Long size;
    @Lob
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="TICKET_ID")
    private ResolveInfo resolveInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        return id != null && id.equals(((Document) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
