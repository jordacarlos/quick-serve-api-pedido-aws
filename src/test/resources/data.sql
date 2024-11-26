-- Inserir um pedido na tabela 'orders'
INSERT INTO orders (status, customer_id, total_order_value, payment_status)
VALUES ('RECEBIDO', 123, 150.00, 'PENDENTE');

-- Inserir itens do pedido na tabela 'order_products'
-- Se o 'order_id' é auto-incrementado, você pode pegar o ID diretamente
-- Uma abordagem comum é primeiro inserir e depois recuperar o último ID gerado manualmente
-- ou usar uma sequência específica do banco de dados.
INSERT INTO order_products (order_id, product_id, product_quantity)
VALUES ((SELECT MAX(order_id) FROM orders), 1, 2);