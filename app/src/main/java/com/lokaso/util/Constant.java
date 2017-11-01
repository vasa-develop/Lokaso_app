package com.lokaso.util;

public class Constant {

    /**
     *  TODO : NOTE : Make DEBUG = false and DEV_MODE = false when production upload
     */
    // TODO : DEBUG MODE
    public static final boolean DEBUG = false;
    // TODO : DEV MODE
    public static final boolean DEV_MODE = true;
    // TODO : LOCAL MODE
    public static final boolean LOCAL_MODE = false;

    /**
     * Server Constants
     */
    private static final String BASE_URL_LOCAL101 = "http://192.168.101.42/lokaso_new";
    private static final String BASE_URL_LOCAL = "http://192.168.2.99/lokaso_web/";
    private static final String BASE_URL_DEV     = "http://targetprogress.in/lokaso/";
    private static final String BASE_URL_PROD    = "http://lokaso.in/app_panel/";

    public static final String BASE_URL = DEV_MODE ? (LOCAL_MODE ? BASE_URL_LOCAL : BASE_URL_DEV) : BASE_URL_PROD;

    public static final String APP_TOKEN_LOCAL            = "%fj38t27v476vj^&^";
    public static final String APP_TOKEN_DEV            = "%fj38t27v476vj^&^h";
    public static final String APP_TOKEN_PROD           = "%fj38t27v476vj^&^hb";
    public static final String APP_TOKEN                =  DEV_MODE ? (LOCAL_MODE ? APP_TOKEN_LOCAL : APP_TOKEN_DEV) : APP_TOKEN_PROD;

    public static final String API_REQUEST              = BASE_URL + "v1/";
    public static final String TAG_TOKEN                = "token";

    /**
     * Intent Extras
     */
    public static final String DATA                     = "data";
    public static final String PROFILE                  = "profile";
    public static final String PROFILE_ID               = "profile_id";
    public static final String TYPE                     = "type";
    public static final String CHAT_ID                  = "chat_id";
    public static final String MESSAGE                  = "message";
    public static final String FROM_USER_ID             = "from_user_id";
    public static final String SUGGESTION               = "suggestion";
    public static final String SOURCE                   = "source";
    public static final String INTENT_QUERIES           = "queries";
    public static final String INTENT_QUERYRESPONSE     = "queryResponse";
    public static final String CREATED_DATE             = "created_date";

    public static final String MODE = "mode";

    public static final int TYPE_SEARCH_NORMAL = 0;
    public static final int TYPE_SEARCH_ALL = 1;

    public static final int FILTER_BY_LATEST = 1;
    public static final int FILTER_BY_DISTANCE = 2;
    public static final int FILTER_BY_EXPIRING = 3;

    public static final String FILTER_SORT_BY           = "filter_sort_by";
    public static final String FILTER_INTEREST          = "filter_interest";

    /**
     * Parameters
     */
    public static final String PARAM_USER_ID            = "user_id";
    public static final String PARAM_LOCATION           = "location";
    public static final String PARAM_INTEREST_ID        = "interest_id";
    public static final String PARAM_SUGGESTION         = "suggestion";
    public static final String PARAM_SUGGESTION_ID      = "suggestion_id";
    public static final String PARAM_CAPTION            = "caption";
    public static final String PARAM_LAT                = "lat";
    public static final String PARAM_LNG                = "lng";
    public static final String PARAM_IMAGE              = "image";
    public static final String PARAM_USER_FRIENDS       = "user_friends";
    public static final String PARAM_EMAIL              = "email";
    public static final String PARAM_PUBLIC_PROFILE     = "public_profile";
    public static final String PARAM_USER_BIRTHDAY      = "user_birthday";
    public static final String PARAM_USER_LOCATION      = "user_location";
    public static final String PARAM_NAME               = "name";
    public static final String PARAM_FIELDS             = "fields";

    public static final String IS_UPDATE                = "is_update";
    public static final String USER                     = "user";
    public static final String USER_ID                  = "user_id";
    public static final String DEVICE_ID                = "device_id";
    public static final String DEVICE_OS                = "device_os";
    public static final String DEVICE_NAME              = "device_name";
    public static final String DEVICE_OS_VERSION        = "device_os_version";
    public static final String APP_VERSION_CODE         = "app_version_code";
    public static final String APP_VERSION_NAME         = "app_version_name";


