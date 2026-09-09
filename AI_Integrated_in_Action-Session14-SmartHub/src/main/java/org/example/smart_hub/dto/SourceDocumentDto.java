package org.example.smart_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceDocumentDto {
    private String documentName;
    private String section;
    private String contentSnippet;
    private Double score;
}
