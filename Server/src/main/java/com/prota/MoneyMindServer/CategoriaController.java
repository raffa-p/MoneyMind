package com.prota.MoneyMindServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import com.prota.MoneyMindServer.DBrepository.CategoriaRepository;
import java.util.List;



/**
 *
 * @author Prota Raffaele
 */
@Controller
@RequestMapping(path="/categoria")
public class CategoriaController {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    
    @PostMapping(path = "/retrieveAll")
    public @ResponseBody ResponseEntity<String> retrieveAllCategories(){
        JsonObject response = new JsonObject();
                
        List all = categoriaRepository.findAll();
        if(all.isEmpty()){
            response.addProperty("result", false);
            response.addProperty("message", "No items found");
            return ResponseEntity.status(404).body(response.toString());
        }
        else{
            response.addProperty("result", true);
            response.addProperty("message", "All items retrieved");
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String jsonList = gson.toJson(all);
            response.addProperty("list", jsonList);
            return ResponseEntity.status(200).body(response.toString());
        }
    }
}



