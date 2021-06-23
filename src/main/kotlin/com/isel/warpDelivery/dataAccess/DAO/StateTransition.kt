package com.isel.warpDelivery.dataAccess.DAO

import java.sql.Timestamp

class StateTransition(val transitionDate : Timestamp,
                      val previousState : String,
                      val nextState : String) {
}