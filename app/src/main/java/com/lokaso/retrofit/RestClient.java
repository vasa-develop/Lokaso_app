package com.lokaso.retrofit;

import android.content.Context;

import com.google.android.gms.location.places.personalized.AliasedPlacesResult;
import com.google.gson.Gson;
import com.lokaso.model.Place;
import com.lokaso.model.PlaceDetail;
import com.lokaso.model.PlaceListLatLng;
import com.lokaso.model.PlaceResult;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retromodel.RetroAction;
import com.lokaso.retromodel.RetroAddInterest;
import com.lokaso.retromodel.RetroAnswer;
import com.lokaso.retromodel.RetroChat;
import com.lokaso.retromodel.RetroChatRoom;
import com.lokaso.retromodel.RetroCreateChatroom;
import com.lokaso.retromodel.RetroCredits;
import com.lokaso.retromodel.RetroDiscoveryComment;
import com.lokaso.retromodel.RetroInterest;
import com.lokaso.retromodel.RetroLogin;
import com.lokaso.retromodel.RetroNotification;
import com.lokaso.retromodel.RetroPlace;
import com.lokaso.retromodel.RetroPlaceDetail;
import com.lokaso.retromodel.RetroProfession;
import com.lokaso.retromodel.RetroProfile;
import com.lokaso.retromodel.RetroQueries;
import com.lokaso.retromodel.RetroQueryResponse;
import com.lokaso.retromodel.RetroQuestion;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.retromodel.RetroResponseComment;
import com.lokaso.retromodel.RetroSetting;
import com.lokaso.retromodel.RetroSuggestion;
import com.lokaso.retromodel.RetroSuggestionPost;
import com.lokaso.retromodel.RetroValidity;
import com.lokaso.util.AppUtil;
import com.lokaso.util.Constant;
import com.lokaso.util.DeviceUtil;
import com.lokaso.util.MyLog;
import com.squareup.okhttp.OkHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

public class RestClient {

    private static final RestAdapter.LogLevel LOG_LEVEL = Constant.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;

    private static final String TAG = RestClient.class.getSimpleName();

    /**
     * @return REST Adapter for url
     */
    private static RestAdapter getRestAdapter(String url) {
        return getRestAdapter(url, null, null);
    }

    /**
     * @return REST Adapter for url
     */
    private static RestAdapter getRestAdapter(String url, Converter converter) {
        return getRestAdapter(url, converter, null);
    }

    /**
     * @return REST Adapter for url
     */
    private static RestAdapter getRestAdapter(String url, RequestInterceptor requestInterceptor) {
        return getRestAdapter(url, null, requestInterceptor);
    }
    /**
     * @return REST Adapter for Email
     */
    private static RestAdapter getRestAdapter(String url, Converter converter, RequestInterceptor requestInterceptor) {

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setLogLevel(LOG_LEVEL)
                .setEndpoint(url)
                .setClient(new OkClient(new OkHttpClient()));

        if(converter!=null) {
            builder.setConverter(converter);
        }

        if(requestInterceptor!=null) {
            builder.setRequestInterceptor(requestInterceptor);
        }
        return builder.build();
    }


    /**
     * @return REST Adapter for Email
     */
    private static RestAdapter getPlacesAdapter(int type) {

        Converter converter = null;
        if(type==PLACE_AUTOCOMPLETE) {
            converter = new PlaceListConvertor();
        }
        else if(type==PLACE_DETAIL) {
            converter = new PlaceDetailConvertor();
        }
        else if(type==PLACE_LIST_LATLNG) {
            converter = new PlaceListLatLngConvertor();
        }

        return getRestAdapter(Constant.PLACE_URL, converter);
    }

    public static String getMapImage(double latitude, double longitude) {

        String mapIcon = "http://lokaso.in/app_panel/upload/misc/ic_map.ico";

        //String url = "http://maps.google.com/maps/api/staticmap?zoom=16&size=600x300&markers="+latitude+","+longitude+"&sensor=false";
        String url = "http://maps.google.com/maps/api/staticmap?zoom=16&size=600x300&markers=icon:"+mapIcon+"|"+latitude+","+longitude+"&sensor=false";
        return url;
    }


