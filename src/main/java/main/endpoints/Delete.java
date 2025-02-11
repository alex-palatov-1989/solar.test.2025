package main.endpoints;

import io.javalin.Javalin;
import main.UserController;

public class Delete extends Util{
        static public void endpoints(Javalin app, UserController controller, Boolean debuglog){
        
        app.post("/delete", (ctx) ->{   
            if( ctx.formParam("id") == null ){
                ctx.status(500); 
                ctx.redirect("/");
            }
            Integer id = Integer.valueOf(ctx.formParam("id"));
            if( controller.getUser(id) == 0 ){
                ctx.status(500); return;
            }                       
            StringBuilder log = new StringBuilder(); 
            Exception e = controller.deleUserData(id, log);
            if( e==null )
            {
                ctx.status(200);
                ctx.redirect("/");
            } 
            else{
                e.printStackTrace();
                ctx.header("errors", e.getMessage());
                ctx.status(500);
                if(debuglog)
                {
                    log.append(e.getMessage());
                    ctx.header("errors", log.toString());
                    System.out.println(log.toString());
                }                                 
                ctx.redirect("/");       
            }
        });
    }
}
