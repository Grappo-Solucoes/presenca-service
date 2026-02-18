package br.com.busco.viagem.viagem.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DestinoTest {

    @Test
    void deveCriarDestinoComValor() {
        // Arrange
        String valor = "Escola Municipal";

        // Act
        Destino destino = Destino.of(valor);

        // Assert
        assertThat(destino.getValor()).isEqualTo(valor);
    }

    @Test
    void deveRetornarEmptyQuandoValorNulo() {
        // Act
        Destino destino = Destino.of(null);

        // Assert
        assertThat(destino).isEqualTo(Destino.EMPTY);
        assertThat(destino.getValor()).isEmpty();
    }

    @Test
    void deveRetornarEmptyQuandoValorVazio() {
        // Act
        Destino destino = Destino.of("");

        // Assert
        assertThat(destino).isEqualTo(Destino.EMPTY);
    }

    @Test
    void deveSerIgualQuandoValorIgual() {
        // Arrange
        Destino destino1 = Destino.of("Escola A");
        Destino destino2 = Destino.of("Escola A");

        // Assert
        assertThat(destino1).isEqualTo(destino2);
        assertThat(destino1.hashCode()).isEqualTo(destino2.hashCode());
    }
}
