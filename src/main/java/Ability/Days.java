package Ability;

public enum Days {
    Mon(1),
    Tue(2),
    Wed(3),
    Thu(4),
    Fri(5),
    Sat(6),
    Sun(7);

    final int day;
    Days(int day){this.day=day;}
    public int getDay(){return day;}
}