    public static final String PLACE_URL                      = "https://maps.googleapis.com";

    public static final String MAP                      = "http://maps.google.com/maps?q=loc:";
    public static final String MAP_PACKAGE              = "com.google.android.apps.maps";
    public static final String API_EMAIL_DOMAIN         = BASE_URL+"include/";
    public static final String METHOD_EMAIL             = "/sendEmail.php";

    public static final String TERMS_URL             = BASE_URL+"include/term_app.php";

    /**
     * User API Endpoints
     */
    public static final String METHOD_AUTH_SIGNUP_LOCAL         = "/users/signup";
    public static final String METHOD_AUTH_SIGNUP_FB_LOCAL      = "/users/signupFb";
    public static final String METHOD_AUTH_SIGNIN_POST          = "/users/login";
    public static final String GCM_API_REGISTER                 = "/gcm_register";
    public static final String METHOD_CHECK_USER                = "/users/checkUser";
    public static final String METHOD_USERS_INTERESTS_SET       = "/users/interests";
    public static final String METHOD_GET_FOLKS                 = "/getFolks";
    public static final String METHOD_POST_SEARCH_FOLKS         = "/folks/search";
    public static final String METHOD_POST_NOTIFICATION_FLAG    = "/users/notification";
    public static final String METHOD_POST_USER_FOLLOW          = "/users/follow";
    public static final String METHOD_GET_PROFILE               = "/getProfile";
    public static final String METHOD_POST_UPDATE_PROFILE       = "/users/updateProfile";
    public static final String METHOD_GET_USER_QUERIES          = "/users/getQueries";
    public static final String METHOD_GET_USER_QUERY_LOCATION   = "users/getQueryLocation";
    public static final String METHOD_GET_USER_FOLLOWERS        = "/users/getFollowers";
    public static final String METHOD_GET_USERS_NOTIFICATIONS   = "/users/getNotifications";
    public static final String METHOD_GET_USERS_CREDITS         = "/users/getCredits";
    public static final String METHOD_POST_USER_SPAM            = "/user/spam";
    public static final String METHOD_POST_CHANGE_PASSWORD      = "/change_password";
    public static final String METHOD_POST_FORGOT_PASSWORD      = "/forgot_password";
    public static final String METHOD_POST_USER_MYDISCOVERY     = "/users/getMyDiscovery";

    public static final String METHOD_GET_PROFESSION_POST       = "/profession";
    public static final String METHOD_GET_INTERESTS_POST        = "/interests";
    public static final String METHOD_GET_SETTING               = "/getSetting";
    public static final String METHOD_GET_SUGGESTION_LIKE_LIST  = "/users/getSuggestionLike";

    /**
     * Suggestion API Endpoints
     */
    public static final String METHOD_POST_SUGGESTION           = "/suggestion";
    public static final String METHOD_POST_SUGGESTION_EDIT      = "/suggestion/edit";
    public static final String METHOD_GET_SUGGESTION            = "/getSuggestion";
    public static final String METHOD_POST_DISCOVERY_FAV        = "/discovery/favourite";
    public static final String METHOD_POST_DISCOVERY_COMMENT    = "/discovery/comment";
    public static final String METHOD_POST_SUGGESTION_SPAM      = "/suggestion/spam";
    public static final String METHOD_POST_SUGGESTION_SHARE     = "/suggestion/share";
    public static final String METHOD_GET_DISCOVERY_COMMENTS    = "/discovery/getComments";
    public static final String METHOD_POST_USER_DISCOVERY       = "/user/discovery";
    public static final String METHOD_GET_USER_DISCOVERY        = "/users/getDiscovery";
    public static final String METHOD_POST_SEARCH_SUGGESTION    = "/suggestion/search";

    /**
     * Chat API Endpoints
     */
    public static final String METHOD_POST_CHAT = "/chat";
    public static final String METHOD_GET_CHAT = "/getChat";
    public static final String METHOD_POST_CREATE_CHAT_ROOM = "/createChatroom";
    public static final String METHOD_GET_USERS_CHAT_ROOM = "/users/chatroom";
    public static final String METHOD_GET_CHAT_UNREAD_COUNT = "/users/getChatUnreadCount";

