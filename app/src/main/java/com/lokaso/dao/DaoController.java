package com.lokaso.dao;

import android.content.Context;

import com.lokaso.model.Interest;
import com.lokaso.model.Place;
import com.lokaso.model.Profession;

import java.util.List;

/**
 * Created by spm3 on 5/29/2015.
 */
public class DaoController {

    private static final String TAG = DaoController.class.getSimpleName();

    public static List<Profession> getAllProfession(Context context) {
        DaoFunctions functions = new DaoFunctions(context);
        List<Profession> items = functions.getAllProfession();
        functions.close();
        return items;
    }

    public static void updateProfessionList(Context context, List<Profession> items) {
        DaoFunctions functions = new DaoFunctions(context);
        functions.updateProfessionList(items);
        functions.close();
    }

    public static String getLatestUpdateProfessionDate(Context context) {
        DaoFunctions functions = new DaoFunctions(context);
        String date = functions.getLatestUpdateProfessionDate();
        functions.close();

        return date;
    }


    public static List<Interest> getAllInterest(Context context) {

        DaoFunctions daoFunctions = new DaoFunctions(context);
        List<Interest> interestList = daoFunctions.getAllInterest();
        daoFunctions.close();
        return interestList;
    }

    public static String getLatestUpdateInterestDate(Context context) {

        DaoFunctions daoFunctions = new DaoFunctions(context);
        String updated_date = daoFunctions.getLatestUpdateInterestDate();
        daoFunctions.close();
        return updated_date;
    }

    public static void updateInterestList(Context context, List<Interest> interestList) {

        DaoFunctions daoFunctions = new DaoFunctions(context);
        daoFunctions.updateInterestList(interestList);
        daoFunctions.close();
    }


    public static void updateLocation(Context context, Place items) {
        DaoFunctions functions = new DaoFunctions(context);
        functions.updateLocation(items);
        functions.close();
    }

    public static List<Place> getAllLocation(Context context) {
        DaoFunctions functions = new DaoFunctions(context);
        List<Place> items = functions.getAllLocation();
        functions.close();
        return items;
    }

    public static List<Place> getRecentLocation(Context context) {
        DaoFunctions functions = new DaoFunctions(context);
        List<Place> items = functions.getRecentLocation();
        functions.close();
        return items;
    }

    public static Place getLocation(Context context, String place_id) {
        DaoFunctions functions = new DaoFunctions(context);
        Place item = functions.getLocationById(place_id);
        functions.close();
        return item;
    }

}
