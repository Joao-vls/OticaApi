package br.com.otica.otica_loja.UseCases.marcas;

import br.com.otica.otica_loja.Entity.Catalogo.Marca;
import br.com.otica.otica_loja.Repository.Catalogo.MarcaRepository;
import br.com.otica.otica_loja.enums.TipoMidia; // Importado para passar o TipoMidia correto
import br.com.otica.otica_loja.service.cms.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;

@Service
public class CriarMarcaUseCase {

    @Autowired
    private MarcaRepository marcaRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    /**
     * Cria uma nova marca no catálogo tratando mídias físicas ou URLs.
     */
    @Transactional
    public Marca criar(String nome,
                       String slug,
                       String descricao,
                       MultipartFile logoFile,
                       String logoUrl,
                       MultipartFile bannerFile,
                       String bannerUrl) throws IOException {

        // 1. Validar duplicidade no banco ANTES de qualquer processamento pesado
        if (marcaRepository.existsByNome(nome)) {
            throw new IllegalArgumentException("Já existe uma marca com este nome.");
        }
        if (marcaRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("Já existe uma marca com este slug.");
        }

        // 2. Criar nova marca (Sem setar ID manualmente se estiver usando @GeneratedValue)
        Marca marca = new Marca();
        marca.setNome(nome);
        marca.setSlug(slug);
        marca.setDescricao(descricao);
        marca.setAtivo(true);
        marca.setCriadoEm(OffsetDateTime.now());
        marca.setAtualizadoEm(OffsetDateTime.now());

        // 3. Resolver as mídias usando a lógica idêntica à do CMS
        marca.setLogoPath(resolverMidia(logoFile, logoUrl));
        marca.setBannerPath(resolverMidia(bannerFile, bannerUrl));

        // 4. Persistir no banco
        return marcaRepository.save(marca);
    }

    /**
     * Lógica reaproveitada para decidir entre arquivo binário, string de URL ou valor padrão.
     */
    private String resolverMidia(MultipartFile file, String url) throws IOException {
        if (file != null && !file.isEmpty()) {
            // Repassa TipoMidia.IMAGE para garantir o resource_type correto nas opções do Cloudinary
            return cloudinaryService.upload(file, TipoMidia.IMAGE);
        }

        if (url != null && !url.isBlank()) {
            return url;
        }

        return null;
    }
}