    /**
     * Query API Endpoints
     */
    public static final String METHOD_GET_QUERIES = "/getQueries";
    public static final String METHOD_POST_ASK_DETAILS = "/asks";
    public static final String METHOD_POST_QUESTIONS = "/questions";
    public static final String METHOD_GET_ANSWER = "/answer";
    public static final String METHOD_POST_FOLLOW_QUERY = "/query/follow";
    public static final String METHOD_POST_EXTEND_QUERY_VALIDITY = "/query/extendValidity";
    public static final String METHOD_POST_QUERY_SPAM = "/query/spam";
    public static final String METHOD_POST_SEARCH_QUERY = "/query/search";

    /**
     * Query Response API Endpoints
     */
    public static final String METHOD_POST_QUERY_RESPONSE = "/query/response";
    public static final String METHOD_GET_QUERY_RESPONSE = "/query/getQueryResponse";
    public static final String METHOD_POST_RESPONSE_COMMENT = "/query/response/comment";
    public static final String METHOD_GET_RESPONSE_COMMENT = "/query/response/getComments";
    public static final String METHOD_POST_RESPONSE_VOTES = "/response/votes";
    public static final String METHOD_POST_QUERY_UPDATE_RESPONSE = "/query/updateResponse";
    public static final String METHOD_POST_RESPONSE_SPAM = "/response/spam";

    public static final String METHOD_AD_VIEW       = "/adView";

    /**
     * Common Constants used in APP
     */
    public static final String APP_NAME = "Lokaso";
    public static final String APP_EMAIL = "lokaso@help.com";
    public static final String APP_EMAIL_FORGOT_PASSWORD_SUBJECT = "Lokaso Forgot Password";
    public static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT2 = "dd/MM/yyyy HH:mm";
    public static final String DATE_FORMAT3 = "yyyy-MM-dd hh:mm:ss";
    public static final String HOURS = "hours";
    public static final String HOUR = "hour";
    public static final String DAY = "day";
    public static final String MIN = "min";
    public static final String DCIM_CAMERA_PATH = "/DCIM/Camera";
    public static final String FACEBOOK = "facebook";
    public static final String FACEBOOK_URL = "https://graph.facebook.com/";
    public static final String FACEBOOK_IMAGE = "/picture?type=large";
    public static final String ERROR = "Some error occurred!";
    public static final String POSITION = "position";
    public static final long ONE_MINUTE_IN_MILLIS = 60000;
    public static final int NOTIFICATION_ID = 1;
    public static final int CAMERA_PIC_REQUEST = 1004;
    public static final int SELECT_FILE1 = 1005;
    public static final int LOCATION_SELECTION = 1006;
    public static final int SELECTION_FILTER = 1007;
    public static final int SELECTION_LOCATION = 1008;
    public static final int CROP_PIC = 1007;
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final int ASPECTX = 1;
    public static final int ASPECTY = 1;
    public static final int OUTPUTX = 200;
    public static final int OUTPUTY = 200;
    public static final int RANGE = 50;
    public static final int FAV = 1;
    public static final int UNFAV = 0;
    public static final int OTP_COUNT = 5;
    public static final int IMAGENAME_COUNT = 12;
    public static final int REQUEST_LIMIT = 10;
    public static final int SPLASH_SHOW_TIME = 1500;
    public static final int size_small      = 150;
    public static final int size_medium     = 300;
    public static final int size_big        = 700;
    public static final int SUGGESTIONS     = 1;
    public static final int QUERIES         = 2;
    public static final int FOLKS           = 3;
    public static final String TAB_TYPE     = "tab_type";

    public static final String ID = "id";
    public static final String QUESTION_ID = "question_id";
    public static final String NAME = "name";
    public static final String SCORE = "score";


    public static final String PLACE                      = "place";

    public static final String FINISH_ACTIVITY = "finish_activity";

    public static final String PLACE_ACTIVITY = "place_activity";
    public static final String AD = "ad";
    public static final String LENGTH = "length";
    public static final String YOUTUBE_ID = "youtube_id";
    public static final String FROM = "from";
}