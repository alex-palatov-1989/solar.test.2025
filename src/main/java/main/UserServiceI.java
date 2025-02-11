package main;

import java.io.IOException;
import java.util.List;

import main.models.UserContact;
import main.models.UserCreditial;
import main.models.UserPhoto;

public interface UserServiceI {
///////////////////////////////////////////////////////////////////////////////////////
    public Integer addUser(StringBuilder log)    throws Exception;
    public Integer getUser(Integer id)           throws Exception;
    public List< String > getAllUsers()          throws Exception;
///////////////////////////////////////////////////////////////////////////////////////
    public Exception addContacts(int idx, UserContact data,     StringBuilder log);
    public Exception addCredits (int idx, UserCreditial data,   StringBuilder log);
    public Exception addPhoto   (int idx, UserPhoto path,       StringBuilder log);
///////////////////////////////////////////////////////////////////////////////////////
    public UserContact      getContacts         (int idx);    
    public UserCreditial    getCreditial        (int idx);
    public UserPhoto        getPhoto            (int idx);
///////////////////////////////////////////////////////////////////////////////////////
    public Integer          getUserCount        ();
    public void         delContacts         (int idx) throws IOException ;    
    public void         delCreditial        (int idx) throws IOException ;
    public void         delPhoto            (int idx) throws IOException ;
    public void         delUser             (int idx) throws Exception ;
}   

