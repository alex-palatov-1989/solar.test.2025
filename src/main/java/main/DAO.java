package main;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


abstract class baseDAO {
    abstract String toJSON(Object value);
    abstract Object fromJSON(String json);
}

public class DAO <T> extends baseDAO{
    private Class<T> ThisClass;
    private Type type;

    public DAO(){                         
        try {
            Type superClass = getClass().getGenericSuperclass();            
            type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
            String qualName = type.getTypeName();

            Class ths = (Class<T>) Class.forName(qualName);
            ThisClass = (Class<T>) Class.forName(ths.getCanonicalName());

        } catch (Exception e) {
            e.printStackTrace();
        }                
    }
    public String toJSON(Object value){       
        
        Map<String , Field> arr = new HashMap();        
        Arrays.asList( ThisClass.getDeclaredFields() )
            .stream().filter(
                (e)->{ return e.isAnnotationPresent(DTO.class); }
            ).forEach(
                (e)->{ arr.put(e.getName(), e); /*System.out.println(e.getName());*/}
            );                            
        
        StringBuilder template = new StringBuilder();
        template.append('{');

        String name; Field field;
        for( Entry<String, Field>  tuple : arr.entrySet() ) {                        
            DTO an = tuple.getValue().getAnnotation(DTO.class);  
            name = tuple.getKey(); field = tuple.getValue();
            try {
                switch( an.type() ){
                    case STR:                
                        template.append(String.format
                            (str, name, field.get(value))
                        );
                        break;
                    case INT:
                        template.append(String.format
                            (inr, name, field.get(value))
                        );
                        break;
                    case FLOAT:
                        template.append(String.format
                            (flt, name, field.get(value))
                        );
                        break;             
                    case ENUM:
                        template.append(String.format
                            (str, name, field.getType().getName(),
                                field.get(value).toString().toUpperCase())
                        );                    
                        break;
                    default:
                        break;
                }   
            }   
            catch (Exception e) {   e.printStackTrace();    }                      
        }
        try {
            template.replace(template.length()-2, template.length(), "}");
        } catch (Exception e) {
            template = new StringBuilder("");
        }        
    return template.toString(); 
    }
    final static String str =  new String( " \"%s\" : \"%s\" , " );
    final static String enm =  new String( " \"%s\" : \"%s.%s\" , " );
    final static String inr =  new String( " \"%s\" : %d , " );        
    final static String flt =  new String( " \"%s\" : %f, " );         
    
    public Object fromJSON(String json){
        T value;         //System.out.println(json);
        try {         
            value = ThisClass.newInstance();

            json = json.split("\\{")[1].split("\\}")[0];
            Arrays.asList(json.split(", ")).stream().forEach(
                (e)->{ 
                    String name = e.split(":")[0];
                    name = name.trim().substring(1, name.trim().length()-1);
                    for (Field f : ThisClass.getDeclaredFields()){
                        try {
                            if(
                                f.getName().equals(name)
                            ){
                                String val  = e.split(":")[1];
                                switch(f.getAnnotation(DTO.class).type())
                                {
                                    case STR:
                                        String str = ((String)val).trim();
                                        f.set(value,  str);                                        
                                        break;
                                    case INT:
                                        String intstr = val .trim();
                                        f.set(value, Integer.valueOf( intstr ));                                        
                                        break;
                                    case FLOAT:
                                        String fltstr = val .trim();
                                        f.set(value, Float.valueOf(fltstr));                                        
                                        break;
                                    case ENUM:
                                        String enmType = val .trim().split(".")[0];  
                                        String enmEntr = val .trim().split(".")[1];                                          
                                        Object enmSet  =  Class.forName(enmType)
                                            .getMethod("valueOf", String.class)
                                            .invoke( (Class<Enum>) f.getType(), enmEntr);
                                        f.set(value, enmSet);                                        
                                        break;
                                }                                
                            }   
                        } catch (Exception err) {
                            //err.printStackTrace();
                        }
                    }                
                }
            );   
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        return (T) value;
    }
}
