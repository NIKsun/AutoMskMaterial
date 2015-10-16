package com.example.material_model_automsk;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Никита on 22.07.2015.
 */


public class Cars  {

    class Car implements Comparable{
        String id;
        String href;
        String img;
        String message;
        String price;
        String mileage;
        String year;
        String city;
        Date timeOfCreate;

        @Override
        public int compareTo(Object another) {
            if(this.timeOfCreate.getTime() / 1000 < ((Car)another).timeOfCreate.getTime() / 1000)
                return -1;
            else if(this.timeOfCreate.getTime() / 1000 > ((Car)another).timeOfCreate.getTime() / 1000)
                return 1;
            else
                return 0;
        }
    }

    Car[] cars;
    int capacity;
    int lastCar;

    public Cars(int len)
    {
        cars = new Car[len];
        capacity = len;
        lastCar = 0;
    }
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static String getCarIdDrom(Element elem)
    {
        if(elem.select("td:nth-child(1) > center > a > img").first() != null)
            if(elem.select("td:nth-child(1) > center > a > img").first().className().equals("pinned"))
                return "pinned";
        return elem.attr("data-bull-id");
    }
    public boolean appendFromDromRu(Element elem)
    {
        if(lastCar >= capacity)
            return false;
        if(elem.select("td:nth-child(1) > center > a > img").first() != null)
            if(elem.select("td:nth-child(1) > center > a > img").first().className().equals("pinned"))
                return false;
        Car currentCar = new Car();
        currentCar.id = elem.attr("data-bull-id");
        Element column = elem.select("td:nth-child(1) > center > nobr > a").first();
        currentCar.href = column.attr("href");

        Date createDate = new Date();
        createDate.setDate(Integer.parseInt(column.text().split("-")[0]));
        createDate.setMonth(Integer.parseInt(column.text().split("-")[1]) - 1);
        createDate.setHours(0);
        createDate.setMinutes(0);
        createDate.setSeconds(0);
        currentCar.timeOfCreate = createDate;
        currentCar.img = elem.select("td.c_i > a:nth-child(2) > img").attr("src");

        currentCar.message = elem.select("td:nth-child(3)").text()+", "+elem.select("td:nth-child(5)").text();
        if(!elem.select("td:nth-child(4)").text().isEmpty())
            currentCar.year = elem.select("td:nth-child(4)").text();
        else
            currentCar.year = "не указан";
        if(!elem.select("td:nth-child(6)").text().isEmpty())
            if(!elem.select("td:nth-child(6)").text().equals("новый"))
                currentCar.mileage = elem.select("td:nth-child(6)").text().split(",")[0]+" 000 км";
            else
                currentCar.mileage = "новый авто";
        else
            currentCar.mileage = "не указан";
        currentCar.price =  elem.select("td:nth-child(8) > span.f14").text();
        currentCar.city = elem.select("td:nth-child(8) > span:nth-child(3)").text();
        cars[lastCar] = currentCar;
        lastCar++;
        return true;
    }

