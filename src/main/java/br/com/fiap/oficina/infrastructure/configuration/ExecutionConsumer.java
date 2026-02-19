package br.com.fiap.oficina.infrastructure.configuration;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.fiap.oficina.domain.model.ExecutionOrder;
import br.com.fiap.oficina.infrastructure.repository.ExecutionRepository;

@Component
public class ExecutionConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionConsumer.class);

    @Autowired
    private ExecutionRepository repository;

    @RabbitListener(queues = "execution-payment-approved-queue")
    public void receiveMessage(Map<String, Object> message) {
        logger.info("📥 [Execution Service] Recebi mensagem do RabbitMQ: {}", message);

        try {
            Long id = Long.valueOf(message.get("serviceOrderId").toString());

            logger.info("✅ Processando OS ID: {}", id);

            ExecutionOrder execution = new ExecutionOrder();
            execution.setServiceOrderId(id);
            execution.setStatus("PENDING");
            repository.save(execution);

            logger.info("✅ Ordem de Execução criada no banco.");

        } catch (Exception e) {
            logger.error("❌ Erro ao processar mensagem: {}", e.getMessage(), e);
        }
    }
}