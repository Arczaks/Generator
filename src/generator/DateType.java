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
    
    private final int[] DAYS_IN_MONTH = { 31, 27, 31, 30, 31, 30, 31, 30, 31, 30, 31, 30};
    
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
      //  System.out.println("lata " + wynik);
        int p = month + 1;
        int d = date.getMonth() - 1;
        for (int i = month + 1; i < date.getMonth() - 1; i++){
            wynik += DAYS_IN_MONTH[i];
        }
       // System.out.println("miesiace " + wynik);
        wynik += DAYS_IN_MONTH[month] - day + date.getDay(); 
       // System.out.println("dni " + wynik);
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
        for (int i = 0; i < (shift % 365 - 365); i++){
            newYear++;
            tempShift -= 365;
        }
        
        if (DAYS_IN_MONTH[month] - day < tempShift){
            newDay = 1;
            tempShift -= day;
            newMonth++;
            while (tempShift > DAYS_IN_MONTH[newMonth]){
                tempShift -= DAYS_IN_MONTH[newMonth];
                newMonth++;
            }
        }
        newDay += tempShift;
 
        
        return new DateType(newDay, newMonth, newYear);
    }
        
}
