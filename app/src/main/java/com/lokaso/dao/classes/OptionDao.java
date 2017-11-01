package com.lokaso.dao.classes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.lokaso.dao.Dao;
import com.lokaso.dao.DaoSession;
import com.lokaso.model.Option;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * DAO for table "+TABLENAME+".
*/
public class OptionDao extends AbstractDao<Option, Long> {

    public static final String TABLENAME = "OptionTable";

    public static final String QUESTION_ID 	= "question_id";
    public static final String NAME 		= "name";
    public static final String TYPE         = "type";
    public static final String POINT       = "point";

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
        public final static Property Id 		    = new Property(++c, Long.class, 	Dao.ID, 		true,  Dao.COLUMN_ID);
        public final static Property QuestionId 	= new Property(++c, Long.class, 	QUESTION_ID, 	false,  QUESTION_ID);
        public final static Property Name 	        = new Property(++c, String.class, 	NAME, 	        false, NAME);
        public final static Property Point         = new Property(++c, Integer.class, 	POINT, 	    false, POINT);
    };

    private DaoSession daoSession;


    public OptionDao(DaoConfig config) {
        super(config);
    }

    public OptionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
    	
    	Log.e(TABLENAME, "CREATE TABLE");    	
        
        String constraint = ifNotExists? Dao.IF_NOT_EXIST: " ";
        db.execSQL(Dao.CREATE_TABLE + constraint + Dao.Q + TABLENAME + Dao.Q + Dao.START_TABLE +
        		
                Dao.Q + Dao.COLUMN_ID 	    + Dao.Q + Dao.TYPE_INTEGER_PRIMARY 	+ Dao.COMMA +
                Dao.Q + QUESTION_ID         + Dao.Q + Dao.TYPE_INTEGER 			+ Dao.COMMA +
                Dao.Q + NAME                + Dao.Q + Dao.TYPE_TEXT 			+ Dao.COMMA +
                Dao.Q + TYPE                + Dao.Q + Dao.TYPE_TEXT 			+ Dao.COMMA +
                Dao.Q + POINT               + Dao.Q + Dao.TYPE_INTEGER
                +Dao.END_TABLE);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Option entity) {
    	 stmt.clearBindings();
    	 
         int c = 0;         
         Long id = entity.getId();
         ++c;
         if (id != null) {
         	 stmt.bindLong(c, id);
         }
        ++c; stmt.bindLong(c, entity.getQuestion_id());
        ++c; stmt.bindString(c, entity.getName());
        ++c; stmt.bindLong(c, entity.getScore());

     	
    }

    /** @inheritdoc */
    @Override
    public Option readEntity(Cursor cursor, int offset) {
    	
    	int c = 0;
        Option entity = new Option(
			 cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getLong(offset + ++c),
                cursor.getString(offset + ++c),
                cursor.getInt(offset + ++c)
        );
        return entity;
    }
    
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Option entity, int offset) {

        int c = 0;
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setQuestion_id(cursor.getLong(offset + ++c));
        entity.setName(cursor.getString(offset + ++c));
        entity.setScore(cursor.getInt(offset + ++c));

     }

    //  ===== BELOW THIS WILL NEVER CHANGE ======
    
    
    
    @Override
    protected void attachEntity(Option entity) {
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
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    
         
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Option entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Option entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
