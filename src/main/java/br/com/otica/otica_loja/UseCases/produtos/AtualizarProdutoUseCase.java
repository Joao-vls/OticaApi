package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import br.com.otica.otica_loja.UseCases.variantes.AtualizarVarianteUseCase;
import br.com.otica.otica_loja.dto.ProdutoUpdateRequestDTO;
import br.com.otica.otica_loja.dto.ProdutoVarianteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtualizarProdutoUseCase {

    private final ProdutoRepository produtoRepository;
    private final AtualizarVarianteUseCase atualizarVarianteUseCase; // Injetado
    private final AtualizarMidiaUseCase atualizarMidiaUseCase;

    @Transactional
    public Produto atualizar(
            UUID produtoId,
            ProdutoUpdateRequestDTO dto,
            MultipartHttpServletRequest request
    ) throws IOException {

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com o ID: " + produtoId));

        if (dto.getNome() != null && !dto.getNome().isBlank()) produto.setNome(dto.getNome());
        if (dto.getSlug() != null && !dto.getSlug().isBlank()) {
            if (!produto.getSlug().equalsIgnoreCase(dto.getSlug()) && produtoRepository.findBySlug(dto.getSlug()).isPresent()) {
                throw new IllegalArgumentException("Já existe um produto com este slug.");
            }
            produto.setSlug(dto.getSlug());
        }
        if (dto.getDescricao() != null) produto.setDescricao(dto.getDescricao());
        if (dto.getPreco() != null) produto.setPreco(dto.getPreco());
        if (dto.getCategoria() != null) produto.setCategoria(dto.getCategoria());
        if (dto.getSpecs() != null) produto.setSpecs(dto.getSpecs());
        if (dto.getMarcaId() != null) produto.setMarcaId(dto.getMarcaId());
        if (dto.getCategoriaId() != null) produto.setCategoriaId(dto.getCategoriaId());
        if (dto.getDestaque() != null) produto.setDestaque(dto.getDestaque());
        if (dto.getAtivo() != null) produto.setAtivo(dto.getAtivo());

        produto.setAtualizadoEm(OffsetDateTime.now());

        // Delega o processamento de variantes para a classe responsável
        Map<String, ProdutoVariante> variantesMapa = atualizarVarianteUseCase.processarVariantes(produto, dto.getVariantes());

        Produto produtoSalvo = produtoRepository.save(produto);

        atualizarMidiaUseCase.processarMidias(
                produtoSalvo,
                dto.getMidias(),
                variantesMapa,
                request
        );

        return produtoRepository.findById(produtoSalvo.getId()).orElse(produtoSalvo);
    }
}