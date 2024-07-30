package com.tooltracker.backend.walltools.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tool_details")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ToolDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tool_details_sequence_gen")
    @SequenceGenerator(name = "tool_details_sequence_gen", sequenceName = "tool_details_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @JsonBackReference // Jackson annotation & used toTo Break inifite loop duriing serialization &
                       // prevent recursion
    @OneToOne(mappedBy = "toolDetails")
    private Tool tool;

    @Column(name = "description") // Optional
    private String description;

    @Column(name = "height") // Optional
    private Double height;

    @Column(name = "width") // Optional
    private Double width;

    @Column(name = "weight") // Optional
    private Double weight;

    @Column(name = "img")
    private String img;

    @PrePersist
    private void settingDefaults() { // Check optional Fields those comes from DTO if its null then give the default
                                     // value

        if (description == null)
            description = "No description available at this moment";

        if (height == null)
            height = 0.0;

        if (width == null)
            width = 0.0;

        if (weight == null)
            weight = 0.0;

        if (img == null)
            img = "";

    }

}
