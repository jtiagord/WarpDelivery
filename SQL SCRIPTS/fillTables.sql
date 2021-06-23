--STORE
INSERT INTO STORE (name, postalcode, address) VALUES ('STORE ONE', '2725-032', 'ADDRESS EXAMPLE 1');
INSERT INTO STORE (name, postalcode, address) VALUES ('STORE TWO', '7359-081', 'ADDRESS EXAMPLE 2');

--USERS
INSERT INTO USERS VALUES ('user1', 'user', 'one', '968488765', 'password1', 'user1@email.com');
INSERT INTO USERS VALUES ('user2', 'user', 'two', '923947365', 'password2', 'user2@email.com');
INSERT INTO USERS VALUES ('user3', 'user', 'three', '917385375', 'password3', 'user3@email.com');

--CLIENT ADDRESSES
INSERT INTO CLIENT_ADDRESS (clientusername, postal_code, address)
VALUES ('user1', '2343-097', 'CLIENT ADDRESS ONE');

INSERT INTO CLIENT_ADDRESS (clientusername, postal_code, address)
VALUES ('user2', '7503-034', 'CLIENT ADDRESS TWO');

--WARPERS
INSERT INTO WARPER VALUES ('user3', 'inactive');

--WARPER VEHICLES
INSERT INTO VEHICLE VALUES ('user3', 'Motociclo', 'AA-01-ZZ');
INSERT INTO VEHICLE VALUES ('user3', 'Ligeiro Passageiros', 'BB-32-AS');

--DELIVERIES
INSERT INTO DELIVERY (warperusername, clientusername, clientphone, state, purchasedate, price, type)
VALUES ('user3', 'user2', '923947365', 'Em distribuição', '2020-06-22 18:10:35', 17.02, 'pequeno porte');

INSERT INTO DELIVERY (warperusername, clientusername, clientphone, state, purchasedate, price, type)
VALUES ('user3', 'user1', '968488765', 'Em processamento', NOW(), 23.50, 'pequeno porte');

--DELIVERY STATE TRANSITIONS
INSERT INTO STATE_TRANSITIONS VALUES (1, '2020-06-22 18:20:33', 'Em processamento', 'Pronto para recolha');
INSERT INTO STATE_TRANSITIONS VALUES (1, '2020-06-22 19:00:25', 'Pronto para recolha', 'Em distribuição');
