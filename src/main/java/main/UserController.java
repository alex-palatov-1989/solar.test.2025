package main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import main.models.UserContact;
import main.models.UserCreditial;
import main.models.UserPhoto;
import main.models.helpers.FullUser;

public class UserController implements UserServiceI{

    final private UserService service;
    public UserController(UserService serviceI){
        this.service = serviceI;
    }    
    public Exception addContacts    
    (int idx, 
        String phone,
        String mail,
        String adress,
    StringBuilder log) 
    {
        return service.addContacts  (idx, new UserContact(phone, mail, adress), log);        
    }

    public Exception addCredits     
    (int idx, 
        String fstName,
        String sndName,
        String trdName,
        String birthDate,
    StringBuilder log) 
    {
        return service.addCredits   (idx, 
        new UserCreditial( fstName, sndName, trdName, birthDate), 
        log);        
    }

    public Exception newContacts   (int idx, StringBuilder  log) {
        return service.addContacts  (idx, new UserContact(), log);        
    }
    public Exception newCredits    (int idx, StringBuilder    log) {
        return service.addCredits   (idx, new UserCreditial(), log);        
    }
    public Exception newPhoto      (int idx, StringBuilder log) {
        return service.addPhoto     (idx, new UserPhoto(idx, main.App.dfltPhoto), log);   
    } 

    public Exception getUserIndex  (StringBuilder html, StringBuilder log) {
        int length = getUserCount();
        HashMap<Integer, FullUser> list = new HashMap<>();

        for( int i=1; i<=length; i++ )
            list.put(   i,  FullUser.fromName( getCreditial(i) )  );  

        LocalDate today = LocalDate.now();  
        LocalDate pastWeek = today.minusDays(7); 
        LocalDate nextWeek = today.plusDays(7);
        List<Integer> sortedKeys = list.entrySet()
            .stream()
            .map(entry -> {                                         
                LocalDate birthDate = entry.getValue().getLocalDate();
                FullUser user = entry.getValue();

                //mark past and nex birthdays                    
                if (birthDate.isAfter(pastWeek) && birthDate.isBefore(today)) 
                    user.mark(-1);
                
                if (birthDate.isAfter(today) && birthDate.isBefore(nextWeek)) 
                    user.mark(1);                                                                                        
            
                //mark far birthdays
                user.setIsFar( birthDate.isBefore(pastWeek) || birthDate.isAfter(nextWeek) );
                return entry;
            })
            .sorted(
                Comparator.comparing(
                    entry -> ((Entry<Integer, FullUser>)entry).getValue().compareABS(today)
                )
            )
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        list.forEach( (k,v)->{ v.addContact (getContacts(k)); } );System.gc();                
        list.forEach( (k,v)->{ v.addPhoto   (getPhoto(k));    } );System.gc();
        
        StringBuilder   template= new StringBuilder();                                                            
        try(UserRender render = new UserRender())
        {
            synchronized (this) {
                sortedKeys.forEach( (i)->{
                    try {
                        template.append(render.getUserHTML( i, list.get(i) ));
                    }   catch (Exception e){
                        e.printStackTrace();
                        if(e.getMessage()!=null)log.append(e.getMessage());                                
                    }                              
                } );   
                html.append(render.renderList(template.toString()));                  
            }                
        } catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage()!=null)
                log.append(e.getMessage());                                
            return e;
        }
        return null;
    }
    public Exception deleUserData(int id, StringBuilder log){                                    
        try {
            synchronized (this) {
                String    path = getPhoto(id).path;
                File      photo= new File("userupload/"+path.substring(1, path.length()-1));
                if( !path.contains(App.dfltPhoto)  )
                    photo.delete();
                
                int sz = getUserCount();

                String src = String.valueOf( sz );
                String dst = String.valueOf( id );
                if( id!=sz )
                {
                    ////////  keys operation  ////////
                    DB.repo(UserContact     .class).replace(src, dst);
                    DB.repo(UserCreditial   .class).replace(src, dst);
                    DB.repo(UserPhoto       .class).replace(src, dst);                   
                    ////////    DAOs bypass   ////////
                }                 
                delUser(sz);    delCreditial(sz);   delContacts(sz);    delPhoto(sz);
                int i = getUserCount();
                
                return null;                
                }                
            } catch (Exception e) {     
                e.printStackTrace();
                return e;
        }                        
    }
    public Exception uploadPhoto(int id, InputStream image, String ext, StringBuilder log){
        synchronized (this) {
            String uri; Exception err = null;
            try {                    
                String    path = getPhoto(id).path;                
                if( path.contains(App.dfltPhoto) )
                {
                    uri = UUID.randomUUID().toString().replace("-", "");
                    uri = uri.concat(ext);
                    err = addPhoto(id, new UserPhoto(id, uri), log);
                }   else uri = path.substring(1, path.length()-1);
                
                File file = new File("userupload/"+uri);                
                Files.copy( 
                    image, file.toPath(), 
                    StandardCopyOption.REPLACE_EXISTING 
                );

                System.out.println( id );

            } catch (Exception e) { e.printStackTrace();
                if( e.getMessage()!=null)
                log.append( e.getMessage() );
                return e;
            }
            return err;
        }        
    }
    

    @Override public Integer getUserCount(){
        return service.getUserCount();
    }    

    @Override public Exception addContacts    (int idx, UserContact data, StringBuilder log) {
        return service.addContacts  (idx, data, log);        
    }
    
    @Override public Exception addCredits     (int idx, UserCreditial data, StringBuilder log) {
        return service.addCredits   (idx, data, log);        
    }
    
    @Override public Exception addPhoto    (int idx, UserPhoto path, StringBuilder log) {
        return service.addPhoto  (idx, path, log);   
    }
    
    @Override public Integer addUser(StringBuilder log) throws Exception {
        return service.addUser(log);        
    }
    
    @Override public List< String > getAllUsers() throws Exception {
        return service.getAllUsers();        
    }
    
    @Override public UserContact getContacts(int idx) {
        return service.getContacts(idx);
        
    }
    
    @Override public UserCreditial getCreditial(int idx) {
        return service.getCreditial(idx);
        
    }
    
    @Override public UserPhoto getPhoto(int idx) {
        return service.getPhoto(idx);
        
    }
    
    @Override public Integer getUser(Integer id) throws Exception {
        return service.getUser(id);        
    }

    @Override
    public void delContacts(int idx) throws IOException {
        service.delContacts(idx);
    }

    @Override
    public void delCreditial(int idx) throws IOException {
        service.delCreditial(idx);
    }

    @Override
    public void delPhoto(int idx) throws IOException {
        service.delPhoto(idx);
    }

    @Override
    public void delUser(int idx) throws Exception {        
        service.delUser(idx);
    }
}
