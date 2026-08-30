package br.com.otica.otica_loja.UseCases.avaliacoes;

import br.com.otica.otica_loja.Repository.Pedidos.PedidoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VerificarCompraUsuarioUseCase {

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    public boolean usuarioComprouECompletouProduto(UUID produtoId, UUID usuarioId) {
        return pedidoItemRepository.existsProdutoEntregueParaUsuario(produtoId, usuarioId);
    }
}