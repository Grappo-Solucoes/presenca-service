package br.com.busco.viagem.sk.ddd;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Order(2)
@Component
@RequiredArgsConstructor
public class HibernateTenantFilter extends OncePerRequestFilter {

    private final EntityManager entityManager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {

            var tenant = TenantContext.get();

            if (tenant != null) {
                entityManager.unwrap(Session.class)
                        .enableFilter("tenantFilter")
                        .setParameter("tenantId", tenant.toUUID());
            }

            filterChain.doFilter(request, response);

        } finally {
            entityManager.unwrap(Session.class)
                    .disableFilter("tenantFilter");
        }
    }
}