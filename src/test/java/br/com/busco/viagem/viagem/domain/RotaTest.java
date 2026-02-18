package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.sk.vo.Rota;
import br.com.busco.viagem.viagem.domain.exceptions.RotaSemOrigemDestinoDefinidas;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RotaTest {

    @Test
    void deveCriarRotaComTodosOsParametros() {
        // Arrange
        Origem origem = Origem.of("Escola A");
        Destino destino = Destino.of("Escola B");
        Distance distancia = new Distance(10, Metrics.KILOMETERS);
        Integer tempoEstimadoMinutos = 30;

        // Act
        Rota rota = Rota.of(origem, destino, distancia, tempoEstimadoMinutos);

        // Assert
        assertThat(rota.getOrigem()).isEqualTo(origem);
        assertThat(rota.getDestino()).isEqualTo(destino);
        assertThat(rota.getDistancia()).isEqualTo(distancia);
        assertThat(rota.getTempoEstimadoMinutos()).isEqualTo(tempoEstimadoMinutos);
        assertThat(rota.getTempoEstimado()).isEqualTo(Duration.ofMinutes(30));
    }

    @Test
    void deveLancarExcecaoQuandoOrigemNula() {
        // Arrange
        Destino destino = Destino.of("Escola B");
        Distance distancia = new Distance(10, Metrics.KILOMETERS);

        // Act & Assert
        assertThatThrownBy(() -> Rota.of(null, destino, distancia, 30))
                .isInstanceOf(RotaSemOrigemDestinoDefinidas.class);
    }

    @Test
    void deveLancarExcecaoQuandoDestinoNulo() {
        // Arrange
        Origem origem = Origem.of("Escola A");
        Distance distancia = new Distance(10, Metrics.KILOMETERS);

        // Act & Assert
        assertThatThrownBy(() -> Rota.of(origem, null, distancia, 30))
                .isInstanceOf(RotaSemOrigemDestinoDefinidas.class);
    }

    @Test
    void deveLancarExcecaoQuandoDistanciaNula() {
        // Arrange
        Origem origem = Origem.of("Escola A");
        Destino destino = Destino.of("Escola B");

        // Act & Assert
        assertThatThrownBy(() -> Rota.of(origem, destino, null, 30))
                .isInstanceOf(RotaSemOrigemDestinoDefinidas.class);
    }

    @Test
    void deveRetornarTempoEstimadoNuloQuandoMinutosNulo() {
        // Arrange
        Origem origem = Origem.of("Escola A");
        Destino destino = Destino.of("Escola B");
        Distance distancia = new Distance(10, Metrics.KILOMETERS);

        // Act
        Rota rota = Rota.of(origem, destino, distancia, null);

        // Assert
        assertThat(rota.getTempoEstimado()).isNull();
    }

    @Test
    void deveSerIgualQuandoOrigemEDestinoIguais() {
        // Arrange
        Origem origem = Origem.of("Escola A");
        Destino destino = Destino.of("Escola B");
        Distance distancia1 = new Distance(10, Metrics.KILOMETERS);
        Distance distancia2 = new Distance(12, Metrics.KILOMETERS);

        // Act
        Rota rota1 = Rota.of(origem, destino, distancia1, 30);
        Rota rota2 = Rota.of(origem, destino, distancia2, 35);

        // Assert
        assertThat(rota1).isEqualTo(rota2);
        assertThat(rota1.hashCode()).isEqualTo(rota2.hashCode());
    }

    @Test
    void deveSerDiferenteQuandoOrigemOuDestinoDiferentes() {
        // Arrange
        Origem origem1 = Origem.of("Escola A");
        Origem origem2 = Origem.of("Escola C");
        Destino destino = Destino.of("Escola B");
        Distance distancia = new Distance(10, Metrics.KILOMETERS);

        // Act
        Rota rota1 = Rota.of(origem1, destino, distancia, 30);
        Rota rota2 = Rota.of(origem2, destino, distancia, 30);

        // Assert
        assertThat(rota1).isNotEqualTo(rota2);
    }
}