package com.hogiabao7725.hotelbooking.converter;

import com.hogiabao7725.hotelbooking.enums.ValueEnum;
import jakarta.persistence.AttributeConverter;

import java.util.Objects;

/**
 * Abstract base converter for mapping Enums to Integer columns in the database using JPA.
 * <p>
 * This class reduces boilerplate code by providing a generic implementation for any Enum
 * that implements the {@link ValueEnum} interface. Subclasses only need to provide
 * their specific Enum class type through the constructor.
 *
 * @param <E> The specific Enum type that implements {@link ValueEnum}.
 */
public abstract class BaseEnumConverter<E extends Enum<E> & ValueEnum>
        implements AttributeConverter<E, Integer> {

    private final Class<E> enumType;
    private final E[] enumConstants; // cache for performance

    /**
     * Constructs a new BaseEnumConverter.
     *
     * @param enumType The class object of the Enum, required for reflection.
     * @throws NullPointerException if the provided enumType is null.
     */
    protected BaseEnumConverter(Class<E> enumType) {
        this.enumType = Objects.requireNonNull(enumType, "enumType must not be null");
        this.enumConstants = enumType.getEnumConstants();
    }

    /**
     * Converts the Enum entity attribute to its corresponding Integer value for the database.
     *
     * @param attribute The Enum attribute to be converted.
     * @return The Integer value of the Enum, or null if the attribute is null.
     */
    @Override
    public Integer convertToDatabaseColumn(E attribute) {
        return (attribute == null) ? null : attribute.getValue();
    }

    /**
     * Converts the Integer value from the database back to its corresponding Enum attribute.
     *
     * @param dbData The Integer value retrieved from the database.
     * @return The corresponding Enum attribute, or null if the database value is null.
     * @throws IllegalArgumentException if the database value does not match any Enum constant.
     */
    @Override
    public E convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;
        for (E constant : enumConstants) {
            if (constant.getValue() == dbData) {
                return constant;
            }
        }
        throw new IllegalArgumentException(
                String.format("Unknown value %d for %s", dbData, enumType.getSimpleName()));
    }
}
