package com.unimib.unimibike.Util;
/**
 *SchedPolicy: set_timerslack_ns write failed: Operation not permitted
 */

public interface ServerRoutes {
    String IP = "192.168.1.3";
    String PORT = "8000";
    String BASE_URL = "http://"+IP+":"+PORT+"/api/";
    String RACKS = BASE_URL + "racks";
    String RENTALS = BASE_URL + "rentals";
    String MOD_POSITION  = BASE_URL + "modifyBikePosition";
    String BIKES = BASE_URL + "bikes";
    String ADD_BIKES = BASE_URL + "addBikes";
    String REMOVE_BIKES = BASE_URL + "removeBikes";
    String BUILDINGS = BASE_URL + "buildings";
    String FIX_REPORT = BASE_URL + "fixedReports";
    String RESERVATIONS = BASE_URL + "reservations";

    String REPORTS = BASE_URL + "reports";

    String USERS = BASE_URL + "users";
    String USER_ID = BASE_URL + "userId";
    String ADD_USER = BASE_URL + "addUser";


    String BIKE_ADDED_HISTORY = BASE_URL + "addedBikes";
    String LOGIN = BASE_URL + "login";

    double MIN_DISTANCE_FROM_RACKS = 2;
}
