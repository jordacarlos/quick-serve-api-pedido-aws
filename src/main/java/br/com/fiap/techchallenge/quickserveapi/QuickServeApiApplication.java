package br.com.fiap.techchallenge.quickserveapi;

import br.com.fiap.techchallenge.quickserveapi.application.handler.controllers.OrderController;

import br.com.fiap.techchallenge.quickserveapi.application.handler.external.DatabaseConnection;
import br.com.fiap.techchallenge.quickserveapi.application.handler.gateway.Gateway;

import br.com.fiap.techchallenge.quickserveapi.application.handler.http.ProductClient;
import br.com.fiap.techchallenge.quickserveapi.application.handler.usecases.OrderCase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.openfeign.EnableFeignClients;

import org.springframework.cloud.openfeign.FeignAutoConfiguration;

@ImportAutoConfiguration({FeignAutoConfiguration.class})
@SpringBootApplication
@EnableFeignClients(basePackages = "br.com.fiap.techchallenge.quickserveapi.application.handler.http")
public class QuickServeApiApplication {

	@Value("${spring.datasource.url}")
	private String springDatasourceUrl;

	@Value("${spring.datasource.username}")
	private String springDatasourceUserName;

	@Value("${spring.datasource.password}")
	private String springDatasourcePassword;

	public static void main(String[] args) {
		SpringApplication.run(QuickServeApiApplication.class, args);
	}

	@Bean
	public DatabaseConnection databaseConnection() {
		String url = this.springDatasourceUrl;
		String user = this.springDatasourceUserName;
		String password = this.springDatasourcePassword;
		return new DatabaseConnection(url, user, password);
	}

	//Gateway
	@Bean
	public Gateway gateway (DatabaseConnection databaseConnection){
		return new Gateway(databaseConnection);
	}

	@Bean
	public OrderController orderController (OrderCase orderCase){
		return new OrderController(orderCase);
	}

	@Bean
	public OrderCase orderCase(Gateway gateway, ProductClient productClient) {
		return new OrderCase(gateway, productClient);
	}

}
