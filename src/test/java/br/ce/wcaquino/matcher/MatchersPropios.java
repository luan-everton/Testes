package br.ce.wcaquino.matcher;

import java.util.Calendar;

public class MatchersPropios {
	public static DiaSemanaMatcher caiEm(Integer diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}
	
	public static DiaSemanaMatcher caiNumaSegunnda() {
		return new DiaSemanaMatcher(Calendar.MONDAY);
	}
	
	
	public static DataDiferencaDiasMatcher ehHojeDataDiferencaDias(Integer qtdDias) {
		return  new DataDiferencaDiasMatcher(qtdDias);
	}
	
	public static DataDiferencaDiasMatcher ehHoje() {
		return  new DataDiferencaDiasMatcher(0);
	}

}
