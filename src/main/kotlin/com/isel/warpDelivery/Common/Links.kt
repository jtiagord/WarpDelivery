package com.isel.warpDelivery.Common

const val HOME="/WarpDelivery"

//Warper related URIs
const val WARPERS="${HOME}/Warpers"
const val WARPER="${WARPERS}/{username}"
const val WARPER_STATE="${WARPER}/State"
const val WARPER_DELIVERIES="${WARPER}/Deliveries"
const val WARPER_DELIVERY="${WARPER_DELIVERIES}/{DeliveryId}"
const val WARPER_DELIVERY_TRANSITIONS="${WARPER_DELIVERY}/Transitions"
const val WARPER_VEHICLES="${WARPER}/Vehicle"
const val WARPER_VEHICLE="${WARPER_VEHICLES}/{id}"
const val DELIVERIES = "${HOME}/Deliveries"
const val DELIVERY = "${HOME}/Deliveries/{DeliveryId}"
const val DELIVERY_STATE = "${HOME}/Deliveries/{DeliveryId}/State"