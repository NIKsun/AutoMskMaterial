package com.example.material_model_automsk;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.SnackBar;
import com.rey.material.widget.Switch;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Никита on 24.09.2015.
 */
class Filter {
    Integer id;
    String mark;
    String model;
    String priceFrom;
    String priceTo;
    String yearFrom;
    String yearTo;
    String milleageFrom;
    String milleageTo;
    String volumeFrom;
    String volumeTo;
    String transmission;
    String typeOfEngine;
    String typeOfCarcase;
    String typeOfWheelDrive;
    Boolean withPhoto;

    String hrefAuto;
    String hrefAvito;
    String hrefDrom;

    private String getRangeString(String name, String value, String from, String to)
    {
        String message = "";
        if(!from.equals("") && from.equals(to))
            return name+":\t только " + from + " " + value + "\n";
        if(!from.equals("")) {
            message += name+":\t от " + from + " " + value;
            if (!to.equals(""))
                message += " до " + to + " " + value;
        }
        else if (!to.equals(""))
            message += name+":\t до " + to + " " + value;
        if(!message.isEmpty())
            message += "\n";
        return message;
    }

    private String getRangeStringFromCheckbox(String name, String value)
    {
        String[] param;
        String[] select;
        String message = "";
        if(!value.equals(""))
        switch (name){
            case "КПП":
                //message += "КПП: ";
                param = new String[]{"механика", "автомат", "робот", "вариатор"};
                select = value.split("");
                for(int i = 1; i<select.length;++i)
                    message+=param[Integer.parseInt(select[i])-1] + ", ";
                message = message.substring(0,message.length()-2);
                break;
            case "Двигатель":
                //message += "Двигатель: ";
                param = new String[]{"бензин", "дизель", "гибрид", "газ", "электро"};
                select = value.split("");
                for(int i = 1; i<select.length;++i)
                    message+=param[Integer.parseInt(select[i])-1] + ", ";
                message = message.substring(0,message.length()-2);
                break;
            case "Кузов":
                //message += "Кузов: ";
                param = new String[]{"пикап", "седан", "хэтчбек", "универсал","внедорожник", "минивен", "лимузин", "купе","кабриолет","фургон"};
                select = value.split("");
                for(int i = 1; i<select.length;++i)
                    message+=param[Integer.parseInt(select[i])] + ", ";
                message = message.substring(0,message.length()-2);
                break;
            case "Привод":
                //message += "Привод: ";
                param = new String[]{"передний", "задний", "полный"};
                select = value.split("");
                for(int i = 1; i<select.length;++i)
                    message+=param[Integer.parseInt(select[i])-1] + ", ";
                message = message.substring(0,message.length()-2);
                break;

        }
        if(!message.isEmpty())
            message = name +": "+message+ "\n";
        return message;
    }

    String getMessage()
    {
        String message = "";
        message += getRangeString("Цена", "руб.", priceFrom, priceTo);
        message += getRangeString("Год", "г.", yearFrom, yearTo);
        message += getRangeString("Пробег", "км.", milleageFrom, milleageTo);
        message += getRangeString("Объем", "л.", volumeFrom, volumeTo);

        message += getRangeStringFromCheckbox("Привод", typeOfWheelDrive);
        message += getRangeStringFromCheckbox("КПП", transmission);
        message += getRangeStringFromCheckbox("Двигатель", typeOfEngine);
        message += getRangeStringFromCheckbox("Кузов", typeOfCarcase);

        message += withPhoto ? "Только с фото" : "";
        return message;
    }

    Filter() {
    }
    void setPrice(CharSequence from, CharSequence to){
        priceFrom = (String) from;
        priceTo = (String) to;
    }
    void setYear(CharSequence from, CharSequence to){
        yearFrom = (String) from;
        yearTo = (String) to;
    }
    void setMilleage(CharSequence from, CharSequence to){
        milleageFrom = (String) from;
        milleageTo = (String) to;
    }
    void setVolume(CharSequence from, CharSequence to){
        volumeFrom = (String) from;
        volumeTo = (String) to;
    }