    private static class PlaceDetailConvertor implements Converter {

        @Override
        public Object fromBody(TypedInput typedInput, Type type) throws ConversionException {
            String text = null;
            try {
                text = fromStream(typedInput.in());
            } catch (IOException ignored) {/*NOP*/ }

            Gson gson = new Gson();
            PlaceDetail placeDetail = gson.fromJson(text, PlaceDetail.class);

            String place_id = placeDetail.getPlace_id();
            String name = placeDetail.getName();
            String description = placeDetail.getDescription();
            String latitude = placeDetail.getLatitude();
            String longitude = placeDetail.getLongitude();

            Place place = new Place(name, description);
            place.setLatLng(latitude, longitude);
            place.setPlace_id(place_id);

            RetroPlaceDetail retroPlaceDetail = new RetroPlaceDetail(place, true, "");
            return retroPlaceDetail;
        }

        @Override
        public TypedOutput toBody(Object o) {
            return null;
        }

        public static String fromStream(InputStream in) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder out = new StringBuilder();
            String newLine = System.getProperty("line.separator");
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(newLine);
            }
            return out.toString();
        }
    }

    private static class PlaceListLatLngConvertor implements Converter {

        @Override
        public Object fromBody(TypedInput typedInput, Type type) throws ConversionException {
            String text = null;
            try {
                text = fromStream(typedInput.in());
            } catch (IOException ignored) {/*NOP*/ }

            Gson gson = new Gson();
            PlaceListLatLng placeListLatLng = gson.fromJson(text, PlaceListLatLng.class);

            MyLog.e(TAG, "response:"+text);

            List<PlaceListLatLng.Detail> list = placeListLatLng.getPlaces();

            List<Place> placeList = new ArrayList<>();

            for (PlaceListLatLng.Detail p : list) {
                String name = p.getName();
                String location = p.getDescription();
                String place_id = p.getPlace_id();
                String latitude = p.getLatitude();
                String longitude = p.getLongitude();

                Place place = new Place(name, location);
                place.setLatLng(latitude, longitude);
                place.setPlace_id(place_id);
                placeList.add(place);
            }

            RetroPlace retroPlace = new RetroPlace(placeList, true, "");
            return retroPlace;
        }

        @Override
        public TypedOutput toBody(Object o) {
            return null;
        }

        public static String fromStream(InputStream in) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder out = new StringBuilder();
            String newLine = System.getProperty("line.separator");
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(newLine);
            }
            return out.toString();
        }
    }

    private static class PlaceListConvertor implements Converter {

        @Override
        public Object fromBody(TypedInput typedInput, Type type) throws ConversionException {
            String text = null;
            try {
                text = fromStream(typedInput.in());
            } catch (IOException ignored) {/*NOP*/ }

            Gson gson = new Gson();
            PlaceResult placeResult = gson.fromJson(text, PlaceResult.class);

            List<PlaceResult.Place> list = placeResult.getPlaces();

            List<Place> placeList = new ArrayList<>();

            for (PlaceResult.Place p : list) {
                String name = p.getName();
                String location = p.getDescription();
                String place_id = p.getPlace_id();

                Place place = new Place(name, location);
                place.setPlace_id(place_id);
                placeList.add(place);
            }

            RetroPlace retroPlace = new RetroPlace(placeList, true, "");
            return retroPlace;
        }

        @Override
        public TypedOutput toBody(Object o) {
            return null;
        }

        public static String fromStream(InputStream in) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder out = new StringBuilder();
            String newLine = System.getProperty("line.separator");
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(newLine);
            }
            return out.toString();
        }
    }

    /**
     * @return API methods for Email
     */
    public static PlacesApiDetail getPlacesApiDetail() {
        int type=PLACE_DETAIL;
        return getPlacesAdapter(type).create(PlacesApiDetail.class);
    }

    /**
     * All Places API's
     */
    public interface PlacesApiDetail {

        @GET("/maps/api/place/details/json?key="+ PLACES_KEY)
        void getPlacesDetail(
                @Query("placeid") String placeid,
                Callback<RetroPlaceDetail> cb
        );
    }


    /**
     * @return API methods for Email
     */
    public static PlacesApiLatLng getPlacesApiLatLng() {
        int type= PLACE_LIST_LATLNG;
        return getPlacesAdapter(type).create(PlacesApiLatLng.class);
    }

    /**
     * All Places API's
     */
    public interface PlacesApiLatLng {

        @GET("/maps/api/geocode/json?sensor=true")
        void getPlaces(
                @Query("latlng") String location,
                Callback<RetroPlace> cb
        );
    }

    /**
     * @return API methods for Email
     */
    public static PlacesApiAutocomplete getPlacesApiAutocomplete() {
        int type= PLACE_AUTOCOMPLETE;
        return getPlacesAdapter(type).create(PlacesApiAutocomplete.class);
    }

    /**
     * All Places API's
     */
    public interface PlacesApiAutocomplete {

        @GET("/maps/api/place/autocomplete/json?radius="+RADIUS+"&key="+ PLACES_KEY)
        void getPlaces(
                //@Query("types") String types,
                @Query("input") String input,
                @Query("location") String location,
                Callback<RetroPlace> cb
        );
    }


    public static final String PLACES_KEY = "AIzaSyDFF1p92K5hOP3EcnFQo8XyFJN7u3gVFrk";
    public static final String RADIUS = "5000";


    public static final int PLACE_AUTOCOMPLETE = 1;
    public static final int PLACE_NEARBY = 2;
    public static final int PLACE_DETAIL = 3;
    public static final int PLACE_LIST_LATLNG = 4;
    /**
     * All Places API's
     */
    public interface PlacesApiNearby {

        @GET("/maps/api/place/nearbysearch/json?radius="+RADIUS+"&key="+ PLACES_KEY)
        void getPlacesNearby(
                @Query("keyword") String keyword,
                @Query("location") String location,
                @Query("type") String type,
                Callback<AliasedPlacesResult> cb
        );

    }

    /**
     * @return REST Adapter for Email
     */
    private static RestAdapter getRestEmailAdapter() {
        return new RestAdapter.Builder()
                .setLogLevel(LOG_LEVEL)
                .setEndpoint(Constant.API_EMAIL_DOMAIN)
                .setClient(new OkClient(new OkHttpClient()))
                .build();
    }

    /**
     * @return API methods for Email
     */
    public static LokasoEmailApi getLokasoEmailApi() {
        return getRestEmailAdapter().create(LokasoEmailApi.class);
    }

    /**
     * All Email API's
     */
    public interface LokasoEmailApi {
        @FormUrlEncoded
        @POST(Constant.METHOD_EMAIL)
        void sendEmail(
                @Field("from_name") String from_name,
                @Field("from_email") String from_email,
                @Field("to_name") String to_name,
                @Field("to_email") String to_email,
                @Field("subject") String subject,
                @Field("data") String data,
                Callback<Response> cb
        );
    }

    /***
     * @return API methods for Lokaso
     */
    public static LokasoApi getLokasoApi() {
        return getLokasoApi(null);
    }
    private static LokasoApi getLokasoApi(final Context context) {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                request.addHeader(Constant.TAG_TOKEN, Constant.APP_TOKEN);

                if(context!=null) {
                    int user_id = MyPreferencesManager.getId(context);
                    String device_id = DeviceUtil.getDeviceId(context);
                    String device_os = DeviceUtil.getDeviceOs(context);
                    String device_os_version = DeviceUtil.getDeviceAndroidVersion(context);
                    int app_version_code = AppUtil.getVersionCode(context);
                    String app_version_name = AppUtil.getVersionName(context);

                    request.addHeader(Constant.USER_ID, ""+ user_id);
                    request.addHeader(Constant.DEVICE_ID, device_id);
                    request.addHeader(Constant.DEVICE_OS, device_os);
                    request.addHeader(Constant.DEVICE_OS_VERSION, device_os_version);
                    request.addHeader(Constant.APP_VERSION_CODE, "" + app_version_code);
                    request.addHeader(Constant.APP_VERSION_NAME, app_version_name);
                }
            }
        };

        return getRestAdapter(Constant.API_REQUEST, requestInterceptor).create(LokasoApi.class);
    }

    /**
     * All Lokaso API's
     */
    public interface LokasoApi {

        @FormUrlEncoded
        @POST(Constant.METHOD_AUTH_SIGNUP_LOCAL)
        void signup(
                @Field("user_id") int user_id,
                @Field("name") String name,
                @Field("fname") String fname,
                @Field("lname") String lname,
                @Field("email") String email,
                @Field("password") String password,
                @Field("image") String image,
                @Field("num_asks") int num_asks,
                @Field("num_responses") int num_responses,
                @Field("about_me") String about_me,
                @Field("profession_id") int profession_id,
                @Field("location") String location,
                @Field("current_lat") double current_lat,
                @Field("current_lng") double current_lng,
                @Field("provider") String provider,
                @Field("facebook_id") String facebook_id,
                @Field("notification_flag") int notification_flag,
                @Field("credits") int credits,
                @Field("refer_code") String refer_code,
                @Field("device_id") String device_id,
                @Field("device_os") String device_os,
                @Field("device_name") String device_name,
                @Field("device_os_version") String device_os_version,
                @Field("app_version_code") int app_version_code,
                @Field("app_version_name") String app_version_name,
                Callback<RetroLogin> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_AUTH_SIGNUP_FB_LOCAL)
        void signupFb(
                @Field("name") String name,
                @Field("email") String email,
                @Field("password") String password,
                @Field("image") String image,
                @Field("num_asks") int num_asks,
                @Field("num_responses") int num_responses,
                @Field("about_me") String about_me,
                @Field("profession_id") int profession_id,
                @Field("location") String location,
                @Field("current_lat") double current_lat,
                @Field("current_lng") double current_lng,
                @Field("provider") String provider,
                @Field("facebook_id") String facebook_id,
                @Field("notification_flag") int notification_flag,
                @Field("credits") int credits,
                @Field("device_id") String device_id,
                @Field("device_os") String device_os,
                @Field("device_name") String device_name,
                @Field("device_os_version") String device_os_version,
                @Field("app_version_code") int app_version_code,
                @Field("app_version_name") String app_version_name,
                Callback<RetroLogin> cb
        );


        @FormUrlEncoded
        @POST("/users/signupFbCheck")
        void signupFbCheck(
                @Field("name") String name,
                @Field("email") String email,
                @Field("password") String password,
                @Field("image") String image,
                @Field("num_asks") int num_asks,
                @Field("num_responses") int num_responses,
                @Field("about_me") String about_me,
                @Field("profession_id") int profession_id,
                @Field("location") String location,
                @Field("current_lat") double current_lat,
                @Field("current_lng") double current_lng,
                @Field("provider") String provider,
                @Field("facebook_id") String facebook_id,
                @Field("notification_flag") int notification_flag,
                @Field("credits") int credits,
                @Field("device_id") String device_id,
                @Field("device_os") String device_os,
                @Field("device_name") String device_name,
                @Field("device_os_version") String device_os_version,
                @Field("app_version_code") int app_version_code,
                @Field("app_version_name") String app_version_name,
                Callback<RetroLogin> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_AUTH_SIGNIN_POST)
        void login(
                @Field("email") String username,
                @Field("password") String password,
                @Field("device_id") String device_id,
                @Field("device_os") String device_os,
                @Field("device_name") String device_name,
                @Field("device_os_version") String device_os_version,
                @Field("app_version_code") int app_version_code,
                @Field("app_version_name") String app_version_name,
                Callback<RetroLogin> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_CHECK_USER)
        void checkUser(
                @Field("email") String username,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_PROFESSION_POST)
        void professions(
                @Field("updated_date") String updated_date,
                Callback<RetroProfession> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_INTERESTS_POST)
        void interests(
                @Field("updated_date") String updated_date,
                Callback<RetroInterest> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_SETTING)
        void getSetting(
                @Field("user_id") int user_id,
                @Field("device_id") String device_id,
                @Field("device_os") String device_os,
                @Field("device_name") String device_name,
                @Field("device_os_version") String device_os_version,
                @Field("app_version_code") int app_version_code,
                @Field("app_version_name") String app_version_name,
                Callback<RetroSetting> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_USERS_INTERESTS_SET)
        void addInterests(
                @Field("user_id") int user_id,
                @Field("interest_ids") String interest_id,
                Callback<RetroAddInterest> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_NOTIFICATION_FLAG)
        void setNotificationFlag(
                @Field("user_id") int user_id,
                @Field("notification_flag") int notification_flag,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_ASK_DETAILS)
        void postQuery(
                @Field("user_id") int user_id,
                @Field("description") String description,
                @Field("valid_until") String valid_until,
                @Field("lat") double lat,
                @Field("lng") double lng,
                @Field("location") String location,
                @Field("interest_id") int interest_id,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_QUESTIONS)
        void getQuestions(
                @Field("interest_id") int interest_id,
                Callback<RetroQuestion> cb
        );

        @Multipart
        @POST(Constant.METHOD_POST_SUGGESTION)
        void createSuggestion(
                @QueryMap Map<String, Object> params,
                @Part("image") TypedFile typedFile,
                @Part("dummy") String dummy,
                Callback<RetroSuggestionPost> cb
        );


        @Multipart
        @POST(Constant.METHOD_POST_SUGGESTION_EDIT)
        void editSuggestion(
                @QueryMap Map<String, Object> params,
                @Part("image") TypedFile typedFile,
                @Part("dummy") String dummy,
                Callback<RetroResponse> cb
        );


/*
        @FormUrlEncoded
        @POST(Constant.METHOD_POST_SUGGESTION_EDIT)
        void editSuggestion(
                @QueryMap Map<String, Object> params,
                @Field("dummy") String dummy,
                Callback<RetroResponse> cb
        );
*/

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_SUGGESTION)
        void getSuggestion(
                @Field("lat") double lat,
                @Field("lng") double lng,
                @Field("range") int range,
                @Field("user_id") int user_id,
                @Field("limit") int limit,
                @Field("position") int position,
                @Field("filter_by") int filter_by,
                @Field("interest_ids") String interest_ids,
                Callback<RetroSuggestion> cb
        );
        @FormUrlEncoded
        @POST(Constant.METHOD_GET_SUGGESTION)
        void getSuggestion(
                @Field("lat") double lat,
                @Field("lng") double lng,
                @Field("range") int range,
                @Field("user_id") int user_id,
                @Field("limit") int limit,
                @Field("position") int position,
                @Field("filter_by") int filter_by,
                @Field("interest_ids") String interest_ids,
                @Field("search_type") int search_type,
                Callback<RetroSuggestion> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_SEARCH_SUGGESTION)
        void searchSuggestion(
                @Field("lat") double lat,
                @Field("lng") double lng,
                @Field("user_id") int user_id,
                @Field("search") String search,
                Callback<RetroSuggestion> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_ANSWER)
        void getAnswers(
                @Field("discovery_id") int discovery_id,
                Callback<RetroAnswer> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_DISCOVERY_FAV)
        void discoveryFav(
                @Field("action") int action,
                @Field("discovery_id") int discovery_id,
                @Field("user_id") int user_id,
                Callback<RetroResponse> cb
        );


        @FormUrlEncoded
        @POST(Constant.METHOD_POST_SUGGESTION_SHARE)
        void suggestionShare(
                @Field("suggestion_id") int suggestion_id,
                @Field("user_id") int user_id,
                Callback<RetroAction> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_DISCOVERY_COMMENT)
        void discoveryComment(
                @Field("comment") String comment,
                @Field("discovery_id") int discovery_id,
                @Field("user_id") int user_id,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_SUGGESTION_SPAM)
        void suggestionSpam(
                @Field("action") int action,
                @Field("suggestion_id") int suggestion_id,
                @Field("user_id") int user_id,
                Callback<RetroAction> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_USER_DISCOVERY)
        void userDiscovery(
                @Field("action") int action,
                @Field("discovery_id") int discovery_id,
                @Field("user_id") int user_id,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_FOLKS)
        void getFolks(
                @Field("lat") double lat,
                @Field("lng") double lng,
                @Field("range") int range,
                @Field("user_id") int user_id,
                @Field("limit") int limit,
                @Field("position") int position,
                @Field("filter_by") int filter_by,
                Callback<RetroProfile> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_SUGGESTION_LIKE_LIST)
        void getSuggestionLikeList(
                @Field("user_id") int user_id,
                @Field("suggestion_id") int suggestion_id,
                @Field("position") int position,
                Callback<RetroProfile> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_SEARCH_FOLKS)
        void searchFolks(
                @Field("lat") double lat,
                @Field("lng") double lng,
                @Field("user_id") int user_id,
                @Field("search") String search,
                Callback<RetroProfile> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_USER_FOLLOW)
        void follow(
                @Field("action") int action,
                @Field("leader") int leader,
                @Field("follower") int follower,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_QUERIES)
        void getQueries(
                @Field("lat") double lat,
                @Field("lng") double lng,
                @Field("range") int range,
                @Field("user_id") int user_id,
                @Field("limit") int limit,
                @Field("position") int position,
                @Field("filter_by") int filter_by,
                @Field("interest_ids") String interest_ids,
                Callback<RetroQueries> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_SEARCH_QUERY)
        void searchQuery(
                @Field("lat") double lat,
                @Field("lng") double lng,
                @Field("user_id") int user_id,
                @Field("search") String search,
                Callback<RetroQueries> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_PROFILE)
        void getProfile(
                @Field("user_id") int user_id,
                @Field("current_user_id") int current_user_id,
                Callback<RetroProfile> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_UPDATE_PROFILE)
        void updateProfile(
                @Field("user_id") int user_id,
                @Field("name") String name,
                @Field("image") String image,
                @Field("about_me") String about_me,
                @Field("profession_id") int profession_id,
                @Field("location") String location,
                @Field("current_lat") double current_lat,
                @Field("current_lng") double current_lng,
                @Field("notification_flag") int notification_flag,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_USER_DISCOVERY)
        void getUserDiscovery(
                @Field("user_id") int user_id,
                Callback<RetroSuggestion> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_USER_MYDISCOVERY)
        void getMyDiscovery(
                @Field("user_id") int user_id,
                @Field("session_user_id") int session_user_id,
                Callback<RetroSuggestion> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_USER_QUERIES)
        void getUserQueries(
                @Field("lat") double lat,
                @Field("lng") double lng,
                @Field("user_id") int user_id,
                Callback<RetroQueries> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_USER_QUERY_LOCATION)
        void getQueryLocation(
                @Field("lat") double lat,
                @Field("lng") double lng,
                @Field("user_id") int user_id,
                Callback<RetroQueries> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_USER_FOLLOWERS)
        void getUserFollowers(
                @Field("lat") double lat,
                @Field("lng") double lng,
                @Field("user_id") int user_id,
                Callback<RetroProfile> cb
        );

        @FormUrlEncoded
        @POST(Constant.GCM_API_REGISTER)
        void gcm_register(
                @Field("user_id") int user_id,
                @Field("device_id") String device_id,
                @Field("gcm_token") String gcm_token,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_CREATE_CHAT_ROOM)
        void createChatRoom(
                @Field("from_user_id") int from_user_id,
                @Field("to_user_id") int to_user_id,
                Callback<RetroCreateChatroom> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_USERS_CHAT_ROOM)
        void getUsersChatRoom(
                @Field("user_id") int user_id,
                Callback<RetroChatRoom> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_CHAT)
        void chat(
                @Field("from_user_id") int from_user_id,
                @Field("to_user_id") int to_user_id,
                @Field("chat_id") int chat_id,
                @Field("message") String message,
                @Field("created_date") String created_date,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_CHAT)
        void getChat(
                @Field("chat_id") int chat_id,
                @Field("user_id") int user_id,
                Callback<RetroChat> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_DISCOVERY_COMMENTS)
        void getDiscoveryComments(
                @Field("discovery_id") int discovery_id,
                Callback<RetroDiscoveryComment> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_QUERY_RESPONSE)
        void addQueryResponse(
                @Field("response") String response,
                @Field("query_id") int query_id,
                @Field("query_user_id") int query_user_id,
                @Field("user_id") int user_id,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_QUERY_RESPONSE)
        void getQueryResponse(
                @Field("query_id") int query_id,
                @Field("user_id") int user_id,
                Callback<RetroQueryResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_USERS_NOTIFICATIONS)
        void getNotifications(
                @Field("user_id") int user_id,
                Callback<RetroNotification> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_FORGOT_PASSWORD)
        void forgotPassword(
                @Field("email") String email,
                @Field("password") String password,
                Callback<RetroProfile> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_CHANGE_PASSWORD)
        void changePassword(
                @Field("email") String email,
                @Field("current_password") String current_password,
                @Field("new_password") String new_password,
                Callback<RetroProfile> cb
        );

        /*@FormUrlEncoded
        @POST(Constant.METHOD_POST_RESPONSE_FAV)
        void responseFav(
                @Field("action") int action,
                @Field("response_id") int response_id,
                @Field("user_id") int user_id,
                Callback<RetroResponse> cb
        );*/

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_RESPONSE_COMMENT)
        void addResponseComment(
                @Field("comment") String comment,
                @Field("response_id") int response_id,
                @Field("user_id") int user_id,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_RESPONSE_COMMENT)
        void getResponseComment(
                @Field("user_id") int user_id,
                @Field("response_id") int response_id,
                Callback<RetroResponseComment> cb
        );

        @Deprecated
        @FormUrlEncoded
        @POST(Constant.METHOD_POST_FOLLOW_QUERY)
        void followQuery(
                @Field("action") int action,
                @Field("query_id") int query_id,
                @Field("user_id") int user_id,
                Callback<RetroResponse> cb
        );


        @FormUrlEncoded
        @POST(Constant.METHOD_POST_FOLLOW_QUERY)
        void followQuery(
                @Field("query_id") int query_id,
                @Field("user_id") int user_id,
                Callback<RetroAction> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_USERS_CREDITS)
        void getUserCredits(
                @Field("user_id") int user_id,
                Callback<RetroCredits> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_GET_CHAT_UNREAD_COUNT)
        void getChatUnreadCount(
                @Field("user_id") int user_id,
                @Field("to_user_id") int to_user_id,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_EXTEND_QUERY_VALIDITY)
        void extendQueryValidity(
                @Field("user_id") int user_id,
                @Field("query_id") int query_id,
                @Field("current_validity") String current_validity,
                @Field("extend_time") String valid_until,
                Callback<RetroValidity> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_RESPONSE_VOTES)
        void responseVotes(
                @Field("user_id") int user_id,
                @Field("response_id") int response_id,
                @Field("action") int action,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_QUERY_UPDATE_RESPONSE)
        void updateResponse(
                @Field("response_id") int response_id,
                @Field("user_id") int user_id,
                @Field("response") String response,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_RESPONSE_SPAM)
        void responseSpam(
                @Field("action") int action,
                @Field("response_id") int response_id,
                @Field("user_id") int user_id,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_QUERY_SPAM)
        void querySpam(
                @Field("action") int action,
                @Field("query_id") int query_id,
                @Field("user_id") int user_id,
                Callback<RetroResponse> cb
        );

        @FormUrlEncoded
        @POST(Constant.METHOD_POST_USER_SPAM)
        void userSpam(
                @Field("action") int action,
                @Field("spam_user_id") int spam_user_id,
                @Field("user_id") int user_id,
                Callback<RetroAction> cb
        );



        @FormUrlEncoded
        @POST(Constant.METHOD_AD_VIEW)
        void setAdPlay(
                @Field("user_id") int user_id,
                @Field("ad_id") int ad_id,
                @Field("type") int type,
                @Field("status") int status,
                @Field("data") String data,
                Callback<RetroResponse> cb
        );


    }
}