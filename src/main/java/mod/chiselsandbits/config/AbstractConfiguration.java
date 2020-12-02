package mod.chiselsandbits.config;

import com.google.common.collect.Sets;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.DeprecationHelper;
import net.minecraftforge.common.ForgeConfigSpec.*;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public abstract class AbstractConfiguration
{

    public static final Set<String> LANG_KEYS = Sets.newLinkedHashSet();

    private static String currentCategory = "";

    protected static void createCategory(final Builder builder, final String key)
    {
        builder.comment(DeprecationHelper.translateToLocal(commentTKey(key))).push(key);
        currentCategory = key;
    }

    protected static void swapToCategory(final Builder builder, final String key)
    {
        finishCategory(builder);
        createCategory(builder, key);
    }

    protected static void finishCategory(final Builder builder)
    {
        builder.pop();
        currentCategory = "";
    }

    private static String nameTKey(final String key)
    {
        final String tKey = currentCategory.isEmpty() ? String.format("mod.%s.config.%s", ChiselsAndBits.MODID, key) : String.format("mod.%s.config.%s.%s", ChiselsAndBits.MODID, currentCategory, key);

        final String[] tKeyParts = tKey.split("\\.");
        String workingKey = "";
        for (int i = 0; i < tKeyParts.length; i++)
        {
            final String tKeyPart = tKeyParts[i];
            workingKey = workingKey.isEmpty() ? tKeyPart : String.format("%s.%s", workingKey, tKeyPart);

            if (i < 3)
                continue;

            LANG_KEYS.add(workingKey);
            LANG_KEYS.add(String.format("%s.comment", workingKey));
        }

        return tKey;
    }

    private static String commentTKey(final String key)
    {
        final String tComKey = String.format("%s.comment", nameTKey(key));
        LANG_KEYS.add(tComKey);
        return tComKey;
    }

    private static Builder buildBase(final Builder builder, final String key)
    {
        return builder.comment(DeprecationHelper.translateToLocal(commentTKey(key))).translation(nameTKey(key));
    }

    protected static BooleanValue defineBoolean(final Builder builder, final String key, final boolean defaultValue)
    {
        return buildBase(builder, key).define(key, defaultValue);
    }

    protected static IntValue defineInteger(final Builder builder, final String key, final int defaultValue)
    {
        return defineInteger(builder, key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    protected static IntValue defineInteger(final Builder builder, final String key, final int defaultValue, final int min, final int max)
    {
        return buildBase(builder, key).defineInRange(key, defaultValue, min, max);
    }

    protected static LongValue defineLong(final Builder builder, final String key, final long defaultValue)
    {
        return defineLong(builder, key, defaultValue, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    protected static LongValue defineLong(final Builder builder, final String key, final long defaultValue, final long min, final long max)
    {
        return buildBase(builder, key).defineInRange(key, defaultValue, min, max);
    }

    protected static DoubleValue defineDouble(final Builder builder, final String key, final double defaultValue)
    {
        return defineDouble(builder, key, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    protected static DoubleValue defineDouble(final Builder builder, final String key, final double defaultValue, final double min, final double max)
    {
        return buildBase(builder, key).defineInRange(key, defaultValue, min, max);
    }

    protected static <T> ConfigValue<List<? extends T>> defineList(
        final Builder builder,
        final String key,
        final List<? extends T> defaultValue,
        final Predicate<Object> elementValidator)
    {
        return buildBase(builder, key).defineList(key, defaultValue, elementValidator);
    }

    protected static <V extends Enum<V>> EnumValue<V> defineEnum(final Builder builder, final String key, final V defaultValue)
    {
        return buildBase(builder, key).defineEnum(key, defaultValue);
    }

    protected static ConfigValue<String> defineString(final Builder builder, final String key, final String defaultValue) {
        return buildBase(builder, key).define(key, defaultValue);
    }
}
