CREATE TABLE STORE (
	storeid serial PRIMARY KEY,
	name varchar(100) NOT NULL,
	postalcode varchar(10) NOT NULL,
	latitude double precision NOT NULL,
	longitude double precision NOT NULL,
	address varchar(100) NOT NULL
);

CREATE TABLE USERS (
	username varchar(50) PRIMARY KEY,
	firstname varchar(50) NOT NULL,
	lastname varchar(50) NOT NULL,
	phonenumber varchar(50) NOT NULL UNIQUE,
	password varchar(50) NOT NULL,
	email varchar(100) NOT NULL CHECK (email LIKE '%@%') UNIQUE
);

CREATE TABLE WARPER (
	username varchar(50) PRIMARY KEY REFERENCES USERS(username) on delete cascade
	--adicionar aqui a foto q ns como será ainda
);

CREATE TABLE DELIVERY (
	deliveryid serial PRIMARY KEY,
	warperusername varchar(50) REFERENCES WARPER(username) on delete cascade,
	clientusername varchar(50) REFERENCES USERS(username) on delete cascade,
	storeid int NOT NULL REFERENCES STORE(storeid),
	state varchar(20) NOT NULL
	CHECK (state IN ('Em processamento', 'Pronto para recolha', 'Em distribuição', 'Entregue', 'Cancelada')),
	clientphone varchar(50),
	purchasedate timestamp NOT NULL,
	deliverdate timestamp CHECK (deliverdate > purchasedate),
	pickupLatitude double precision NOT NULL,
	pickupLongitude double precision NOT NULL,
	deliverLatitude double precision NOT NULL,
	deliverLongitude double precision NOT NULL,
	deliverAddress varchar(100),
	rating int CHECK (rating >= 1 and rating <= 5),
	reward decimal(10,2) CHECK (reward >= 0),
	type varchar(50) NOT NULL --pode vir a ser mudado
);

CREATE TABLE VEHICLE (
	username varchar(50) REFERENCES WARPER(username) on delete cascade,
	vehicletype varchar(50) NOT NULL,
	vehicleregistration varchar(50) NOT NULL,
	PRIMARY KEY (username, vehicleregistration)
);

CREATE TABLE STATE_TRANSITIONS (
	deliveryid int REFERENCES DELIVERY(deliveryid) on delete cascade,
	transitiondate timestamp,
	previousstate varchar(20) NOT NULL CHECK (previousstate IN (
		'Em processamento', 'Pronto para recolha',
		'Em distribuição', 'Entregue', 'Cancelada')),
	nextstate varchar(20) NOT NULL CHECK (nextstate IN (
		'Em processamento', 'Pronto para recolha',
		'Em distribuição', 'Entregue', 'Cancelada')), --convinha ver se ha melhor forma de fazer isto
	PRIMARY KEY (deliveryid, transitiondate)
);

CREATE TABLE CLIENT_ADDRESS (
	addressid serial,
	clientusername varchar(50) REFERENCES USERS(username) on delete cascade,
	latitude double precision NOT NULL,
	longitude double precision NOT NULL,
	postal_code varchar(10) NOT NULL,
	address varchar(100) NOT NULL,
	PRIMARY KEY (addressid, clientusername)
);