package br.com.fiap.oficina.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.fiap.oficina.domain.model.ExecutionOrder;
import br.com.fiap.oficina.infrastructure.repository.ExecutionRepository;

@ExtendWith(MockitoExtension.class)
public class ExecutionControllerTest {

    @Mock
    private ExecutionRepository repository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ExecutionController controller;

    @Test
    public void deveListarTodasAsExecucoes() {
        when(repository.findAll()).thenReturn(List.of(new ExecutionOrder()));

        List<ExecutionOrder> result = controller.listAll();

        assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void deveIniciarExecucaoComSucesso() {
        ExecutionOrder mockOrder = new ExecutionOrder();
        mockOrder.setId("1");

        when(repository.findById("1")).thenReturn(Optional.of(mockOrder));
        when(repository.save(any(ExecutionOrder.class))).thenReturn(mockOrder);

        ResponseEntity<ExecutionOrder> response = controller.startExecution("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("EM_EXECUCAO", response.getBody().getStatus());
        assertNotNull(response.getBody().getStartedAt());
        verify(repository, times(1)).save(mockOrder);
    }

    @Test
    public void deveRetornarNotFoundAoTentarIniciarExecucaoInexistente() {
        when(repository.findById("99")).thenReturn(Optional.empty());

        ResponseEntity<ExecutionOrder> response = controller.startExecution("99");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(repository, times(0)).save(any(ExecutionOrder.class));
    }

    @Test
    public void deveCriarFakeComSucesso() {
        ExecutionOrder mockOrder = new ExecutionOrder();
        mockOrder.setMechanicName("Jão da Oficina");

        when(repository.save(any(ExecutionOrder.class))).thenReturn(mockOrder);

        ExecutionOrder result = controller.createFake();

        assertEquals("Jão da Oficina", result.getMechanicName());
        verify(repository, times(1)).save(any(ExecutionOrder.class));
    }

    @Test
    public void deveFinalizarExecucaoComSucesso() {
        ResponseEntity<String> response = controller.finishExecution(123L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Execução finalizada e notificada.", response.getBody());
        verify(rabbitTemplate, times(1)).convertAndSend(eq("execution-finished-queue"), any(Map.class));
    }
}