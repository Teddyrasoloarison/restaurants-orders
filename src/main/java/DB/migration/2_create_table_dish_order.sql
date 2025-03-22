CREATE TABLE dish_order (
                            idDishOrder SERIAL PRIMARY KEY,
                            idOrder INTEGER NOT NULL,
                            name VARCHAR(255) NOT NULL,
                            quantityToOrder NUMERIC(10,2) NOT NULL,
                            price NUMERIC(10,2) NOT NULL,
                            FOREIGN KEY (idOrder) REFERENCES orders (idOrder) ON DELETE CASCADE
);