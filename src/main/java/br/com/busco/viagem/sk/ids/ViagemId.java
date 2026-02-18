package br.com.busco.viagem.sk.ids;

import br.com.busco.viagem.sk.ddd.DomainObjectId;
import lombok.NonNull;

public class ViagemId extends DomainObjectId {

    public static final ViagemId VAZIO = new ViagemId();

    protected ViagemId() {
        super("");
    }

    public ViagemId(String uuid) {
        super(uuid);
    }

    public static ViagemId randomId() {
        return randomId(ViagemId.class);
    }

    public static ViagemId fromString(@NonNull String uuid) {
        return fromString(uuid, ViagemId.class);
    }

    public boolean isEmpty() {
        return this.equals(VAZIO) || this.equals(new ViagemId());
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
