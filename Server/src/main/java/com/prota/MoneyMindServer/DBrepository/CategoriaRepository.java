package com.prota.MoneyMindServer.DBrepository;

import com.prota.MoneyMindServer.DBentity.Categoria;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Prota Raffaele
 */

public interface CategoriaRepository extends CrudRepository<Categoria, String> {
        
    @Override
    List<Categoria> findAll();

}
