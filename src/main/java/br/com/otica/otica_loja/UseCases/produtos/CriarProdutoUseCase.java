package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.dto.ProdutoRequestDTO;
import br.com.otica.otica_loja.service.cms.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CriarProdutoUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Transactional
    public Produto criar(ProdutoRequestDTO dto, List<MultipartFile> arquivosMidias) throws IOException {

        if (produtoRepository.findBySlug(dto.slug()).isPresent()) {
            throw new IllegalArgumentException("Já existe um produto com este slug.");
        }

        // 1. Cria o Produto base
        Produto produto = new Produto();
        produto.setMarcaId(dto.marcaId());
        produto.setCategoriaId(dto.categoriaId());
        produto.setNome(dto.nome());
        produto.setSlug(dto.slug());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setCategoriaOculos(dto.categoriaOculos());
        produto.setSpecs(dto.specs() != null ? dto.specs() : "{}");
        produto.setDestaque(dto.destaque() != null ? dto.destaque() : false);
        produto.setAtivo(true);
        produto.setCriadoEm(OffsetDateTime.now());
        produto.setAtualizadoEm(OffsetDateTime.now());

        // 2. Mapeia e vincula as variantes
        Map<String, ProdutoVariante> variantesMapa = new HashMap<>();
        List<ProdutoVariante> variantesEntidade = new ArrayList<>();

        if (dto.variantes() != null) {
            for (var vDto : dto.variantes()) {
                ProdutoVariante variante = new ProdutoVariante();
                variante.setNome(vDto.nome());
                variante.setSku(vDto.sku());
                variante.setCodigoBarras(vDto.codigoBarras());
                variante.setColorName(vDto.colorName());
                variante.setColorHex(vDto.colorHex());
                variante.setColorImagePath(vDto.colorImagePath());
                variante.setPesoGramas(vDto.pesoGramas());
                variante.setStock(vDto.stock() != null ? vDto.stock() : 0);
                variante.setEstoqueMinimo(vDto.estoqueMinimo() != null ? vDto.estoqueMinimo() : 0);
                variante.setPriceOverride(vDto.priceOverride());
                variante.setAtivo(true);
                variante.setCriadoEm(OffsetDateTime.now());
                variante.setAtualizadoEm(OffsetDateTime.now());

                variante.setProduto(produto);
                variantesEntidade.add(variante);

                if (vDto.refVariante() != null && !vDto.refVariante().isBlank()) {
                    variantesMapa.put(vDto.refVariante(), variante);
                }
            }
        }
        produto.setVariantes(variantesEntidade);

        // 3. Processa mídias mescladas com a lógica híbrida
        List<ProdutoMidia> midiasEntidade = new ArrayList<>();

        if (dto.midias() != null) {
            int indexArquivo = 0; // Controla qual arquivo binário pegar da lista

            for (var mDto : dto.midias()) {
                MultipartFile arquivoFisico = null;

                // Se NÃO foi fornecida uma URL externa, significa que precisamos buscar o arquivo físico correspondente
                if (mDto.urlExterna() == null || mDto.urlExterna().isBlank()) {
                    if (arquivosMidias != null && indexArquivo < arquivosMidias.size()) {
                        arquivoFisico = arquivosMidias.get(indexArquivo);
                        indexArquivo++; // Avança o ponteiro apenas se consumiu um arquivo
                    }
                }

                // Executa a resolução híbrida do seu CMS
                String pathResolvido = resolverMidiaProduto(arquivoFisico, mDto.urlExterna(), mDto.path());

                if (pathResolvido == null) continue;

                ProdutoMidia midia = new ProdutoMidia();
                midia.setTipo(mDto.tipo());
                midia.setPath(pathResolvido);
                midia.setThumbnailPath(mDto.thumbnailPath());
                midia.setPosterPath(mDto.posterPath());
                midia.setOrdem(mDto.ordem() != null ? mDto.ordem() : 0);
                midia.setCriadoEm(OffsetDateTime.now());
                midia.setProduto(produto);

                // Vincula à variante se a referência bater
                if (mDto.refVariante() != null && variantesMapa.containsKey(mDto.refVariante())) {
                    midia.setVariante(variantesMapa.get(mDto.refVariante()));
                }

                midiasEntidade.add(midia);
            }
        }
        produto.setMidias(midiasEntidade);

        return produtoRepository.save(produto);
    }

    private String resolverMidiaProduto(MultipartFile file, String url, String valorAtual) throws IOException {
        if (file != null && !file.isEmpty()) {
            return cloudinaryService.upload(file);
        }
        if (url != null && !url.isBlank()) {
            return url;
        }
        return valorAtual;
    }
}