    void insertToDb(Context context){

        final DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("marka", this.mark);
        cv.put("model", this.model);
        cv.put("yearFrom", this.yearFrom);
        cv.put("yearTo", this.yearTo);

        cv.put("priceFrom", this.priceFrom);
        cv.put("priceTo", this.priceTo);
        cv.put("milleageFrom", this.milleageFrom);
        cv.put("milleageTo", this.milleageTo);

        cv.put("volumeFrom", this.volumeFrom);
        cv.put("volumeTo", this.volumeTo);
        cv.put("transmission", this.transmission);
        cv.put("bodyType", this.typeOfCarcase);

        cv.put("engineType", this.typeOfEngine);
        cv.put("withPhoto", this.withPhoto ? 1 : 0);
        cv.put("driveType", this.typeOfWheelDrive);
        db.insert("filters", null, cv);
        db.close();
        db = dbHelper.getWritableDatabase();
        Cursor cur = db.query("filters", new String[]{"id"}, null, null, null, null, null, null);
        cur.moveToLast();
        id = cur.getInt(cur.getColumnIndex("id"));

        db.close();
    }

    void getHref(Context context){

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //year
        Integer startYear = !this.yearFrom.equals("") ? Integer.valueOf(this.yearFrom) : 1980;
        String startYeard = "";
        if(startYear != 1980)
            startYeard = String.valueOf(startYear);
        Integer endYear = !this.yearTo.equals("") ? Integer.valueOf(this.yearTo) : 2015;
        String endYeard = "";
        if(endYear != 2015)
            endYeard = String.valueOf(endYear);

        //price
        String[] temp = this.priceFrom.split(" ");
        String temp_str = "";
        for(int i=0; i<temp.length;i++){
            temp_str+=temp[i];
        }
        Integer startPrice = !temp_str.equals("") ? Integer.valueOf(temp_str) : 0; //sPref.getInt("StartPrice",0);
        temp = this.priceTo.split(" ");
        temp_str = "";
        for(int i=0; i<temp.length;i++){
            temp_str+=temp[i];
        }
        Integer endPrice = !temp_str.equals("") ? Integer.valueOf(temp_str) : 10000000;//sPref.getInt("EndPrice",100000000);

        //trans
        //region trans
        String trans_str = this.transmission;
        if(trans_str.equals(""))
            trans_str="1234";
        String[] trans = trans_str.split("");

        String[] trans_arr_avito = {"","860-","861-","14754-","14753-"};
        String trans_avito_req = ".185_";

        String[] trans_arr_avto = {"","&search%5Bgearbox%5D%5Bautomatic_auto%5D=1","&search%5Bgearbox%5D%5Bmanual_all%5D=1","&search%5Bgearbox%5D%5Bautomatic_robot%5D=1","&search%5Bgearbox%5D%5Bautomatic_variator%5D=1"};
        String trans_avto_req = "";


        //trans unique drom ccka
        String[] trans_arr_drom = {"","&transmission=2","&transmission=1"}; //2 auto 1 man
        String trans_drom_req = "";

        if(trans_str.length()==1){
            if(trans[1].equals("2"))
                trans_drom_req = trans_arr_drom[2];
            else
                trans_drom_req = trans_arr_drom[1];
        }
        else{
            if(trans[2].equals("2"))
                trans_drom_req = trans_arr_drom[0];
            else
                trans_drom_req = trans_arr_drom[1];
        }

        for(int i = 1; i < trans.length; ++i){
            trans_avto_req+=trans_arr_avto[Integer.parseInt(trans[i])];
            trans_avito_req+=trans_arr_avito[Integer.parseInt(trans[i])];
        }
        if(trans_str.equals("1234"))
            trans_avto_req="";
        //endregion

        //engine type
        //region engine type
        //enginetype
        String engine_str = this.typeOfEngine;
        if(engine_str.equals(""))
            engine_str="12345";
        String[] engine = engine_str.split("");

        String[] engine_arr_avito = {"","862-","864-","863-","14752-","14751-"};
        String engine_avito_req = ".186_";

        String[] engine_arr_avto = {"","&search%5Bengine_type%5D%5Bgasoline%5D=1","&search%5Bengine_type%5D%5B1260%5D=1","&search%5Bengine_type%5D%5B1256%5D=1","&search%5Bengine_type%5D%5B1262%5D=1","&search%5Bengine_type%5D%5B1257%5D=1"};
        String engine_avto_req = "";

        //enginetype unique for drom
        String[] engine_arr_drom = {"","&fueltype=1","&fueltype=2"}; // 1 - benz 2 - diz
        String engine_drom_req = "";

        boolean drom_req_true = true;
        if(engine_str.length()==1){
            if(engine[1].equals("1"))
                engine_drom_req = engine_arr_drom[1];
            else{
                if(engine[1].equals("2"))
                    engine_drom_req = engine_arr_drom[2];
                else
                    drom_req_true = false;
            }
        }
        else
        if(engine[2].equals("2"))
            engine_drom_req = engine_arr_drom[0];
        else {
            if(engine[1].equals("1"))
                engine_drom_req = engine_arr_drom[1];
            else{
                if(engine[1].equals("2"))
                    engine_drom_req = engine_arr_drom[2];
                else
                    drom_req_true = false;
            }
        }



        for(int i = 1; i < engine.length; ++i){
            engine_avto_req+=engine_arr_avto[Integer.parseInt(engine[i])];
            engine_avito_req +=engine_arr_avito[Integer.parseInt(engine[i])];
        }
        if(engine_str.equals("12345"))
            engine_avto_req="";
        //endregion

        //drive
        //region drive type
         String privod_str = this.typeOfWheelDrive;
        if(privod_str.equals(""))
            privod_str="123";
        String[] privod = privod_str.split("");

        String[] privod_arr_avito = {"","8851-","8852-","8853-"};
        String privod_avito_req = ".695_";

        String[] privod_arr_avto = {"","&search%5Bdrive%5D%5B180%5D=1","&search%5Bdrive%5D%5B181%5D=1","&search%5Bdrive%5D%5B7%5D=1"};
        String privod_avto_req = "";

        String[] privod_arr_drom = {"","&privod=1","&privod=2","&privod=3"}; //luboi per zad poln
        String privod_drom_req = "";

        if(privod_str.length()==3){
            privod_drom_req = privod_arr_drom[0];
        }
        else
            privod_drom_req = privod_arr_drom[Integer.parseInt(privod[1])];



        for(int i = 1; i < privod.length; ++i){
            privod_avto_req+=privod_arr_avto[Integer.parseInt(privod[i])];
            privod_avito_req+=privod_arr_avito[Integer.parseInt(privod[i])];
        }
        if(privod_str.equals("123"))
            privod_avto_req="";
        //endregion

        //body
        //region body
        //body
        String body_str = this.typeOfCarcase;
        if(body_str.equals(""))
            body_str="1234567890";
        String[] body = body_str.split("");

        String[] body_arr_avito = {"","869-","872-","870-","4804-","4806-","867-","866-","865-","871-","868-"};
        String body_avito_req = ".187_";

        String[] body_arr_avto = {"","&search%5Bbody_type%5D%5Bg_sedan%5D=1","&search%5Bbody_type%5D%5Bg_hatchback%5D=1","&search%5Bbody_type%5D%5Bg_wagon%5D=1","&search%5Bbody_type%5D%5Bg_offroad%5D=1","&search%5Bbody_type%5D%5Bg_minivan%5D=1","&search%5Bbody_type%5D%5Bg_limousine%5D=1","&search%5Bbody_type%5D%5Bg_coupe%5D=1","&search%5Bbody_type%5D%5Bg_cabrio%5D=1","&search%5Bbody_type%5D%5Bg_furgon%5D=1","&search%5Bbody_type%5D%5Bg_pickup%5D=1"};
        String body_avto_req = "";

        String[] body_arr_drom = {"","&frametype10=10","&frametype5=5","&frametype3=3","&frametype7=7",
                "&frametype6=6","","&frametype1=1","&frametype11=11",
                "","&frametype12=12"};
        String body_drom_req = "";

        for(int i = 1; i < body.length; ++i){
            if(Integer.parseInt(body[i])==0){
                body_avto_req += body_arr_avto[10];
                body_avito_req += body_arr_avito[10];
                body_drom_req += body_arr_drom[10];
                continue;
            }
            body_avto_req+=body_arr_avto[Integer.parseInt(body[i])];
            body_avito_req+=body_arr_avito[Integer.parseInt(body[i])];
            body_drom_req+=body_arr_drom[Integer.parseInt(body[i])];
        }
        if(body_str.equals("1234567890")) {
            body_avto_req = "";
            body_drom_req = "";
        }
        //endregion


        //volume is another!!!!!!! (0-36)
        String[] data = new String[]{"0.0","0.6","0.7","0.8","0.9","1.0","1.1","1.2","1.3","1.4","1.5","1.6","1.7","1.8","1.9","2.0","2.1","2.2","2.3","2.4","2.5","2.6","2.7","2.8","2.9","3.0","3.1","3.2","3.3","3.4","3.5","4.0","4.5","5.0","5.5","6.0","6.0+"};
        int startVolume = !this.volumeFrom.equals("") ? java.util.Arrays.asList(data).indexOf(this.volumeFrom) : 0;
        int endVolume = !this.volumeTo.equals("") ? java.util.Arrays.asList(data).indexOf(this.volumeTo) : 36;
        String[] volume_arr_avto = new String[]{"0.0","0.6","0.7","0.8","0.9","1.0","1.1","1.2","1.3","1.4","1.5","1.6","1.7","1.8","1.9","2.0","2.1","2.2","2.3","2.4","2.5","2.6","2.7","2.8","2.9","3.0","3.1","3.2","3.3","3.4","3.5","4.0","4.5","5.0","5.5","6.0","10.0"};
        String[] volume_arr_avito = new String[]{"15775", "15776", "15777", "15778", "15779", "15780", "15781", "15782", "15783", "15784", "15785", "15786", "15787", "15788", "15789", "15790", "15791", "15792", "15793", "15794", "15795", "15796", "15797", "15798", "15799", "15800", "15801", "15802", "15803", "15804", "15805", "15810", "15815", "15820", "15825", "15830", "15831"};
        String[] volume_arr_drom = new String[]{"0.0","0.0","0.7","0.8","1.0","1.0","1.1","1.2","1.3","1.4","1.5","1.6","1.7","1.8","1.9","2.0","2.2","2.2","2.3","2.4","2.5","2.7","2.7","2.8","3.0","3.0","3.2","3.2","3.3","3.5","3.5","4.0","4.5","5.0","5.5","6.0",""};


        //probeg is another (0-61)
        data = new String[]{"0","5","10","15","20","25","30","35","40","45","50","55", "60", "65", "70","75","80","85","90","95","100","110","120","130","140","150","160","170","180","190","200","210","220","230","240","250","260","270","280","290","300","310","320","330","340","350","360","370","380","390","400","410","420","430","440","450","460","470","480","490","500","600"};
        String[] data_view = new String[data.length];
        for(int n = 1; n < data.length-1 ; ++n ){
            data_view[n]=data[n]+" 000";
        }
        data_view[0]="0";
        data_view[data.length-1]="500 000+";
        int probegvalFrom = !this.milleageFrom.equals("") ? java.util.Arrays.asList(data_view).indexOf(this.milleageFrom) : 0;
        int probegvalTo = !this.milleageTo.equals("") ? java.util.Arrays.asList(data_view).indexOf(this.milleageTo) : 61;
        String[] probeg_arr_avto = new String[]{"0","5000","10000","15000","20000","25000","30000","35000","40000","45000","50000","55000", "60000", "65000", "70000","75000","80000","85000","90000","95000","100000","110000","120000","130000","140000","150000","160000","170000","180000","190000","200000","210000","220000","230000","240000","250000","260000","270000","280000","290000","300000","310000","320000","330000","340000","350000","360000","370000","380000","390000","400000","410000","420000","430000","440000","450000","460000","470000","480000","490000","500000","100000000"};
        String[] probeg_arr_avito = new String[]{"15483", "15486", "15487", "15490", "15492", "15494", "15496", "15498", "15500", "15502", "15505", "15506", "15509", "15510", "15512", "15513", "15516", "15517", "15520", "15521", "15524", "15527", "15528", "15531", "15533", "15535", "15536", "15539", "15540", "15542", "15544", "15545", "15546", "15547", "15548", "15554", "15556", "15557", "15558", "15559", "15560", "15561", "15562", "15563", "15564", "15565", "15566", "15567", "15568", "15569", "15570", "15571", "15572", "15573", "15574", "15575", "15576", "15577", "15578", "15579", "15581", "15582"};


        //constructor for auto.ru
        String begin = "http://auto.ru/cars";
        String end = "/all/?sort%5Bcreate_date%5D=desc&search%5Bgeo_region%5D=38%2C87";
        String year1="&search%5Byear%5D%5Bmin%5D=";
        String year2="&search%5Byear%5D%5Bmax%5D=";
        String price1="&search%5Bprice%5D%5Bmin%5D="+startPrice+"%D1%80%D1%83%D0%B1.";
        String price2="&search%5Bprice%5D%5Bmax%5D="+endPrice+"%D1%80%D1%83%D0%B1.";
        String photo ="";
        String eng_vol1 = "&search%5Bengine_volume%5D%5Bmin%5D=";
        String eng_vol2 = "&search%5Bengine_volume%5D%5Bmax%5D=";
        String probegFrom = "&search%5Brun%5D%5Bmin%5D="+probeg_arr_avto[probegvalFrom]+"%D0%BA%D0%BC";
        String probegTo = "&search%5Brun%5D%5Bmax%5D="+probeg_arr_avto[probegvalTo]+"%D0%BA%D0%BC";

        //constructor for avito
        String begin_avito = "https://www.avito.ru/moskva/avtomobili";
        Map<Integer, String> map = new HashMap<Integer, String>();
        //region map create
        map.put(1970,"782");
        map.put(1980,"873");
        map.put(1985,"878");
        map.put(1990,"883");
        map.put(1991,"884");
        map.put(1992,"885");
        map.put(1993,"886");
        map.put(1994,"887");
        map.put(1995,"888");
        map.put(1996,"889");
        map.put(1997,"890");
        map.put(1998,"891");
        map.put(1999,"892");
        map.put(2000,"893");
        map.put(2001,"894");
        map.put(2002,"895");
        map.put(2003,"896");
        map.put(2004,"897");
        map.put(2005,"898");
        map.put(2006,"899");
        map.put(2007,"900");
        map.put(2008,"901");
        map.put(2009,"902");
        map.put(2010,"2844");
        map.put(2011,"2845");
        map.put(2012,"6045");
        map.put(2013,"8581");
        map.put(2014,"11017");
        map.put(2015,"13978");
        //endregion
        String startYearAvito = map.get(startYear);
        String endYearAvito = map.get(endYear);
        String year1a = "188_";
        String year2a = "b";
        String price1a = "&pmin=";
        String price2a = "&pmax=";
        String photoa = "";
        String eng_vol1a = "1374_";
        String eng_vol2a = "b";
        String probegaFrom = "1375_"+probeg_arr_avito[probegvalFrom]+"b";
        String probegaTo = probeg_arr_avito[probegvalTo];

        //constructor for drom
        String begin_drom = "http://moscow.drom.ru/auto";
        String end_drom = "/all/page@@@page/?order_d=dsc";
        String photodrom ="";
        String price1drom = "&minprice=";
        String price2drom = "&maxprice=";
        String year1drom = "&minyear=";
        String year2drom = "&maxyear=";
        String eng_vol1drom = "&mv=";
        String eng_vol2drom = "&xv=";

        //photo
        if(this.withPhoto){
            photo ="&search%5Bphoto%5D%5B1%5D=1";
            photoa = "&i=1";
            photodrom = "&ph=1";
        }



        String posMarkString = this.mark;
        String posModelString = this.model; //sPref.getString("SelectedModel", "Любая" );

        //get mark
        String marka = "";
        String markaavito = "";
        String markadrom = "";
        String marka_for_dialog = "###";


        if(!posMarkString.equals("Любая")) {
            Cursor cursorMark = db.query("marksTable", null, "markauser=?", new String[]{posMarkString}, null, null, null);
            cursorMark.moveToFirst();

            marka = "/" + cursorMark.getString(cursorMark.getColumnIndex("markarequest"));
            markaavito = "/" + cursorMark.getString(cursorMark.getColumnIndex("markarequestavito"));
            markadrom = "/" + cursorMark.getString(cursorMark.getColumnIndex("markarequestdrom"));
            marka_for_dialog = cursorMark.getString(cursorMark.getColumnIndex("markauser"));
        }
        //get model
        String model = "";
        String modelavito = "";
        String modeldrom = "";
        if(!posModelString.equals("Любая")) {
            Cursor cursorModel = db.query("modelsTable", null, "modeluser=?", new String[]{posModelString}, null, null, null);
            cursorModel.moveToFirst();
            model = "/" + cursorModel.getString(cursorModel.getColumnIndex("modelrequest"));
            modelavito = "/" + cursorModel.getString(cursorModel.getColumnIndex("modelrequestavito"));
            modeldrom = "/" + cursorModel.getString(cursorModel.getColumnIndex("modelrequestdrom"));
        }

        //put two plus one request
        hrefAuto = "###";
        hrefAvito = "###";
        hrefDrom = "###";
        if(!(marka.equals("/###")) && !(model.equals("/###")))
            hrefAuto = begin + marka + model + end + year1 + startYear.toString() + year2 + endYear.toString() + price1 + price2+photo+eng_vol1+volume_arr_avto[startVolume]+eng_vol2+volume_arr_avto[endVolume]+probegFrom+probegTo+body_avto_req+privod_avto_req+trans_avto_req+engine_avto_req;
        if(!(markaavito.equals("/###")) && !(modelavito.equals("/###")))
            hrefAvito = begin_avito+markaavito+modelavito+"/?"+photoa+price1a+startPrice+price2a+endPrice+"&f="+year1a+startYearAvito+year2a+endYearAvito+"."+eng_vol1a+volume_arr_avito[startVolume]+eng_vol2a+volume_arr_avito[endVolume]+"."+probegaFrom+probegaTo+body_avito_req+privod_avito_req+trans_avito_req+engine_avito_req;

        if(!(markadrom.equals("/###")) && !(modeldrom.equals("/###")) && drom_req_true!=false)
            hrefDrom = begin_drom + markadrom + modeldrom + end_drom + photodrom + price1drom + startPrice + price2drom
                    + endPrice + year1drom + startYeard + year2drom+ endYeard
                    + eng_vol1drom + volume_arr_drom[startVolume] + eng_vol2drom + volume_arr_drom[endVolume] + trans_drom_req
                    + engine_drom_req + privod_drom_req + body_drom_req + "&go_search=2";

        db.close();
    }
}

