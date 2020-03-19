package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matcher.MatchersPropios.caiEm;
import static br.ce.wcaquino.matcher.MatchersPropios.ehHoje;
import static br.ce.wcaquino.matcher.MatchersPropios.ehHojeDataDiferencaDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matcher.DiaSemanaMatcher;
import br.ce.wcaquino.matcher.MatchersPropios;
import br.ce.wcaquino.utils.DataUtils;
import buildermaster.BuilderMaster;

public class LocacaoServiceTest {

	private LocacaoService service;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException excepiton = ExpectedException.none();

	@Before
	public void setup() {
		service = new LocacaoService();

	}

	@Test
	public void deveAlugarFilme() throws Exception {
	    Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());

		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		//error.checkThat(locacao.getDataLocacao(), ehHoje());
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
		//error.checkThat(locacao.getDataLocacao(), ehHojeDataDiferencaDias(1));

	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().SemEstoque().agora());

		// acao
		service.alugarFilme(usuario, filmes);

	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		// cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		// acao

		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}

	}

	@Test
	public void naoDeveAlugarFilmeSemfilme() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = umUsuario().agora();
		excepiton.expect(LocadoraException.class);
		excepiton.expectMessage("Filme vazio");
		// acao
		service.alugarFilme(usuario, null);

	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes =Arrays.asList(umFilme().agora());
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		//verificao
		//Assert.assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
		Assert.assertThat(retorno.getDataRetorno(), MatchersPropios.caiNumaSegunnda());

	}
	public static void main(String[] args) {
		
		new BuilderMaster().gerarCodigoClasse(Locacao.class);
	}
}
