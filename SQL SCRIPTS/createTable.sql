
CREATE OR REPLACE FUNCTION generate_id() RETURNS char(32) AS $$
        BEGIN
                RETURN replace(gen_random_uuid ()::text,'-','');
        END;
$$ LANGUAGE plpgsql;


CREATE TABLE STORE (
	storeid char(32) PRIMARY KEY DEFAULT generate_id(),
	name varchar(100) NOT NULL,
	postalcode varchar(10) NOT NULL,
	latitude double precision NOT NULL,
	longitude double precision NOT NULL,
	address varchar(100) NOT NULL,
	apiKey char(32) UNIQUE DEFAULT generate_id() NOT NULL 
);


CREATE TABLE WARPER (
	username varchar(50) PRIMARY KEY,
	firstname varchar(50) NOT NULL,
	lastname varchar(50) NOT NULL,
	phonenumber varchar(50) NOT NULL UNIQUE,
	password varchar(100) NOT NULL,
	email varchar(100) NOT NULL CHECK (email LIKE '%@%') UNIQUE
);

CREATE TABLE DELIVERY (
	deliveryid char(32) PRIMARY KEY DEFAULT generate_id(),
	warperusername varchar(50) REFERENCES WARPER(username) on delete cascade,
	storeid char(32) NOT NULL REFERENCES STORE(storeid),
	state varchar(20) NOT NULL
	CHECK (state IN ('Looking for Warper','Delivering', 'Delivered', 'Cancelled')),
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
	deliveryid char(32) REFERENCES DELIVERY(deliveryid) on delete cascade,
	transitiondate timestamp,
	previousstate varchar(20) NOT NULL CHECK (previousstate IN ('Looking for Warper','Delivering', 'Delivered', 'Cancelled')),
	nextstate varchar(20) NOT NULL CHECK (nextstate IN
		('Looking for Warper','Delivering', 'Delivered', 'Cancelled')),	--convinha ver se ha melhor forma de fazer isto
	PRIMARY KEY (deliveryid, transitiondate)
);
