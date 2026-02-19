package br.com.fiap.oficina.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "execution_orders")
public class ExecutionOrder {

    @Id
    private String id;

    private Long serviceOrderId;
    private String mechanicName;
    private String status;
    private List<String> checklist;
    private String technicalComments;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}