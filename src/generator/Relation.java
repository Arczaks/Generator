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
public class Relation {
    
    public enum Type{
        GREATER, LESS, IS, ONE_OF;
    }
    
    private final String element;
    
    private final String dir;
    
    private final Type type;
    
    public Relation(){
        type = null;
        element = null;
        dir = null;
    }
   
    public Relation(String type, String element, String path){
        this.element = element;
        this.type = Type.valueOf(type);
        dir = path;
    }
    
    @Override
    public String toString(){
        return "- Relacja -\n"
                + "typ: "+ type  + "\n"
                + "dir: "  + dir + "\n"
                + "element: " + element; 
    }
    
    public String getElement(){
        return element;   
    }
    
    public String getDir() {
        return dir;
    }
    
    public Type getType() {
        return type;
    }
}
