package io.oreto.toil.dsl.column;

import io.oreto.toil.dsl.filter.Condition;

public interface VarChar extends Column<CharSequence> {
    Condition eq(CharSequence value);
    Condition equalsIgnoreCase(CharSequence value);

    Condition notEqualsIgnoreCase(CharSequence value);

    Condition like(CharSequence value);
    Condition likeIgnoreCase(CharSequence value);

    Condition notLike(CharSequence value);
    Condition notLikeIgnoreCase(CharSequence value);

    Condition contains(CharSequence value);

    Condition containsIgnoreCase(CharSequence value);

    Condition notContains(CharSequence value);
    Condition notContainsIgnoreCase(CharSequence value);

    Condition startsWith(CharSequence value);
    Condition startsWithIgnoreCase(CharSequence value);

    Condition notStartingWith(CharSequence value);
    Condition notStartingWithIgnoreCase(CharSequence value);

    Condition endsWith(CharSequence value);
    Condition endsWithIgnoreCase(CharSequence value);

    Condition notEndingWith(CharSequence value);
    Condition notEndingWithIgnoreCase(CharSequence value);
}
