package com.tech.task;

class Range {
    private final Double from;
    private final Double to;

    public Range(Double from, Double to) {
        this.from = from;
        this.to = to;
    }

    public Double getFrom() {
        return from;
    }

    public Double getTo() {
        return to;
    }

    boolean inRange(Double value) {
        return value >= from && value < to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range range = (Range) o;

        if (!from.equals(range.from)) return false;
        return to.equals(range.to);

    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Range{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}