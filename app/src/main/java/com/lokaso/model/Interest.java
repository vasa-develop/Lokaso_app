package com.lokaso.model;

import com.lokaso.dao.DaoSession;
import com.lokaso.dao.classes.InterestDao;
import com.lokaso.dao.classes.OptionDao;

import java.io.Serializable;

import de.greenrobot.dao.DaoException;

/**
 * Created by Androcid on 09-Jan-17.
 */
public class Interest implements Serializable {

    private long id;
    private String name;
    private String image;
    private long display_order;
    private String created_date;
    private String updated_date;
    private long status;
    private boolean isChecked;

    public Interest(int id, String name, String image, long display_order, String created_date, String updated_date, int status) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.display_order = display_order;
        this.created_date = created_date;
        this.updated_date = updated_date;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public long getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(long display_order) {
        this.display_order = display_order;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }




    //FOR DAO
    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient InterestDao myDao;

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getInterestDao() : null;
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
