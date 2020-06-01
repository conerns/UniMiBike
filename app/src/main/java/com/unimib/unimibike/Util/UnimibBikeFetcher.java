package com.unimib.unimibike.Util;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//da ricordare di dover fare la import anche dei restanti
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.BikeHistory;
import com.unimib.unimibike.Model.BikeState;
import com.unimib.unimibike.Model.Rental;
import com.unimib.unimibike.Model.Report;
import com.unimib.unimibike.Model.User;
import com.unimib.unimibike.Model.Building;
import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.R;

import java.util.ArrayList;
import java.util.List;

public class UnimibBikeFetcher {

    private static final String TAG = "UnimibBikeFetcher";
    private static final String LAT = "?lat=";
    private static final String LONG = "&long=";
    private static final String MAX_DISTANCE = "&max_distance=";
    private static final String ACTIVE = "?active=yes";

    public static void postUserLogin(final Context context, String email, String password, final ServerResponseParserCallback<User> return_value){
        final JSONObject dati_json_form = createJSONLogin(email, password);

        Log.d("basic request-", "non entra in post User Login ");
        ServerRequest.getInstance(context).postBasicRequest(ServerRoutes.LOGIN, dati_json_form, new NetworkCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    User user = new User();
                    user.setEmail(response.get("email").toString());
                    return_value.onSuccess(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(int statusCode, JSONObject cache) {
                return_value.onError(getErrorTitle(statusCode), getErrorMessage(statusCode));;
            }
        });
    }

