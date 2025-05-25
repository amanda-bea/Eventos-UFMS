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
    // Se você preferir que os nomes das constantes (Cultura, Educacao) sejam exibidos diretamente,
    // você pode remover o construtor e o campo displayName, e o @Override de toString().
    // O Java usará o .name() da constante por padrão se toString() não for sobrescrito.
    // Para ComboBox, sobrescrever toString() é a forma padrão de customizar a exibição.
}