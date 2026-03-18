package br.com.busco.presenca.domain.exceptions;

public final class NaoPossivelJustificarPresenca extends IllegalStateException {
    public NaoPossivelJustificarPresenca() {
        super("Não pode justificar se já está presente");
    }
}
