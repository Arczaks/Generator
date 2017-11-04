/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;

/**
 *
 * @author Archax
 */
public class Column{
    private final String name;
    private String maxValue;
    private String minValue;
    private final Boolean numbers;
    private final List<Relation> relations;
    private final String parameter;
    
    private final String type;
    
    private static final int NUMBERS = 0;
    private static final int LOWERCASE = 1;
    private static final int UPPERCASE= 2;
    
    private Integer lastValue;
 
    public Column(String name, String max, String min, Boolean numbers, List<Relation> relations, String type, String parameter){
        this.name = name;
        if (!"Date".equals(type)){
            Float tMax = Float.parseFloat(max);
            Float tMin = Float.parseFloat(min);
            if (tMax < tMin){
                String temp = max;
                max = min;
                min = temp;
            }
        } else{
            DateType tMax = new DateType(max);
            DateType tMin = new DateType(min);
            if (tMax.compareTo(tMin) < 0){
                String temp = max;
                max = min;
                min = temp;
            } 
        }
        maxValue = max;
        minValue = min;
        this.numbers = numbers;
        this.relations = new ArrayList();
        this.relations.addAll(relations);
        this.type = type;
        this.parameter = parameter;
        
//        if ("Date".equals(type)){
//            this.dateTest();
//        }
    }
    
    public void Generate(List<HSSFRow> sheet, int index){
        lastValue = 1;
        sheet.get(0).createCell(index).setCellValue(name);
        sheet.forEach((c) -> {
            if (c.getRowNum() != 0){
                try {
                    c.createCell(index).setCellValue(randomValue());
                } catch (TooMuchQuantityException ex) {
                    Logger.getLogger(Column.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
      
    }
    
    private String randomValue() throws TooMuchQuantityException{
        Random generator = new Random();
        String ret = "";
        while (!isCorrect(ret)){
            switch (type){
                case "Integer":
                {
                    if (parameter.equals("id")){
                        ret = lastValue.toString();
                        lastValue++; 
                    } else {
                        Integer temp = generator.nextInt(Integer.parseInt(maxValue) - Integer.parseInt(minValue)) + Integer.parseInt(minValue);
                        ret = temp.toString();
                    }
                    break;
                }
                case "Float":
                {
                    Float temp = generator.nextFloat() * Float.parseFloat(maxValue) + Float.parseFloat(minValue);             
                    ret = temp.toString();
                    break;
                }
                case "String":
                {
                    int length = generator.nextInt(Integer.parseInt(maxValue) - Integer.parseInt(minValue)) + Integer.parseInt(minValue);
                    for (int i = 0; i < length; i++){
                        int rand;
                        if (numbers == true){
                            int t = generator.nextInt(3);
                            System.out.println(t);
                            switch (t){
                                case NUMBERS:
                                {
                                    rand = generator.nextInt(10);
                                    rand += '0';
                                    break;
                                }
                                default:
                                {
                                    rand = generator.nextInt(26);
                                    switch (t){
                                        case LOWERCASE:
                                        {
                                            rand += 'a';
                                            break;
                                        }
                                        case UPPERCASE:
                                        {
                                            rand += 'A';
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        } else{
                            int t = generator.nextInt(2) + 1;
                            rand = generator.nextInt(26);
                            switch (t){
                                case LOWERCASE:
                                {
                                    rand += 'a';
                                    break;
                                }
                                case UPPERCASE:
                                {
                                    rand += 'A';
                                    break;
                                }
                            }      
                        }
                        char c = (char) rand;
                        ret += c;
                    }
                    break;
                }
                case "Date" :
                {
                    ret = getNextDate(new DateType(maxValue), new DateType(minValue), generator);
                    //System.out.println("max " + new DateType(maxValue).toString() + " min " + new DateType(minValue).toString());
                    break;
                }
            }
        }
        return ret;
    }
    
    private String getNextDate(DateType max, DateType min, Random generator){
        return min.getNextDate(generator.nextInt(max.getDifference(min))).toString();
    }
    
    @Override
    public String toString(){
        return "--- Kolumna ---\n" 
                + "nazwa: " + name + "\n"
                + "type: " + type + "\n"
                + "max value: " + maxValue + "\n"
                + "min value: " + minValue + "\n"
                + "numbers? " + numbers + "\n"
                + "parameter: " + parameter + "\n"
                + relations.toString();
    }
    
    private void dateTest(){
        DateType max = new DateType(maxValue);
        DateType min = new DateType(minValue);
        System.out.println("+++ date +++\n" + max.toString() + "\n" + min.toString());
    }
    
    private boolean isCorrect(String value) throws TooMuchQuantityException{
        if (value.equals("")){
            return false;
        } else {
            switch (parameter){
                case "id":
                {
                    return true;
                }
                case "inc":
                {
                    minValue = value;
                    if (minValue.equals(maxValue)){
                        throw new TooMuchQuantityException("inc");
                    }
                    return true;
                }
                case "dec":
                {
                    maxValue = value;
                    if (minValue.equals(maxValue)){
                        throw new TooMuchQuantityException("dec");
                    }
                    return true;
                }
                case "null":
                {
                    return true;
                }
            }
        }
        return false;
    }
}
