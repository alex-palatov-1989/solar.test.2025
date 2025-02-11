package main.models;

import main.DTO;
import main.Model;

public @Model
class UserContact{
    @DTO      public String phone   ="";    
    @DTO      public String mail    ="";     
    @DTO      public String adress  ="";     

    public UserContact( String phone, String mail, String adress)
    {
        this.phone = phone; this.mail = mail; this.adress = adress;
    }
    public UserContact(){}
}