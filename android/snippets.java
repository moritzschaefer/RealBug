//only some snippets for later use


/**
 * Gets all the necessary parameters to generate a create-POST-JSON-Object for a RealBug
 *
 * @param longitude     The GPS longitude 
 * @param latitude      The GPS latitude 
 * @param description   The description from the user for the RealBug
 * @param image         The image the user took to document the RealBug as JPG
 * @return              Returns a json-object as String 
 */

        
public static final String CREATE_BUG_JSON_KEY_POSITION = "position";
public static final String CREATE_BUG_JSON_KEY_DESCRIPTION = "description";
public static final String CREATE_BUG_JSON_KEY_IMAGE = "image";
public static final String URL = "obscure-fjord-9100.herokuapp.com";

public String jsonString(double longitude, double latitude, String description, ByteBuffer image) {
    JSONObject object = new JSONObject();
    try {
        object.put(CREATE_BUG_JSON_KEY_POSITION, String.format("%f,%f", longitude, latitude));
        object.put(CREATE_BUG_JSON_KEY_DESCRIPTION, description);
        object.put(CREATE_BUG_JSON_KEY_IMAGE, );
    } catch (JSONException e) {
        e.printStackTrace();
    }
    return object.toString();
}

/**
 * Send a post request to the server with the given content, the post content type is JSON
 * 
 *
 */
public void realBugPost(String jsonPostObject) {
    DefaultHttpClient httpclient = new DefaultHttpClient(); 
    HttpPost httppost = new HttpPost(URL+"/RealBug");
    StringEntity se = new StringEntity(jsonPostObject);
    httppost.setEntity(se);

    httppost.setHeader("Accept", "application/json");
    httppost.setHeader("Content-type", "application/json");

    ResponseHandler responseHandler = new BasicResponseHandler();
    try {
        httpclient.execute(httppost, responseHandler);
    }
    catch (HttpResponseException e) {
        //Filter e for 400 (client error) 500 (user error) 300 (forward... change to code to automatically follow :))
        e.printStackTrace();
        return;
        //Do some user feedback to singalize the fail
    }
}


