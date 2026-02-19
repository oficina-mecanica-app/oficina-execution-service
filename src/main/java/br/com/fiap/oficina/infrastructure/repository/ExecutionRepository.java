package br.com.fiap.oficina.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.fiap.oficina.domain.model.ExecutionOrder;

@Repository
public interface ExecutionRepository extends MongoRepository<ExecutionOrder, String> {
    Optional<ExecutionOrder> findByServiceOrderId(Long serviceOrderId);
}