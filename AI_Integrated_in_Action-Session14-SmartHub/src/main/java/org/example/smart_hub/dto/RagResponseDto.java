package org.example.smart_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagResponseDto {
    private String question;
    private String answer;
    private List<SourceDocumentDto> sourceDocuments;
    private boolean foundInKnowledgeBase;
}
