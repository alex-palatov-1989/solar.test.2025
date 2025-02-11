package main.dataclasses;

import java.io.IOException;

import main.DAO;
import main.DB;
import main.DTOservice;
import main.models.UserContact;

public
class UserContactDAO extends DAO<UserContact>{
    final  static Class dataclass = UserContact.class;
   

    private UserContact deflt = new UserContact();    
    private String dto = new String();//DTOservice.newDTO();    
    public UserContactDAO(){
        super();     
    }    
    public Exception addUserData(   int id,     UserContact data,   StringBuilder log){        

        String err = "";    
        dto = DTOservice.serialize(data, err);
        log.append(err).append("\n>_");
        
        Exception ret = DB.repo(dataclass)
            .put( String.valueOf(id),  dto
        );
        //DB.close();
        return ret;
    }
    public UserContact getUserContact(int id){
        dto = DB.repo( dataclass ).get(   String.valueOf(id)  );
        DB.close();
        return DTOservice.deserialize(dto, deflt);
    }
    public void deleteUserContact(int id) throws IOException{
        DB.repo( dataclass )
            .delete(   String.valueOf(id)  );
        //DB.close();        
    }
}