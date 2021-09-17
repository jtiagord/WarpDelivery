create or replace function addStateTransition() RETURNS TRIGGER
LANGUAGE PLPGSQL
as $$
DECLARE
curr_delivery_id integer;
prev_state varchar(20);
next_state varchar(20);
BEGIN
    SELECT deliveryId INTO curr_delivery_id FROM inserted;
    SELECT state INTO prev_state FROM prev;
    SELECT state INTO next_state FROM inserted;

    INSERT INTO STATE_TRANSITIONS (deliveryId, transitiondate, previous_state, next_state) VALUES
	(curr_delivery_id, NOW(), prev_state, next_state );
    RETURN NULL;
END;$$;

create trigger addStateTransition AFTER UPDATE ON DELIVERY
REFERENCING NEW TABLE AS inserted OLD TABLE AS prev
FOR EACH ROW
WHEN (OLD.state IS DISTINCT FROM NEW.state)
EXECUTE PROCEDURE addStateTransition();


CREATE OR REPLACE FUNCTION generate_id() RETURNS char(32) AS $$
        BEGIN
                RETURN replace(gen_random_uuid ()::text,'-','');
        END;
$$ LANGUAGE plpgsql;


create or replace function addDeliveryDate() RETURNS TRIGGER
LANGUAGE PLPGSQL
as $$
DECLARE 
BEGIN
    IF(NEW.state = 'Delivered') THEN
		NEW.deliverdate = now();
		RETURN NEW;
    END IF;
	NEW.deliverdate = now();
	
	RETURN NEW;
END;$$;

CREATE TRIGGER adddeliverydate
BEFORE UPDATE ON DELIVERY
FOR EACH ROW EXECUTE PROCEDURE addDeliveryDate();
	
