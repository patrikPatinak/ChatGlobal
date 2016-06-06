package com.example.boush.dreamchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Client on 20.5.2016.
 */
public class Server {

    public void login(String email, String password, final Context context){

        Map<String, String> postParam = new HashMap<String, String>();
            postParam.put(Constants.KEY_EMAIL, email);
            postParam.put(Constants.KEY_PASSWORD, password);
            Log.d("Volley JSON to send ", new JSONObject(postParam).toString());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    Constants.loginUrl, new JSONObject(postParam),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("Volley ", response.toString());
                            Log.d("Response status code", String.valueOf(response));
                            String token = null;
                            int userid;
                            try {
                                token = response.getString(Constants.KEY_TOKEN);
                                userid = response.getInt(Constants.KEY_USERID);
                                Log.d("Volley token", token);
                                Log.d("Volley userid", String.valueOf(userid));
                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.remove(Constants.KEY_TOKEN);
                                editor.remove(Constants.KEY_USERID);
                                editor.apply();
                                editor.putInt(Constants.KEY_USERID, userid);
                                editor.putString(Constants.KEY_TOKEN, token);
                                editor.apply();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {



                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Volley ", "Error: " + error.getMessage());
                    // Handle the error
                    Log.d("Error status code", String.valueOf(error.networkResponse.statusCode));
                    //error.networkResponse.data;

                }
            })

            {
                /**
                 * Passing some request headers
                 * */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.tag_json_obj);

