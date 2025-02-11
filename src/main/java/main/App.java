package main;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import main.endpoints.Delete;
import main.endpoints.Get;
import main.endpoints.Post;



public class App {
    
    final static String  rootPath = "/home/virus/Documents/test-java/test/";    
    final static Integer PORT = 8080;
    final static String PATH = "localhost";

    final public static String  URL  = String.format("http://%s:%d/", PATH, PORT);
    final public static String dfltPhoto = "dfltAvatar.png";
    final public static Boolean debuglog = true;

    public static void main(String[] args) {
        
        UserController controller = new UserController( new UserService() );        
        Javalin app = Javalin.create(
            cfg->{
                cfg.addStaticFiles(rootPath+"static", Location.EXTERNAL);
                cfg.addStaticFiles(rootPath+"userupload", Location.EXTERNAL);
            }
        );        
        Get     .endpoints(app, controller, debuglog);
        Post    .endpoints(app, controller, debuglog);
        Delete  .endpoints(app, controller, debuglog);

        app.start(PORT);
    }
}

