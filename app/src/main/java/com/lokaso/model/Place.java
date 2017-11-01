package com.lokaso.model;

import com.lokaso.dao.DaoSession;
import com.lokaso.dao.classes.InterestDao;
import com.lokaso.dao.classes.LocationDao;

import java.io.Serializable;

import de.greenrobot.dao.DaoException;

/**
 * Created by Androcid on 14-Oct-16.
 */

public class Place implements Serializable {

    int type = 0;

    // id would be null
    String id;

    private String place_id;

    private String name;
    private String location;

    private String latitude;
    private String longitude;

    private String created_date;
    private String updated_date;

    private long status;

    private boolean isSearchingGps;

    public Place(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public Place(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Place(String id, String place_id, String name, String location, String latitude, String longitude, String created_date, String updated_date, int status) {

        this.id = id;
        this.place_id = place_id;
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.created_date = created_date;
        this.updated_date = updated_date;
        this.status = status;
    }



    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatLng(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public boolean isSearchingGps() {
        return isSearchingGps;
    }

    public void setSearchingGps(boolean searchingGps) {
        isSearchingGps = searchingGps;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return place_id;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }






    //FOR DAO
    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient LocationDao myDao;

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLocationDao() : null;
    }

    /** Convenient call for {@link de.greenrobot.dao.AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /** Convenient call for {@link de.greenrobot.dao.AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** Convenient call for {@link de.greenrobot.dao.AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
}
