package br.com.fiap.techchallenge.quickserveapi.application.handler.api;

import br.com.fiap.techchallenge.quickserveapi.application.handler.controllers.OrderController;
import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.*;
import br.com.fiap.techchallenge.quickserveapi.application.handler.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/quick_service/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class Order {

    private final OrderController orderController;

    @Autowired
    public Order(OrderController orderController) {
        this.orderController = orderController;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Inserir novo pedido",
            description = "Este endpoint insere um novo pedido"
    )
    public OrderResponseDTO placeOrder(@RequestBody OrderPostEntity orderInput) {
        return this.orderController.save(orderInput);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Encontrar Pedido por ID",
            description = "Este endpoint é utilizado para encontrar pedido por ID"
    )
    public Object FindOrderById(@PathVariable Long id) {
        try{
            var pedido = this.orderController.findById(id);
            return new ResponseEntity<>(pedido, HttpStatus.ACCEPTED).getBody();
        }catch (NotFoundException notFoundException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(notFoundException.getMessage());
        }
    }

    @GetMapping("/payment/{id}")
    @Operation(
            summary = "Encontrar Status do Pagamento por ID",
            description = "Este endpoint é utilizado para encontrar o status do pagamento do Pedido"
    )
    public Object checkPaymentStatus(@PathVariable Long id) {
        try {
            var pedido = this.orderController.checkPaymentStatus(id);
            return new ResponseEntity<>(pedido, HttpStatus.ACCEPTED).getBody();
        } catch (NotFoundException notFoundException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(notFoundException.getMessage());
        }
    }

    @GetMapping("/")
    @Operation(
            summary = "Buscar tudo",
            description = "Este endpoint é utilizado para buscar todos os pedido na base"
    )
    public Object findAll() {
        try {
            List<OrderResponseDTO> orders = orderController.findAll();
            return new ResponseEntity<>(orders, HttpStatus.OK).getBody();
        } catch (NotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(
            summary = "Buscar por filtro",
            description = "Este endpoint é utilizado para buscar todos os pedido na base utilizando o filtro ordenado por" +
                    "PRONTO" +
                    "EM_PREPARACAO" +
                    "RECEBIDO"
    )
    public List<OrderResponseDTO> listByFilters(@RequestParam(defaultValue = "order_id ASC") String sortOrder) {
        return orderController.findAllSorted(sortOrder);
    }

    @PutMapping(value = "/{id}/{status}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Atualiza o status de um pedido",
            description = "Este endpoint atualiza o status de um pedido com base no ID fornecido e no novo status."
    )
    public ResponseEntity<Object> updateOrderEntityStatus(@PathVariable Long id, @PathVariable String status) {
        try {
            // Verifica se o status é válido
            if (!OrderStatusEnum.isValid(status)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Status inválido");
            }

            // Converte o status para o enum correspondente
            OrderStatusEnum orderStatus = OrderStatusEnum.valueOf(status.toUpperCase());

            // Busca o pedido pelo ID
            OrderResponseDTO order = this.orderController.findById(id);

            // Verifica se o pedido foi encontrado
            if (order == null) {
                throw new NotFoundException("Pedido não encontrado");
            }

            // Atualiza o status do pedido
            order.setStatus(orderStatus.name());

            // Chama o método para atualizar o status do pedido no controlador
            this.orderController.updateStatus(order);

            // Retorna a resposta com o status HTTP 202 Accepted
            return new ResponseEntity<>(order, HttpStatus.ACCEPTED);

        } catch (NotFoundException notFoundException) {
            // Caso o pedido não seja encontrado, retorna o erro com o status HTTP 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(notFoundException.getMessage());
        } catch (Exception e) {
            // Caso ocorra outro erro, retorna o erro genérico com o status HTTP 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar o pedido: " + e.getMessage());
        }
    }

    @PutMapping(value = "/payment-approver/{id}/{statusPayment}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Atualiza o status do pagamento",
            description = "Este endpoint atualiza o status do pagamento do pedido"
    )
    public ResponseEntity<Object> paymentApprover(@PathVariable Long id, @PathVariable OrderPaymentStatusEnum statusPayment) {
        try {
            // Busca o pedido pelo ID
            OrderResponseDTO order = this.orderController.findById(id);

            // Verifica se o pedido foi encontrado
            if (order == null) {
                throw new NotFoundException("Pedido não encontrado");
            }

            // Atualiza o status de pagamento do pedido
            order.setPaymentStatus(statusPayment.toString());

            // Chama o método para aprovar o pagamento
            this.orderController.paymentApprover(order);

            // Retorna a resposta com o status HTTP 202 Accepted
            return new ResponseEntity<>(order, HttpStatus.ACCEPTED);
        } catch (NotFoundException notFoundException) {
            // Caso o pedido não seja encontrado, retorna o erro com o status HTTP 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(notFoundException.getMessage());
        }
    }
}



