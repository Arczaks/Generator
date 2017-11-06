/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generator;

/**
 *
 * @author Archax
 */
public class DateType implements Comparable<DateType>{
    
    private final int day;
    private final int month;
    private final int year;
    
    private final int[] DAYS_IN_MONTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    
    public DateType(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }
    
    public DateType(String date){
        int index;
        String temp = "";
        String date2 = date;
        index = date2.indexOf(".");
        int i = 0;
        for (; i < index; i++){
            temp += date2.charAt(i);
        }
        i++;
        day = Integer.parseInt(temp);
        temp = "";
        
        index = date2.indexOf(".", index + 1);
        for (; i < index; i++){
            temp += date2.charAt(i);
        }
        i++;
        month = Integer.parseInt(temp);
        temp = "";
        
        for (; i < date2.length(); i++){
            temp += date2.charAt(i);
        }
        year = Integer.parseInt(temp);
    }
    
    public int getDay(){
        return day;
    }
    
    public int getMonth(){
        return month;
    }
    
    public int getYear(){
        return year;
    }

    public int getDifference(DateType date) {
        int wynik = (year - date.getYear()) * 365;
        if (month < date.getMonth()){
            for (int i = month; i <= date.getMonth(); i++){
                wynik -= DAYS_IN_MONTH[i - 1];
            }   
        } else {
            if (month != date.getMonth()){
                for (int i = date.getMonth(); i <= month; i++){
                    wynik += DAYS_IN_MONTH[i - 1];
                }
            }
        }
        if (month > date.getMonth()){
            wynik = wynik - DAYS_IN_MONTH[month - 1] + day - date.getDay();
        } else {
            if (month < date.getMonth()){
                wynik = wynik + day + DAYS_IN_MONTH[date.getMonth() - 1] - date.getDay(); 
            } else {
                wynik = wynik + day - date.getDay();
            }
        }
        return wynik;
    }
    
    @Override
    public int compareTo(DateType o) {
        return this.getDifference(o);
    }
    
    @Override
    public String toString(){
        return day+ "." + month + "." + year;
    }

    public DateType getNextDate(int shift){
        int newDay = day;
        int newMonth = month;
        int newYear = year;
        
        int tempShift = shift;
        for (int i = 0; i < (shift / 365); i++){
            newYear++;
            tempShift -= 365;
        }

        if (DAYS_IN_MONTH[month - 1] - day < tempShift){
            newDay = 1;
            tempShift -= DAYS_IN_MONTH[month - 1] - day;
            newMonth++;
            if ( newMonth > 12){
                    newMonth = 1;
                    newYear++;
                }
            while (tempShift > DAYS_IN_MONTH[newMonth - 1]){
                tempShift -= DAYS_IN_MONTH[newMonth - 1];
                newMonth++;
                if ( newMonth > 12){
                    newMonth = 1;
                    newYear++;
                }
            }
        }
        newDay += tempShift;
        if (newDay > DAYS_IN_MONTH[newMonth - 1]){
            newDay -= DAYS_IN_MONTH[newMonth - 1];
            newMonth++;
        }
 
        if (newDay <= 0){
          System.out.println("shift: " + shift + " old day: " + this.toString() + " new day: " + newDay + "." + newMonth + "." + newYear);
        }
        return new DateType(newDay, newMonth, newYear);
    }
        
}
