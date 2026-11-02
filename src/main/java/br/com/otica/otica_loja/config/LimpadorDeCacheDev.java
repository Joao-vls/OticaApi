package br.com.otica.otica_loja.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LimpadorDeCacheDev implements CommandLineRunner {

    private final CacheManager cacheManager;

    @Override
    public void run(String... args) {
        // Itera sobre todos os "potes" de cache conhecidos e limpa um por um
        cacheManager.getCacheNames().forEach(nomeDoCache -> {
            cacheManager.getCache(nomeDoCache).clear();
        });
        System.out.println("🔥 [DEV] Todos os caches do Redis foram limpos com sucesso no startup!");
    }
}