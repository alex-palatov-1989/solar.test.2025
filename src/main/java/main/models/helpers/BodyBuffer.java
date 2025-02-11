package main.models.helpers;

import main.DTOservice;

public class BodyBuffer {
    StringBuilder buffer = new StringBuilder();

    public StringBuilder get(){ return buffer; }
    public <T> void addProp(String name, T obj, StringBuilder logger){
        if( obj == null)    System.out.println(name +" is null!\n\n\n");
        
        String err = "";
        String json = DTOservice.serialize(obj, err);        
        logger.append(err);

        if(buffer.length()!=0)buffer.append(",");
        buffer.append(
          String.format(" '%s'' :{%s}", name, json)  
        );
    }
    public String toRes(){
        return String.format("{%s}", buffer.toString());
    }
    public String toArr(){
        return String.format("[%s]", buffer.toString());
    }    
    String fstKey = "username";
    String sndKey = "contacts";
    String trdKey = "imgpath";
    public <T,K,N> void appendArray( T fst, K snd, N trd ){
        if(buffer.length()>1)buffer.append(",");
        String err ="ignoreErr";

        try {         
            buffer.append("{")
                .append(String.format(" \"%s\" :{%s}", fstKey, DTOservice.serialize(fst, err) ))
                .append(String.format(" \"%s\" :{%s}", sndKey, DTOservice.serialize(snd, err) ))
                .append(String.format(" \"%s\" :{%s}", trdKey, DTOservice.serialize(trd, err) ))
            .append("}");   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
