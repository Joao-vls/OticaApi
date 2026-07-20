package br.com.otica.otica_loja.UseCases.midias;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoMidiaRepository;
import br.com.otica.otica_loja.enums.TipoMidia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class UploadMidiaProdutoUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    @Autowired
    private ProdutoMidiaRepository midiaRepository;

    /**
     * Faz upload de mídia associada a um produto.
     */
    public ProdutoMidia uploadParaProduto(UUID produtoId,
                                          TipoMidia tipo,
                                          String path,
                                          String thumbnailPath,
                                          String posterPath,
                                          Integer ordem) {

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        ProdutoMidia midia = new ProdutoMidia();
        midia.setId(UUID.randomUUID());
        midia.setProduto(produto);
        midia.setTipo(tipo);
        midia.setPath(path);
        midia.setThumbnailPath(thumbnailPath);
        midia.setPosterPath(posterPath);
        midia.setOrdem(ordem != null ? ordem : 0);
        midia.setCriadoEm(OffsetDateTime.now());

        return midiaRepository.save(midia);
    }

    /**
     * Faz upload de mídia associada a uma variante de produto.
     */
    public ProdutoMidia uploadParaVariante(UUID varianteId,
                                           TipoMidia tipo,
                                           String path,
                                           String thumbnailPath,
                                           String posterPath,
                                           Integer ordem) {

        ProdutoVariante variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada."));

        ProdutoMidia midia = new ProdutoMidia();
        midia.setId(UUID.randomUUID());
        midia.setVariante(variante);
        midia.setTipo(tipo);
        midia.setPath(path);
        midia.setThumbnailPath(thumbnailPath);
        midia.setPosterPath(posterPath);
        midia.setOrdem(ordem != null ? ordem : 0);
        midia.setCriadoEm(OffsetDateTime.now());

        return midiaRepository.save(midia);
    }
}
