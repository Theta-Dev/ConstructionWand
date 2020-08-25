package thetadev.constructionwand.basics.options;

public interface IEnumOption
{
	public int getOrdinal();
	public String getOptionKey();
	public String getValue();
	default String getTranslationKey() {
		return getOptionKey() + "." + getValue();
	}
	public IEnumOption next(boolean dir);
	public IEnumOption fromName(String name);
}
