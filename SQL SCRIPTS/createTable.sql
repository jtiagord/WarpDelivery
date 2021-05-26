CREATE TABLE STORE (
	storeId serial PRIMARY KEY,
	name varchar(100) NOT NULL,
	postal_code varchar(10) NOT NULL,
	address varchar(100) NOT NULL,
);


CREATE TABLE DELIVERY (
	deliveryId serial PRIMARY KEY,
	state varchar(20) NOT NULL 
	CHECK (state IN ('Em processamento', 'Pronto para recolha', 'Em distribuição', 'Entregue', 'Cancelada')), 
	purchase_date timestamp NOT NULL,
	delivery_date timestamp CHECK (delivery_date > purchase_date),
	rating int CHECK (rating >= 1 and rating <= 5),
	price decimal(10,2) NOT NULL CHECK (price >= 0),
	type varchar(50) NOT NULL --pode vir a ser mudado
);

CREATE TABLE USERS (
	username varchar(50) PRIMARY KEY,
	firstname varchar(50) NOT NULL,
	lastname varchar(50) NOT NULL,
	phonenumber varchar(50) NOT NULL,
	password varchar(50) NOT NULL,
	email varchar(100) NOT NULL CHECK (email LIKE '%@%')
);

CREATE TABLE WARPER (
	username varchar(50) PRIMARY KEY REFERENCES USERS(username)
	--adicionar aqui a foto q ns como será ainda
);

CREATE TABLE VEHICLE (
	username varchar(50) REFERENCES WARPER(username),
	vehicletype varchar(50) NOT NULL,
	vehicleregistration varchar(50) NOT NULL,
	PRIMARY KEY (username, vehicleregistration)
);

CREATE TABLE STATE_TRANSITIONS (
	deliveryId int REFERENCES DELIVERY(deliveryId),
	transitiondate timestamp,
	previous_state varchar(20) NOT NULL CHECK (previous_state IN ( 
		'Em processamento', 'Pronto para recolha',
		'Em distribuição', 'Entregue', 'Cancelada')),
	next_state varchar(20) NOT NULL CHECK (next_state IN (
		'Em processamento', 'Pronto para recolha',
		'Em distribuição', 'Entregue', 'Cancelada')), --convinha ver se ha melhor forma de fazer isto
	PRIMARY KEY (deliveryId, transitiondate)
);

CREATE TABLE CLIENT_ADDRESS (
	addressId serial,
	clientUsername varchar(50) REFERENCES USERS(username),
	postal_code varchar(10) NOT NULL,
	address varchar(100) NOT NULL,
	PRIMARY KEY (addressId, clientUsername)
);