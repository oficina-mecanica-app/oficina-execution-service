package br.com.fiap.oficina.presentation.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.oficina.domain.model.ExecutionOrder;
import br.com.fiap.oficina.infrastructure.repository.ExecutionRepository;

@RestController
@RequestMapping("/execution")
public class ExecutionController {

    @Autowired
    private ExecutionRepository repository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping
    public List<ExecutionOrder> listAll() {
        return repository.findAll();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<ExecutionOrder> startExecution(@PathVariable String id) {
        Optional<ExecutionOrder> optionalOrder = repository.findById(id);
        
        if (optionalOrder.isPresent()) {
            ExecutionOrder order = optionalOrder.get();
            order.setStatus("EM_EXECUCAO");
            order.setStartedAt(LocalDateTime.now());
            repository.save(order);
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }
    
    // Endpoint de teste para criar dados fake (já que não temos o RabbitMQ ligado ainda)
    @PostMapping("/fake")
    public ExecutionOrder createFake() {
        ExecutionOrder order = new ExecutionOrder();
        order.setMechanicName("Jão da Oficina");
        order.setStatus("AGUARDANDO");
        order.setServiceOrderId(123L);
        return repository.save(order);
    }

    @PostMapping("/{osId}/finish")
    public ResponseEntity<String> finishExecution(@PathVariable Long osId) {
        
        System.out.println("🔧 Mecânico finalizou a OS: " + osId);
        
        Map<String, Object> event = new HashMap<>();
        event.put("osId", osId);
        event.put("status", "COMPLETED");
        
        rabbitTemplate.convertAndSend("execution-finished-queue", event);

        return ResponseEntity.ok("Execução finalizada e notificada.");
    }
}