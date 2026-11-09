package GESTOR_CINEMAR_CENTER.DEV.repository;

import GESTOR_CINEMAR_CENTER.DEV.model.Asiento;
import GESTOR_CINEMAR_CENTER.DEV.model.AsientoHold;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsientoHoldRepository extends JpaRepository<AsientoHold, Long> {

    @Query("""
            SELECT h FROM AsientoHold h
            JOIN FETCH h.asiento
            JOIN FETCH h.cliente
            WHERE h.funcion = :funcion AND h.expiresAt > :ahora
            """)
    List<AsientoHold> findVigentesByFuncion(
            @Param("funcion") Funcion funcion,
            @Param("ahora") LocalDateTime ahora
    );

    Optional<AsientoHold> findByFuncionAndAsiento(Funcion funcion, Asiento asiento);

    Optional<AsientoHold> findByFuncionAndAsientoAndExpiresAtAfter(
            Funcion funcion,
            Asiento asiento,
            LocalDateTime ahora
    );

    List<AsientoHold> findByFuncionAndClienteAndExpiresAtAfter(
            Funcion funcion,
            Cliente cliente,
            LocalDateTime ahora
    );

    void deleteByFuncionAndCliente(Funcion funcion, Cliente cliente);

    void deleteByFuncionAndAsientoAndCliente(Funcion funcion, Asiento asiento, Cliente cliente);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM AsientoHold h WHERE h.expiresAt <= :ahora")
    int deleteExpirados(@Param("ahora") LocalDateTime ahora);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM AsientoHold h WHERE h.funcion = :funcion AND h.asiento IN :asientos")
    int deleteByFuncionAndAsientoIn(
            @Param("funcion") Funcion funcion,
            @Param("asientos") List<Asiento> asientos
    );
}
