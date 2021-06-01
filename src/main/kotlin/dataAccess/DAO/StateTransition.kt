package dataAccess.DAO

import java.sql.Timestamp

class StateTransition(val deliveryId : Long,
                      val transitionDate : Timestamp,
                      val previousState : String,
                      val nextState : String) {
}