package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.viagem.domain.exceptions.PlanejamentoPeriodoSemDatasPrevistas;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

public class PeriodoPlanejadoTest {

    @Test
    void deveCriarPeriodoPlanejadoComDatas() {
        // Arrange
        LocalDateTime partida = LocalDateTime.of(2024, 1, 1, 8, 0);
        LocalDateTime chegada = LocalDateTime.of(2024, 1, 1, 10, 0);

        // Act
        PeriodoPlanejado periodo = PeriodoPlanejado.of(partida, chegada);

        // Assert
        assertThat(periodo.getPartida()).isEqualTo(partida);
        assertThat(periodo.getChegada()).isEqualTo(chegada);
        assertThat(periodo.getDuracaoEstimada()).isEqualTo(Duration.ofHours(2));
    }

    @Test
    void deveLancarExcecaoQuandoPartidaNula() {
        // Arrange
        LocalDateTime chegada = LocalDateTime.now();

        // Act & Assert
        assertThatThrownBy(() -> PeriodoPlanejado.of(null, chegada))
                .isInstanceOf(PlanejamentoPeriodoSemDatasPrevistas.class);
    }

    @Test
    void deveLancarExcecaoQuandoChegadaNula() {
        // Arrange
        LocalDateTime partida = LocalDateTime.now();

        // Act & Assert
        assertThatThrownBy(() -> PeriodoPlanejado.of(partida, null))
                .isInstanceOf(PlanejamentoPeriodoSemDatasPrevistas.class);
    }

    @Test
    void deveSerIgualQuandoPartidaEChegadaIguais() {
        // Arrange
        LocalDateTime partida = LocalDateTime.of(2024, 1, 1, 8, 0);
        LocalDateTime chegada = LocalDateTime.of(2024, 1, 1, 10, 0);

        // Act
        PeriodoPlanejado periodo1 = PeriodoPlanejado.of(partida, chegada);
        PeriodoPlanejado periodo2 = PeriodoPlanejado.of(partida, chegada);

        // Assert
        assertThat(periodo1).isEqualTo(periodo2);
        assertThat(periodo1.hashCode()).isEqualTo(periodo2.hashCode());
    }

    @Test
    void deveCalcularDuracaoEstimadaCorretamente() {
        // Arrange
        LocalDateTime partida = LocalDateTime.of(2024, 1, 1, 8, 30);
        LocalDateTime chegada = LocalDateTime.of(2024, 1, 1, 9, 45);
        Duration duracaoEsperada = Duration.ofMinutes(75);

        // Act
        PeriodoPlanejado periodo = PeriodoPlanejado.of(partida, chegada);

        // Assert
        assertThat(periodo.getDuracaoEstimada()).isEqualTo(duracaoEsperada);
    }
}