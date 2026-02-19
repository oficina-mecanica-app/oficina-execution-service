package br.com.fiap.oficina.infrastructure.configuration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.fiap.oficina.domain.model.ExecutionOrder;
import br.com.fiap.oficina.infrastructure.repository.ExecutionRepository;

@ExtendWith(MockitoExtension.class)
public class ExecutionConsumerTest {

    @Mock
    private ExecutionRepository repository;

    @InjectMocks
    private ExecutionConsumer consumer;

    @Test
    public void deveProcessarMensagemESalvarNoBancoComSucesso() {
        Map<String, Object> message = new HashMap<>();
        message.put("serviceOrderId", 50L);

        consumer.receiveMessage(message);

        verify(repository, times(1)).save(any(ExecutionOrder.class));
    }

    @Test
    public void deveTratarExcecaoQuandoMensagemNaoTiverId() {
        Map<String, Object> message = new HashMap<>();

        consumer.receiveMessage(message);

        verify(repository, times(0)).save(any(ExecutionOrder.class));
    }
}