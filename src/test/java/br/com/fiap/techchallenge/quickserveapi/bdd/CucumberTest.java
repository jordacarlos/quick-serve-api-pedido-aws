package br.com.fiap.techchallenge.quickserveapi.bdd;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectFile;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
//@SelectClasspathResource("test/resources/features/Pedido.feature") // Caminho para seu arquivo feature
@SelectFile("src/test/resources/features/Pedido.feature")

public class CucumberTest {

}