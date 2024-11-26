package br.com.fiap.techchallenge.quickserveapi.application.handler.interfaces;

import java.util.List;
import java.util.Map;

public interface dbconnection {
    List<Map<String, Object>> buscarPorParametros(String tabela, String[] campos, ParametroBd[] parametros);

    List<Map<String, Object>> Inserir(String tabela, String[] campos, ParametroBd[] parametros);

    List<Map<String, Object>> Update(String tabela, String[] campos, ParametroBd[] parametros);
}
