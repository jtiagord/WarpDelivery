CREATE TABLE STORE (
	storeid bigserial PRIMARY KEY,
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
	deliveryid bigserial PRIMARY KEY,
	warperusername varchar(50) REFERENCES WARPER(username) on delete cascade,
	clientusername varchar(50) REFERENCES USERS(username) on delete cascade,
	storeid int NOT NULL REFERENCES STORE(storeid),
	state varchar(20) NOT NULL
	CHECK (state IN ('Looking for Warper','Delivering', 'Delivered', 'Canceled')),
	clientphone varchar(50),
	purchasedate timestamp NOT NULL DEFAULT NOW(),
	deliverdate timestamp CHECK (deliverdate > purchasedate),
	deliverLatitude double precision NOT NULL,
	deliverLongitude double precision NOT NULL,
	deliverAddress varchar(100),
	rating int CHECK (rating >= 1 and rating <= 5),
	reward decimal(10,2) CHECK (reward >= 0),
	type varchar(50) NOT NULL
	CHECK (type IN ('small', 'medium', 'large'))
);

CREATE TABLE VEHICLE (
	username varchar(50) REFERENCES WARPER(username) on delete cascade,
	vehicletype varchar(50) NOT NULL CHECK (vehicletype IN ('small', 'medium', 'large')),
	vehicleregistration varchar(50) NOT NULL,
	PRIMARY KEY (username, vehicleregistration)
);

CREATE TABLE STATE_TRANSITIONS (
	deliveryid int REFERENCES DELIVERY(deliveryid) on delete cascade,
	transitiondate timestamp,
	previousstate varchar(20) NOT NULL CHECK (previousstate IN ('Looking for Warper','Delivering', 'Delivered', 'Canceled')),
	nextstate varchar(20) NOT NULL CHECK (nextstate IN
		('Looking for Warper','Delivering', 'Delivered', 'Canceled')),	--convinha ver se ha melhor forma de fazer isto
	PRIMARY KEY (deliveryid, transitiondate)
);

CREATE TABLE CLIENT_ADDRESS (
	addressid bigserial,
	clientusername varchar(50) REFERENCES USERS(username) on delete cascade,
	latitude double precision NOT NULL,
	longitude double precision NOT NULL,
	postal_code varchar(10) NOT NULL,
	address varchar(100) NOT NULL,
	PRIMARY KEY (addressid, clientusername)
);