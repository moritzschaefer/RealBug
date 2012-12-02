package com.hackathon.realbugtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class RealBugUtils {
            
    public static final String REAL_BUG_JSON_KEY_POSITION = "position";
    public static final String REAL_BUG_JSON_KEY_DESCRIPTION = "description";
    public static final String REAL_BUG_JSON_KEY_IMAGE = "image";
    public static final String REAL_BUG_JSON_KEY_ID = "id";
    public static final String URL = "http://obscure-fjord-9100.herokuapp.com/index.php";
    
    public static final String LOG_TAG = "RealBugUtils";

    //TODO: Think about using this class as parameter for the create/post call
    /**
     * Container class for one entry in the RealBug-list
     */
    public static class RealBugData {
        public double longitude;
        public double latitude;
        public String description;
        public String imageLink;
        public RealBugData(double longitude, double latitude, String description, String imageLink) {
            init(longitude, latitude, description, imageLink);
        }
        public RealBugData(String position, String description, String imageLink) {
            String[] doubleStrings = position.split(",");
            if(doubleStrings.length != 2) {
                Log.e(LOG_TAG, "error in position!. has to be of format float,float");
                return; //Error
            }
            init(Double.parseDouble(doubleStrings[0]),Double.parseDouble(doubleStrings[1]), description, imageLink);
        }
        private void init(double longitude, double latitude, String description, String imageLink) {
            this.longitude = longitude; this.latitude = latitude; this.description = description; this.imageLink = imageLink;
        }
    }


    /**
     * Gets all the necessary parameters to generate a create-POST-JSON-Object for a RealBug
     *
     * @param longitude     The GPS longitude 
     * @param latitude      The GPS latitude 
     * @param description   The description from the user for the RealBug
     * @return              Returns a json-object as String 
     */
    private static String jsonStringCreate(double longitude, double latitude, String description) {
        JSONObject object = new JSONObject();
        try {
            object.put(REAL_BUG_JSON_KEY_POSITION, String.format("%f,%f", longitude, latitude));
            object.put(REAL_BUG_JSON_KEY_DESCRIPTION, description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    /**
     * Send a post request to the server with the given content, the post content type is JSON
     *
     * @param longitude     The GPS longitude 
     * @param latitude      The GPS latitude 
     * @param description   The description from the user for the RealBug
     * @param image         The image the user took to document the RealBug as JPG
     *
     */
    public static void realBugPost(double longitude, double latitude, String description, byte[] image) {
        String jsonPostObject = jsonStringCreate(longitude, latitude, description);
        DefaultHttpClient httpclient = new DefaultHttpClient(); 
        HttpPost httppost = new HttpPost(URL+"/RealBug");
        StringEntity se;
        try {
        	se = new StringEntity(jsonPostObject);
        } catch (UnsupportedEncodingException e) {
        	e.printStackTrace();
        	return;
        }
        httppost.setEntity(se);

        httppost.setHeader("Accept", "application/json"); 
        httppost.setHeader("Content-Type", "application/json");
        HttpResponse response;
        try {
            response = httpclient.execute(httppost);
        }
        catch (HttpResponseException e) {
            //Filter e for 400 (client error) 500 (user error) 300 (forward... change to code to automatically follow :))
            e.printStackTrace();
            return;
            //Do some user feedback to singalize the fail
        }
        catch (ClientProtocolException e) {
        	e.printStackTrace();
        	return;
        }
        catch (IOException e) {
        	e.printStackTrace();
        	return;
        }
        HttpEntity entity = response.getEntity();
        InputStream inputStream;
        try {
        	inputStream = entity.getContent();
        }
        catch(IOException e) {
        	e.printStackTrace();
        	return;
        }
        JSONObject obj;
        try {
        	String returnValue = convertStreamToString(inputStream);
        	Log.d(LOG_TAG, returnValue);
        	obj = new JSONObject(returnValue);
            updateRealBugImg(obj.getInt(REAL_BUG_JSON_KEY_ID), image);
        }
        catch(JSONException e) {
        	e.printStackTrace();
        	return;
        }

    }
    /**
     * Get all RealBugs in radius around given position and return as a list 
     * 
     * @param longitude Current position longitude from GPS
     * @param latitude Current position latitude from GPS
     * @param radius Search radius from current position for RealBugs in meters
     *
     * @return List of all RealBugs with max-distance radius from current position
     */

    public static List<RealBugData> realBugAreaList(double longitude, double latitude, double radius) {
        DefaultHttpClient httpclient = new DefaultHttpClient(); 
        //TODO make this with HttpParams !!!!
        HttpGet httpget = new HttpGet(URL+String.format("/RealBug?ln=%f&lt=%f&rt=%f",longitude, latitude, radius));
        
        
        httpget.setHeader("Accept", "application/json");
        //httpget.setHeader("Content-Type", "application/json");
        HttpResponse response;

        try {
            response = httpclient.execute(httpget);
        }
        catch (Exception e) {
            //Filter e for 400 (client error) 500 (user error) 300 (forward... change to code to automatically follow :))
            e.printStackTrace();
            return new ArrayList<RealBugData>();
            //Do some user feedback to singalize the fail
        }
        HttpEntity entity = response.getEntity();
        if(entity == null) 
            return new ArrayList<RealBugData>(); //Return empty list
        JSONArray jsonResults;
        try {
        	InputStream instream = entity.getContent();
        

        	jsonResults = new JSONArray(convertStreamToString(instream));
        }
        catch(IOException e) {
        	e.printStackTrace();
        	return new ArrayList<RealBugData>();
        }
        catch(JSONException e) {
        	e.printStackTrace();
        	return new ArrayList<RealBugUtils.RealBugData>();
        }

        httpget.abort();

        

        List<RealBugData> returnArray = new ArrayList<RealBugData>();
        for(int i=0; i<jsonResults.length(); i++) {
        	int id;
        	String position, description;
        	try {
	            JSONObject jsonObject = jsonResults.getJSONObject(i);
	            description =  jsonObject.getString(REAL_BUG_JSON_KEY_DESCRIPTION);
	            position =  jsonObject.getString(REAL_BUG_JSON_KEY_POSITION);
	            id =  jsonObject.getInt(REAL_BUG_JSON_KEY_ID);
        	} catch(JSONException e) {
        		e.printStackTrace();
        		return new ArrayList<RealBugUtils.RealBugData>();
        	}
            String image = String.format("/RealBug/%d/img", id);
            returnArray.add(new RealBugData(position, description, image));
        }
        return returnArray;
    }

    /**
     * Get all RealBugs 
     * 
     * @return List of all RealBugs 
     */

    public static List<RealBugData> realBugList() {
        DefaultHttpClient httpclient = new DefaultHttpClient(); 
        HttpGet httpget = new HttpGet(URL+"/RealBug");
        httpget.setHeader("Accept", "application/json");
        //httpget.setHeader("Content-Type", "application/json");
        HttpResponse response;

        try {
            response = httpclient.execute(httpget);
        }
        catch (Exception e) {
            //Filter e for 400 (client error) 500 (user error) 300 (forward... change to code to automatically follow :))
            e.printStackTrace();
            return new ArrayList<RealBugData>();
            //Do some user feedback to singalize the fail
        }
        HttpEntity entity = response.getEntity();
        if(entity == null) 
            return new ArrayList<RealBugData>(); //Return empty list
        JSONArray jsonResults;
        try {
        	InputStream instream = entity.getContent();
        

        	jsonResults = new JSONArray(convertStreamToString(instream));
        }
        catch(IOException e) {
        	e.printStackTrace();
        	return new ArrayList<RealBugData>();
        }
        catch(JSONException e) {
        	e.printStackTrace();
        	return new ArrayList<RealBugUtils.RealBugData>();
        }

        httpget.abort();

        

        List<RealBugData> returnArray = new ArrayList<RealBugData>();
        for(int i=0; i<jsonResults.length(); i++) {
        	int id;
        	String position, description;
        	try {
	            JSONObject jsonObject = jsonResults.getJSONObject(i);
	            description =  jsonObject.getString(REAL_BUG_JSON_KEY_DESCRIPTION);
	            position =  jsonObject.getString(REAL_BUG_JSON_KEY_POSITION);
	            id =  jsonObject.getInt(REAL_BUG_JSON_KEY_ID);
        	} catch(JSONException e) {
        		e.printStackTrace();
        		return new ArrayList<RealBugUtils.RealBugData>();
        	}
            String image = String.format(URL+"/RealBug/%d/img", id);
            returnArray.add(new RealBugData(position, description, image));
        }
        return returnArray;
    }

    public static RealBugData realBugById(int id) {
        DefaultHttpClient httpclient = new DefaultHttpClient(); 
        HttpGet httpget = new HttpGet(URL+String.format("/RealBug/%d", id));

        httpget.setHeader("Accept", "application/json");
        //httpget.setHeader("Content-Type", "application/json");
        HttpResponse response;

        try {
            response = httpclient.execute(httpget);
        }
        catch (Exception e) {
            //Filter e for 400 (client error) 500 (user error) 300 (forward... change to code to automatically follow :))
            e.printStackTrace();
            return new RealBugData("", "", "");
            //Do some user feedback to singalize the fail
        }
        HttpEntity entity = response.getEntity();
        if(entity == null) 
            return new RealBugData("", "", ""); //Return empty list
        try {
	        InputStream instream = entity.getContent();
	
	        JSONObject jsonObj = new JSONObject(convertStreamToString(instream));
	
	        httpget.abort();
	
	        return new RealBugData(jsonObj.getString(REAL_BUG_JSON_KEY_POSITION),
	                               jsonObj.getString(REAL_BUG_JSON_KEY_DESCRIPTION),
	                               String.format("/RealBug/%d/img",jsonObj.getInt(REAL_BUG_JSON_KEY_ID)));
        }
        catch(IOException e) {
        	e.printStackTrace();
        	return new RealBugData("", "", "");
        }
        catch(JSONException e) {
        	e.printStackTrace();
        	return new RealBugData("", "", "");
        }
    }

    public static void updateRealBugImg(int id, byte[] image) {
        DefaultHttpClient httpclient = new DefaultHttpClient(); 
        HttpPut httpput = new HttpPut(URL+String.format("/RealBug/%d/img",id));

        httpput.setHeader("Content-Type", "image/jpeg");
        httpput.setEntity(new ByteArrayEntity(image));
        

        try {
            httpclient.execute(httpput);
        }
        catch (Exception e) {
            //Filter e for 400 (client error) 500 (user error) 300 (forward... change to code to automatically follow :))
            e.printStackTrace();
            return;
            //Do some user feedback to singalize the fail
        }
    }

    public static byte[] realBugImage(int id) {
        DefaultHttpClient httpclient = new DefaultHttpClient(); 
        HttpGet httpget = new HttpGet(URL+String.format("/RealBug/%d/img", id));

        httpget.setHeader("Accept", "image/jpeg");
        HttpResponse response;

        try {
            response = httpclient.execute(httpget);
        }
        catch (Exception e) {
            //Filter e for 400 (client error) 500 (user error) 300 (forward... change to code to automatically follow :))
            e.printStackTrace();
            return new byte[0];
            //Do some user feedback to singalize the fail
        }
        byte[] imageBuffer;
        try {
        	imageBuffer = EntityUtils.toByteArray(response.getEntity());
        } catch(IOException e) {
        	e.printStackTrace();
        	return new byte[0];
        }
        return imageBuffer;

    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}

