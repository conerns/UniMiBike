package com.unimib.unimibike.Util;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//da ricordare di dover fare la import anche dei restanti
import com.unimib.unimibike.Model.User;
import com.unimib.unimibike.Model.Building;
import com.unimib.unimibike.Model.Rack;
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

        Log.d("basic request-", "non entra in post User Login");
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

    private static User getUserFromJSONObject(JSONObject response) {
        if (response.length() != 0) {
            User user = new User();
            try {
                user.setEmail(response.getString("email"));
                user.setUserState(response.getInt("user_state_id"));
                user.setmRole(response.getString("role"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return user;
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
