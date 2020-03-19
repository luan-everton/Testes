package br.ce.wcaquino.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.servicos.CalculadoraTest;
import br.ce.wcaquino.servicos.CalculoValorLotacaoTeste;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

 //@RunWith(Suite.class)
@SuiteClasses({
	CalculoValorLotacaoTeste.class,
	CalculadoraTest.class,
	LocacaoServiceTest.class
})
public class SuiteExecucao {
	

}
