package br.com.busco.viagem.viagem.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrigemTest {

    @Test
    void deveCriarOrigemComValor() {
        // Arrange
        String valor = "Terminal Central";

        // Act
        Origem origem = Origem.of(valor);

        // Assert
        assertThat(origem.getValor()).isEqualTo(valor);
    }

    @Test
    void deveRetornarEmptyQuandoValorNulo() {
        // Act
        Origem origem = Origem.of(null);

        // Assert
        assertThat(origem).isEqualTo(Origem.EMPTY);
        assertThat(origem.getValor()).isEmpty();
    }

    @Test
    void deveRetornarEmptyQuandoValorVazio() {
        // Act
        Origem origem = Origem.of("");

        // Assert
        assertThat(origem).isEqualTo(Origem.EMPTY);
    }

    @Test
    void deveSerIgualQuandoValorIgual() {
        // Arrange
        Origem origem1 = Origem.of("Terminal A");
        Origem origem2 = Origem.of("Terminal A");

        // Assert
        assertThat(origem1).isEqualTo(origem2);
        assertThat(origem1.hashCode()).isEqualTo(origem2.hashCode());
    }
}