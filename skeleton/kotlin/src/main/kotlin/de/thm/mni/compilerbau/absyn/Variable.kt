package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.types.Type

/**
 * This class is the abstract superclass of any variable in an SPL program.
 *
 * A variable is either a simple named variable ([NamedVariable]) or an array access ([ArrayAccess]).
 * Every variable has a semantic [Type] which has to be filled in during phase 4.
 */
sealed class Variable(position: Position) : Node(position) {
    var dataType: Type? = null

    // The ershov number is used for to determine order of evaluation if the register optimization is used
    var ershovNumber: Int? = null
}