class Monitor {
    Integer id;
    Filter filter;
    Boolean isActive;
    Integer countOfNewCars;
    String hrefAuto;
    String hrefAvito;
    String hrefDrom;


    Monitor() {
    }
    Monitor(Filter filter,Context context) {
        isActive = true;
        this.filter = filter;
        countOfNewCars = 0;
        filter.getHref(context);
        hrefAuto = filter.hrefAuto;
        hrefAvito = filter.hrefAvito;
        hrefDrom = filter.hrefDrom;
    }

    void insertToDb(Context context)
    {
        final DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("filter_id", filter.id);
        cv.put("count_of_new_cars", countOfNewCars);
        cv.put("is_active", isActive ? 1 : 0);

        cv.put("href_auto", hrefAuto);
        cv.put("href_avito", hrefAvito);
        cv.put("href_drom", hrefDrom);

        db.insert("monitors", null, cv);
        db.close();
    }
}

public class MonitorCardAdapter extends RecyclerView.Adapter<MonitorCardAdapter.MonitorViewHolder>{

    public static class MonitorViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView monitorStatus;
        TextView monitorMarkAndModel;
        TextView monitorFilterInfo;
        TextView monitorCountOfNewCars;
        Switch monitorSwitch;
        ImageView iv;
        LinearLayout ll;

        MonitorViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv_mon);
            monitorStatus = (TextView)itemView.findViewById(R.id.cv_mon_status);
            monitorMarkAndModel = (TextView)itemView.findViewById(R.id.cv_mon_mark_and_model);
            monitorFilterInfo = (TextView)itemView.findViewById(R.id.cv_mon_filter_info);
            monitorCountOfNewCars = (TextView)itemView.findViewById(R.id.cv_mon_count_of_new_cars);
            monitorSwitch = (Switch)itemView.findViewById(R.id.cv_mon_switch_status);
            ll = (LinearLayout)itemView.findViewById(R.id.cv_mon_lin_lay_clickable);
            iv = (ImageView)itemView.findViewById(R.id.cv_mon_popup);
        }
    }

    List<Monitor> monitors;
    Activity parentActivity;
    RecyclerView rv;

    Monitor tempMonitor;
    int tempPosition;
    private int activeMonitorCounter;

    MonitorCardAdapter(List<Monitor> monitors, Activity parentActivity, RecyclerView myself, int startActiveMonitorCounter){
        this.monitors = monitors;
        this.parentActivity = parentActivity;
        this.rv = myself;
        this.activeMonitorCounter = startActiveMonitorCounter;
    }

    @Override
    public int getItemCount() {
        return monitors.size();
    }

    @Override
    public MonitorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_monitor, viewGroup, false);
        MonitorViewHolder mvh = new MonitorViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(final MonitorViewHolder monitorViewHolder, final int i) {
        if(i == getItemCount()-1) {
            monitorViewHolder.ll.setMinimumHeight(0);
            monitorViewHolder.ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
            monitorViewHolder.cv.setVisibility(View.INVISIBLE);
            return;
        }

        monitorViewHolder.cv.setVisibility(View.VISIBLE);
        monitorViewHolder.ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Resources resources = monitorViewHolder.monitorStatus.getContext().getResources();

        if(monitors.get(i).filter.mark.equals("Любая"))
            monitorViewHolder.monitorMarkAndModel.setText("Любая марка");
        else{
            if(monitors.get(i).filter.model.equals("Любая"))
                monitorViewHolder.monitorMarkAndModel.setText(monitors.get(i).filter.mark + " " + "Любая модель");
            else
                monitorViewHolder.monitorMarkAndModel.setText(monitors.get(i).filter.mark + " " + monitors.get(i).filter.model);
        }

        //monitorViewHolder.monitorMarkAndModel.setText(monitors.get(i).filter.mark + " " + monitors.get(i).filter.model);
        monitorViewHolder.monitorMarkAndModel.setTypeface(null, Typeface.BOLD);
        monitorViewHolder.monitorFilterInfo.setText(monitors.get(i).filter.getMessage());

        monitorViewHolder.monitorSwitch.setChecked(monitors.get(i).isActive);
        monitorViewHolder.monitorSwitch.setOnCheckedChangeListener(null);
        monitorViewHolder.monitorSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch aSwitch, boolean b) {
                Resources resources = monitorViewHolder.monitorStatus.getContext().getResources();
                if (b) {
                    activeMonitorCounter++;
                    monitorViewHolder.monitorStatus.setTextColor(resources.getColor(R.color.myPrimaryDarkColor));
                    monitorViewHolder.monitorStatus.setText(resources.getText(R.string.monitor_is_active));
                } else {
                    activeMonitorCounter--;
                    monitorViewHolder.monitorStatus.setTextColor(resources.getColor(R.color.colorPrimaryQuarter));
                    monitorViewHolder.monitorStatus.setText(resources.getText(R.string.monitor_is_not_active));
                }

                if (activeMonitorCounter != 0)
                    Toast.makeText(parentActivity, "Новый период: " + activeMonitorCounter * 3 + "минут", Toast.LENGTH_SHORT).show();

                monitors.get(i).isActive = b;
                notifyDataSetChanged();
                SQLiteDatabase db = new DbHelper(parentActivity).getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("is_active", b);
                db.update("monitors", cv, "id = ?", new String[]{String.valueOf(monitors.get(i).id)});
                db.close();
            }
        });


        monitorViewHolder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(monitorViewHolder.iv.getContext(), monitorViewHolder.iv);
                popup.getMenu().add(R.string.popup_edit);
                popup.getMenu().add(R.string.popup_delete);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch ((String)item.getTitle())
                        {
                            case "Изменить": ;break;
                            case "Удалить":
                                remove(i);
                                break;
                        }

                        return false;
                    }
                });
            }
        });

        if(monitors.get(i).isActive) {
            monitorViewHolder.monitorStatus.setTextColor(resources.getColor(R.color.myPrimaryDarkColor));
            monitorViewHolder.monitorStatus.setText(resources.getText(R.string.monitor_is_active));
        }
        else {
            monitorViewHolder.monitorStatus.setTextColor(resources.getColor(R.color.colorPrimaryQuarter));
            monitorViewHolder.monitorStatus.setText(resources.getText(R.string.monitor_is_not_active));
        }

       if (monitors.get(i).countOfNewCars == 0)
            monitorViewHolder.monitorCountOfNewCars.setVisibility(View.INVISIBLE);
        else {
            monitorViewHolder.monitorCountOfNewCars.setVisibility(View.VISIBLE);
            if (monitors.get(i).countOfNewCars < 10)
                monitorViewHolder.monitorCountOfNewCars.setText(" " + String.valueOf(monitors.get(i).countOfNewCars) + " ");
            else if (monitors.get(i).countOfNewCars < 100)
                monitorViewHolder.monitorCountOfNewCars.setText(String.valueOf(monitors.get(i).countOfNewCars));
            else {
                monitorViewHolder.monitorCountOfNewCars.setTextSize(23);
                monitorViewHolder.monitorCountOfNewCars.setPadding(7, 10, 7, 10);
                monitorViewHolder.monitorCountOfNewCars.setText("99+");
            }
        }

        monitorViewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm =
                        (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if(netInfo != null && netInfo.isConnectedOrConnecting()) {
                    Intent intent = new Intent(v.getContext(), ListOfCarsActivity.class);
                    intent.putExtra("monitorID", monitors.get(i).id);
                    v.getContext().startActivity(intent);
                }
                else
                {
                    SnackBar mSnackBar = ((MainActivity)v.getContext()).getSnackBar();

                    if(v.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        mSnackBar.applyStyle(R.style.SnackBarSingleLine);
                        mSnackBar.show();
                    }
                    else
                    {
                        mSnackBar.applyStyle(R.style.Material_Widget_SnackBar_Tablet_MultiLine);
                        mSnackBar.text("Нет удалось подключиться к серверу. Проверьте соеденение с интернетом.")
                        .actionText("Ок")
                        .duration(4000)
                        .show();
                    }
                }
            }
        });
    }

    private void deleteItemFromDB(String id)
    {
        SQLiteDatabase db = new DbHelper(parentActivity).getWritableDatabase();
        db.delete("monitors", "id = ?", new String[]{id});
        db.close();
    }

    public void finableRemove(){
        if(tempMonitor != null)
            deleteItemFromDB(String.valueOf(tempMonitor.id));
        tempMonitor = null;
    }

    public void remove(final int position) {
        activeMonitorCounter--;

        if(tempMonitor != null)
            deleteItemFromDB(String.valueOf(tempMonitor.id));

        tempMonitor=monitors.get(position);
        tempPosition=position;

        monitors.remove(position);
        notifyItemRemoved(position);
        for (int i = position;i<monitors.size();i++)
            notifyItemChanged(i);

        SnackBar sb = ((MainActivity)parentActivity).getSnackBar();
        sb.applyStyle(R.style.SnackBarSingleLine);
        sb.text("Монитор удален")
                .actionText("Восстановить")
                .duration(2500)
                .actionClickListener(new SnackBar.OnActionClickListener() {
                    @Override
                    public void onActionClick(SnackBar snackBar, int i) {
                        activeMonitorCounter++;
                        monitors.add(tempPosition, tempMonitor);
                        notifyItemInserted(tempPosition);
                        for (int iter = tempPosition; iter <monitors.size(); iter++)
                            notifyItemChanged(iter);
                        rv.scrollToPosition(tempPosition);
                        tempMonitor = null;
                    }
                });
        sb.show();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
