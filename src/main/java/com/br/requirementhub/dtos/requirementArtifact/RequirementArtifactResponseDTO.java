package com.br.requirementhub.dtos.requirementArtifact;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequirementArtifactResponseDTO {
    private Long id;
    private String identifier;
    private String name;
    private String type;
    private String description;
    private String file;
    private Long requirementId;
}