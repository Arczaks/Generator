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
public class TooMuchQuantityException extends Exception{
   
    private final String msg;
    
    public TooMuchQuantityException (String msg){
        this.msg = msg;
    }
    
    @Override
    public String getMessage(){
       return ("\ntoo much Quantity\n" + msg);
   } 
}
