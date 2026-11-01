package br.com.otica.otica_loja.dto.cms;

import java.util.List;

public class VitrineCacheDTO {
    private List<VitrineResponseDTO> vitrines;

    // Construtor vazio necessário para o Jackson (Redis)
    public VitrineCacheDTO() {}

    public VitrineCacheDTO(List<VitrineResponseDTO> vitrines) {
        this.vitrines = vitrines;
    }

    public List<VitrineResponseDTO> getVitrines() {
        return vitrines;
    }

    public void setVitrines(List<VitrineResponseDTO> vitrines) {
        this.vitrines = vitrines;
    }
}