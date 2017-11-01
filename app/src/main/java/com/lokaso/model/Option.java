package com.lokaso.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.lokaso.dao.DaoSession;
import com.lokaso.dao.classes.OptionDao;
import com.lokaso.util.Constant;

import de.greenrobot.dao.DaoException;

/**
 * Created by spm3 on 4/7/2015.
 */
public class Option implements Parcelable{

    @SerializedName(Constant.ID)
    Long id;

    @SerializedName(Constant.QUESTION_ID)
    Long question_id;

    @SerializedName(Constant.NAME)
    String name;

    @SerializedName(Constant.SCORE)
    int score;


    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


    public Option(Long id, Long question_id, String name, int score) {
        this.id = id;
        this.question_id = question_id;
        this.name = name;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public Long getQuestion_id() {
        return question_id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuestion_id(Long question_id) {
        this.question_id = question_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    //FOR DAO
    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient OptionDao myDao;

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getOptionDao() : null;
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

    //PARSING
    private Option(Parcel in) {
        id          = in.readLong();
        question_id = in.readLong();
        name        = in.readString();
        score       = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {
        out.writeLong(id);
        out.writeLong(question_id);
        out.writeString(name);
        out.writeInt(score);

    }

    public static final Creator<Option> CREATOR = new Creator<Option>() {
        public Option createFromParcel(Parcel in) {
            return new Option(in);
        }

        public Option[] newArray(int size) {
            return new Option[size];
        }
    };
}