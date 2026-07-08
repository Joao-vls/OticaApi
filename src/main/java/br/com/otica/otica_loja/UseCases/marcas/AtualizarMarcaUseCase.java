package br.com.otica.otica_loja.UseCases.marcas;

import br.com.otica.otica_loja.Entity.Catalogo.Marca;
import br.com.otica.otica_loja.Repository.Catalogo.MarcaRepository;
import br.com.otica.otica_loja.service.cms.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AtualizarMarcaUseCase {

    @Autowired
    private MarcaRepository marcaRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Transactional
    public Marca atualizar(UUID marcaId,
                           String nome,
                           String slug,
                           String descricao,
                           Boolean ativo,
                           MultipartFile logoFile,
                           String logoUrl,
                           MultipartFile bannerFile,
                           String bannerUrl) throws IOException {

        Marca marca = marcaRepository.findById(marcaId)
                .orElseThrow(() -> new IllegalArgumentException("Marca não encontrada."));

        // Validações antes de alterar a entidade
        if (nome != null && !nome.isBlank() && !marca.getNome().equalsIgnoreCase(nome)) {
            if (marcaRepository.existsByNome(nome)) {
                throw new IllegalArgumentException("Já existe uma marca com este nome.");
            }
        }
        if (slug != null && !slug.isBlank() && !marca.getSlug().equalsIgnoreCase(slug)) {
            if (marcaRepository.existsBySlug(slug)) {
                throw new IllegalArgumentException("Já existe uma marca com este slug.");
            }
        }

        // Alterações de dados textuais
        if (nome != null) marca.setNome(nome);
        if (slug != null) marca.setSlug(slug);
        if (descricao != null) marca.setDescricao(descricao);
        if (ativo != null) marca.setAtivo(ativo);

        // Processamento seguro das mídias (passando o valor atual como fallback)
        marca.setLogoPath(resolverMidia(logoFile, logoUrl, marca.getLogoPath()));
        marca.setBannerPath(resolverMidia(bannerFile, bannerUrl, marca.getBannerPath()));

        marca.setAtualizadoEm(OffsetDateTime.now());

        return marcaRepository.save(marca);
    }

    private String resolverMidia(MultipartFile file, String url, String valorAtual) throws IOException {
        if (file != null && !file.isEmpty()) {
            return cloudinaryService.upload(file);
        }
        if (url != null && !url.isBlank()) {
            return url;
        }
        return valorAtual;
    }
}