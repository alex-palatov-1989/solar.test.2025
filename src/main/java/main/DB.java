package main;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;

import org.lmdbjava.Dbi;
import static org.lmdbjava.DbiFlags.MDB_CREATE;
import org.lmdbjava.Env;
import org.lmdbjava.Txn;


public class DB {

    private String      dbName = "microstorage";
    private String      dbDirectoryName =  "db";    
    private Dbi<ByteBuffer> db;
    private Env env;
    final Integer max_size = 640;

    public DB() {
        File path = new File(
            dbDirectoryName+"/"+_path
        );
        if( !path.exists() )try {
            Files.createDirectory(
                path.toPath()
            );            
            //System.out.println(path.toPath());

        } catch (IOException e) {
            System.out.println("critical err cant create db dir");            
            System.out.println(e.getMessage());
        }
            //System.out.println(path.toPath().toAbsolutePath());
        try {
            env = Env.create().open(path);
            db  = env.openDbi(dbName, MDB_CREATE);   
        } catch (Exception e) {
            System.out.println("critical err cant create open db"); 
            System.out.println(e.getMessage());
        }        
    }        

    private ByteBuffer getBuffer(String value) throws IOException{
        if( value.length() > max_size) throw new IOException(
            "max size set: "+max_size.toString());        
        ByteBuffer buf = allocateDirect(value.length());
        buf.put(   value.getBytes(UTF_8)   );
        return (ByteBuffer) buf.flip();
    }
    private ByteBuffer getBuffer(long value) throws IOException{     
        String val = Long.toUnsignedString(value);
        ByteBuffer buf = allocateDirect(val.length());
        buf.put( val.getBytes(UTF_8) );
        return (ByteBuffer) buf.flip();
    }    

    public <T extends String> Exception put(String _key, T _val){               
        synchronized(mtx){
            if( _key==null || _key.length()==0 ){ 
                return new IOException("key is null"); 
            }
            if( _val==null || _val.length()==0 ){ 
                return new IOException("val is null"); 
            }
            try {            
                db.put( getBuffer(    _key), getBuffer(_val) );
                db.put( getBuffer("~"+_key), getBuffer(_val.hashCode()));            
            } catch (Exception e) {
                e.printStackTrace();
                return e;
            }        
            return null;
        }
    }
    public <T extends String> T get(String _key)    
    {        
        synchronized(mtx){
            Txn<ByteBuffer> rtx = env.txnRead();         
            String val;  ByteBuffer buf;
            try {
                ByteBuffer key = getBuffer(    _key);
                ByteBuffer hsh = getBuffer("~"+_key);
                buf = db.get(rtx, key);    
                if( buf == null )return null;
                
                val = UTF_8.decode(buf).toString();
                buf = db.get(rtx, hsh);    
                
                long crc = Long.parseUnsignedLong(UTF_8.decode(buf).toString());
                if( crc != val.hashCode() )
                {                
                    System.out.println("deleting corrupted data for "+_key);
                    db.delete(rtx, getBuffer(_key)); return (T)"null";
                }
            }   catch (Exception e) {
                e.printStackTrace();    return (T)"null";            
            }    
            finally{ rtx.commit(); }
            if(val instanceof String)   return (T)val;
            else return (T)"null";
        }        
    }
    public void delete(String key) throws IOException {
        synchronized(mtx){
            if( !db.delete( getBuffer(key) ) )
            throw new IOException(key+" not found");
        }
    }
    public void replace(String src, String dst) throws IOException{
        synchronized (mtx) {
            put(dst, get(src));
        }
    }

    static Boolean ready(){
        synchronized (mtx) {
            return _ready;
        }
    }
    static Boolean path(String p){ 
        synchronized (mtx) {
            return _path.contains(p); 
        }
    };

    public static void close(){   
        synchronized (mtx) {
            if(_inst != null && _inst.env != null)
            {  
                _ready = false;  _inst.env.close();                
            }
        }
    }
    public static DB repo(Class path){
        synchronized (mtx) {
            if(!path(path.getName())){
                close(); _ready = false; _path=path.getName();
            }
    
            if(ready()) return _inst; 
            else { 
                _inst = new DB(); _ready = true; return _inst;
            }
        }
    }
    final static Object mtx = new Object();    
    static private String   _path   = "";
    static private Boolean  _ready  = false;
    static private DB       _inst  = null;
}


