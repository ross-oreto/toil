package io.oreto.toil.dsl.column;


import io.oreto.toil.dsl.Table;
import io.oreto.toil.dsl.filter.Condition;
import io.oreto.toil.dsl.filter.Operator;
import io.oreto.toil.dsl.filter.Param;
import io.oreto.toil.dsl.function.Lower;

public class VarCharImpl extends ColumnImpl<CharSequence> implements VarChar {
    public VarCharImpl(String name, boolean nullable, Table table) {
        super(name, CharSequence.class, nullable, table);
    }

    @Override
    public Condition eq(CharSequence value) {
        return new Condition(this, Operator.EQ, Param.of(value.toString()));
    }

    @Override
    public Condition equalsIgnoreCase(CharSequence value) {
        return new Condition(new Lower(this), Operator.EQ, Param.of(value.toString().toLowerCase()));
    }

    @Override
    public Condition ne(CharSequence value) {
        return new Condition(this, Operator.NE, Param.of(value.toString()));
    }

    @Override
    public Condition notEqualsIgnoreCase(CharSequence value) {
        return new Condition(new Lower(this), Operator.NE, Param.of(value.toString().toLowerCase()));
    }

    @Override
    public Condition like(CharSequence value) {
        return new Condition(this, Operator.LIKE, Param.of(value));
    }

    @Override
    public Condition likeIgnoreCase(CharSequence value) {
        return new Condition(new Lower(this), Operator.LIKE, Param.of(value.toString().toLowerCase()));
    }

    @Override
    public Condition notLike(CharSequence value) {
        return new Condition(this, Operator.NOT_LIKE, Param.of(value));
    }

    @Override
    public Condition notLikeIgnoreCase(CharSequence value) {
        return new Condition(new Lower(this), Operator.NOT_LIKE, Param.of(value.toString().toLowerCase()));
    }

    @Override
    public Condition contains(CharSequence value) {
        return new Condition(this, Operator.LIKE, Param.of(String.format("%%%s%%", value)));
    }

    @Override
    public Condition containsIgnoreCase(CharSequence value) {
        return new Condition(new Lower(this), Operator.LIKE, Param.of(String.format("%%%s%%", value.toString().toLowerCase())));
    }

    @Override
    public Condition notContains(CharSequence value) {
        return new Condition(this, Operator.NOT_LIKE, Param.of(String.format("%%%s%%", value)));
    }

    @Override
    public Condition notContainsIgnoreCase(CharSequence value) {
        return new Condition(new Lower(this), Operator.NOT_LIKE, Param.of(String.format("%%%s%%", value.toString().toLowerCase())));
    }

    @Override
    public Condition startsWith(CharSequence value) {
        return new Condition(this, Operator.LIKE, Param.of(String.format("%s%%", value)));
    }

    @Override
    public Condition startsWithIgnoreCase(CharSequence value) {
        return new Condition(new Lower(this), Operator.LIKE, Param.of(String.format("%s%%", value.toString().toLowerCase())));
    }

    @Override
    public Condition notStartingWith(CharSequence value) {
        return new Condition(this, Operator.NOT_LIKE, Param.of(String.format("%s%%", value)));
    }

    @Override
    public Condition notStartingWithIgnoreCase(CharSequence value) {
        return new Condition(new Lower(this), Operator.NOT_LIKE, Param.of(String.format("%s%%", value.toString().toLowerCase())));
    }

    @Override
    public Condition endsWith(CharSequence value) {
        return new Condition(this, Operator.LIKE, Param.of(String.format("%%%s", value)));
    }

    @Override
    public Condition endsWithIgnoreCase(CharSequence value) {
        return new Condition(new Lower(this), Operator.LIKE, Param.of(String.format("%%%s", value.toString().toLowerCase())));
    }

    @Override
    public Condition notEndingWith(CharSequence value) {
        return new Condition(this, Operator.NOT_LIKE, Param.of(String.format("%%%s", value)));
    }

    @Override
    public Condition notEndingWithIgnoreCase(CharSequence value) {
        return new Condition(new Lower(this), Operator.NOT_LIKE, Param.of(String.format("%%%s", value.toString().toLowerCase())));
    }
}
