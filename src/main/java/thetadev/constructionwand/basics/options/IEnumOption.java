package thetadev.constructionwand.basics.options;

public interface IEnumOption
{
	public int getOrdinal();
	public String getOptionKey();
	public String getValue();
	public String getTranslationKey();
	public IEnumOption next(boolean dir);
	public IEnumOption fromName(String name);
}
