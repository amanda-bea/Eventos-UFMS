package com.ufms.eventos.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppDataManager {

    private static final String NOME_PASTA_APP = ".eventos-ufms-data";
    private static final Path CAMINHO_BASE = Paths.get(System.getProperty("user.home"), NOME_PASTA_APP);
    private static final Path CAMINHO_IMAGENS = CAMINHO_BASE.resolve("imagens");

    /**
     * Garante que as pastas de dados da aplicação existam.
     */
    public static void inicializarPastas() {
        try {
            File pasta = CAMINHO_IMAGENS.toFile();
            if (!pasta.exists()) {
                boolean sucesso = pasta.mkdirs();
                if (sucesso) {
                    System.out.println("Pasta de dados criada em: " + CAMINHO_IMAGENS.toString());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar pastas de dados da aplicação.");
            e.printStackTrace();
        }
    }

    /**
     * Retorna o caminho absoluto onde uma nova imagem deve ser salva.
     */
    public static Path getCaminhoParaNovaImagem(String extensao) {
        String nomeUnico = java.util.UUID.randomUUID().toString() + "." + extensao;
        return CAMINHO_IMAGENS.resolve(nomeUnico);
    }
}