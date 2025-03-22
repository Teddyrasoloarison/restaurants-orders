CREATE TABLE orders (
                        idOrder SERIAL PRIMARY KEY,
                        tableNumber type_of_table,
                        customerArrivalDateTime TIMESTAMP NOT NULL,
                        amountPaid FLOAT DEFAULT 0,
                        amountDue FLOAT NOT NULL,
                        order_status status
);



