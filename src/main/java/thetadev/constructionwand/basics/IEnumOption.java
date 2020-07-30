package thetadev.constructionwand.basics;

public interface IEnumOption
{
	public String getOptionKey();
	public String getValue();
	public String getTranslationKey();
	public IEnumOption next();
	public IEnumOption fromName(String name);
}
