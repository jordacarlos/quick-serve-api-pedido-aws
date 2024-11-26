CREATE TABLE order_products
(
    id BIGSERIAL PRIMARY KEY,  -- A chave primária define a constraint de unicidade
    order_id BIGINT,           -- Referência ao pedido
    product_id BIGINT,         -- Identificador do produto, sem chave estrangeira
    product_quantity INTEGER   -- Quantidade do produto
);
