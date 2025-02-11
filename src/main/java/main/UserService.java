package main;

import java.io.IOException;
import java.util.List;

import main.dataclasses.UserContactDAO;
import main.dataclasses.UserCreditialDAO;
import main.dataclasses.UserDAO;
import main.dataclasses.UserPhotoDAO;
import main.models.User;
import main.models.UserContact;
import main.models.UserCreditial;
import main.models.UserPhoto;

public class UserService implements UserServiceI{
///////////////////////////////////////////////////////////////////////////////////////    
    private Class   user    = User.class;
    private Class   contact = UserContact.class;
    private Class   credits = UserCreditial.class;
    private Class   photo   = UserPhoto.class;
///////////////////////////////////////////////////////////////////////////////////////
/// 
/// 
/// 
///////////////////////////////////////////////////////////////////////////////////////
    @Override public Integer addUser(StringBuilder log) throws Exception{

        return ((UserDAO)DTOservice.getServe(user.hashCode()))
            .createUser(log);      
    }
    @Override public Integer getUser(Integer id) throws Exception{

        return ((UserDAO)DTOservice.getServe(user.hashCode()))
            .findUser( id );      
    }
    @Override public List< String > getAllUsers() throws Exception{
        return ((UserDAO)DTOservice.getServe(user.hashCode()))
            .getAllUsers();
       
    }
    @Override public Integer getUserCount(){
        return ((UserDAO)DTOservice.getServe(user.hashCode()))
            .getCount().size();      
    }
///////////////////////////////////////////////////////////////////////////////////////
    @Override public Exception addContacts(int idx, UserContact data, StringBuilder log){
        return 
        ((UserContactDAO)DTOservice.getServe(contact.hashCode()))
            .addUserData(
                idx,    data,   log
            );
    }
    @Override public Exception addCredits(int idx, UserCreditial data, StringBuilder log){
        return 
        ((UserCreditialDAO)DTOservice.getServe(credits.hashCode()))
            .addCredits(
                idx,    data,   log
            );
    }
    @Override public Exception addPhoto(int idx, UserPhoto path,StringBuilder log){      
        return 
        ((UserPhotoDAO)DTOservice.getServe(photo.hashCode()))
            .addPhoto(
                idx,    path,   log
        );
    }
///////////////////////////////////////////////////////////////////////////////////////
    @Override public UserContact getContacts    (int idx){
        return 
        ((UserContactDAO)DTOservice.getServe(contact.hashCode()))
            .getUserContact(idx);
    }
    @Override public UserCreditial getCreditial   (int idx){
        return 
        ((UserCreditialDAO)DTOservice.getServe(credits.hashCode()))
            .getUserCredits(idx);
    }
    @Override public UserPhoto getPhoto       (int idx){        
        return 
        ((UserPhotoDAO)DTOservice.getServe(photo.hashCode()))
            .getPhoto(idx);
    }
///////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void delContacts(int idx)  throws IOException {
        ((UserContactDAO)DTOservice.getServe(contact.hashCode()))
            .deleteUserContact(idx);    
    }

    @Override
    public void delCreditial(int idx)  throws IOException {
        ((UserCreditialDAO)DTOservice.getServe(credits.hashCode()))
            .deleteUserCredits(idx);   
    }

    @Override
    public void delPhoto(int idx) throws IOException {         
        ((UserPhotoDAO)DTOservice.getServe(photo.hashCode()))
            .deletePhoto(idx);  
    }

    @Override
    public void delUser(int idx) throws Exception {
        ((UserDAO)DTOservice.getServe(user.hashCode()))
            .deleteUser(idx);
    }
///////////////////////////////////////////////////////////////////////////////////////
}