    public static void postAddBike(final Context context, int user_id, int rack_id, int unlock_code, final ServerResponseParserCallback<Bike> return_value){
        final JSONObject oggetto_add_bike = createJSONAdd(user_id,rack_id,unlock_code);

        ServerRequest.getInstance(context).postBasicRequest(ServerRoutes.ADD_BIKES, oggetto_add_bike, new NetworkCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                    return_value.onSuccess(new Bike());

            }

            @Override
            public void onError(int statusCode, JSONObject cache) {
                return_value.onError("Add error", "Qualcosa è andato storto");
            }
        });
    }

    public static void postModifyBike(final Context context,int bike_id, int rack_id,int user_id, final ServerResponseParserCallback<Bike> return_value){
        final JSONObject oggettoModifica = createJSONModify(bike_id, rack_id, user_id);
        ServerRequest.getInstance(context).postBasicRequest(ServerRoutes.MOD_POSITION, oggettoModifica, new NetworkCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                return_value.onSuccess(new Bike());
            }

            @Override
            public void onError(int statusCode, JSONObject cache) {
                return_value.onError("Modify Error", "Dati inseriti non validi");
            }
        });
    }

    public static void postRemoveBike(final Context context,int bike_id, int user_id, final ServerResponseParserCallback<Bike> return_value){
        final JSONObject oggettoModifica = createJSONRemove(bike_id, user_id);
        ServerRequest.getInstance(context).postBasicRequest(ServerRoutes.REMOVE_BIKES, oggettoModifica, new NetworkCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                return_value.onSuccess(new Bike());
            }

            @Override
            public void onError(int statusCode, JSONObject cache) {
                return_value.onError("Remove error", "Qualcosa è andato storto");
            }
        });
    }

    private static JSONObject createJSONRemove(int bike_id, int user_id) {JSONObject mJson = new JSONObject();
        try{
            mJson.put("user_id", user_id);
            mJson.put("bike_id", bike_id);
            return mJson;
        }catch (JSONException e){
            e.getCause();
        }
        return null;

    }

    private static JSONObject createJSONModify(int bike_id, int rack_id, int user_id) {
        JSONObject mJson = new JSONObject();
        try{
            mJson.put("user_id", user_id);
            mJson.put("rack_id", rack_id);
            mJson.put("bike_id", bike_id);
            return mJson;
        }catch (JSONException e){
            e.getCause();
        }
        return null;
    }

    private static JSONObject createJSONAdd(int user_id, int rack_id, int unlock_code){
        JSONObject mJson = new JSONObject();
        try{
            mJson.put("user_id", user_id);
            mJson.put("rack_id", rack_id);
            mJson.put("unlock_code", unlock_code);
            return mJson;
        }catch (JSONException e){
            e.getCause();
        }
        return null;
    }

    public static void postAddUser(final Context context, String email, final ServerResponseParserCallback<User> return_value){
        final JSONObject dati_json_form = new JSONObject();
        try {
            dati_json_form.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerRequest.getInstance(context).postBasicRequest(ServerRoutes.ADD_USER, dati_json_form, new NetworkCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    User user = new User();
                    user.setEmail(response.get("email").toString());
                    user.setmRole("standard");
                    user.setUserState(1);
                    return_value.onSuccess(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(int statusCode, JSONObject cache) {
                return_value.onError(getErrorTitle(statusCode), getErrorMessage(statusCode));;
            }
        });
    }

    public static void getUserId(final Context context, final String email,
                                final ServerResponseParserCallback<User> serverResponseParserCallback) {
        String url = ServerRoutes.USER_ID+"?email="+email;
        ServerRequest.getInstance(context).getBasicRequest(url, new NetworkCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                serverResponseParserCallback.onSuccess(getUserFromJSONObject(response));
            }

            @Override
            public void onError(int statusCode, JSONObject cache) {
                serverResponseParserCallback.onError(getErrorTitle(statusCode), getErrorMessage(statusCode));
            }
        });
    }

    /**
     * Calls GET /racks endpoint
     *
     * @param context                      context where the method is called
     * @param serverResponseParserCallback interface used to get the response
     */
    public static void getRacks(final Context context,
                                final ServerResponseParserCallback<List<Rack>> serverResponseParserCallback) {
        String url = ServerRoutes.RACKS;
        Log.d(TAG, url);
        ServerRequest.getInstance(context).getBasicRequest(url, new NetworkCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                serverResponseParserCallback.onSuccess(getRacksFromJSONObject(response));
            }

            @Override
            public void onError(int statusCode, JSONObject cache) {
                serverResponseParserCallback.onError(getErrorTitle(statusCode), getErrorMessage(statusCode));
            }
        });
    }

    /**
     * Calls GET /racks/{id} endpoint
     *
     * @param context                      context where the method is called
     * @param rackID                       id of the requested rack
     * @param serverResponseParserCallback interface used to get the response
     */
    public static void getRack(final Context context, final int rackID,
                               final ServerResponseParserCallback<Rack> serverResponseParserCallback) {
        String url = ServerRoutes.RACKS + "/" + rackID;
        Log.d(TAG, url);
        ServerRequest.getInstance(context).getBasicRequest(url, new NetworkCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                serverResponseParserCallback.onSuccess(getRackFromJSONObject(response));
            }

            @Override
            public void onError(int statusCode, JSONObject cache) {
                serverResponseParserCallback.onError(getErrorTitle(statusCode), getErrorMessage(statusCode));
            }
        });
    }

    /**
     * Calls GET /bikes/{id} endpoint
     *
     * @param context                      context where the method is called
     * @param bikeID                       id of the requested bike
     * @param serverResponseParserCallback interface used to get the response
     */
    public static void getBike(final Context context, final int bikeID,
                               final ServerResponseParserCallback<Bike> serverResponseParserCallback) {
        String url = ServerRoutes.BIKES + "/" + bikeID;
        Log.d(TAG, url);
        ServerRequest.getInstance(context).getBasicRequest(url, new NetworkCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                serverResponseParserCallback.onSuccess(getBikeFromJSONObject(response));
            }

            @Override
            public void onError(int statusCode, JSONObject cache) {
                serverResponseParserCallback.onError(getErrorTitle(statusCode), getErrorMessage(statusCode));
            }
        });
    }


    /**
     * Calls POST /reports endpoint
     *
     * @param context                      context where the method is called
     * @param report                       report to send
     * @param serverResponseParserCallback interface used to get the response
     */
    public static void postReport(final Context context, Report report,
                                  final ServerResponseParserCallback<Report> serverResponseParserCallback) {
        JSONObject jsonObject = createJSONObjectFromReport(report);
        ServerRequest.getInstance(context).postBasicRequest(ServerRoutes.REPORTS, jsonObject, new NetworkCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                serverResponseParserCallback.onSuccess(getReportFromJSONObject(response));
            }

            @Override
            public void onError(int statusCode, JSONObject cache) {
                serverResponseParserCallback.onError(getErrorTitle(statusCode), getErrorMessage(statusCode));
            }
        });
    }

    /**
     * Calls POST /rentals endpoint
     *
     * @param context                      context where the method is called
     * @param bikeID                       id of the bike to rent
     * @param serverResponseParserCallback interface used to get the response
     */
    public static void postRental(final Context context, int bikeID, int userID,
                                  final ServerResponseParserCallback<Rental> serverResponseParserCallback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userID);
            jsonObject.put("bike_id", bikeID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, jsonObject.toString());
        ServerRequest.getInstance(context).postBasicRequest(ServerRoutes.RENTALS, jsonObject, new NetworkCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                serverResponseParserCallback.onSuccess(getRentalFromJSONObject(response));
            }

            @Override
            public void onError(int statusCode, JSONObject cache) {
                serverResponseParserCallback.onError(getErrorTitle(statusCode), getErrorMessage(statusCode));
            }
        });
    }



    /**
     * Calls PUT /rentals/{id} endpoint
     *
     * @param context                      context where the method is called
     * @param rentalID                     id of the rental to be ended
     * @param rackID                       id of the rack where end the rental
     * @param serverResponseParserCallback interface used to get the response
     */
    public static void putRental(final Context context, int rentalID, int rackID,
                                 final ServerResponseParserCallback<Rental> serverResponseParserCallback) {
        String url = ServerRoutes.RENTALS + "/" + rentalID;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rack_id", rackID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, jsonObject.toString());
        ServerRequest.getInstance(context).putBasicRequest(url, jsonObject, new NetworkCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                serverResponseParserCallback.onSuccess(getRentalFromJSONObject(response));
            }

            @Override
            public void onError(int statusCode, JSONObject cache) {
                serverResponseParserCallback.onError(getErrorTitle(statusCode), getErrorMessage(statusCode));
            }
        });
    }

    public static void postAddedBikes(final Context context, final int user_id,
                                      final ServerResponseParserCallback<List<BikeHistory>> serverResponseParserCallback){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerRequest.getInstance(context).postBasicRequest(ServerRoutes.BIKE_ADDED_HISTORY, jsonObject,
                new NetworkCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        serverResponseParserCallback.onSuccess(createListBikeHistoryFromObject(response));
                    }

                    @Override
                    public void onError(int statusCode, JSONObject cache) {
                        serverResponseParserCallback.onError(getErrorTitle(statusCode), getErrorMessage(statusCode));
                    }
                });
    }


    public static List<BikeHistory> createListBikeHistoryFromObject(JSONObject response){
        if (response != null) {
            List<BikeHistory> bikes = new ArrayList<>();
            try {
                JSONArray mJson = response.getJSONArray("bikes");
                for (int i = 0; i < mJson.length(); i++) {
                    bikes.add(createBikeHistoryFromJSONObject(mJson.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return bikes;
        }
        return null;
    }

    public static void getRentals(final Context context, int user_id, final boolean active,
                                  final ServerResponseParserCallback<List<Rental>> serverResponseParserCallback) {
        String url = ServerRoutes.RENTALS + "/?id=" + user_id;
        if(active)
            url += "&active=yes";
        Log.d(TAG, url);
        ServerRequest.getInstance(context).getBasicRequest(url, new NetworkCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                if(active){
                    List<Rental> rentalInProgress = new ArrayList<>();
                    rentalInProgress.add(getRentalFromJSONObject(response));
                    serverResponseParserCallback.onSuccess(rentalInProgress);
                }
                else serverResponseParserCallback.onSuccess(getRentalsFromJSONObject(response));
            }

            @Override
            public void onError(int statusCode, JSONObject cache) {
                serverResponseParserCallback.onError(getErrorTitle(statusCode), getErrorMessage(statusCode));
            }
        });
    }

    private static List<Rental> getRentalsFromJSONObject(JSONObject response) {
        if (response != null) {
            List<Rental> rentals = new ArrayList<>();
            try {
                JSONArray mJson = response.getJSONArray("rentals");
                for (int i = 0; i < mJson.length(); i++) {
                    rentals.add(createRentalFromJSONObject(mJson.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return rentals;
        }
        return null;
    }




    private static User getUserFromJSONObject(JSONObject response) {
        if (response.length() != 0) {
            User user = new User();
            try {
                user.setEmail(response.getString("email"));
                user.setUserState(response.getInt("user_state_id"));
                user.setmRole(response.getString("role"));
                user.setmId(response.getInt("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return user;
        }
        return null;
    }

    private static Rack getRackFromJSONObject(JSONObject response) {
        if (response != null) {
            Rack rack = new Rack();
            try {
                JSONObject mJson = response.getJSONObject("rack");
                rack = createRackFromJSONObject(mJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return rack;
        }
        return null;
    }

    private static List<Rack> getRacksFromJSONObject(JSONObject response) {
        if (response != null) {
            List<Rack> racks = new ArrayList<>();
            try {
                JSONArray mJson = response.getJSONArray("racks");
                for (int i = 0; i < mJson.length(); i++) {
                    racks.add(createRackFromJSONObject(mJson.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return racks;
        }
        return null;
    }

    private static Rental getRentalFromJSONObject(JSONObject response) {
        if (response != null) {
            Rental rental = new Rental();
            try {
                JSONObject mJson = response.getJSONObject("rental");
                rental = createRentalFromJSONObject(mJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return rental;
        }
        return null;
    }


    private static Report getReportFromJSONObject(JSONObject response) {
        if (response != null) {
            Report report = new Report();
            try {
                JSONObject mJson = response.getJSONObject("report");
                report.setBikeId(mJson.getInt("bike_id"));
                report.setUserId(mJson.getInt("user_id"));
                report.setType(mJson.getInt("type"));
                report.setDescription(mJson.getString("description"));
                report.setCreatedOn(mJson.getString("created_on"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return report;
        }
        return null;
    }

    private static Bike getBikeFromJSONObject(JSONObject response) {
        if (response != null) {
            Bike bike = new Bike();
            try {
                JSONObject mJson = response.getJSONObject("bike");
                bike = createBikeFromJSONObject(mJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return bike;
        }
        return null;
    }







    private static JSONObject createJSONLogin(String email,String password){
        JSONObject mJson = new JSONObject();
        try{
            mJson.put("email", email);
            mJson.put("password", password);
            return mJson;
        }catch (JSONException e){
            e.getCause();
        }
        return null;
    }


    private static Rack createRackFromJSONObject(JSONObject mJson) {
        if (mJson != null) {
            Rack rack = new Rack();
            try {
                rack.setId(mJson.getInt("id"));
                rack.setAvailableStands(mJson.getInt("available_stands"));
                rack.setAvailableBikes(mJson.getInt("available_bikes"));
                rack.setLatitude(mJson.getDouble("latitude"));
                rack.setLongitude(mJson.getDouble("longitude"));
                rack.setAddressLocality(mJson.getString("address_locality"));
                rack.setStreetAddress(mJson.getString("street_address"));
                rack.setLocationDescription(mJson.getString("location_description"));
                List<Building> buildings = new ArrayList<>();
                JSONArray buildingsArray = mJson.getJSONArray("buildings");
                for (int i = 0; i < buildingsArray.length(); i++) {
                    buildings.add(createBuildingFromJSONObject(buildingsArray.getJSONObject(i)));
                }
                rack.setBuildings(buildings);
                if (mJson.has("distance")) {
                    rack.setDistance(mJson.getDouble("distance"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return rack;
        }
        return null;
    }

    private static Building createBuildingFromJSONObject(JSONObject mJson) {
        if (mJson != null) {
            Building building = new Building();
            try {
                building.setId(mJson.getInt("id"));
                building.setName(mJson.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return building;
        }
        return null;
    }


    private static JSONObject createJSONObjectFromReport(Report report) {
        if (report != null) {
            JSONObject mJson = new JSONObject();
            try {
                mJson.put("bike_id", report.getBikeId());
                mJson.put("description", report.getDescription());
                mJson.put("user_id", report.getUserId());
                mJson.put("type", report.getType());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mJson;
        }
        return null;
    }



    private static Rental createRentalFromJSONObject(JSONObject mJson) {
        if (mJson != null) {
            Rental rental = new Rental();
            try {
                rental.setId(mJson.getInt("id"));
                rental.setBike(createBikeFromJSONObject(mJson.getJSONObject("bike")));
                rental.setStartedOn(mJson.getString("started_on"));
                rental.setStartRack(createRackFromJSONObject(mJson.getJSONObject("start_rack")));
                if (mJson.has("completed_on")) {
                    rental.setCompletedOn(mJson.getString("completed_on"));
                }
                if (mJson.has("end_rack")) {
                    rental.setEndRack(createRackFromJSONObject(mJson.getJSONObject("end_rack")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return rental;
        }
        return null;
    }



    private static Bike createBikeFromJSONObject(JSONObject mJson) {
        if (mJson != null) {
            Bike bike = new Bike();
            try {
                bike.setId(mJson.getInt("id"));
                bike.setUnlockCode(mJson.getInt("unlock_code"));
                if (mJson.has("bike_state")) {
                    bike.setBikeState(createBikeStateFromJSONObject(mJson.getJSONObject("bike_state")));
                }
                if (mJson.has("rack")) {
                    bike.setRack(createRackFromJSONObject(mJson.getJSONObject("rack")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return bike;
        }
        return null;
    }

    private static BikeState createBikeStateFromJSONObject(JSONObject mJson) {
        if (mJson != null) {
            BikeState bikeState = new BikeState();
            try {
                //bikeState.setId(mJson.getInt("id"));
                bikeState.setDescription(mJson.getString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return bikeState;
        }
        return null;
    }


    public static BikeHistory createBikeHistoryFromJSONObject(JSONObject mJson){
        if(mJson != null){
            BikeHistory bike = new BikeHistory();
            try {
                bike.setmBikeId(mJson.getInt("id"));
                bike.setmBikeDescription(mJson.getString("description"));
                bike.setmCreatedOn(mJson.getString("created_on"));
                bike.setmUnlockCode(mJson.getInt("unlock_code"));
                bike.setmRackBuildings(mJson.getString("location_description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return bike;
        }
        return null;
    }





    private static String getErrorTitle(int statusCode) {
        String errorTitle = "Errore";
        switch (statusCode) {
            case ServerRequest.NO_CONNECTION:
                errorTitle = "Nessuna connessione di rete";
                break;
            case ServerRequest.TIMEOUT_ERROR:
                errorTitle = "Tempo scaduto";
                break;
            case ServerRequest.GENERIC_ERROR:
                break;
        }
        return errorTitle;
    }

    private static String getErrorMessage(int statusCode) {
        String errorMessage = "Sconosciuto";
        switch (statusCode) {
            case ServerRequest.NO_CONNECTION:
                errorMessage = "Assicurati di essere connesso ad una rete Wi-Fi o mobile.";
                break;
            case ServerRequest.TIMEOUT_ERROR:
                errorMessage = "E' passato troppo tempo dalla richiesta, si prega di riprovare.";
                break;
            case ServerRequest.GENERIC_ERROR:
                errorMessage = "Qualcosa è andato storto, si prega di riprovare.";
                break;
        }
        return errorMessage;
    }

}
