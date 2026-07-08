package br.com.otica.otica_loja.UseCases.avaliacoes;

import br.com.otica.otica_loja.Entity.Avaliacao.ProdutoAvaliacao;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.Repository.Avaliacao.ProdutoAvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CriarAvaliacaoUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoAvaliacaoRepository avaliacaoRepository;

    /**
     * Cria uma nova avaliação para um produto.
     */
    public ProdutoAvaliacao criar(UUID produtoId,
                                  UUID usuarioId,
                                  String nomeUsuario,
                                  Integer nota,
                                  String titulo,
                                  String texto,
                                  String imagemPath) {

        // 1. Verificar se produto existe
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        // 2. Validar nota
        if (nota == null || nota < 1 || nota > 5) {
            throw new IllegalArgumentException("A nota deve estar entre 1 e 5.");
        }

        // 3. Criar avaliação
        ProdutoAvaliacao avaliacao = new ProdutoAvaliacao();
        avaliacao.setProduto(produto);
        avaliacao.setUsuarioId(usuarioId);
        avaliacao.setNomeUsuario(nomeUsuario);
        avaliacao.setNota(nota);
        avaliacao.setTitulo(titulo);
        avaliacao.setTexto(texto);
        avaliacao.setImagemPath(imagemPath);
        avaliacao.setAprovado(false); // inicialmente não aprovado
        avaliacao.setDataAvaliacao(LocalDate.now());
        avaliacao.setCriadoEm(OffsetDateTime.now());

        // 4. Persistir
        return avaliacaoRepository.save(avaliacao);
    }
}
