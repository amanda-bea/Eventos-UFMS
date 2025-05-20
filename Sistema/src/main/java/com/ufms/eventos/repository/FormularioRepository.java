package com.ufms.eventos.repository;

import com.ufms.eventos.model.Formulario; //nadaaa a ver isso aqui ainda não sei o fazer
import java.util.ArrayList;
import java.util.List;

public class FormularioRepository {

    private List<Formulario> formularios;

    public FormularioRepository() {
        this.formularios = new ArrayList<>();
    }

    public Formulario addFormulario(Formulario formulario) {
        formularios.add(formulario);
        return formulario;
    }

    public Formulario updateFormulario(Long id, Formulario formularioAtualizado) {
        for (int i = 0; i < formularios.size(); i++) {
            if (formularios.get(i).getId() == id) {
                formularios.set(i, formularioAtualizado);
                return formularioAtualizado;
            }
        }
        return null;
    }

    public Formulario getFormulario(Long id) {
        for (Formulario formulario : formularios) {
            if (formulario.getId() == id) {
                return formulario;
            }
        }
        return null;
    }

    public List<Formulario> getFormularios() {
        return formularios;
    }

}
