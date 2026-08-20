package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.dto.ProdutoRequestDTO;
import br.com.otica.otica_loja.enums.TipoMidia;
import br.com.otica.otica_loja.service.cms.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CriarProdutoUseCase {

    private final ProdutoRepository produtoRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public Produto criar(ProdutoRequestDTO dto,
                         List<MultipartFile> arquivosImagens,
                         List<MultipartFile> arquivosVideos,
                         List<MultipartFile> arquivosThumbnails,
                         List<MultipartFile> arquivos3d) throws IOException {

        if (produtoRepository.findBySlug(dto.slug()).isPresent()) {
            throw new IllegalArgumentException("Já existe um produto com este slug.");
        }

        // 1. Cria a estrutura do Produto base
        Produto produto = new Produto();
        produto.setMarcaId(dto.marcaId());
        produto.setCategoriaId(dto.categoriaId());
        produto.setNome(dto.nome());
        produto.setSlug(dto.slug());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setCategoria(dto.categoria());
        produto.setSpecs(dto.specs() != null ? dto.specs() : "{}");
        produto.setDestaque(dto.destaque() != null && dto.destaque());
        produto.setAtivo(true);
        produto.setCriadoEm(OffsetDateTime.now());
        produto.setAtualizadoEm(OffsetDateTime.now());

        // 2. Mapeia e vincula as variantes
        Map<String, ProdutoVariante> variantesMapa = new HashMap<>();
        Set<ProdutoVariante> variantesEntidade = new LinkedHashSet<>();

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

        // 3. Salva no banco PRIMEIRA VEZ para validar restrições e relacionamentos
        Produto produtoSalvo = produtoRepository.save(produto);

        // 4. Se o banco salvou sem erros, realiza os uploads dos arquivos
        Set<ProdutoMidia> midiasEntidade = new LinkedHashSet<>();

        if (dto.midias() != null) {
            int indexImagem = 0;
            int indexVideo = 0;
            int indexThumbnail = 0;
            int index3d = 0;

            for (var mDto : dto.midias()) {
                MultipartFile arquivoFisico = null;
                MultipartFile thumbnailFisica = null;

                TipoMidia tipoMidia = mDto.tipo();
                if (mDto.path() != null && mDto.path().toLowerCase().endsWith(".glb")) {
                    tipoMidia = TipoMidia.THREE_D;
                }

                if (mDto.urlExterna() == null || mDto.urlExterna().isBlank()) {
                    if (tipoMidia == TipoMidia.THREE_D && arquivos3d != null && index3d < arquivos3d.size()) {
                        arquivoFisico = arquivos3d.get(index3d++);
                    } else if (tipoMidia == TipoMidia.VIDEO && arquivosVideos != null && indexVideo < arquivosVideos.size()) {
                        arquivoFisico = arquivosVideos.get(indexVideo++);
                    } else if (tipoMidia == TipoMidia.IMAGE && arquivosImagens != null && indexImagem < arquivosImagens.size()) {
                        arquivoFisico = arquivosImagens.get(indexImagem++);
                    }
                }

                if (tipoMidia == TipoMidia.VIDEO && (mDto.urlExternaThumbnail() == null || mDto.urlExternaThumbnail().isBlank())) {
                    if (arquivosThumbnails != null && indexThumbnail < arquivosThumbnails.size()) {
                        thumbnailFisica = arquivosThumbnails.get(indexThumbnail++);
                    }
                }

                // O upload no Cloudinary ocorre somente após a primeira persistência com sucesso no DB
                String pathResolvido = resolverMidiaProduto(arquivoFisico, mDto.urlExterna(), mDto.path(), tipoMidia);
                String thumbResolvido = resolverMidiaProduto(thumbnailFisica, mDto.urlExternaThumbnail(), mDto.thumbnailPath(), TipoMidia.IMAGE);

                if (pathResolvido == null) {
                    continue;
                }

                ProdutoMidia midia = new ProdutoMidia();
                midia.setTipo(tipoMidia);
                midia.setPath(pathResolvido);
                midia.setThumbnailPath(thumbResolvido);
                midia.setPosterPath(mDto.posterPath());
                midia.setOrdem(mDto.ordem() != null ? mDto.ordem() : 0);
                midia.setCriadoEm(OffsetDateTime.now());
                midia.setProduto(produtoSalvo);

                if (mDto.refVariante() != null && variantesMapa.containsKey(mDto.refVariante())) {
                    midia.setVariante(variantesMapa.get(mDto.refVariante()));
                }

                midiasEntidade.add(midia);
            }
        }

        produtoSalvo.setMidias(midiasEntidade);

        // Atualiza o produto com as mídias salvas
        return produtoRepository.save(produtoSalvo);
    }

    private String resolverMidiaProduto(MultipartFile file, String url, String valorAtual, TipoMidia tipo) throws IOException {
        if (file != null && !file.isEmpty()) {
            return cloudinaryService.upload(file, tipo);
        }

        if (url != null && !url.isBlank()) {
            return url;
        }

        if (valorAtual != null && !valorAtual.startsWith("http://") && !valorAtual.startsWith("https://")) {
            return null;
        }

        return valorAtual;
    }
}