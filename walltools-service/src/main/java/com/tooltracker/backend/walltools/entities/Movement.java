package com.tooltracker.backend.walltools.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.tooltracker.backend.walltools.enums.MovementStatus;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.FetchType;

@Audited(withModifiedFlag = false)
@AuditOverride(forClass = BaseModel.class)

@Entity
@Table(name = "movement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "createdAt", column = @Column(name = "pickup_date"))
public class Movement extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movement_sequence_gen")
    @SequenceGenerator(name = "movement_sequence_gen", sequenceName = "movement_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    /*
     * Relation Between Tool & Movement is set to be ManyToMany coz
     * [1] ManyToMany provide a clean and symmetric for this approach so it allows
     * for more flexibility when querying and manipulating data.
     * [2] It is more convenient to extend the relationship to include a join table
     * with additional fields.
     */

    @NotAudited

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movement_tool", joinColumns = @JoinColumn(name = "movement_id"), inverseJoinColumns = @JoinColumn(name = "tool_id"))
    private List<Tool> takenTools;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_status", nullable = false)
    private MovementStatus movementStatus;

}
