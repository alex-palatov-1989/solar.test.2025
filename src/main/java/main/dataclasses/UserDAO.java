package main.dataclasses;

import java.util.ArrayList;
import java.util.List;

import main.DAO;
import main.DB;
import main.DTOservice;
import main.models.User;

public class UserDAO extends DAO<User>{
    final static Class dataclass = User.class;    

    private String dto = new String();//DTOservice.newDTO();    
    public UserDAO(){
        super();     
    }
    static int _length = 0;
    static synchronized int length(){ return _length; }
    static synchronized void length(int i){_length=i; }
    User dflt = new User();

    public ArrayList<String> getCount(){
        int idx = 0;
        String temp;
        ArrayList<String> data  = new ArrayList<>();
        try {
         
        while (true) {             
            temp = DB.repo(dataclass)
                .get( String.valueOf(++idx) );
            
            if( temp == null ) break;
            else data.add(temp);
        }                    
        } catch (Exception e) { e.printStackTrace();
        } finally { DB.close();
        }
        
        length( idx-1 );
        return  data;
    }
    public List<String> getAllUsers(){
        return getCount();
    }

    public Integer createUser(StringBuilder log) throws Exception{
        getCount();
        Exception exc = null;
        User user = new User();
        try {
            length( length()+1 );
            user = new User( length() );

            String err="";
            dto = DTOservice.serialize(user, err);
            log.append(err).append(">_");

            exc = DB.repo(dataclass)
                .put(   String.valueOf(user.idx), dto   );        

        } catch (Exception e) { e.printStackTrace();
        } finally { DB.close();
        }

        if (exc == null) return user.idx;
        else throw exc;
    }
    public Integer findUser(int id) throws Exception{        
        
        String ret = DB.repo( dataclass )
            .get( String.valueOf(id) );        

        //DB.close();   
        if( ret == null )  return 0;
        else return id;        
    }
    public void deleteUser(int id) throws Exception{        
        DB.repo( dataclass )
            .delete( String.valueOf(id) );        

        //DB.close();     
        length( length()-1 );
    }
}