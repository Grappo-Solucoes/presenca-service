ALTER TABLE public.presenca ENABLE ROW LEVEL SECURITY;
create policy "tenant isolation presenca"
on public.presenca
for all
using (
  tenant_id = (auth.jwt() ->> 'tenant_id')
)
with check (
  tenant_id = (auth.jwt() ->> 'tenant_id')
);