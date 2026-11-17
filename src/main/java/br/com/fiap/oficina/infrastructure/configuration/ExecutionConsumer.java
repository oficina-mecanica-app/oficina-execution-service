package br.com.fiap.oficina.infrastructure.configuration;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import br.com.fiap.oficina.domain.model.ExecutionOrder;
import br.com.fiap.oficina.infrastructure.repository.ExecutionRepository;

/**
 * Cria a ordem de execução quando o pagamento da OS é aprovado.
 *
 * - Idempotente: se a ordem de execução já existe (reentrega da mensagem),
 *   não cria uma segunda.
 * - Nenhuma exceção é engolida: falhas são propagadas para o retry do listener
 *   e, esgotadas as tentativas, a mensagem vai para a DLQ (ver RabbitMQConfig).
 */
@Component
public class ExecutionConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionConsumer.class);

    private final ExecutionRepository repository;

    public ExecutionConsumer(ExecutionRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "execution-payment-approved-queue")
    public void receiveMessage(Map<String, Object> message) {
        Long serviceOrderId = extractServiceOrderId(message);

        if (repository.findByServiceOrderId(serviceOrderId).isPresent()) {
            logger.info("Ordem de execução da OS {} já existe; mensagem duplicada ignorada", serviceOrderId);
            return;
        }

        ExecutionOrder execution = new ExecutionOrder();
        execution.setServiceOrderId(serviceOrderId);
        execution.setStatus("PENDING");
        repository.save(execution);

        logger.info("Ordem de execução criada para a OS {}", serviceOrderId);
    }

    private Long extractServiceOrderId(Map<String, Object> message) {
        Object value = message.get("serviceOrderId");
        if (value == null) {
            throw new AmqpRejectAndDontRequeueException("Mensagem sem serviceOrderId: " + message);
        }
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            throw new AmqpRejectAndDontRequeueException("serviceOrderId inválido: " + value, e);
        }
    }
}
