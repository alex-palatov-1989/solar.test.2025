package main.models;


import main.DTO;
import main.Model;

@Model
public class UserCreditial{
    @DTO      public String fstName="";    
    @DTO      public String sndName="";     
    @DTO      public String trdName="";     

    @DTO      public String birthDate = "";

    public UserCreditial( String fst, String snd, String trd, String birth){
        this.fstName = fst; this.sndName = snd;
        this.trdName = trd; this.birthDate= birth;
    }
    public UserCreditial(){}
}