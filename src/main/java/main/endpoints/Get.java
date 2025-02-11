package main.endpoints;

import java.util.HashMap;

import io.javalin.Javalin;
import main.UserController;
import main.UserRender;
import main.models.UserCreditial;
import main.models.helpers.BodyBuffer;
import main.models.helpers.FullUser;



public class Get extends Util {
    public static void endpoints(Javalin app, UserController controller, Boolean debuglog){
        app.get("/", (ctx) -> {
                StringBuilder html = new StringBuilder();
                StringBuilder log  = new StringBuilder();
                Exception e = controller.getUserIndex(html, log);
                if( e!=null ){
                    ctx.header("errors", e.toString());
                    ctx.status(500);
                    return;
                }                
                if(debuglog && log.toString().length()!=0)
                {
                    System.out.println(log.toString());
                    ctx.header("errors", log.toString());
                }
                ctx.html(html.toString());                
        });




///////////////////////////////////////////////////////////////////////////////////////
///////////////////             NOT USED ENDPOINTS                  ///////////////////
///////////////////////////////////////////////////////////////////////////////////////
        app.get("/api/all", (ctx) -> {
            int length = controller.getUserCount();
            BodyBuffer buf = new BodyBuffer();
            Integer i=1;
            HashMap<Integer, FullUser> list = new HashMap<>();
                for( i=1; i<=length; i++ ){
                    UserCreditial user = controller.getCreditial(i);
                    FullUser tmp = FullUser.fromName( user );
                    list.put(   i, tmp  );                    
                }   
                list.forEach( (k,v)->{ v.addContact (controller.getContacts(k)); } );System.gc();                
                list.forEach( (k,v)->{ v.addPhoto   (controller.getPhoto(k));    } );System.gc();
                try {                    
                    list.forEach( (k,v)->{ 
                        buf.appendArray(v.gCreditial(), v.gContact(), v.gPhoto());    
                    } );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            ctx.json(buf.toArr());            
        });
///////////////////////////////////////////////////////////////////////////////////////
        app.get("/api/add", (ctx) -> {
            StringBuilder   log = new StringBuilder();
            int id;
            try {
                id = controller.addUser(log);
            } catch (Exception e) {
                e.printStackTrace();
                log.append(e.getMessage());
                ctx.status(500);
                return;
            }                
            logException(
                controller.newContacts(id, log),log);                    
            logException(
                controller.newCredits (id, log),log);
            logException(
                controller.newPhoto   (id, log),log);
            
            if(debuglog)System.out.println(log.toString());
            if(debuglog)
                ctx.header("errors", log.toString());

        ctx.result(String.format("{ \"newid\"=%d } ",id));                
        ctx.status(200);            
        });
///////////////////////////////////////////////////////////////////////////////////////
        app.get("/api/id/:id", (ctx) -> {
        try {
            Integer id = Integer.valueOf(ctx.pathParam("id"));
            if( id==null || controller.getUser(id) == 0 )            
                ctx.status(404);
                            
            else{                
                FullUser user = FullUser.fromName(controller.getCreditial(id));
                user.addContact (controller.getContacts (id));
                user.addPhoto   (controller.getPhoto    (id));
                
                UserRender render = new UserRender();
                ctx.html( render.getUserHTML( id, user ) );
                ctx.status(200);
                render.close();System.gc();
            }
        } catch (Exception e) {
            if(e.getMessage()!=null)
            ctx.result("ERROR:\n"+e.getMessage());
            ctx.status(500);
        }
        });
///////////////////////////////////////////////////////////////////////////////////////
    }
}

