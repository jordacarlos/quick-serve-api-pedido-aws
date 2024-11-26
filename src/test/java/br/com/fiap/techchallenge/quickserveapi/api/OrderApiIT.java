package br.com.fiap.techchallenge.quickserveapi.api;

import br.com.fiap.techchallenge.quickserveapi.utils.BuildOrderHelper;
import br.com.fiap.techchallenge.quickserveapi.utils.BuildOrderResponseDTO;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderPaymentStatusEnum.APROVADO;
import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderStatusEnum.EM_PREPARACAO;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql("/data.sql")
public class OrderApiIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup(){
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private final String urlOrder = "/quick_service/orders";

    @Nested
    class RegistrarPedido {
        @Test
        void devePermitirRegistrarPedido()  {
            var pedido = BuildOrderHelper.buildOrder();

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(pedido)//.log().all()
                    .when()
                    .post(urlOrder)
                    .then()//.log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/order.schema.json"));
        }
        @Test
        void deveGerarExececao_QuandoRegistrarMensagem_PayloadXML()  {
            String xmlPayload = "<mensagem><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_XML_VALUE)
                    .body(xmlPayload) // XML payload
                    .when()
                    .post(urlOrder)
                    .then().log().all()
                    .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())  // Espera código 400
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }
    }

    @Nested
    class BuscarPedido {
        @Test
        @Sql(scripts = {"/clean.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void devePermitirBuscarPedido() {
            var id = "1";
            RestAssured.when().get(urlOrder + "/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @Sql(scripts = {"/clean.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void deveGerarExcecao_QuandoBuscarPedido_IdNaoExiste()  {
            var id ="4555";
            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(urlOrder + "/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Pedido não encontrado"));
        }

        @Test
        @Sql(scripts = {"/clean.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void devePermitirBuscarStatusPagamento() {
            var id = "1";
            RestAssured.when().get(urlOrder + "/payment/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @Sql(scripts = {"/clean.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void deveGerarExcecao_QuandoBuscarStatusPagamento_IdNaoExiste()  {
            var id = "4555"; // ID inexistente no banco de dados

            given()
                    .filter(new AllureRestAssured()) // Filtro de log
                    .contentType(MediaType.APPLICATION_JSON_VALUE) // Tipo de conteúdo JSON
                    .when()
                    .get(urlOrder + "/payment/{id}", id) // Realiza a requisição GET
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Pedido não encontrado"));
        }
    }

    @Nested
    class AlterarPedido {
        @Test
        @Sql(scripts = {"/clean.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void devePermitirAlterarStatusPedido()  {
            // Definindo o ID e status do pedido
            var id = "1";
            var pedido = BuildOrderResponseDTO.buildOrderDTO();

            pedido.setId(Long.valueOf(id));
            pedido.setStatus(String.valueOf(EM_PREPARACAO));

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(pedido)
                    .when().put(urlOrder + "/{id}/{status}", id,EM_PREPARACAO)
                    .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/order.schema.json"));
        }

        @Test
        @Sql(scripts = {"/clean.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void deveGerarExcecao_QuandoAlterarStatusPedido_IdNaoExiste()  {
            var id ="4555";
            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().put(urlOrder + "/{id}/{status}", id,EM_PREPARACAO)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Pedido não encontrado"));
        }

        @Test
        @Sql(scripts = {"/clean.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void deveGerarExcecao_QuandoStatusInvalidoForPassado() {
            var id = "4555";
            var statusInvalido = "INVALIDO";

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().put(urlOrder + "/{id}/{status}", id, statusInvalido)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Status inválido"));
        }

        @Test
        @Sql(scripts = {"/clean.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void deveGerarExcecao_QuandoAlterarStatusPedido_PayloadXML(){
            String xmlPayload = "<AlteracaoStatusPedido><id>123456</id><status>EM_PREPARACAO</status></AlteracaoStatusPedido>";

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_XML_VALUE)
                    .body(xmlPayload) // XML payload
                    .when()
                    .put(urlOrder + "/{id}/{status}", "123456",EM_PREPARACAO)
                    .then().log().all()
                    .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())  // Espera código 400
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));

        }

        @Test
        @Sql(scripts = {"/clean.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void devePermitirAlterarPagamentoPedido()  {
            // Definindo o ID e status do pedido
            var id = "1";
            var pedido = BuildOrderResponseDTO.buildOrderDTO();

            pedido.setId(Long.valueOf(id));
            pedido.setPaymentStatus(String.valueOf(APROVADO));

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(pedido)
                    .when().put(urlOrder + "/payment-approver/{id}/{statusPayment}", id,APROVADO)
                    .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/order.schema.json"));
        }

        @Test
        @Sql(scripts = {"/clean.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void deveGerarExcecao_QuandoAlterarPagamentoPedido_IdNaoExiste()  {
            var id ="4555";
            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().put(urlOrder + "/payment-approver/{id}/{statusPayment}", id,APROVADO)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Pedido não encontrado"));
        }

        @Test
        @Sql(scripts = {"/clean.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void deveGerarExcecao_QuandoAlterarPagamentoPedido_PayloadXML(){
            String xmlPayload = "<AlteracaoPagamentoPedido><id>123456</id><status>APROVADO</status></AlteracaoPagamentoPedido>";

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_XML_VALUE)
                    .body(xmlPayload) // XML payload
                    .when()
                    .put(urlOrder + "/payment-approver/{id}/{statusPayment}", "123456",APROVADO)
                    .then().log().all()
                    .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())  // Espera código 400
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));

        }
    }

    @Nested
    class ListarPedidos {

        @Test
        void devePermitirListarMensagens()  {
            given()
                    .filter(new AllureRestAssured())
                    .when().get(urlOrder + "/")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/orderAll.schema.json"));
        }

        @Test
        void devePermitirListarPedidosComOrdenacao()  {
            given()
                    .filter(new AllureRestAssured())
                    .when().get(urlOrder + "/list")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/orderAll.schema.json"));
        }
    }

}
