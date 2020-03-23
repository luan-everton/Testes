package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Usuario;

public interface EmailSevice {

	public void notificarAtraso(Usuario usuario);
}
