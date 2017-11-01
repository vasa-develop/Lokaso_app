package com.lokaso.dao.classes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.lokaso.dao.Dao;
import com.lokaso.dao.DaoSession;
import com.lokaso.model.Interest;
import com.lokaso.model.Place;
import com.lokaso.util.MyLog;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * DAO for table "+TABLENAME+".
*/
public class LocationDao extends AbstractDao<Place, String> {

    private static final String TAG = LocationDao.class.getSimpleName();

    public static final String TABLENAME = "LocationTable";

    public static final String PLACE_ID         = "place_id";
    public static final String NAME 		    = "name";
    public static final String lOCATION         = "description";
    public static final String LATITUDE         = "latitude";
    public static final String LONGITUDE        = "longitude";
    public static final String CREATED_DATE     = "created_date";
    public static final String UPDATED_DATE     = "updated_date";
    public static final String STATUS           = "status";

     /*
        this.name = name;
        this.genre = genre;
        this.image = image;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.rating = rating;
        this.status = status;*/

    /**
     * Properties of entity "+TABLENAME+".<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
    	static int c = -1;
        public final static Property Id 	    = new Property(++c, String.class, 	Dao.ID, 	    true,  Dao.COLUMN_ID);
        public final static Property PlaceId 	= new Property(++c, String.class, 	PLACE_ID, 	    false, PLACE_ID);
        public final static Property Name 	    = new Property(++c, String.class, 	NAME, 	        false, NAME);
        public final static Property Location   = new Property(++c, String.class, 	lOCATION, 	    false, lOCATION);
        public final static Property Latitude 	= new Property(++c, String.class, 	LATITUDE, 	    false, LATITUDE);
        public final static Property Longitude 	= new Property(++c, String.class, 	LONGITUDE, 	    false, LONGITUDE);
        public final static Property CreatedDate= new Property(++c, String.class, 	CREATED_DATE, 	false, CREATED_DATE);
        public final static Property UpdatedDate= new Property(++c, String.class, 	UPDATED_DATE, 	false, UPDATED_DATE);
        public final static Property Status     = new Property(++c, Long.class, 	STATUS, 	    false, STATUS);
    };

    private DaoSession daoSession;

    public LocationDao(DaoConfig config) {
        super(config);
    }

    public LocationDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
    	
    	Log.e(TABLENAME, "CREATE TABLE");
        
        String constraint = ifNotExists? Dao.IF_NOT_EXIST: " ";
        db.execSQL(Dao.CREATE_TABLE + constraint + Dao.Q + TABLENAME + Dao.Q + Dao.START_TABLE +
                Dao.Q + Dao.COLUMN_ID 	    + Dao.Q + Dao.TYPE_TEXT_PRIMARY 	+ Dao.COMMA +
                Dao.Q + PLACE_ID            + Dao.Q + Dao.TYPE_TEXT     		+ Dao.COMMA +
                Dao.Q + NAME                + Dao.Q + Dao.TYPE_TEXT 			+ Dao.COMMA +
                Dao.Q + lOCATION            + Dao.Q + Dao.TYPE_TEXT 			+ Dao.COMMA +
                Dao.Q + LATITUDE            + Dao.Q + Dao.TYPE_TEXT 			+ Dao.COMMA +
                Dao.Q + LONGITUDE           + Dao.Q + Dao.TYPE_TEXT 			+ Dao.COMMA +
                Dao.Q + CREATED_DATE        + Dao.Q + Dao.TYPE_TEXT 			+ Dao.COMMA +
                Dao.Q + UPDATED_DATE        + Dao.Q + Dao.TYPE_TEXT 			+ Dao.COMMA +
                Dao.Q + STATUS              + Dao.Q + Dao.TYPE_INTEGER
                +Dao.END_TABLE);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Place entity) {
    	stmt.clearBindings();

        int c = 0;
        String id = entity.getId();
        MyLog.e(TAG, "IDPRIMARY00 : " + id);
        ++c;
        if (id != null) {
            stmt.bindString(c, id);
        }
        ++c; stmt.bindString(c, entity.getPlace_id());
        ++c; stmt.bindString(c, entity.getName());
        ++c; stmt.bindString(c, entity.getLocation());
        ++c; stmt.bindString(c, entity.getLatitude());
        ++c; stmt.bindString(c, entity.getLongitude());
        ++c; stmt.bindString(c, entity.getCreated_date());
        ++c; stmt.bindString(c, entity.getUpdated_date());
        ++c; stmt.bindLong(c, entity.getStatus());
     	
    }


    /** @inheritdoc */
    @Override
    public Place readEntity(Cursor cursor, int offset) {
    	
    	int c = 0;
        Place entity = new Place(
			 cursor.isNull(offset + 0) ? null : (cursor.getString(offset + 0)), // id
                cursor.getString(offset + ++c),
                cursor.getString(offset + ++c),
                cursor.getString(offset + ++c),

                cursor.getString(offset + ++c),
                cursor.getString(offset + ++c),

                cursor.getString(offset + ++c),
                cursor.getString(offset + ++c),
                cursor.getInt(offset + ++c)
        );
        return entity;
    }
    
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Place entity, int offset) {

        int c = 0;
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setPlace_id(cursor.getString(offset + ++c));
        entity.setName(cursor.getString(offset + ++c));
        entity.setLocation(cursor.getString(offset + ++c));
        entity.setLatitude(cursor.getString(offset + ++c));
        entity.setLongitude(cursor.getString(offset + ++c));
        entity.setCreated_date(cursor.getString(offset + ++c));
        entity.setUpdated_date(cursor.getString(offset + ++c));
        entity.setStatus(cursor.getLong(offset + ++c));

     }

    //  ===== BELOW THIS WILL NEVER CHANGE ======

    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Place entity, long rowId) {
//        entity.setId(rowId);
//        return rowId;

        entity.setId(entity.getId());
        return entity.getId();

    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Place entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }


    @Override
    protected void attachEntity(Place entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }


    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'"+TABLENAME+"'";
        db.execSQL(sql);
    }


    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
