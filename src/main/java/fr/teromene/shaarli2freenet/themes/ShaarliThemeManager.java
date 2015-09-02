package fr.teromene.shaarli2freenet.themes;

public abstract class ShaarliThemeManager {

	public static ShaarliTheme getTheme(String themeName) {
		System.out.println(themeName);
		if(themeName.equalsIgnoreCase("shaarli")) {
			return new ShaarliThemeShaarli();
		} else {
			return new ShaarliThemeBasic();						
		}
	}
	
}
