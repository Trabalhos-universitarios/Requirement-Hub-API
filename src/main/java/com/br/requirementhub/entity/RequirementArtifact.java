package com.br.requirementhub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "requirement_artifact")
public class RequirementArtifact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "identify")
    private String identify;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

//    @Lob
    @Column(name = "artifact")
    private byte[] artifact;

    @ManyToOne
    @JoinColumn(name = "id_requisito", nullable = false)
    private Requirement requirement;
}