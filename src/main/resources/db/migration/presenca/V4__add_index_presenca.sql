create index idx_presenca_status on presenca(status);
create index idx_presenca_viagem on presenca(viagem_id);
create index idx_presenca_aluno on presenca(aluno_id);
create index idx_presenca_tenant on presenca(tenant_id);
create index idx_presenca_tenant_viagem
    on presenca(tenant_id, viagem_id);
create index idx_presenca_tenant_aluno
    on presenca(tenant_id, aluno_id);
create index idx_presenca_tenant_viagem_status
    on presenca(tenant_id, viagem_id, status);