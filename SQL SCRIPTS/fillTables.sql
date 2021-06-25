--STORE
INSERT INTO STORE (storeid,name, postalcode, address, latitude,longitude) VALUES (1,'STORE ONE', '2725-032', 'ADDRESS EXAMPLE 1',123.231,123.312);
INSERT INTO STORE (storeid,name, postalcode, address, latitude,longitude) VALUES (2,'STORE TWO', '7359-081', 'ADDRESS EXAMPLE 2',13.231,12.312);

--USERS
INSERT INTO USERS VALUES ('user1', 'user', 'one', '968488765', 'password1', 'user1@email.com');
INSERT INTO USERS VALUES ('user2', 'user', 'two', '923947365', 'password2', 'user2@email.com');
INSERT INTO USERS VALUES ('user3', 'user', 'three', '917385375', 'password3', 'user3@email.com');

--CLIENT ADDRESSES
INSERT INTO CLIENT_ADDRESS (clientusername, latitude, longitude, postal_code, address)
VALUES ('user1', 10.149322, 23.231321, '2343-097', 'CLIENT ADDRESS ONE');

INSERT INTO CLIENT_ADDRESS (clientusername, latitude, longitude, postal_code, address)
VALUES ('user2', 41.149322, 12.231321, '2343-097', 'CLIENT ADDRESS TWO');

--WARPERS
INSERT INTO WARPER VALUES ('user3');

--WARPER VEHICLES
INSERT INTO VEHICLE VALUES ('user3', 'Motociclo', 'AA-01-ZZ');
INSERT INTO VEHICLE VALUES ('user3', 'Ligeiro Passageiros', 'BB-32-AS');

--DELIVERIES
INSERT INTO DELIVERY (deliveryid, warperusername, clientusername, storeid, state, clientphone, purchasedate, deliverdate,
					  pickupLatitude, pickupLongitude, deliverLatitude, deliverLongitude, deliverAddress, rating, reward, type)
					  VALUES (1,'user3', 'user2', 1, 'Em distribuição', '923947365', '2020-06-22 18:10:35', null,
							  12.343421, 12.31231, 12.343421, 13.31231, 'Rua teste 123', null, null, 'pequena');


INSERT INTO DELIVERY (deliveryid , warperusername, clientusername, storeid, state, clientphone, purchasedate, deliverdate,
					  pickupLatitude, pickupLongitude, deliverLatitude, deliverLongitude, deliverAddress, rating, reward, type)
					  VALUES (2,'user3', 'user1', 1, 'Pronto para recolha', '923947365', '2020-06-22 18:10:35', null,
							  12.343421, 14.31231, 13.343421, 13.31231, 'Rua teste 123', null, null, 'pequena');


--DELIVERY STATE TRANSITIONS
INSERT INTO STATE_TRANSITIONS VALUES (1, '2020-06-22 18:20:33', 'Em processamento', 'Pronto para recolha');
INSERT INTO STATE_TRANSITIONS VALUES (1, '2020-06-22 19:00:25', 'Pronto para recolha', 'Em distribuição');