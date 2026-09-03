package com.wdiscute.utils;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public interface StringRepresentableAutoForEnums extends StringRepresentable
{
    @Override
    default String getSerializedName()
    {
        return ((Enum<?>) this).name().toLowerCase(Locale.ROOT);
    }
}
