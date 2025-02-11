package main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;


public class DTOservice {
    
    static boolean    _ready; 
    static DTOservice _instance;
    static{
        instance();
    }
    
    synchronized static Boolean    ready()   { return _ready; }
    synchronized static DTOservice instance(){ 
        if( !ready() ){
            _instance = new DTOservice();    
            _instance.init();
            _ready = true;
        }
        return _instance; 
    }    
    private HashMap<Integer, Class> subs;
    private void init(){
        HashMap<Integer, Class> subs = new HashMap<>();

        for(Class clazz : findModels("main.models")){                        
            try {
                String name = clazz.getSimpleName();
                Class dao = Class.forName( 
                    String.format("main.dataclasses.%sDAO", name)
                );

                subs.put( clazz.hashCode(), dao);
            } catch (Exception e) {                
                e.printStackTrace();
            }            
        }
        this.subs = subs;
    }    
    public
    synchronized static baseDAO getServe(Integer search){        
        try {
            Class clazz = instance().subs.get(search);        
            baseDAO ret = (baseDAO)clazz.newInstance();            
            return ret;            
        } catch (Exception e) { e.printStackTrace();
        }   System.out.println("cant find sub service dao for:"+search);
        return new DAO<Object>();
    }

    public
    static <T> T deserialize(String json, T obj){ 
        try {
            Integer hsh = obj.getClass().hashCode();
            baseDAO tmp = getServe(hsh);
            
            obj = (T)tmp.fromJSON(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null; 
        }
        return obj;
    }
    public
    static <T> String serialize(T obj, String err){ 
        String json = new String();
        try {
            Integer hsh = obj.getClass().hashCode();
            baseDAO tmp = getServe(hsh);
                json    = tmp.toJSON(obj);

        } catch (Exception e) {
            if( e.getMessage() != null)
            err.concat(e.getMessage());
            return null; 
        }
        return json;
    }        

    private Set<Class> findModels(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
            .getResourceAsStream(packageName.replaceAll("[.]", "/"));            
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        return  reader.lines()
            .filter(line -> line.endsWith(".class"))
            .map(
                line -> getClass(line, packageName)                
            )
            .filter( type-> type.isAnnotationPresent(Model.class) )
            .collect(Collectors.toSet());
    }
    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}

/*
 *     synchronized static void registerClass(Class dao, Class data){
        int hsh;
        try {
            hsh = data.hashCode();
            instance().subs.put(hsh, (baseDAO)dao.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
 */

     /*
    private class JSONwrap<T extends String>{
        private String data = new String();
        public String read (){ return data; }
        public void   write(String jsn){ 
            if(jsn == null) jsn = "";
            data = (String)new String(jsn);
        }
    }
    public class JSON   extends     JSONwrap<String>{};    
    public
    static JSON newDTO(){ return instance().new JSON(); }
    */