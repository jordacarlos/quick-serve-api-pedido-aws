package br.com.fiap.techchallenge.quickserveapi.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig{

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Quick Serve API para MicroServiço Order - Tech Challange - 7SOAT - Grupo 51 ")
                        .description("Bem-vindo a documentação das APIs do Quick Serve MicroServiço Order"));
    }
}