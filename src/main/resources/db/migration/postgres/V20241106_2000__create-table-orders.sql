CREATE TABLE orders
(
    order_id BIGSERIAL PRIMARY KEY,  -- PRIMARY KEY já define a constraint
    status VARCHAR(100),
    customer_id VARCHAR(255),
    total_order_value DOUBLE PRECISION DEFAULT 0.0,
    payment_status character varying(100)
);