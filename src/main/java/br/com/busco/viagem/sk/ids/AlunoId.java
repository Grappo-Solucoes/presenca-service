package br.com.busco.viagem.sk.ids;

import br.com.busco.viagem.sk.ddd.DomainObjectId;
import lombok.NonNull;

public class AlunoId extends DomainObjectId {

    public static final AlunoId VAZIO = new AlunoId();

    protected AlunoId() {
        super("");
    }

    public AlunoId(String uuid) {
        super(uuid);
    }

    public static AlunoId randomId() {
        return randomId(AlunoId.class);
    }

    public static AlunoId fromString(@NonNull String uuid) {
        return fromString(uuid, AlunoId.class);
    }

    public boolean isEmpty() {
        return this.equals(VAZIO) || this.equals(new AlunoId());
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
