package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor

/**
 * This class represents an expression, combining two expressions with an operator.
 * Example: 3 * i
 *
 * Binary expressions always combine two expressions of the type integer with one of 10 possible operators.
 * The operator defines, how the left and the right expression are combined.
 * The semantic type of an expression is dependant of the operator.
 *
 * @param position     The position of the expression in the source code.
 * @param operator     The operator used in this expression.
 * @param leftOperand  The operand on the left-hand side of the operator.
 * @param rightOperand The operand on the right-hand side of the operator.
 */
class BinaryExpression(
    position: Position,
    val operator: Operator,
    val leftOperand: Expression,
    val rightOperand: Expression
) : Expression(position) {
    enum class Operator {
        ADD, // +
        SUB, // -
        MUL, // *
        DIV, // /
        EQU, // =
        NEQ, // #
        LST, // <
        LSE, // <=
        GRT, // >
        GRE; // >=

        /**
         * Checks whether the operator is an arithmetic operator.
         *
         * @return true if the operator is an arithmetic operator.
         */
        val isArithmetic: Boolean get() = TODO() //TODO: Implement yourself if you need this

        /**
         * Checks whether the operator is a comparison operator.
         *
         * @return true if the operator is a comparison operator.
         */
        val isComparison: Boolean get() = TODO() //TODO: Implement yourself if you need this

        /**
         * Flips the operator if it is a comparison operator
         *
         * @return The "opposite" comparison operator
         */
        fun flipComparison(): Operator = TODO() //TODO: Implement yourself if you need this
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString(): String = formatAst("BinaryExpression", operator, leftOperand, rightOperand)
}
