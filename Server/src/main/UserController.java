package com.prota.MoneyMindServer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.prota.MoneyMindServer.DBrepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prota.MoneyMindServer.DBentity.User;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;


/**
 *
 * @author Prota Raffaele
 */
@Controller
@RequestMapping(path="/user")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    
    
    @PostMapping(path = "/login")
    public @ResponseBody ResponseEntity<String> manageLogin(@RequestBody String parameters) {

        JsonObject jsonObject = JsonParser.parseString(parameters).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        String password = jsonObject.get("password").getAsString();

        return authenticate(username, password);
    }
    
    
    
    @PostMapping(path = "/signup")
    public @ResponseBody ResponseEntity<String> manageSignup(@RequestBody String parameters) {

        JsonObject jsonObject = JsonParser.parseString(parameters).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        String password = jsonObject.get("password").getAsString();

        return register(username, password);
    }
    
    
    
    private ResponseEntity<String> authenticate(String username, String password){
        JsonObject response = new JsonObject();
        
        Optional<User> userOptional = userRepository.findById(username); 
        if (userOptional.isPresent()){
            User usr = userOptional.get();
            if(usr.getPassword().equals("")){
                response.addProperty("result", false);
                response.addProperty("message", "Password field void");
                return ResponseEntity.status(400).body(response.toString());
            }
            else if(BCrypt.checkpw(password, usr.getPassword())){
                response.addProperty("result", true);
                response.addProperty("message", "Password correct");
                return ResponseEntity.status(200).body(response.toString());
            }
            else{ 
                response.addProperty("result", false);
                response.addProperty("message", "Wrong password");
                return ResponseEntity.status(400).body(response.toString());
            }
        }
        else{
            response.addProperty("result", false);
            response.addProperty("message", "Wrong username");
            return ResponseEntity.status(404).body(response.toString());
        }
    }
    
    
    
    private ResponseEntity<String> register(String username, String password){
        JsonObject response = new JsonObject();

        if (userRepository.existsById(username)) {
            response.addProperty("result", false);
            response.addProperty("message", "Username non disponibile");
            return ResponseEntity.status(400).body(response.toString());
        }
        else{
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            User user = new User(username, hashedPassword);
            if(userRepository.save(user) != null){
                response.addProperty("result", true);
                response.addProperty("message", "Registratione completata");
                return ResponseEntity.status(200).body(response.toString());
            }
            else{
                response.addProperty("result", false);
                response.addProperty("message", "Errore connessione database");
                return ResponseEntity.status(500).body(response.toString());
            }
        }
    }
}



