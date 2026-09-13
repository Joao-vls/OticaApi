package br.com.otica.otica_loja.UseCases.cupons;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarCuponsUseCase {

    @Autowired
    private CupomRepository cupomRepository;

    public List<Cupom> listarTodos() {
        return cupomRepository.findAll();
    }
}