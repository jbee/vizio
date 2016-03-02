package vizio;

public class Query {

	public static enum Property {
		temp, status, goal, stimulus,
		heat, version, start, end, age,
		users, active_users, passive_users,
		area, product,
		summary,
		exploitable
	}

	public static enum Operator {
		//'=' | '!=' | '>' | '<' | '>=' | '<=' | '~' | '!~' | '/' | '!/'
		eq("="), neq("!="), gt(">"), lt("<"), ge(">="), le("<="), in("~"), nin("!~"), any("/"), nany("!/");

		public String symbol;

		private Operator(String symbol) {
			this.symbol = symbol;
		}

		public static Operator forSymbol(String symbol) {
			for (Operator op : values()) {
				if (op.symbol.equals(symbol))
					return op;
			}
			throw new IllegalArgumentException(symbol);
		}
	}

	public static class Filter {

		public Property prop;
		public Operator op;
		public String[] value;

		public Filter(Property prop, Operator op, String[] value) {
			super();
			this.prop = prop;
			this.op = op;
			this.value = value;
		}

		@Override
		public String toString() {
			String vs = value.length == 1 ? value[0] : "{"+join(value)+"}";
			return prop.name()+op.symbol+vs;
		}
	}

	public static class Range {

		public final int start;
		public final int end;

		public Range(int start, int end) {
			super();
			this.start = start;
			this.end = end;
		}

		@Override
		public String toString() {
			String e = end < 0 ? "*" : String.valueOf(end);
			String s  = start < 0 ? "*" : String.valueOf(start);
			return String.format("{%s %s}", s, e);
		}
	}

	public Range range;
	public Filter[] filters;
	public Property[] orders;

	@Override
	public String toString() {
		return String.format("%s[%s]<%s>", range, join(filters), join(orders));
	}

	static String join(Object[] values) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			if (i > 0) b.append(' ');
			b.append(values[i]);
		}
		return b.toString();
	}
}
