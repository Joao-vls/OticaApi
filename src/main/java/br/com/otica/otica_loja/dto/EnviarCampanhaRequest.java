package br.com.otica.otica_loja.dto;

import java.util.List;

/**
 * Record que transporta os dados do disparo de e-mail do Front-end (Painel Admin) para o Backend.
 */
public record EnviarCampanhaRequest(
        String titulo,
        String assunto,
        String conteudoHtml,
        String publicoAlvo, // Opções: "TODOS_MEMBROS", "NEWSLETTER", "AMBOS", "ESPECIFICOS"
        List<String> emailsEspecificos // Usado apenas quando publicoAlvo for "ESPECIFICOS"
) {}