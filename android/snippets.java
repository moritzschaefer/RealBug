
public class RealBugNetwork {
            
    public static final String REAL_BUG_JSON_KEY_POSITION = "position";
    public static final String REAL_BUG_JSON_KEY_DESCRIPTION = "description";
    public static final String REAL_BUG_JSON_KEY_IMAGE = "image";
    public static final String REAL_BUG_JSON_KEY_IMAGE = "id";
    public static final String URL = "obscure-fjord-9100.herokuapp.com";

    //TODO: Think about using this class as parameter for the create/post call
    /**
     * Container class for one entry in the RealBug-list
     */
    public class RealBugData {
        public double longitude;
        public double latitude;
        public String description;
        public String imageLink;
        public RealBugData(double longitude, double latitude, String description, String imageLink) {
            init(longitude, latitude, description, imageLink);
        }
        public RealBugData(String position, String description, String imageLink) {
            Double.parseDouble();
            String[] doubleStrings = position.split(",");
            if(doubleStrings.length != 2) {
                Log.e("error in position!. has to be of format float,float");
                return; //Error
            }
            init(Double.parseDouble(doubleStrings[0]),Double.parseDouble(doubleStrings[1]), description, imageLink);
        }
        private init(double longitude, double latitude, String description, String imageLink) {
            this.longitude = longitude; this.latitude = latitude; this.description = description; this.imageLink = this.imageLink;
        }
    }


    /**
     * Gets all the necessary parameters to generate a create-POST-JSON-Object for a RealBug
     *
     * @param longitude     The GPS longitude 
     * @param latitude      The GPS latitude 
     * @param description   The description from the user for the RealBug
     * @param image         The image the user took to document the RealBug as JPG
     * @return              Returns a json-object as String 
     */
    private String jsonStringCreate(double longitude, double latitude, String description, ByteBuffer image) {
        JSONObject object = new JSONObject();
        try {
            object.put(REAL_BUG_JSON_KEY_POSITION, String.format("%f,%f", longitude, latitude));
            object.put(REAL_BUG_JSON_KEY_DESCRIPTION, description);
            object.put(REAL_BUG_JSON_KEY_IMAGE, );
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
    public void realBugPost(double longitude, double latitude, String description, ByteBuffer image) {

        String jsonPostObject = jsonStringCreate(longitude, latitude, description, image);
        DefaultHttpClient httpclient = new DefaultHttpClient(); 
        HttpPost httppost = new HttpPost(URL+"/RealBug");
        StringEntity se = new StringEntity(jsonPostObject);
        httppost.setEntity(se);

        //httppost.setHeader("Accept", "application/json"); //not necessary!
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

    /**
     * Get all RealBugs in radius around given position and return as a list 
     * 
     * @param longitude Current position longitude from GPS
     * @param latitude Current position latitude from GPS
     * @param radius Search radius from current position for RealBugs in meters
     *
     * @return List of all RealBugs with max-distance radius from current position
     */

    public List<RealBugData> realBugAreaList(double longitude, double latitude, double radius) {
        DefaultHttpClient httpclient = new DefaultHttpClient(); 
        HttpGet httpget = new HttpGet(URL+String.format("/RealBug/%f,%f,%f",longitude, latitude, radius));

        httpget.setHeader("Accept", "application/json");
        //httpget.setHeader("Content-type", "application/json");
        HttpResponse response;

        ResponseHandler responseHandler = new BasicResponseHandler();
        try {
            response = httpclient.execute(httppost, responseHandler);
        }
        catch (HttpResponseException e) {
            //Filter e for 400 (client error) 500 (user error) 300 (forward... change to code to automatically follow :))
            e.printStackTrace();
            return;
            //Do some user feedback to singalize the fail
        }
        HttpEntity entity = response.getEntity();
        if(entity == null) 
            return new ArrayList<RealBugData>(); //Return empty list
        InputStream instream = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
        StringBuilder sb = new StringBuilder;
        
        String line = null;
        while((line = reader.readLine()) != null)
            sb.append(line+"\n");
        String result = sb.toString();

        instream.close();
        httpget.abort();

        
        JSONArray jsonResults = new JSONArray(result);

        List<RealBugData> returnArray = new ArrayList<RealBugData>(jsonResults.length);
        for(int i=0; i<jsonResults.length(); i++) {
            JSONObject jsonObject = jsonResults.getJSONObject(i);
            String description =  jsonObject.getString(REAL_BUG_JSON_KEY_DESCRIPTION);
            String position =  jsonObject.getString(REAL_BUG_JSON_KEY_POSITION);
            int id =  jsonObject.getInt(REAL_BUG_JSON_KEY_ID);
            String image = String.format("/RealBug/%d/img", id);
            returnArray.set(i, new RealBugData(position, description, image);
        }
        return returnArray;
    }

}
