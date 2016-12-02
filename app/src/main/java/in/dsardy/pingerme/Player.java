package in.dsardy.pingerme;

/**
 * Created by Shubham on 11/24/2016.
 */

public class Player {

    String name , mobile ,age ,gender;
    double lat,lang;
    int intrest;
    String time;

    public Player(String name , String mobile , double lat , double lang , String age , String gender , int intrest , String time){

        this.age=age;
        this.gender=gender;
        this.lang=lang;
        this.lat=lat;
        this.name =name;
        this.mobile=mobile;
        this.intrest=intrest;
        this.time = time;

    }
    public Player(){

    }

    public String getTime() {
        return time;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public int getIntrest() {
        return intrest;
    }

    public double getLang() {
        return lang;
    }

    public double getLat() {
        return lat;
    }

    public String getMobile() {
        return mobile;
    }

    public String getName() {
        return name;
    }


}
