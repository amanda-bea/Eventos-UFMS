package com.ufms.eventos.repository;

import java.util.HashSet;
import java.util.List;

import com.ufms.eventos.model.PresencaConfirmada;
import com.ufms.eventos.model.RespostaFormulario;

/**
 * Interface para padronizar as operações de repositório para a entidade PresencaConfirmada.
 * Esta interface será implementada tanto por PresencaConfirmadaRepository (implementação em memória)
 * quanto por PresencaConfirmadaRepositoryJDBC (implementação com persistência em banco de dados).
 */
public interface IPresencaConfirmadaRepository {

    /**
     * Salva uma resposta de formulário.
     * @param resposta A resposta de formulário a ser salva.
     */
    public void salvar(RespostaFormulario resposta);

    /**
     * Lista todas as respostas de formulário associadas a uma ação específica.
     * @param acaoNome O nome da ação.
     * @return Uma lista de respostas de formulário.
     */
    public List<RespostaFormulario> listarPorAcao(String acaoNome);

    /**
     * Retorna todas as presenças confirmadas armazenadas.
     * @return Um conjunto de todas as presenças confirmadas.
     */
    public HashSet<PresencaConfirmada> getPresencasConfirmadas();

    /**
     * Adiciona uma nova presença confirmada.
     * @param presencaConfirmada A presença confirmada a ser adicionada.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean addPresencaConfirmada(PresencaConfirmada presencaConfirmada);

    /**
     * Remove uma presença confirmada.
     * @param presencaConfirmada A presença confirmada a ser removida.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean removePresencaConfirmada(PresencaConfirmada presencaConfirmada);

    /**
     * Conta o número de presenças confirmadas para uma ação específica.
     * @param idAcao O ID da ação.
     * @return O número de presenças confirmadas.
     */
    public long countByAcaoId(Long idAcao);
}
