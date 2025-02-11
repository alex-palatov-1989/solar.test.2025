package main.dataclasses;

import java.io.IOException;

import main.DAO;
import main.DB;
import main.DTOservice;
import main.models.UserPhoto;

public
class UserPhotoDAO extends DAO<UserPhoto>{
    final static Class dataclass = UserPhoto.class;

    private String dto = new String();//DTOservice.newDTO();    
    public UserPhotoDAO(){
        super();
    }
    UserPhoto deflt = new UserPhoto();

    public Exception addPhoto(int id, UserPhoto photo, StringBuilder log){

        String err = "";    
        dto = DTOservice.serialize(photo, err);
        log.append(err).append("\n>_");

        Exception ret = DB.repo(dataclass)
            .put(  String.valueOf(id),  dto   );
        //DB.close();
        return ret;
    }
    public UserPhoto getPhoto(int id){
        dto = DB.repo( dataclass )
            .get(   String.valueOf(id) ) ;

        //DB.close();
        return DTOservice.deserialize(dto, deflt);
    }
    public void deletePhoto(int id) throws IOException{
        DB.repo(dataclass).put( String.valueOf(id), dto );      //todo bag cause dont found key
        //DB.repo( dataclass ).delete(   String.valueOf(id)  );
        //DB.close();        
    }
}