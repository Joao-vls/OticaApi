package br.com.otica.otica_loja.UseCases.carrinho;

import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CriarCarrinhoUseCase {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Cria um carrinho para o usuário, caso não exista.
     */
    public Carrinho criar(UUID usuarioId) {
        // 1. Verificar se usuário existe
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // 2. Verificar se já existe carrinho para o usuário
        if (carrinhoRepository.existsByUsuarioId(usuarioId)) {
            throw new IllegalArgumentException("Já existe um carrinho para este usuário.");
        }

        // 3. Criar novo carrinho
        Carrinho carrinho = new Carrinho();
        carrinho.setUsuarioId(usuarioId);
        carrinho.setCriadoEm(OffsetDateTime.now());
        carrinho.setAtualizadoEm(OffsetDateTime.now());

        // 4. Persistir
        return carrinhoRepository.save(carrinho);
    }
}