    public boolean addFromAutoRu(Element elem)
    {
        if(lastCar >= capacity)
            return false;
        Car currentCar = new Car();
        if(elem == null){
            return false;
        }
        Pattern pattern = Pattern.compile("card_id\":\"([0-9]+).+created\":([^,]+)");
        Matcher matcher = pattern.matcher(elem.attr("data-stat_params"));
        if(matcher.find()){
            currentCar.id = matcher.group(1);
            currentCar.timeOfCreate = new Date(Long.parseLong(matcher.group(2)));
        }
        else
            return false;
        currentCar.timeOfCreate.setSeconds(0);

        currentCar.href = elem.select("td.sales-list-cell.sales-list-cell_images > a").first().attr("href");
        currentCar.img = elem.select("td.sales-list-cell.sales-list-cell_images > a > img").first().attr("data-original");
        if(currentCar.img.isEmpty() == true)
            currentCar.img = elem.select("td.sales-list-cell.sales-list-cell_images > a > img").first().attr("src");

        if (currentCar.img.indexOf("/i/all7/img/no-photo-thumb.png") != -1)
            currentCar.img = "http://auto.ru/i/all7/img/no-photo-thumb.png";

        currentCar.message = elem.select("td.sales-list-cell.sales-list-cell_mark_id").first().text();
        currentCar.price =  elem.select("td.sales-list-cell.sales-list-cell_price").first().text();
        currentCar.year =  elem.select("td.sales-list-cell.sales-list-cell_year").first().text();
        currentCar.mileage=  elem.select("td.sales-list-cell.sales-list-cell_run").first().text();
        currentCar.city =  elem.select("td.sales-list-cell.sales-list-cell_poi_id > div.sales-list-region.ico-appear").first().text();
        cars[lastCar] = currentCar;
        lastCar++;
        return true;
    }
    public static Date getDateAuto(Element elem)
    {
        if(elem == null){
            return null;
        }
        Pattern pattern = Pattern.compile("created\":([^,]+)");
        Matcher matcher = pattern.matcher(elem.attr("data-stat_params"));
        if(matcher.find()){
            Date buf = new Date(Long.parseLong(matcher.group(1)));
            buf.setSeconds(0);
            return buf;
        }
        return null;
    }
    public static Date getDateAvito(Element elem)
    {
        String[] date = elem.select("div.description > div.data > div").text().split(" ");
        Date result = new Date();
        if(date.length == 2)
        {
            if(date[0].equals("Сегодня"))
            {
                result.setMinutes(Integer.parseInt(date[1].split(":")[1]));
                result.setHours(Integer.parseInt(date[1].split(":")[0]));
            }
            else {
                result = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L);
                result.setMinutes(Integer.parseInt(date[1].split(":")[1]));
                result.setHours(Integer.parseInt(date[1].split(":")[0]));
            }
        }
        else
        {
            switch (date[1])
            {
                case "января": result.setMonth(0); break;
                case "февраля": result.setMonth(1); break;
                case "марта": result.setMonth(2); break;
                case "апреля": result.setMonth(3); break;
                case "мая": result.setMonth(4); break;
                case "июня": result.setMonth(5); break;
                case "июля": result.setMonth(6); break;
                case "августа": result.setMonth(7); break;
                case "сентября": result.setMonth(8); break;
                case "октября": result.setMonth(9); break;
                case "ноября": result.setMonth(10); break;
                case "декабря": result.setMonth(11); break;
            }
            result.setMinutes(Integer.parseInt(date[2].split(":")[1]));
            result.setHours(Integer.parseInt(date[2].split(":")[0]));
            result.setDate(Integer.parseInt(date[0]));
        }
        result.setSeconds(0);
        return result;
    }
    public long getCarDateLong(int i)
    {
        return cars[i].timeOfCreate.getTime();
    }
    public Date getCarDate(int i)
    {
        return cars[i].timeOfCreate;
    }
    public String getMessage(int pos)
    {
        String message = "";
        message  += "<h6><font face=fantasy color=#08088A>" + cars[pos].message + "</font></h6>";
        message += "<h6>Цена: " + cars[pos].price +"</h6>";
        message += "<font color=#585858>Год: " + cars[pos].year;
        message += "<br>Пробег: " + cars[pos].mileage;
        message += "<br>Место: " + cars[pos].city + "</font>";
        return message;
    }

    public void sortByDateAvito()
    {
        Arrays.sort(cars, Collections.reverseOrder());
    }

    public void addSeparator(String resourceName, int countOfCars)
    {
        Car c = new Car();
        c.id = "separator";
        c.href = resourceName;
        c.mileage = String.valueOf(countOfCars);
        c.img = "http://auto.ru/i/all7/img/no-photo-thumb.png";
        c.timeOfCreate = new Date();
        cars[lastCar] = c;
        lastCar++;
    }
    public static Cars merge(Cars carsAvto, Cars carsAvito, Cars carsDrom)
    {
        Cars result = new Cars(carsAvto.getLength() + carsAvito.getLength() + carsDrom.getLength() + 3);
        if(carsAvto.getLength()>0)
        {
            result.addSeparator("Auto.ru",carsAvto.getLength());
            int counter = 0;
            for(int i=0;i<carsAvto.getLength();i++)
            {
                result.cars[result.getLength()] = carsAvto.cars[counter];
                counter++;
                result.lastCar++;
            }
        }
        if(carsDrom.getLength()>0)
        {
            result.addSeparator("Drom.ru",carsDrom.getLength());

            int counter = 0;
            for(int i=0;i<carsDrom.getLength();i++)
            {
                result.cars[result.getLength()] = carsDrom.cars[counter];
                counter++;
                result.lastCar++;
            }
        }
        if(carsAvito.getLength()>0)
        {
            result.addSeparator("Avito.ru",carsAvito.getLength());
            int counter = 0;
            for(int i=0;i<carsAvito.getLength();i++)
            {
                result.cars[result.getLength()] = carsAvito.cars[counter];
                counter++;
                result.lastCar++;
            }
        }
        return result;
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public boolean addFromAvito(Element elem)
    {
        Car currentCar = new Car();
        if(elem == null){
            return false;
        }
        currentCar.id = elem.attr("id");
        currentCar.img = elem.select("div.b-photo > a > img").attr("data-srcpath");
        if(currentCar.img.isEmpty())
            currentCar.img = elem.select("div.b-photo > a > img").attr("src");
        if(currentCar.img.isEmpty())
            currentCar.img = "//auto.ru/i/all7/img/no-photo-thumb.png";

        currentCar.img = "http:" + currentCar.img;

        currentCar.href = "https://www.avito.ru" + elem.select("div.description > h3 > a").attr("href");

        currentCar.message = elem.select("div.description > h3 > a").text();
        currentCar.year = currentCar.message.split(", ")[1];
        currentCar.message = currentCar.message.split(", ")[0];

        String buf = elem.select("div.description > div.about").text();
        if(buf.split("\\.")[0].endsWith("руб")) {
            currentCar.price = buf.split("\\.")[0];
            buf = buf.substring(buf.indexOf('.'));
        }
        else
            currentCar.price = "не указана";

        Pattern pattern = Pattern.compile("([0-9]|\\s)+км");
        Matcher matcher = pattern.matcher(buf);
        if(matcher.find()) {
            currentCar.mileage = matcher.group(0);
            currentCar.message += buf.substring(buf.indexOf(currentCar.mileage)+currentCar.mileage.length());
        }
        else {
            currentCar.mileage = "не указан";
            currentCar.message += buf;
        }

        String[] date = elem.select("div.description > div.data > div").text().split(" ");
        if(date.length == 2)
        {
            if(date[0].equals("Сегодня"))
            {
                currentCar.timeOfCreate = new Date();
                currentCar.timeOfCreate.setMinutes(Integer.parseInt(date[1].split(":")[1]));
                currentCar.timeOfCreate.setHours(Integer.parseInt(date[1].split(":")[0]));
            }
            else {
                currentCar.timeOfCreate = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L);
                currentCar.timeOfCreate.setMinutes(Integer.parseInt(date[1].split(":")[1]));
                currentCar.timeOfCreate.setHours(Integer.parseInt(date[1].split(":")[0]));
            }
        }
        else
        {
            currentCar.timeOfCreate = new Date();
            switch (date[1])
            {
                case "января": currentCar.timeOfCreate.setMonth(0); break;
                case "февраля": currentCar.timeOfCreate.setMonth(1); break;
                case "марта": currentCar.timeOfCreate.setMonth(2); break;
                case "апреля": currentCar.timeOfCreate.setMonth(3); break;
                case "мая": currentCar.timeOfCreate.setMonth(4); break;
                case "июня": currentCar.timeOfCreate.setMonth(5); break;
                case "июля": currentCar.timeOfCreate.setMonth(6); break;
                case "августа": currentCar.timeOfCreate.setMonth(7); break;
                case "сентября": currentCar.timeOfCreate.setMonth(8); break;
                case "октября": currentCar.timeOfCreate.setMonth(9); break;
                case "ноября": currentCar.timeOfCreate.setMonth(10); break;
                case "декабря": currentCar.timeOfCreate.setMonth(11); break;
            }
            currentCar.timeOfCreate.setMinutes(Integer.parseInt(date[2].split(":")[1]));
            currentCar.timeOfCreate.setHours(Integer.parseInt(date[2].split(":")[0]));
            currentCar.timeOfCreate.setDate(Integer.parseInt(date[0]));
        }
        currentCar.timeOfCreate.setSeconds(0);

        if(!elem.select("div.description > div.data > p:nth-child(2)").text().isEmpty())
            currentCar.city = elem.select("div.description > div.data > p:nth-child(2)").text();
        else
            currentCar.city = "не указано";

        cars[lastCar] = currentCar;
        lastCar++;
        return true;
    }

    public String getHref(int pos)
    {
        if(cars[pos].href.substring(0,4).equals("http"))
            return cars[pos].href;
        else
            return "http://"+cars[pos].href;
    }
    public String getImg(int pos)
    {
        if(cars[pos].img.substring(0,4).equals("http"))
            return cars[pos].img;
        else
            return "http:"+cars[pos].img;
    }
    public int getLength()
    {
        return lastCar;
    }
}
