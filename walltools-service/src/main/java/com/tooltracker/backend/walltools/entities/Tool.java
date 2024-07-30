package com.tooltracker.backend.walltools.entities;

import java.util.List;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import com.tooltracker.backend.walltools.enums.ToolStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited(withModifiedFlag = false)
@AuditOverride(forClass = BaseModel.class)

@Entity
@Table(name = "tool")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Tool extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tool_sequence_gen")
    @SequenceGenerator(name = "tool_sequence_gen", sequenceName = "tool_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "tool_name", unique = true)
    private String toolName;

    @NotAudited
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_tool_details_id", referencedColumnName = "id")
    private ToolDetails toolDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "tool_status")
    private ToolStatus toolStatus;

    /*
     * Relation Between Tool & Movement is set to be ManyToMany coz
     * [1] ManyToMany provide a clean and symmetric for this approach so it allows
     * for more flexibility when querying and manipulating data.
     * [2] It is more convenient to extend the relationship to include a join table
     * with additional fields.
     */
    @NotAudited
    @ManyToMany(mappedBy = "takenTools")
    private List<Movement> movements;

    @PrePersist
    private void setDefaults() {
        if (toolStatus == null) // If Tool Status null from DTO then give it default value
            toolStatus = ToolStatus.NOT_SET; // This because the tool status might be unkown for admin at creation time
                                             // & need to be checked to set
                                             // status later

        /*
         * if toolDetails null then create instance for it, to avoid any nulls in fk_ in
         * bidirectional relation
         */
        if (toolDetails == null)
            toolDetails = new ToolDetails();
    }

}
