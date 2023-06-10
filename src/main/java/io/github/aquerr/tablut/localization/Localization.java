package io.github.aquerr.tablut.localization;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localization
{
    private static final Locale LOCALE = Locale.getDefault();
    private static ResourceBundle resourceBundle;

    static
    {
        try
        {
            resourceBundle = ResourceBundle.getBundle("lang/tablut", LOCALE);
        }
        catch (Exception exception)
        {
            resourceBundle = ResourceBundle.getBundle("lang/tablut", Locale.US);
        }
    }

    public static String translate(String key)
    {
        return resourceBundle.getString(key);
    }

    private Localization()
    {
        throw new IllegalStateException("You should not instantiate this class!");
    }
}
