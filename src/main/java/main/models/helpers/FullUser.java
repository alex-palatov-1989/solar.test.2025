package main.models.helpers;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;

import org.javatuples.Triplet;

import main.models.UserContact;
import main.models.UserCreditial;
import main.models.UserPhoto;

public class FullUser {
    private Triplet<UserContact, UserCreditial, UserPhoto> triple;
    public FullUser(UserContact contact, UserCreditial credits, UserPhoto photo)
    {
        triple = new Triplet<>(contact, credits, photo);
    }
    
    public static FullUser fromName( UserCreditial name ){
        return new FullUser(null, name, null);
    }
    public void addContact(UserContact contact){
        triple = triple.setAt0(contact);
    }
    public void addPhoto(UserPhoto photo){
        triple = triple.setAt2(photo);
    }

    public UserContact   gContact()     { return triple.getValue0(); }
    public UserCreditial gCreditial()   { return triple.getValue1(); }    
    public UserPhoto     gPhoto()       { return triple.getValue2(); }
    
    public  void    mark(int i){marked = i;}
    public  int     mark()     {return marked;}    
    public  void    setIsFar(Boolean f){ far = f; }
    public  Boolean far = false;
    private int     marked = 0;
    
    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public LocalDate getLocalDate(){
        LocalDate birthDate = LocalDate.now();
        try {
            birthDate = format.parse( getBirth() )
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        }   catch (Exception e) {
            e.printStackTrace();
        } 
        return  birthDate;
    }
    public long compareABS(LocalDate date) {             
        return Math.abs(getLocalDate().toEpochDay() - date.toEpochDay());
    }
    public String getBirth(){   int i = gCreditial().birthDate.length();
        return gCreditial().birthDate.substring(1, i-1);
    }
}