        /*HttpURLConnection connection = (HttpURLConnection) new URL(loginUrl).openConnection();
                String encoded = Base64.encodeToString((email + ":" + password).getBytes("UTF-8"), Base64.NO_WRAP);
                connection.setRequestProperty("Authorization", "Basic " + encoded);
                connection.setRequestMethod("POST");
                connection.setInstanceFollowRedirects(true);
                connection.connect();
                int resCode = connection.getResponseCode();
                Log.d("Response kod: ", String.valueOf(resCode));*/


    }

    public void register(String nickname, String email, String password, Context context, final VolleyCallback callback){

            Map<String, String> postParam = new HashMap<String, String>();
            postParam.put(Constants.KEY_EMAIL, email);
            postParam.put(Constants.KEY_PASSWORD, password);
            postParam.put(Constants.KEY_NICKNAME, nickname);
            Log.d("Volley JSON to send ", new JSONObject(postParam).toString());

            final Context appcontext = context;

            //final JsonObjectRequest jsonObjReq = new JsonObjectRequest
            final ServerStatusRequestObject jsonObjReq = new ServerStatusRequestObject(Request.Method.POST,
                    Constants.registerUrl, new JSONObject(postParam).toString(),
                    new Response.Listener() {

                        @Override
                        public void onResponse(Object response) {
                            //Log.d("Status kod", String.valueOf(response));
                            callback.onSuccess((Integer) response);
                        }

                        /*@Override
                        public void onResponse(JSONObject response) {

                            Log.d("Status kod", String.valueOf(response));
                            /*Log.d("Volley ", response.toString());
                            String success = null;

                            try {
                                success = response.getString("success");
                                Log.d("Volley Reg Success", success);
                                if (success.equals("true")){
                                    setReg(true);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }*/
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //VolleyLog.d("Volley ", "Error: " + error.getMessage())
                            /*if (error.networkResponse != null) {
                                int kod = error.networkResponse.statusCode;
                                Log.d("Error Response code", String.valueOf(kod));
                            }*/
                            Log.d("Error kod", error.getMessage()+"");
                        }
            }) {
                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.tag_json_obj);

    }

    /*public void update(String username, String email, String password, String phone, String firstName, String lastName, Context context){
        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("username", username);
        postParam.put("email", email);
        postParam.put("password", password);
        postParam.put("phone", phone);
        postParam.put("firstName", firstName);
        postParam.put("lastName", lastName);

        Log.d("Volley JSON to send ", new JSONObject(postParam).toString());

        final Context appContext = context;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                updateUrl, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley ", response.toString());
                        String token = null;
                        int userid;
                        try {
                            token = response.getString("token");
                            userid = response.getInt("userid");
                            Log.d("Volley token", token);
                            Log.d("Volley userid", String.valueOf(userid));
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear();
                            editor.apply();
                            editor.putInt("userid", userid);
                            editor.putString("token", token);
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley ", "Error: " + error.getMessage());
            }
        })

        {
            /**
             * Passing some request headers
             */
            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }*/

  /*  public boolean updatePassword(String password, Context context){
        Map<String, String> postParam = new HashMap<String, String>();

        postParam.put("password", password);

        Log.d("Volley JSON to send ", new JSONObject(postParam).toString());

        final Context appContext = context;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constants.updateUrl, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley ", response.toString());
                        String token = null;
                        int userid;
                        try {
                            token = response.getString("token");
                            userid = response.getInt("userid");
                            Log.d("Volley token", token);
                            Log.d("Volley userid", String.valueOf(userid));
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear();
                            editor.apply();
                            editor.putInt("userid", userid);
                            editor.putString("token", token);
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley ", "Error: " + error.getMessage());
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.tag_json_obj);
    }
    */

    public void logout(final Context context, final VolleyCallback callback){

            Map<String, String> postParam = new HashMap<String, String>();
            Log.d("Volley JSON to send ", new JSONObject(postParam).toString());

            ServerStatusRequestObject jsonObjReq = new ServerStatusRequestObject(Request.Method.DELETE,
                    Constants.loginUrl, new JSONObject(postParam).toString(),
                    new Response.Listener() {

                        @Override
                        public void onResponse(Object response) {
                            Log.d("Kod logout", String.valueOf((Integer) response));
                            callback.onSuccess((Integer) response);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //VolleyLog.d("Volley ", "Error: " + error.getMessage());
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        callback.onSuccess(401);
                    } else if (error instanceof AuthFailureError) {
                        callback.onSuccess(401);
                    } else if (error instanceof ServerError) {
                        //TODO
                    } else if (error instanceof NetworkError) {
                        //TODO
                    } else if (error instanceof ParseError) {
                        //TODO
                    }
                    //Log.d("Error kod", error.getMessage()+"");
                    //Log.d("Kod", String.valueOf(error.networkResponse.statusCode));
                }
            })

            {
                /**
                 * Passing some request headers
                 * */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    String token = prefs.getString(Constants.KEY_TOKEN, null);
                    int userid = prefs.getInt(Constants.KEY_USERID, 0);
                    headers.put(Constants.KEY_USERID, String.valueOf(4));
                    headers.put(Constants.KEY_TOKEN, "a019ed400268a575b4638727d8f2b4");
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.tag_json_obj);

    }

    public void sendRequest(final Context context, int id){
        Map<String, Integer> postParam = new HashMap<String, Integer>();
        postParam.put(Constants.KEY_RECEIVER, id);

        Log.d("Volley JSON to send ", new JSONObject(postParam).toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constants.requestUrl, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley ", response.toString());
                        String success = null;

                        try {
                            success = response.getString(Constants.KEY_MESSAGE);
                            Log.d("Volley Reg Success", success);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {



            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Volley ", "Error: " + error.getMessage());
                Log.d("Error kod", error.getMessage()+"");
            }
        })

        {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String token = prefs.getString(Constants.KEY_TOKEN, null);
                int userid = prefs.getInt(Constants.KEY_USERID, 0);
                headers.put(Constants.KEY_USERID, String.valueOf(userid));
                headers.put(Constants.KEY_TOKEN, token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.tag_json_obj);

    }

    public void cancelReq(int requestid, final Context context){
        Map<String, Integer> postParam = new HashMap<String, Integer>();
        postParam.put(Constants.KEY_REQUESTID, requestid);

        Log.d("Volley JSON to send ", new JSONObject(postParam).toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                Constants.requestUrl, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley ", response.toString());
                        String success = null;

                        try {
                            success = response.getString(Constants.KEY_MESSAGE);
                            Log.d("Volley Reg Success", success);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {



            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Volley ", "Error: " + error.getMessage());
                Log.d("Error kod", error.getMessage()+"");
            }
        })

        {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String token = prefs.getString(Constants.KEY_TOKEN, null);
                int userid = prefs.getInt(Constants.KEY_USERID, 0);
                headers.put(Constants.KEY_USERID, String.valueOf(userid));
                headers.put(Constants.KEY_TOKEN, token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.tag_json_obj);

    }

    public void acceptReq(int requestid, final Context context){
        Map<String, Integer> postParam = new HashMap<String, Integer>();
        postParam.put(Constants.KEY_REQUESTID, requestid);

        Log.d("Volley JSON to send ", new JSONObject(postParam).toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constants.reqAcceptUrl, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley ", response.toString());
                        String success = null;

                        try {
                            success = response.getString(Constants.KEY_MESSAGE);
                            Log.d("Volley Reg Success", success);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {



            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Volley ", "Error: " + error.getMessage());
                Log.d("Error kod", error.getMessage()+"");
            }
        })

        {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String token = prefs.getString(Constants.KEY_TOKEN, null);
                int userid = prefs.getInt(Constants.KEY_USERID, 0);
                headers.put(Constants.KEY_USERID, String.valueOf(userid));
                headers.put(Constants.KEY_TOKEN, token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.tag_json_obj);

    }

    public void loadReqs(final Context context){
        Map<String, String> postParam = new HashMap<String, String>();
        Log.d("Volley JSON to send ", new JSONObject(postParam).toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Constants.reqAcceptUrl, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley ", response.toString());
                        List<com.example.boush.dreamchat.Request> reqs = new ParseJSON().getRequests(response);
                        Log.d("Request 1", String.valueOf(reqs.get(0).getRequestid())+" "+reqs.get(0).getSurname());
                        /*ParseRequests reqs = new ParseRequests(response.toString());
                        reqs.parseJSON();
                        Log.d("Volley message", ParseRequests.message);
                        Log.d("Volley array[0]", String.valueOf(ParseRequests.reqids[0])+" "+String.valueOf(ParseRequests.userids[0])
                        +" "+ ParseRequests.names[0]+" "+ ParseRequests.surnames[0]+" "+ ParseRequests.nicknames[0]);*/
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley ", "Error: " + error.getMessage());
                // Handle the error
                Log.d("Error status code", String.valueOf(error.networkResponse.statusCode));
                //error.networkResponse.data;

            }
        })

        {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.tag_json_obj);
    }

    public void getContacts(final Context context, final VolleyCallback callback){
        Map<String, String> postParam = new HashMap<String, String>();
        Log.d("Volley JSON to send ", new JSONObject(postParam).toString());

        //JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.GET, Constants.contactsUrl, new Response.Listener<JSONArray>());
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, Constants.contactsUrl, "{}", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Volley ", response.toString());
                JSONObject object = new JSONObject();
                try {
                    object.put(Constants.KEY_USERID, 5);
                    object.put(Constants.KEY_NAME, "Jozef");
                    object.put(Constants.KEY_SURNAME, "Zelený");
                    object.put(Constants.KEY_NICKNAME, "jozko007");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray ja = new JSONArray();
                ja.put(object);

                JSONObject object2 = new JSONObject();
                try {
                    object2.put(Constants.KEY_USERID, 3);
                    object2.put(Constants.KEY_NAME, "Chuck");
                    object2.put(Constants.KEY_SURNAME, "Norris");
                    object2.put(Constants.KEY_NICKNAME, "chuckn0rris");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ja.put(object2);

                Log.d("JSONArray", ja.toString());

                /*JSONObject mainObj = new JSONObject();
                try {
                    mainObj.put("", ja);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                callback.onSuccess(ja);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley ", "Error: " + error.getMessage());
                // Handle the error
                //Log.d("Error status code", String.valueOf(error.networkResponse.statusCode));
                //error.networkResponse.data;
                //Log.d("error", error.getMessage());
                /*if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    callback.onSuccess(401);
                } else if (error instanceof AuthFailureError) {
                    callback.onSuccess(401);
                } else if (error instanceof ServerError) {
                    //TODO
                } else if (error instanceof NetworkError) {
                    //TODO
                } else if (error instanceof ParseError) {
                    //TODO
                }*/
            }
        })

        {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<String, String>();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String token = prefs.getString(Constants.KEY_TOKEN, null);
            int userid = prefs.getInt(Constants.KEY_USERID, 0);
            headers.put(Constants.KEY_USERID, String.valueOf(3));
            headers.put(Constants.KEY_TOKEN, "a019ed400268a575b4638727d8f2b4");
            headers.put("Content-Type", "application/json");
            return headers;
        }
        };

        AppController.getInstance().addToRequestQueue(req, Constants.tag_json_obj);

        /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Constants.contactsUrl, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley ", response.toString());
                        JSONObject object = new JSONObject();
                        try {
                            object.put(Constants.KEY_USERID, 5);
                            object.put(Constants.KEY_NAME, "Jozef");
                            object.put(Constants.KEY_SURNAME, "Zelený");
                            object.put(Constants.KEY_NICKNAME, "jozko007");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONArray ja = new JSONArray();
                        ja.put(object);

                        JSONObject object2 = new JSONObject();
                        try {
                            object2.put(Constants.KEY_USERID, 3);
                            object2.put(Constants.KEY_NAME, "Chuck");
                            object2.put(Constants.KEY_SURNAME, "Norris");
                            object2.put(Constants.KEY_NICKNAME, "chuckn0rris");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ja.put(object2);

                        JSONObject mainObj = new JSONObject();
                        try {
                            mainObj.put("", ja);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.onSuccess(mainObj);
                        //List<Contact> contacts = new ParseJSON().getContacts(response);
                        //Log.d("Contact 1", String.valueOf(contacts.get(0).getTitle()));
                        /*ParseRequests reqs = new ParseRequests(response.toString());
                        reqs.parseJSON();
                        Log.d("Volley message", ParseRequests.message);
                        Log.d("Volley array[0]", String.valueOf(ParseRequests.reqids[0])+" "+String.valueOf(ParseRequests.userids[0])
                        +" "+ ParseRequests.names[0]+" "+ ParseRequests.surnames[0]+" "+ ParseRequests.nicknames[0]);*/
                    /*}
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley ", "Error: " + error.getMessage());
                // Handle the error
                //Log.d("Error status code", String.valueOf(error.networkResponse.statusCode));
                //error.networkResponse.data;
                Log.d("1. error", String.valueOf(error instanceof NoConnectionError));
                Log.d("error", error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d("1. error", String.valueOf(error instanceof NoConnectionError));
                    callback.onSuccess(401);
                } else if (error instanceof AuthFailureError) {
                    callback.onSuccess(401);
                } else if (error instanceof ServerError) {
                    //TODO
                } else if (error instanceof NetworkError) {
                    //TODO
                } else if (error instanceof ParseError) {
                    //TODO
                }

            }
        })

        {
            /**
             * Passing some request headers
             * */
            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String token = prefs.getString(Constants.KEY_TOKEN, null);
                int userid = prefs.getInt(Constants.KEY_USERID, 0);
                headers.put(Constants.KEY_USERID, String.valueOf(3));
                headers.put(Constants.KEY_TOKEN, "a019ed400268a575b4638727d8f2b4");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.tag_json_obj);
        AppController.getInstance().addToRequestQueue(req, Constants.tag_json_obj);*/
    }



}


