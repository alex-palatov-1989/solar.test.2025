package main.endpoints;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import main.UserController;
import main.models.UserContact;

public class Post extends Util{
    static public void endpoints(Javalin app, UserController controller, Boolean debuglog){
        
        app.post("/username", (ctx) ->{             
            
            StringBuilder log = new StringBuilder();     
            Integer id = controller.addUser(log);
            
            if( controller.getUser(id) == 0 ){
                ctx.status(400); return;
            }           
            logException(   controller.newContacts(id, log),log);                    
            logException(   controller.newCredits (id, log),log);
            logException(   controller.newPhoto   (id, log),log);                        
            if(debuglog)    ctx.header("errors", log.toString());
                                  
            Exception e = controller.addCredits(id,
                ctx.formParam("username"),
                ctx.formParam("secondname"),
                ctx.formParam("thirdname"),
                ctx.formParam("birthday"),
                log
            );                  
            if( e!=null && debuglog ){ 
                e.printStackTrace(); 
                log.append(e.getMessage()); 
            }
            ctx.status(200);        
            ctx.redirect("/");
        });

        app.post("/contacts", (ctx) ->{             
            if( ctx.formParam("id") == null ){
                ctx.status(400); return;
            }
            Integer id = Integer.valueOf(ctx.formParam("id"));
            if( controller.getUser(id) == 0 ){
                ctx.status(400); return;
            }                        
            StringBuilder log = new StringBuilder();
            Exception e = controller.addContacts(id, 
                new UserContact(
                    ctx.formParam("cell"),
                    ctx.formParam("mail"),
                    ctx.formParam("adress")
                ),
                log
            );                        
            if( e!=null && debuglog ){ 
                e.printStackTrace(); 
                log.append(e.getMessage()); 
            }
            if(debuglog)    
            ctx.header("errors", log.toString());
            ctx.status(200);
            ctx.redirect("/");
        });
        app.post("/upload",  (ctx) ->{

            StringBuilder log = new StringBuilder();
            String idArg; String uri; Integer id;             
            Exception err;                   
            try{                    
                UploadedFile image = ctx.uploadedFile("img");
                if( image == null ){ ctx.status(400); return; }

                idArg = ctx.formParam( "idx") ; 
                id = Integer.valueOf(idArg);
                err = controller.uploadPhoto(id, image.getContent(), image.getExtension(), log);

            }   catch (Exception e ){ 
                e.printStackTrace(); 
                ctx.status(500); 
                if( e.getMessage()!=null)
                ctx.header("errors", e.getMessage());
                return;
            }                                            
            if( err == null ){
                ctx.status(200);
                ctx.redirect("/");                
            }else{
                if(debuglog && err.getMessage()!=null )
                    ctx.header("errors", err.getMessage());
                err.getStackTrace();
                ctx.status(400);
            }
        });
    }
}
