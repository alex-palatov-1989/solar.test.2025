package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.stream.Stream;

import main.models.UserContact;
import main.models.UserCreditial;
import main.models.UserPhoto;
import main.models.helpers.FullUser;

public class UserRender implements AutoCloseable{

    @Override
    public void close() throws Exception {
        //throw new UnsupportedOperationException("Not supported yet.");
        //rd.close();
    }
    enum Tag{
        USERLIST        ("%USERLIST%"),
        USERFORM        ("%USERFORM%"),
        USER            ("%USER%"),
        ID              ("%ID%"),
        URL             ("%URL%"),

        NAME1           ("%NAME1%"),
        NAME2           ("%NAME2%"),
        NAME3           ("%NAME3%"),

        BIRTH           ("%BIRTH%"),

        CELL            ("%CELL%"),
        ADRESS          ("%ADRESS%"),
        EMAIL           ("%MAIL%"),
        
        PHOTO           ("%PHOTO%"),

        ISFAR           ("%ISFAR%"),
        ISPAST          ("%ISPAST%");

        String val;
        Tag( String t ){ val = t; };
    }

    static String hat=""; 
    static String mid_userform_end;
    static String usercardTemplate;
    static{
        String mid="";  String end="";
        try(BufferedReader read = new BufferedReader( new FileReader("html/userlist.html") )){                
            StringBuilder template= new StringBuilder();
            read.lines().forEach(
                line -> template.append(line).append("\n")
            );  
            String[] body = template.toString().split(Tag.USERLIST.val);    
            hat = body[0];
            mid = body[1].split(Tag.USERFORM.val)[0];   
            end = body[1].split(Tag.USERFORM.val)[1];

        }   catch (Exception e) { e.printStackTrace();
            System.out.println("crit err cant read main html template");
        } 
        try(BufferedReader read = new BufferedReader( new FileReader("html/userform.html") )){                
            StringBuilder userform= new StringBuilder();
            read.lines().map(
                line -> line.replace(Tag.URL.val, App.URL)
            ).forEach(
                line -> userform.append(line).append("\n")
            );  
            mid_userform_end = mid + userform.toString() + end;
        }   catch (Exception e) { e.printStackTrace();
            System.out.println("crit err cant read userform template");
        } 
        try(BufferedReader read = new BufferedReader( new FileReader("html/usercard.html") )){                
            StringBuilder usercard= new StringBuilder();
            read.lines().forEach(
                line -> usercard.append(line).append("\n")
            );  
            usercardTemplate   =  usercard.toString();
        }   catch (Exception e) { e.printStackTrace();
            System.out.println("crit err cant read usercard template");
        } 
    }
    final static Object mtx = new Object();
    public String renderList(String userlist)
    {
        synchronized (mtx) {
            return hat +userlist+ mid_userform_end;
        }
    }

    public String getUserHTML(Integer id, FullUser user){
        HashMap<Tag, String> map = new HashMap<>();
        map.put(Tag.ID , "\""+id.toString()+"\"");
        if(user.gCreditial()!=null){    UserCreditial data = user.gCreditial();
            map.put(Tag.NAME1, data.fstName);
            map.put(Tag.NAME2, data.sndName);
            map.put(Tag.NAME3, data.trdName);
            map.put(Tag.BIRTH, data.birthDate);
        }
        if(user.gContact()!=null){      UserContact data = user.gContact();
            map.put(Tag.CELL,   data  .phone);
            map.put(Tag.EMAIL,  data  .mail);
            map.put(Tag.ADRESS, data  .adress);
        }                
        if(user.gPhoto()!=null){        UserPhoto  data = user.gPhoto();
            map.put(Tag.PHOTO,  data  .path);
        }                
        map.put(Tag.URL,  "@"+App.URL+"@");

        if(user.far)
            map.put(Tag.ISFAR,  "% far%");
        else
            map.put(Tag.ISFAR,  "");

        if(user.mark() == 1)
            map.put(Tag.ISPAST, "% next%" );
        if(user.mark() ==-1)
            map.put(Tag.ISPAST, "% past%" );
        if(user.mark() == 0)
            map.put(Tag.ISPAST, "" );
        
    return readTags(map);
    }
    private String readTags(HashMap<Tag, String> map){
        StringBuilder ret = new StringBuilder();        
        Stream.of(usercardTemplate.split("\n"))
            .map(
                (line)->{
                    for( Tag tag : EnumSet.allOf (Tag.class))
                    {                        
                        String var = map.get(tag);
                        if ( var==null ) continue;   
                        if( var.length()==0 )var="";
                        else var = var.trim()
                            .substring(1,var.length()-1);
                        line = line.replace(    tag.val,   var    );                                         
                    }
                    return line;
                }
            )
            .forEach(   line->ret.append(line).append("\n")     );                    
        return ret.toString();
    }            
}
