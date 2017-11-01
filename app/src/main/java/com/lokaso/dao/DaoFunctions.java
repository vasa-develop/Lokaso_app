package com.lokaso.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.lokaso.dao.classes.InterestDao;
import com.lokaso.dao.classes.LocationDao;
import com.lokaso.dao.classes.OptionDao;
import com.lokaso.dao.classes.ProfessionDao;
import com.lokaso.model.Interest;
import com.lokaso.model.Option;
import com.lokaso.model.Place;
import com.lokaso.model.Profession;
import com.lokaso.util.MyLog;

import java.util.List;


public class DaoFunctions {
	
	private static final String TAG = DaoFunctions.class.getSimpleName();
    private Context context;

	private DaoMaster daoMaster;
	private DaoSession daoSession;
    private SQLiteDatabase db;	
    private DaoMaster.DevOpenHelper helper;

    private InterestDao interestDao;
    private ProfessionDao professionDao;
    private LocationDao locationDao;

    private OptionDao optionDao;


    /*
        private Long user_id;
        private Long meeting_id;
    */

    public DaoFunctions(Context context) {
		this.context = context;
		helper = new DaoMaster.DevOpenHelper(context, Dao.DATABASE, null);		
	}	
		
	public void close(){
		helper.close();		 
	}
			
	private void closeDao(){
		 db.close();
		 daoSession.clear();		 
	}

	private void initDao() {
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();

        interestDao = daoSession.getInterestDao();
        professionDao = daoSession.getProfessionDao();
        locationDao = daoSession.getLocationDao();

        optionDao = daoSession.getOptionDao();
	}







    //TODO OPTION
    public void updateInterest(Interest item) {
        initDao();
        interestDao.insertOrReplaceInTx(item);
        closeDao();
    }
    public void updateInterestList(List<Interest> list) {
        initDao();
        interestDao.insertOrReplaceInTx(list);
        closeDao();
    }
    public List<Interest> getAllInterest(){
        List<Interest> dataList;
        initDao();
        dataList = interestDao.queryBuilder()
                .where(InterestDao.Properties.Status.eq(1))
                .orderAsc(InterestDao.Properties.DisplayOrder)
                .list();
        closeDao();
        return dataList;
    }
    public String getLatestUpdateInterestDate(){
        List<Interest> dataList;
        initDao();
        dataList = interestDao.queryBuilder()
                .orderDesc(InterestDao.Properties.UpdatedDate)
                .limit(1)
                .list();
        closeDao();

        String date = "";
        if(dataList!=null && dataList.size()>0) {
            Interest interest = dataList.get(0);
            date = interest.getUpdated_date();
        }

        return date;
    }
    private List<Interest> getInterestListById(Long id){
        List<Interest> dataList;
        //initDao();
        dataList = interestDao.queryBuilder().where(InterestDao.Properties.Id.eq(id)).list();
        //closeDao();
        return dataList;
    }

    public List<Interest> getInterestsById(Long id){
        List<Interest> dataList;
        initDao();
        dataList = interestDao.queryBuilder().where(InterestDao.Properties.Id.eq(id)).list();
        closeDao();
        return dataList;
    }
    public Interest getInterestById(Long id) {
        initDao();
        Interest item = interestDao.loadByRowId(id);
        closeDao();
        return item;
    }
    public void deleteInterest(Interest item) {
        initDao();
        interestDao.deleteInTx(item);
        closeDao();
    }
    public void deleteAllInterest() {
        initDao();
        interestDao.deleteAll();
        closeDao();
    }



    //TODO PROFESSION
    public void updateProfession(Profession item) {
        initDao();
        professionDao.insertOrReplaceInTx(item);
        closeDao();
    }
    public void updateProfessionList(List<Profession> list) {
        initDao();
        professionDao.insertOrReplaceInTx(list);
        closeDao();
    }
    public List<Profession> getAllProfession(){
        List<Profession> dataList;
        initDao();
        dataList = professionDao.queryBuilder()
                .where(ProfessionDao.Properties.Status.eq(1))
                .list();
        closeDao();
        return dataList;
    }
    public String getLatestUpdateProfessionDate(){
        List<Profession> dataList;
        initDao();
        dataList = professionDao.queryBuilder()
                .orderDesc(ProfessionDao.Properties.UpdatedDate)
                .limit(1)
                .list();
        closeDao();

        String date = "";
        if(dataList!=null && dataList.size()>0) {
            Profession profession = dataList.get(0);
            date = profession.getUpdated_date();
        }

        return date;
    }
    private List<Profession> getProfessionListById(Long id){
        List<Profession> dataList;
        //initDao();
        dataList = professionDao.queryBuilder().where(ProfessionDao.Properties.Id.eq(id)).list();
        //closeDao();
        return dataList;
    }

