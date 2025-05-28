package com.ufms.eventos.services;

import java.util.List;
import java.util.stream.Collectors;

import com.ufms.eventos.dto.RespostaFormularioDTO;
import com.ufms.eventos.model.RespostaFormulario;
import com.ufms.eventos.repository.RespostaFormularioRepository;

public class RespostaFormularioService {
    private RespostaFormularioRepository repository = new RespostaFormularioRepository();

    public void salvarResposta(RespostaFormularioDTO dto) {
        RespostaFormulario resposta = new RespostaFormulario();
        resposta.setNome(dto.getNome());
        resposta.setEmail(dto.getEmail());
        resposta.setCpf(dto.getCpf());
        resposta.setCurso(dto.getCurso());
        resposta.setRespostasExtras(dto.getRespostasExtras());
        resposta.setNomeAcao(dto.getAcaoNome());
        repository.salvar(resposta);
    }

    public List<RespostaFormularioDTO> listarRespostasPorAcao(String acaoNome) {
        return repository.listarPorAcao(acaoNome).stream().map(r -> {
            RespostaFormularioDTO dto = new RespostaFormularioDTO();
            dto.setNome(r.getNome());
            dto.setEmail(r.getEmail());
            dto.setCpf(r.getCpf());
            dto.setCurso(r.getCurso());
            dto.setRespostasExtras(r.getRespostasExtras());
            dto.setAcaoNome(r.getNomeAcao());
            return dto;
        }).collect(Collectors.toList());
    }
}
