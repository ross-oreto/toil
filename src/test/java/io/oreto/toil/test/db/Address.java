package io.oreto.toil.test.db;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Address implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String line;

    public Long getId() {
        return id;
    }

    @JsonProperty("ID")
    public void setId(Long id) {
        this.id = id;
    }

    public String getLine() {
        return line;
    }

    @JsonProperty("LINE")
    public void setLine(String line) {
        this.line = line;
    }

    public Address withId(Long id) {
        this.id = id;
        return this;
    }
    public Address withLine(String line) {
        this.line = line;
        return this;
    }

    @Override
    public String toString() {
        return getLine();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), line);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Address address)) {
            return false;
        }
        return Objects.equals(address.getId(), getId())
                && Objects.equals(address.getLine(), getLine());
    }
}
