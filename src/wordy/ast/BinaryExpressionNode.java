package wordy.ast;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

import wordy.interpreter.EvaluationContext;

import static wordy.ast.Utils.orderedMap;

/**
 * Two expressions joined by an operator (e.g. “x plus y”) in a Wordy abstract syntax tree.
 */
public class BinaryExpressionNode extends ExpressionNode {
    public enum Operator {
        ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, EXPONENTIATION
    }

    private final Operator operator;
    private final ExpressionNode lhs, rhs;

    public BinaryExpressionNode(Operator operator, ExpressionNode lhs, ExpressionNode rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Map<String, ASTNode> getChildren() {
        return orderedMap(
            "lhs", lhs,
            "rhs", rhs);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        BinaryExpressionNode that = (BinaryExpressionNode) o;
        return this.operator == that.operator
            && this.lhs.equals(that.lhs)
            && this.rhs.equals(that.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, lhs, rhs);
    }

    @Override
    public String toString() {
        return "BinaryExpressionNode{"
            + "operator=" + operator
            + ", lhs=" + lhs
            + ", rhs=" + rhs
            + '}';
    }

    @Override
    protected String describeAttributes() {
        return "(operator=" + operator + ')';
    }

    @Override
    protected double doEvaluate(EvaluationContext context){
        double lhs = this.lhs.doEvaluate(context);
        double rhs = this.rhs.doEvaluate(context);
        if (this.operator.equals(Operator.ADDITION)) return lhs + rhs;
        else if (this.operator.equals(Operator.SUBTRACTION)) return lhs - rhs;
        else if (this.operator.equals(Operator.MULTIPLICATION)) return lhs * rhs;
        else if (this.operator.equals(Operator.DIVISION)) return lhs / rhs;
        else return Math.pow(lhs, rhs);
    }

    public void compile(PrintWriter out) {
        if (this.operator.equals(Operator.EXPONENTIATION)) {
            out.append("Math.pow(");
            this.lhs.compile(out);
            out.append(",");
            this.rhs.compile(out);
            out.append(")");
        }
        else {
            out.append("(");
            this.lhs.compile(out);
            if (this.operator.equals(Operator.ADDITION)) out.append("+");
            else if (this.operator.equals(Operator.SUBTRACTION)) out.append("-");
            else if (this.operator.equals(Operator.MULTIPLICATION)) out.append("*");
            else if (this.operator.equals(Operator.DIVISION)) out.append("/");
            this.rhs.compile(out);
            out.append(")");
        }
    }
}
