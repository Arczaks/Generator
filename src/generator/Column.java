/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

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
    private List<String> possibleValues;
 
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
        if (this.relations.isEmpty()){
            this.relations.add(new Relation());
        }
        this.type = type;
        this.parameter = parameter;
        
        if (this.relations.get(0).getType() == Relation.Type.ONE_OF){
            possibleValues = new ArrayList();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(new File(relations.get(0).getDir())));
                try {
                    String line = null;
                    while ((line = reader.readLine()) != null){
                        possibleValues.add(line);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Column.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Column.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void Generate(List<HSSFRow> sheet, int index){
        lastValue = 1;
        sheet.get(0).createCell(index).setCellValue(name);
        sheet.forEach((c) -> {
            if (c.getRowNum() != 0){
                try {
                    c.createCell(index).setCellValue(randomValue(c.getRowNum(), sheet.get(0).getSheet()));
                } catch (TooMuchQuantityException ex) {
                    Logger.getLogger(Column.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
      
    }
    
    private String randomValue(int index, HSSFSheet sheet) throws TooMuchQuantityException{
        String ret = "";
        Random generator = new Random();
        if (!(relations.get(0).getType() == Relation.Type.IS)) {
            if (!(relations.get(0).getType() == Relation.Type.ONE_OF)){
                while (!isCorrect(ret, index, sheet)){
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
                            Float temp = generator.nextFloat() *  ( Float.parseFloat(maxValue) - Float.parseFloat(minValue)) + Float.parseFloat(minValue);             
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
            } else {
                ret = possibleValues.get(generator.nextInt(possibleValues.size()));
            }
        } else { 
            InputStream inp = null;
            try {
                inp = new FileInputStream(relations.get(0).getDir());
                try {
                    HSSFWorkbook wb = new HSSFWorkbook(inp);
                    HSSFSheet sheet_temp = wb.getSheetAt(0);
                    int columnIndex = 0;
                    for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
                        //System.out.println(" sheet " + sheet.getRow(0).getCell(i).getStringCellValue() + " rel " + relations.get(0).getElement());
                        if (sheet.getRow(0).getCell(i).getStringCellValue().equals(relations.get(0).getElement())){
                            columnIndex = i;
                            break;
                        }
                    }
                    ret = sheet.getRow(index).getCell(columnIndex).getStringCellValue();
                } catch (IOException ex) {
                    Logger.getLogger(Column.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Column.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    inp.close();
                } catch (IOException ex) {
                    Logger.getLogger(Column.class.getName()).log(Level.SEVERE, null, ex);
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
    
    private boolean isCorrect(String value, int index, HSSFSheet sheet) throws TooMuchQuantityException{
        boolean ret = false;
        if (value.equals("")){
            return false;
        } else {
            switch (parameter){
                case "id":
                {
                    ret = true;
                    break;
                }
                case "inc":
                {
                    String temp = minValue;
                    minValue = value;
                    if (minValue.equals(maxValue)){
                        minValue = temp;
                    }
                    ret = true;
                    break;
                }
                case "dec":
                {
                    String temp = maxValue;
                    maxValue = value;
                    if (minValue.equals(maxValue)){
                        maxValue = temp;
                    }
                    ret = true;
                    break;
                }
                case "null":
                {
                    ret = true;
                    break;
                }
            }
            
        }
        if (ret){
            if (relations.get(0).getType() == Relation.Type.GREATER || relations.get(0).getType() == Relation.Type.LESS){
                InputStream inp = null;
                try {
                    if (!"HERE".equals(relations.get(0).getDir())) {
                        inp = new FileInputStream(relations.get(0).getDir());
                    }
                    try {
                        HSSFWorkbook wb;
                        HSSFSheet sheet_temp;
                        if (inp == null){
                            sheet_temp = sheet;
                        } else {
                            wb = new HSSFWorkbook(inp); 
                            sheet_temp = wb.getSheetAt(0);
                        }
 
                        int columnIndex = 0;
                        for (int i = 0; i < sheet_temp.getRow(0).getLastCellNum(); i++) {
                            //System.out.println(" sheet " + sheet.getRow(0).getCell(i).getStringCellValue() + " rel " + relations.get(0).getElement());
                            if (sheet_temp.getRow(0).getCell(i).getStringCellValue().equals(relations.get(0).getElement())){
                                columnIndex = i;
                                break;
                            }
                        }
                        
                        switch (this.type){
                            case "Integer":
                            {
                                if (relations.get(0).getType() == Relation.Type.GREATER){
                                    ret = Integer.parseInt(sheet_temp
                                            .getRow(index)
                                            .getCell(columnIndex)
                                            .getStringCellValue()) <= Integer.parseInt(value);
                                } else {
                                    if (relations.get(0).getType() == Relation.Type.LESS){
                                        ret = Integer.parseInt(sheet_temp
                                                .getRow(index)
                                                .getCell(columnIndex)
                                                .getStringCellValue()) >= Integer.parseInt(value);
                                    }
                                }
                                break;
                            }
                            case "Float":
                            {
                                if (relations.get(0).getType() == Relation.Type.GREATER){
                                    ret = Float.parseFloat(sheet_temp
                                            .getRow(index)
                                            .getCell(columnIndex)
                                            .getStringCellValue()) <= Float.parseFloat(value);
                                } else {
                                    if (relations.get(0).getType() == Relation.Type.LESS){
                                        ret = Float.parseFloat(sheet_temp
                                                .getRow(index)
                                                .getCell(columnIndex)
                                                .getStringCellValue()) >= Float.parseFloat(value);
                                    }
                                }
                                break;
                            }
                            case "Date":
                            {          
                                if (relations.get(0).getType() == Relation.Type.GREATER){
                                    ret = new DateType(sheet_temp
                                            .getRow(index)
                                            .getCell(columnIndex)
                                            .getStringCellValue()).compareTo(new DateType(value)) < 0;
                                } else {
                                    if (relations.get(0).getType() == Relation.Type.LESS){
                                        ret = new DateType(sheet_temp
                                                .getRow(index)
                                                .getCell(columnIndex)
                                                .getStringCellValue()).compareTo(new DateType(value)) > 0;
                                    }
                                }
                                break;
                            }
                        }
   
                    } catch (IOException ex) {
                        Logger.getLogger(Column.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Column.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        if (inp != null){
                            inp.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Column.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return ret;
    }
}
