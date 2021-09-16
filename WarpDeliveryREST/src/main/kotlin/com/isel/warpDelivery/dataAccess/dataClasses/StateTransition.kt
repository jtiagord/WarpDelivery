package com.isel.warpDelivery.dataAccess.dataClasses

import java.sql.Timestamp

class StateTransition(val transitionDate : Timestamp,
                      val previousState : String,
                      val nextState : String) {
}