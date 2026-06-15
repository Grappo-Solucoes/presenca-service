CREATE SCHEMA IF NOT EXISTS auth;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_proc p
        JOIN pg_namespace n ON n.oid = p.pronamespace
        WHERE n.nspname = 'auth'
          AND p.proname = 'jwt'
          AND pg_get_function_identity_arguments(p.oid) = ''
    ) THEN
        CREATE FUNCTION auth.jwt()
        RETURNS jsonb
        LANGUAGE sql
        STABLE
        AS $function$
            SELECT COALESCE(current_setting('request.jwt.claims', true)::jsonb, '{}'::jsonb)
        $function$;
    END IF;
END
$$;

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
