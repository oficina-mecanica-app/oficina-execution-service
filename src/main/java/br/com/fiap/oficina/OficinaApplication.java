package br.com.fiap.oficina;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// O "exclude" aqui desativa a tela de login padrão e o erro de falta de configuração
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class OficinaApplication {

    public static void main(String[] args) {
        SpringApplication.run(OficinaApplication.class, args);
    }
}