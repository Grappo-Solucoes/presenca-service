package br.com.busco.presenca.sk.ddd;

import jakarta.persistence.PrePersist;

public class TenantEntityListener {

    @PrePersist
    public void setTenant(Object entity) {

        if (entity instanceof AbstractEntity<?> abstractEntity) {

            if (abstractEntity.getTenantId() == null) {
                abstractEntity.setTenantId(TenantContext.get());
            }
        }
    }
}