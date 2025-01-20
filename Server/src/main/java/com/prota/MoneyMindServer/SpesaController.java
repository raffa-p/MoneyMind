package com.prota.MoneyMindServer;

import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prota.MoneyMindServer.Common.SpesaCategoria;
import com.prota.MoneyMindServer.DBentity.Spesa;
import java.util.List;
import com.prota.MoneyMindServer.DBrepository.SpesaRepository;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;



/**
 *
 * @author Prota Raffaele
 */
@Controller
@RequestMapping(path="/spese")
public class SpesaController {
    
    @Autowired
    private SpesaRepository spesaRepository;

    @PostMapping(path = "/add")
    public @ResponseBody ResponseEntity<String> manageAdd(@RequestBody String parameters) {

        JsonObject jsonObject = JsonParser.parseString(parameters).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        double costo = jsonObject.get("costo").getAsDouble();
        String categoria = jsonObject.get("category").getAsString();
        boolean ricorrente = jsonObject.get("flagRecurrent").getAsString().equals("yes");
        int ricorrenzaNumero = jsonObject.get("ricorrenzaNumero").getAsString().equals("")? 0 : jsonObject.get("ricorrenzaNumero").getAsInt();
        String ricorrenzaPeriodo = jsonObject.get("ricorrenzaPeriodo").getAsString();
        Timestamp prossimoPagamento = null;
        
        if(ricorrente){
            try{
                Timestamp today = Timestamp.valueOf(LocalDateTime.now());
                LocalDateTime futureDate;
                switch (ricorrenzaPeriodo) {
                    case "Giorni" -> futureDate = today.toLocalDateTime().plusDays(ricorrenzaNumero);
                    case "Mesi" -> futureDate = today.toLocalDateTime().plusMonths(ricorrenzaNumero);
                    case "Anni" -> futureDate = today.toLocalDateTime().plusYears(ricorrenzaNumero);
                    default -> throw new IllegalArgumentException("Formato non supportato: " + ricorrenzaPeriodo);
                }
                prossimoPagamento = Timestamp.valueOf(futureDate);
            }
            catch(IllegalArgumentException i){
                System.out.println(i.getMessage());
            }
        }
        
        return add(costo, categoria, username, ricorrente, ricorrenzaNumero, ricorrenzaPeriodo, prossimoPagamento);
    }
    
    @PostMapping(path = "/remove")
    public @ResponseBody ResponseEntity<String> manageRemove(@RequestBody String parameters) {

        JsonObject jsonObject = JsonParser.parseString(parameters).getAsJsonObject();
        Long  id = jsonObject.get("id").getAsLong();
        
        return remove(id);

    }
    
    @PostMapping(path = "/retrieveAll")
    public @ResponseBody ResponseEntity<String> manageRetrieve(@RequestBody String parameters) {

        JsonObject jsonObject = JsonParser.parseString(parameters).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        
        return retrieve(username, false);
    }
    
    @PostMapping(path = "/retrieveAllRecurrents")
    public @ResponseBody ResponseEntity<String> manageRetrieveRecurrents(@RequestBody String parameters) {

        JsonObject jsonObject = JsonParser.parseString(parameters).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        
        return retrieve(username, true);

    }
    
    @PostMapping(path = "/status")
    public @ResponseBody ResponseEntity<String> getStatus(@RequestBody String parameters) {
        JsonObject jsonObject = JsonParser.parseString(parameters).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        JsonObject response = new JsonObject();
        
        List<Spesa> all = spesaRepository.findAllByUsernameAndAnnoAndMese(username); // spese non ricorrenti
        
        List<Spesa> ricorrenti = spesaRepository.findRicorrentiNelPeriodo(username); //spese ricorrenti
        
        double somma = 0;
        if(ricorrenti != null && !ricorrenti.isEmpty()){
            for(Spesa spesa : ricorrenti){
                if(spesa.getTimestamp().toLocalDateTime().toLocalDate().isEqual(LocalDate.now())){
                    somma += spesa.getCosto();
                }
                else{ somma += spesa.getCosto() * spesa.getRicorrenzeMese(); }
            }
        }

        if(all != null && !all.isEmpty()){ for(Spesa spesa : all){ somma += spesa.getCosto(); } }

        response.addProperty("result", true);
        response.addProperty("message", "Status retrieved");
        response.addProperty("status", somma);
        return ResponseEntity.status(200).body(response.toString());
        
    }
    
    @PostMapping(path = "/retrieveSumCategory")
    public @ResponseBody ResponseEntity<String> manageRetrieveSumCategory(@RequestBody String parameters) {

        JsonObject jsonObject = JsonParser.parseString(parameters).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
       
        return retrieveSumCategory(username);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private ResponseEntity<String> add(double costo, String categoria, String username, boolean ricorrente, int ricorrenzaNumero, String ricorrenzaPeriodo, Timestamp prossimoPagamento){
        JsonObject response = new JsonObject();
        
        Spesa spesa;
        if(!ricorrente){ spesa = new Spesa(costo, categoria, username, ricorrente); }
        else{ 
            String ricorrenza = ricorrenzaNumero + "-" + ricorrenzaPeriodo;
            spesa = new Spesa(costo, categoria, username, ricorrente, ricorrenza, prossimoPagamento); 
        }
        
        if(spesaRepository.save(spesa) != null){
            response.addProperty("result", true);
            response.addProperty("message", "New cost added");
            return ResponseEntity.status(200).body(response.toString());
        }
        else{
            response.addProperty("result", false);
            response.addProperty("message", "Error during insert");
            return ResponseEntity.status(500).body(response.toString());
        }
    }
    
    private ResponseEntity<String> remove(Long id){
        JsonObject response = new JsonObject();
        try{
            spesaRepository.deleteById(id);
            response.addProperty("result", true);
            response.addProperty("message", "Cost removed");
            return ResponseEntity.status(200).body(response.toString());
        }
        catch(Exception e){
            response.addProperty("result", false);
            response.addProperty("message", "Error during remove");
            return ResponseEntity.status(400).body(response.toString());
        }
    }
    
    private ResponseEntity<String> retrieve(String username, boolean ricorrenti) {
    
    Gson gson = new Gson();
    JsonObject response = new JsonObject();

    try {
        List<Spesa> all;
        if (ricorrenti){ all = spesaRepository.findAllByUsernameAndRicorrente(username); } 
        else{ all = spesaRepository.findAllByUsername(username); }

        if (all.isEmpty()){ 
            response.addProperty("result", false);
            response.addProperty("message", "No items found");
            return ResponseEntity.status(404).body(response.toString());
        } 
        else{ 
            response.addProperty("result", true);
            response.addProperty("message", "All items retrieved");
            String jsonList = gson.toJson(all);
            response.addProperty("list", jsonList);            
            return ResponseEntity.status(200).body(response.toString());
        }
    } catch (Exception e) {
        response.addProperty("result", false);
            response.addProperty("message", "Database error: " + e.getMessage());
            return ResponseEntity.status(500).body(response.toString());
    }
}

    
    private ResponseEntity<String> retrieveSumCategory(String username){
        JsonObject response = new JsonObject();
        List<SpesaCategoria> all = spesaRepository.findAllByCategory(username);
        if(all.isEmpty()){
            response.addProperty("result", false);
            response.addProperty("message", "No items found");
            return ResponseEntity.status(404).body(response.toString());
        }
        else{
            response.addProperty("result", true);
            response.addProperty("message", "All items retrieved");
            Gson gson = new Gson();
            String jsonList = gson.toJson(all);
            response.addProperty("list", jsonList);            
            return ResponseEntity.status(200).body(response.toString());
        }
    }
}