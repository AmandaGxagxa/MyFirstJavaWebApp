import spark.ModelAndView;
import spark.ResponseTransformer;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class App {
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
    // private static ResponseTransformer Hello;
    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        staticFiles.location("/public");

        List<String> usersList = new ArrayList<String>();
        get("/greet", (req, res) -> "Hello!");
        get("/greet/:username", (req, res) -> "Hello! "+ req.params(":username"));
        get("/greet/:username/language/:language", (req, res) ->{
            String name = req.params("username");
            String language = req.params("language");
            return getTheMessage(name, language);
        });

        get("/", (req, res) -> {

            Map<String, Object> map = new HashMap<>();
            return new ModelAndView(map, "hello.handlebars");

        }, new HandlebarsTemplateEngine());

        post("/", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            // create the greeting message
            // String lang = req.queryParams("language");
            String name = req.queryParams("username");
            String language = req.queryParams("language");
            if (name != "" && language !=null ){
            String message = getTheMessage(name,language);
             map.put("message", message);
//             return getTheMessage(name,language);
            }else{

            }
            if(!usersList.contains(name.toUpperCase())){
                usersList.add(name.toUpperCase());
            }

           int userCount = usersList.size();
            // put it in the map which is passed to the template - the value will be merged into the template


            map.put("username",usersList);
            map.put("userCount",userCount);

            return new ModelAndView(map, "hello.handlebars");
        }, new HandlebarsTemplateEngine());

        get("/greeted", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            return new ModelAndView(map, "greetedNames.handlebars");

        }, new HandlebarsTemplateEngine());

        post("/greeted", (req, res) -> {
            Map<String, Object> map = new HashMap<>();

            //create the greeting message
            String greeting = "Hello, " + req.queryParams("username").toUpperCase();
            if(!usersList.contains(req.queryParams("username").toUpperCase()) ){
                usersList.add(req.queryParams("username").toUpperCase());
            }
            int userCount = usersList.size();
            // put it in the map which is passed to the template - the value will be merged into the template


            map.put("username",usersList);
            map.put("userCount",userCount);
            return new ModelAndView(map, "greetedNames.handlebars");
        }, new HandlebarsTemplateEngine());
    };

    private static String getTheMessage(String name, String language){
        String greetingMessage = "";

        switch (language){
            case "IsiXhosa":
                greetingMessage =  "Mholo, "+name;
                break;
            case "English":
                greetingMessage =  "Hi, "+name;
                break;
            case "Afrikaans":
                greetingMessage =  "Hallo, "+name;
                break;
            default:
                greetingMessage =  "Hello!";
                break;
        }
        return greetingMessage;
    }

}
