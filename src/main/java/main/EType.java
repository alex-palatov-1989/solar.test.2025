package main;


public enum EType{
    STR("STR"), INT("INT"), 
    FLOAT("FLOAT"), ENUM("ENUM");

    String id;
    EType(String i){id = i;};
}
