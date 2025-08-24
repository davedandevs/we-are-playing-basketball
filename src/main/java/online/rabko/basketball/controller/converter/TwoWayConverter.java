package online.rabko.basketball.controller.converter;

import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Two-way converter base: resolves generic types via ResolvableType, supports nulls, and registers
 * both directions for Spring's ConversionService.
 */
public abstract class TwoWayConverter<S, T> implements GenericConverter {

    private final Class<S> sourceClass;
    private final Class<T> targetClass;

    /**
     * Constructor.
     */
    @SuppressWarnings("unchecked")
    protected TwoWayConverter() {
        ResolvableType self = ResolvableType.forClass(getClass()).as(TwoWayConverter.class);
        this.sourceClass = (Class<S>) self.getGeneric(0).toClass();
        this.targetClass = (Class<T>) self.getGeneric(1).toClass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<ConvertiblePair> pairs = new LinkedHashSet<>(2);
        pairs.add(new ConvertiblePair(sourceClass, targetClass));
        pairs.add(new ConvertiblePair(targetClass, sourceClass));
        return pairs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public Object convert(@Nullable Object source, @NonNull TypeDescriptor sourceType,
        @NonNull TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }

        Class<?> actualSourceType = sourceType.getType();

        if (sourceClass.isAssignableFrom(actualSourceType)) {
            return convert(sourceClass.cast(source));
        } else if (targetClass.isAssignableFrom(actualSourceType)) {
            return convertBack(targetClass.cast(source));
        }

        throw new IllegalArgumentException(
            "Unsupported conversion: " + actualSourceType.getName() + " -> " + targetType.getType()
                .getName()
        );
    }

    /**
     * Forward conversion: S -> T.
     */
    protected abstract T convert(S source);

    /**
     * Backward conversion: T -> S.
     */
    protected abstract S convertBack(T target);
}
