package com.prota.MoneyMindServer.Common;

import com.prota.MoneyMindServer.DBentity.Spesa;
import com.prota.MoneyMindServer.DBrepository.SpesaRepository;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author Prota Raffaele
 */
public class SpesaUpdater {
    
    @Autowired
    private SpesaRepository spesaRepository;
    
    @Scheduled(cron = "0 0 0 * * *")
    public void updateProssimoPagamento(){
        List<Spesa> spese = spesaRepository.findAllByRicorrente();
        
        for(Spesa spesa : spese){
            if(LocalDateTime.now().isBefore(spesa.getProssimoPagamento().toLocalDateTime()) || spesa.getProssimoPagamento() == null){
                try{
                    LocalDateTime today = LocalDateTime.now();
                    LocalDateTime futureDate;
                    String ricorrenzaPeriodo = spesa.getRicorrenza().split("-")[1];
                    int ricorrenzaNumero = Integer.parseInt(spesa.getRicorrenza().split("-")[0]);
                    switch (ricorrenzaPeriodo) {
                        case "Giorni" -> futureDate = today.plusDays(ricorrenzaNumero);
                        case "Mesi" -> futureDate = today.plusMonths(ricorrenzaNumero);
                        case "Anni" -> futureDate = today.plusYears(ricorrenzaNumero);
                        default -> throw new IllegalArgumentException("Formato non supportato: " + ricorrenzaPeriodo);
                    }
                    int ricorrenzeMese = 0;
                    if(LocalDate.now().getDayOfMonth() != 1){ ricorrenzeMese = spesa.getRicorrenzeMese() + 1; }
                    spesaRepository.updateProssimoPagamentoById(spesa.getID(), Timestamp.valueOf(futureDate), ricorrenzeMese);
                }
                catch(IllegalArgumentException i){
                    System.out.println(i.getMessage());
                }
            }
        }
    }
}
