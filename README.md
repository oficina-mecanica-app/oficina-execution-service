# 🔧 Oficina Execution Service

## Autor
- **Nome:** Lucas Eduardo Peters Scuissiatto
- **RM:** RM365455
- **Curso:** Pós-Graduação em Arquitetura de Software

---

## 📖 Descrição

O **Execution Service** é responsável pela gestão operacional da oficina. Ele controla a entrada de veículos na área técnica, a atribuição de mecânicos e o rastreamento do progresso de cada reparo após a confirmação financeira.

### Tecnologias Utilizadas
* **Java 21 / Spring Boot 3:** Base da aplicação.
* **Spring Data JPA/MongoDB:** Persistência do histórico de execuções.
* **RabbitMQ:** Recebimento de autorizações de serviço e envio de notificações de conclusão.
* **JUnit 5 & Mockito:** Testes unitários com foco em estados de transição.)

## 🏗️ Arquitetura e Fluxo de Trabalho

O serviço opera como o "consumidor final" da autorização de pagamento:

1. **Aguardando Autorização**: O serviço monitora a fila `execution-payment-approved-queue.
2. **Entrada na Fila**: Ao receber o evento de pagamento aprovado do `Billing Service, o serviço cria automaticamente uma `ExecutionOrder com status `PENDING.
3. **Início da Manutenção**: Através do endpoint `/execution/{id}/start`, o mecânico assume a OS, alterando o status para `EM_EXECUCAO e registrando o timestamp de início.
4. **Finalização e Notificação**: Ao concluir o reparo via endpoint `/finish, o serviço publica um evento na `execution-finished-queue para que o `OS Service possa encerrar o ciclo de vida da Ordem de Serviço.


## 📥 Fila de Entrada (Consumer)
* **`execution-payment-approved-queue`**: Recebe o ID da OS autorizado para início imediato da manutenção.

## 📤 Fila de Saída (Producer)
* **`execution-finished-queue`**: Notifica o ecossistema que o veículo está pronto e o serviço foi concluído.

## 🧪 Qualidade e Manutenibilidade

Este serviço foi desenvolvido seguindo as melhores práticas de manutenibilidade do **SonarQube**:

* **Rating A**: Qualidade estrutural de código e ausência de "Code Smells".
* **Cobertura de Testes**: Superior a 90%, cobrindo todos os fluxos de sucesso e tratamento de exceções de mensagens.

**Comandos de Teste::**
```bash
# Executar testes unitários
mvn clean test

# Gerar análise no SonarQube
mvn sonar:sonar \
  -Dsonar.projectKey=oficina-execution-service \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=admin \
  -Dsonar.password=admin123
```
