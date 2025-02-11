package main.dataclasses;

import java.io.IOException;

import main.DAO;
import main.DB;
import main.DTOservice;
import main.models.UserCreditial;


public class UserCreditialDAO extends DAO<UserCreditial>{
    final static Class dataclass = UserCreditial.class;  

    private String dto = new String();//DTOservice.newDTO();    
    public UserCreditialDAO(){
        super();     
    }
    UserCreditial deflt = new UserCreditial();
    
    public Exception addCredits(int id,  UserCreditial data, StringBuilder log){   
        String err = "";    
        dto = DTOservice.serialize(data, err);
        log.append(err).append("\n>_");

        Exception ret = DB.repo(dataclass)
            .put( String.valueOf(id),  dto
        );

        //DB.close();
        return ret;
    }
    public UserCreditial getUserCredits(int id){
        dto = DB.repo(UserCreditial.class)
            .get(   String.valueOf(id)  );
        DB.close();
        return DTOservice.deserialize(dto, deflt);
    }
    public void deleteUserCredits(int id) throws IOException{
        DB.repo(UserCreditial.class)
            .delete(   String.valueOf(id)  );
        //DB.close();        
    }
}