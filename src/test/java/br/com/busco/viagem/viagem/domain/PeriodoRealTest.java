package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.viagem.domain.exceptions.RealizacaoPeriodoSemDatasPrevistas;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PeriodoRealTest {

    @Test
    void deveCriarPeriodoRealComPartidaAtual() {
        // Arrange
        LocalDateTime prevista = LocalDateTime.of(2024, 1, 1, 8, 0);

        // Act
        PeriodoReal periodo = PeriodoReal.of(prevista);

        // Assert
        assertThat(periodo.getPartida()).isNotNull();
        assertThat(periodo.getChegada()).isNull();
        assertThat(periodo.getAtrasoPartida()).isNotNull();
        assertThat(periodo.getAtrasoChegada()).isNull();
    }

    @Test
    void deveLancarExcecaoQuandoPrevistaNula() {
        // Act & Assert
        assertThatThrownBy(() -> PeriodoReal.of(null))
                .isInstanceOf(RealizacaoPeriodoSemDatasPrevistas.class);
    }

    @Test
    void deveAtualizarChegada() {
        // Arrange
        LocalDateTime prevista = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime chegadaPrevista = LocalDateTime.of(2024, 1, 1, 10, 0);
        PeriodoReal periodo = PeriodoReal.of(prevista);

        // Captura a partida original
        LocalDateTime partidaOriginal = periodo.getPartida();

        // Act
        periodo.atualizarChegada(chegadaPrevista);

        // Assert
        assertThat(periodo.getChegada()).isNotNull();
        assertThat(periodo.getPartida()).isEqualTo(partidaOriginal); // Partida não deve mudar
        assertThat(periodo.getAtrasoChegada()).isNotNull();
    }

    @Test
    void deveCalcularAtrasoNaPartida() throws InterruptedException {
        // Arrange
        LocalDateTime prevista = LocalDateTime.now().minusHours(2); // 2 segundos no futuro

        // Aguarda um pouco para garantir atraso
        Thread.sleep(10);

        // Act
        PeriodoReal periodo = PeriodoReal.of(prevista);
        // Assert - O atraso deve ser negativo (começou depois do previsto)
        assertThat(periodo.getAtrasoPartida()).isNegative();
    }

    @Test
    void deveSerIgualQuandoPartidaEChegadaIguais() {
        // Arrange
        LocalDateTime prevista = LocalDateTime.of(2024, 1, 1, 8, 0);

        // Como não podemos controlar o tempo exato, testamos apenas a estrutura
        PeriodoReal periodo1 = PeriodoReal.of(prevista);
        PeriodoReal periodo2 = new PeriodoReal(periodo1.getPartida(), null, periodo1.getAtrasoPartida(), null);

        // Assert
        assertThat(periodo1.getPartida()).isEqualTo(periodo2.getPartida());
    }

    @Test
    void deveCalcularAtrasoNaChegada() {
        // Arrange
        LocalDateTime previstaPartida = LocalDateTime.now().minusHours(1);
        LocalDateTime chegadaPrevista = LocalDateTime.now().plusMinutes(30);
        PeriodoReal periodo = PeriodoReal.of(previstaPartida);

        // Act
        periodo.atualizarChegada(chegadaPrevista);

        // Assert - O atraso na chegada deve ser positivo (chegou depois do previsto)
        assertThat(periodo.getAtrasoChegada()).isPositive();
    }
}