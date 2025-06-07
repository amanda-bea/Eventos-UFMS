package com.ufms.eventos.model;

public enum Categoria {
    CULTURA("Cultura"), 
    EDUCACAO("Educação"),
    SAUDE("Saúde"),
    TECNOLOGIA("Tecnologia"),
    ESPORTE("Esporte"),
    LAZER("Lazer"),
    OUTROS("Outros");

    private final String displayName;

    Categoria(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}