package com.isel.warpDelivery.common

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

const val CLIENTS="${HOME}/Client"
const val CLIENT="${CLIENTS}/{Username}"
const val CLIENT_DELIVERIES="${CLIENT}/deliveries"
const val CLIENT_DELIVERY="${CLIENT_DELIVERIES}/{DeliveryID}"
const val CLIENT_DELIVERY_TRANSITIONS="${CLIENT_DELIVERY}/transitions"
const val CLIENT_ADDRESSES="${CLIENT}/addresses"
const val CLIENT_ADDRESS="${CLIENT_ADDRESSES}/{AddressId}"