package br.com.fiap.techchallenge.quickserveapi.application.handler.http;

import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.ProductDTO;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.logging.Logger;


@FeignClient(name = "quick-serve", url = "http://localhost:8081/quick_service")
public interface ProductClient {

    // Método para buscar um produto por ID
    @GetMapping("/products/{id}")
    ProductDTO getProductById(@PathVariable Long id); // Correção no PathVariable

}