    public List<Profession> getProfessionsById(Long id){
        List<Profession> dataList;
        initDao();
        dataList = professionDao.queryBuilder().where(ProfessionDao.Properties.Id.eq(id)).list();
        closeDao();
        return dataList;
    }
    public Profession getProfessionById(Long id) {
        initDao();
        Profession item = professionDao.loadByRowId(id);
        closeDao();
        return item;
    }
    public void deleteProfession(Profession item) {
        initDao();
        professionDao.deleteInTx(item);
        closeDao();
    }
    public void deleteAllProfession() {
        initDao();
        professionDao.deleteAll();
        closeDao();
    }




    //TODO LOCATION
    public void updateLocation(Place item) {
        initDao();
        locationDao.insertOrReplaceInTx(item);

/*
        Place place = getLocationByPlaceId(item.getPlace_id());

        MyLog.e(TAG, "IDPRIMARY : " + item.getId());

        if(place==null) {
            locationDao.insert(item);
        }
        else {
            MyLog.e(TAG, "IDPRIMARY2 : " + place.getId());
            locationDao.update(item);
        }
*/

        closeDao();
    }
    public void updateLocationList(List<Place> list) {
        initDao();
        locationDao.insertOrReplaceInTx(list);
        closeDao();
    }
    public List<Place> getAllLocation(){
        List<Place> dataList;
        initDao();
        dataList = locationDao.queryBuilder()
                .where(LocationDao.Properties.Status.eq(1))
                .list();
        closeDao();
        return dataList;
    }
    public List<Place> getRecentLocation(){
        List<Place> dataList;
        initDao();
        dataList = locationDao.queryBuilder()
                .where(LocationDao.Properties.Status.eq(1))
                .orderDesc(LocationDao.Properties.UpdatedDate)
                .limit(5)
                .list();
        closeDao();
        return dataList;
    }
    public String getLatestUpdateLocationDate(){
        List<Place> dataList;
        initDao();
        dataList = locationDao.queryBuilder()
                .orderDesc(LocationDao.Properties.UpdatedDate)
                .limit(1)
                .list();
        closeDao();

        String date = "";
        if(dataList!=null && dataList.size()>0) {
            Place interest = dataList.get(0);
            date = interest.getUpdated_date();
        }

        return date;
    }
    private List<Place> getLocationListById(Long id){
        List<Place> dataList;
        //initDao();
        dataList = locationDao.queryBuilder().where(LocationDao.Properties.Id.eq(id)).list();
        //closeDao();
        return dataList;
    }

    public List<Place> getLocationsById(Long id){
        List<Place> dataList;
        initDao();
        dataList = locationDao.queryBuilder().where(LocationDao.Properties.Id.eq(id)).list();
        closeDao();
        return dataList;
    }
    private Place getLocationByPlaceId(String id) {
        List<Place> dataList = locationDao.queryBuilder().where(LocationDao.Properties.PlaceId.eq(id)).list();
        Place item = (dataList!=null && dataList.size()>0) ? dataList.get(0) : null;;

        return item;
    }
    public Place getLocationById(String id) {
        initDao();
        List<Place> dataList = locationDao.queryBuilder().where(LocationDao.Properties.PlaceId.eq(id)).list();
        Place item = (dataList!=null && dataList.size()>0) ? dataList.get(0) : null;
        closeDao();
        return item;
    }
    /*
    public Place getLocationById(Long id) {
        initDao();
        Place item = locationDao.loadByRowId(id);
        closeDao();
        return item;
    }*/
    public void deleteLocation(Place item) {
        initDao();
        locationDao.deleteInTx(item);
        closeDao();
    }
    public void deleteAllLocation() {
        initDao();
        locationDao.deleteAll();
        closeDao();
    }





    //TODO OPTION
    public void updateOption(Option item) {
        initDao();
        optionDao.insertOrReplaceInTx(item);
        closeDao();
    }
    public void updateOptionList(List<Option> list) {
        initDao();
        optionDao.insertOrReplaceInTx(list);
        closeDao();
    }
    public List<Option> getAllOptions(){
        List<Option> dataList;
        initDao();
        dataList = optionDao.queryBuilder().list();
        closeDao();
        return dataList;
    }

    private List<Option> getOptionListById(Long id){
        List<Option> dataList;
        //initDao();
        dataList = optionDao.queryBuilder().where(OptionDao.Properties.QuestionId.eq(id)).list();
        //closeDao();
        return dataList;
    }

    public List<Option> getOptionsById(Long id){
        List<Option> dataList;
        initDao();
        dataList = optionDao.queryBuilder().where(OptionDao.Properties.QuestionId.eq(id)).list();
        closeDao();
        return dataList;
    }
    public Option getOptionById(Long id) {
        initDao();
        Option item = optionDao.loadByRowId(id);
        closeDao();
        return item;
    }
    public void deleteOption(Option item) {
        initDao();
        optionDao.deleteInTx(item);
        closeDao();
    }
    public void deleteAllOptions() {
        initDao();
        optionDao.deleteAll();
        closeDao();
    }




}
