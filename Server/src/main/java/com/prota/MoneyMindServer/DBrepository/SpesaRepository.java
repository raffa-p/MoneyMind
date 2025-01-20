package com.prota.MoneyMindServer.DBrepository;

import com.prota.MoneyMindServer.Common.SpesaCategoria;
import com.prota.MoneyMindServer.DBentity.Spesa;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Prota Raffaele
 */
@Repository
public interface SpesaRepository extends CrudRepository<Spesa, Long> {
    
    /**
     * Custom query in native SQL
     * Find all costs for the current month for logged user
     * 
     * @param username
     * @return 
     */    
    @Query(value = "SELECT * FROM spesa WHERE username = :username AND ricorrente = FALSE AND YEAR(timestamp) = YEAR(CURRENT_DATE) AND MONTH(timestamp) = MONTH(CURRENT_DATE)", nativeQuery = true)
    List<Spesa> findAllByUsernameAndAnnoAndMese(@Param("username") String username);
    
    /**
     * Custom query in native SQL
     * Find all costs for logged user checking ricorrente
     * 
     * @param username
     * @return 
     */
    @Query(value = "SELECT * FROM spesa WHERE username = :username AND ricorrente = TRUE", nativeQuery = true)
    List<Spesa> findAllByUsernameAndRicorrente(@Param("username") String username);
    
    
    List<Spesa> findAllByUsername(String username);
    
    
    /**
     * Return cost of every category in selected period
     * 
     * @param username
     * @return 
     */
    @Query(value = "SELECT categoria, SUM(costo) AS spesaCategoria FROM spesa WHERE username = :username AND YEAR(timestamp) = YEAR(CURRENT_DATE) AND MONTH(timestamp) = MONTH(CURRENT_DATE) GROUP BY categoria", nativeQuery = true)
    List<SpesaCategoria> findAllByCategory(@Param("username") String username);

    @Query(value = "SELECT * FROM spesa WHERE username = :username AND ricorrente = TRUE", nativeQuery = true)
    List<Spesa> findRicorrentiNelPeriodo(@Param("username") String username);
    
    /**
     * Find all recurrents spesa elements
     * 
     * @return 
     */
    @Query(value = "SELECT * FROM spesa WHERE ricorrente = TRUE", nativeQuery = true)
    List<Spesa> findAllByRicorrente();
    
    @Modifying
    @Query(value = "UPDATE spesa SET prossimopagamento = :prossimopagamento, ricorrenzemese = :ricorrenzemese WHERE id = :id", nativeQuery = true)
    void updateProssimoPagamentoById(@Param("id") Long id, @Param("prossimopagamento") Timestamp prossimopagamento, @Param("ricorrenzemese") int ricorrenzemese);
}
