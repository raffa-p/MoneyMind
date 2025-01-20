package com.prota.MoneyMindServer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prota.MoneyMindServer.DBentity.Spesa;
import com.prota.MoneyMindServer.DBrepository.SpesaRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Prota Raffaele
 */
@SpringBootTest
public class SpesaControllerTest {

    @Autowired
    private SpesaRepository spesaRepository;

    @Autowired
    private SpesaController spesaController;

    private double totaleGetStatus = 0;

    @BeforeEach
    public void setUp() {
        System.out.println(Timestamp.valueOf(LocalDateTime.now().minusMonths(1)));
        totaleGetStatus += spesaRepository.save(new Spesa(5.0, "musica", "unitTest", true, "1-mesi", Timestamp.valueOf(LocalDateTime.now().minusMonths(1)), Timestamp.valueOf(LocalDateTime.now()), 1)).getCosto();
        totaleGetStatus += spesaRepository.save(new Spesa(6, "musica", "unitTest", false)).getCosto();
        totaleGetStatus += spesaRepository.save(new Spesa(3, "musica", "unitTest", false)).getCosto();
        totaleGetStatus += spesaRepository.save(new Spesa(2, "musica", "unitTest", false)).getCosto();
        totaleGetStatus += spesaRepository.save(new Spesa(17, "musica", "unitTest", false)).getCosto();
    }

    @AfterEach
    public void tearDown() {
        List<Spesa> speseTest = spesaRepository.findAllByUsername("unitTest");
        while (!speseTest.isEmpty()) {
            spesaRepository.deleteById(speseTest.get(0).getID());
            speseTest.remove(0);
        }
    }

    @Test
    public void testGetStatus() {
        JsonObject request = new JsonObject();
        request.addProperty("username", "unitTest");

        ResponseEntity<String> response = spesaController.getStatus(request.toString());

        assertEquals(200, response.getStatusCode().value());
        JsonObject responseJson = JsonParser.parseString(response.getBody()).getAsJsonObject();
        double result = responseJson.get("status").getAsDouble();
        assertEquals(totaleGetStatus, result);
    }
}
