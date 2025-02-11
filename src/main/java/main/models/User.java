package main.models;


import main.DTO;
import main.EType;
import main.Model;

@Model
public class User{
    @DTO(type = EType.INT)
    public int idx = 0;    

    public User(Integer i){ idx = i; }
    public User(){ idx=0; }    
}