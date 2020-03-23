package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;


import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matcher.MatchersPropios;
import br.ce.wcaquino.utils.DataUtils;


@RunWith(PowerMockRunner.class)
@PrepareForTest(LocacaoService.class)
public class LocacaoServiceTest {

	@InjectMocks
	private LocacaoService service; 
	@Mock
	private SPCService spc ;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailSevice email;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException excepiton = ExpectedException.none();

	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void deveAlugarFilme() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(27 , 03, 2020));


		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(),DataUtils.obterData(27, 03, 2020)), is(true));

		error.checkThat(isMesmaData(locacao.getDataRetorno(),DataUtils.obterData(28, 03, 2020)), is(true));

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
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes =Arrays.asList(umFilme().agora());
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 03, 2020));
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		//verificao
		//Assert.assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
		Assert.assertThat(retorno.getDataRetorno(), MatchersPropios.caiNumaSegunnda());

	}
	@Test
	public void naoDeveAlugarParaNegativado() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme>filmes =Arrays.asList(umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(usuario)).thenReturn(true);
		
		excepiton.expect(LocadoraException.class);
		excepiton.expectMessage("Usuario negativado");
		//acao
		service.alugarFilme(usuario, filmes);
		//verificacao
		verify(spc).possuiNegativacao(usuario);
		
		
	}
	
	@Test
	public void DeveEnviarEmailParaLocacoesAtrasadas() {
		//cenaro
		Usuario usuario = umUsuario().agora();
		List<Locacao> locacoes = 
				Arrays.asList(umLocacao().comUsuario(usuario).comDataRetorno(obterDataComDiferencaDias(-2)).agora());
		
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		//acao
		service.notificacaoAtrasasos();
		
		//verificacao
		verify(email).notificarAtraso(usuario);
		
		
		
	}
	@Test
	public void deveTratarerroSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		
		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha no sistema "));
		//verificacao
		excepiton.expect(LocadoraException.class);
		excepiton.expectMessage("Problemas com SPC, tente novamente");
		
		
		//acao
		service.alugarFilme(usuario, filmes);
		
		
	}
	
	
}
