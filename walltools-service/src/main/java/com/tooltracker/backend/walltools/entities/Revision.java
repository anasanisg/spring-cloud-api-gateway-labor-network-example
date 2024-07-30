package com.tooltracker.backend.walltools.entities;

import java.time.LocalDateTime;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@RevisionEntity
@Getter
@Setter
public class Revision {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revision_sequence_gen")
    @SequenceGenerator(name = "revision_sequence_gen", sequenceName = "revision_sequence", allocationSize = 1)
    @RevisionNumber
    private long id;

    @RevisionTimestamp
    private LocalDateTime created;

}
