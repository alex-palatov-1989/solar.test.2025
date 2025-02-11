package main.endpoints;

public class Util {

    static void logException(Exception e, StringBuilder log){
        if( e == null )return; if(e.getMessage() == null) return;
        log.append(e.getMessage());
    }
    
}
