package br.com.busco.viagem.sk.ids;

import br.com.busco.viagem.sk.ddd.DomainObjectId;
import lombok.NonNull;

public class PresencaId extends DomainObjectId {

    public static final PresencaId VAZIO = new PresencaId();

    protected PresencaId() {
        super("");
    }

    public PresencaId(String uuid) {
        super(uuid);
    }

    public static PresencaId randomId() {
        return randomId(PresencaId.class);
    }

    public static PresencaId fromString(@NonNull String uuid) {
        return fromString(uuid, PresencaId.class);
    }

    public boolean isEmpty() {
        return this.equals(VAZIO) || this.equals(new PresencaId());
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
