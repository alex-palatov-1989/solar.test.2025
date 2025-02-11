package main.models;

import main.DTO;
import main.EType;
import main.Model;

@Model
public class UserPhoto{
    @DTO(type = EType.INT)
    public int idx = 0;   

    @DTO(type = EType.STR)
    public String path;

    public UserPhoto(int i, String path )
    {
        idx = i;    this.path=path;        
    }
    public UserPhoto(){}
}