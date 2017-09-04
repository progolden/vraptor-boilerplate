package br.com.caelum.vraptor.boilerplate.company;

import br.com.caelum.vraptor.boilerplate.ComponentFactory;

public final class CompanyThemeFactory extends ComponentFactory<CompanyTheme> {

	private static CompanyThemeFactory instance = new CompanyThemeFactory();
	
	public static CompanyThemeFactory getInstance() {
		return instance;
	}
	
	public static CompanyTheme getDefaultTheme() {
		return instance.get(0);
	}
	
	private CompanyThemeFactory() {
		this.register(new ThemeDefault());
	}
	
	@Override
	public String toJSON() {
		StringBuilder json = new StringBuilder();
		json.append("[");
		for (int t = 0; t < this.size(); t++) {
			if (t > 0)
				json.append(",");
			CompanyTheme theme = (CompanyTheme) this.get(t);
			json
				.append("{")
				.append("\"css\":\"").append(theme.getCSSFile()).append("\"")
				.append(",\"label\":\"").append(theme.getDisplayName()).append("\"")
				.append(",\"id\":\"").append(theme.getId()).append("\"")
				.append("}")
			;
		}
		json.append("]");
		return json.toString();
	}
	
	public class ThemeDefault extends CompanyTheme {
		@Override
		public String getDisplayName() {
			return "Tema Padrão";
		}
		@Override
		public String getCSSFile() {
			return "theme-default.css";
		}
	}
}
