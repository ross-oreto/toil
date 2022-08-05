package io.oreto.toil.dsl.filter;

import java.util.ArrayList;
import java.util.List;

public class Where {
    public interface Logical {
        String combiner();
        String operator();
        Condition[] conditions();
    }
    static class LogicalAnd implements Logical {
        Condition[] conditions;
        public LogicalAnd(Condition[] conditions) {
            this.conditions = conditions;
        }

        @Override
        public String combiner() {
            return Operator.AND.name();
        }

        @Override
        public String operator() {
            return Operator.AND.name();
        }

        @Override
        public Condition[] conditions() {
            return conditions;
        }
    }
    static class LogicalOr extends LogicalAnd {
        public LogicalOr(Condition[] conditions) {
            super(conditions);
        }

        @Override
        public String combiner() {
            return Operator.AND.name();
        }

        @Override
        public String operator() {
            return Operator.OR.name();
        }
    }

    static class LogicalAndOr extends LogicalAnd {
        public LogicalAndOr(Condition[] conditions) {
            super(conditions);
        }

        @Override
        public String combiner() {
            return Operator.AND.name();
        }

        @Override
        public String operator() {
            return Operator.OR.name();
        }
    }

    static class LogicalOrAnd extends LogicalAnd {
        public LogicalOrAnd(Condition[] conditions) {
            super(conditions);
        }

        @Override
        public String combiner() {
            return Operator.OR.name();
        }

        @Override
        public String operator() {
            return Operator.AND.name();
        }
    }

    private final List<Logical> logic;

    protected Where(Condition... conditions) {
        logic = new ArrayList<>();
        and(conditions);
    }

    public List<Logical> getLogic() {
        return logic;
    }

    public Where and(Condition... conditions) {
        logic.add(new LogicalAnd(conditions));
        return this;
    }

    public Where or(Condition... conditions) {
        logic.add(new LogicalOr(conditions));
        return this;
    }

    public Where andOr(Condition... conditions) {
        logic.add(new LogicalAndOr(conditions));
        return this;
    }

    public Where orAnd(Condition... conditions) {
        logic.add(new LogicalOrAnd(conditions));
        return this;
    }
}
