package liquidjava.rj_language.ast;

import java.util.List;

import liquidjava.diagnostics.errors.LJError;
import liquidjava.rj_language.visitors.ExpressionVisitor;

public class Enumerate extends Expression {

    private final String enumTypeName;
    private final String enumConstantName;

    public Enumerate(String enumTypeName, String enumConstantName) {
        this.enumTypeName = enumTypeName;
        this.enumConstantName = enumConstantName;
    }

    public String getEnumTypeName() {
        return enumTypeName;
    }

    public String getEnumConstantName() {
        return enumConstantName;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) throws LJError {
        return visitor.visitEnumerate(this);
    }

    @Override
    public void getVariableNames(List<String> toAdd) {
        // end leaf
    }

    @Override
    public void getStateInvocations(List<String> toAdd, List<String> all) {
        // end leaf
    }

    @Override
    public boolean isBooleanTrue() {
        return false;
    }

    @Override
    public String toString() {
        return enumTypeName + "." + enumConstantName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((enumTypeName == null) ? 0 : enumTypeName.hashCode());
        result = prime * result + ((enumConstantName == null) ? 0 : enumConstantName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Enumerate other = (Enumerate) obj;
        return enumTypeName.equals(other.enumTypeName) && enumConstantName.equals(other.enumConstantName);
    }

    @Override
    public Expression clone() {
        return new Enumerate(enumTypeName, enumConstantName);
    }
}
