package de.thm.mni.compilerbau.phases._04a_tablebuild;

import de.thm.mni.compilerbau.absyn.*;
import de.thm.mni.compilerbau.absyn.visitor.DoNothingVisitor;
import de.thm.mni.compilerbau.table.*;
import de.thm.mni.compilerbau.types.ArrayType;
import de.thm.mni.compilerbau.types.Type;
import de.thm.mni.compilerbau.utils.SplError;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to create and populate a {@link SymbolTable} containing entries for every symbol in the currently
 * compiled SPL program.
 * Every declaration of the SPL program needs its corresponding entry in the {@link SymbolTable}.
 * <p>
 * Calculated {@link Type}s can be stored in and read from the dataType field of the {@link Expression},
 * {@link TypeExpression} or {@link Variable} classes.
 */
public class TableBuilder {
    private final boolean showTables;
    SymbolTable table;

    public TableBuilder(boolean showTables) {
        this.showTables = showTables;
    }

    public SymbolTable buildSymbolTable(Program program) {
        table = TableInitializer.initializeGlobalTable();
        TableBuilderVisitor visitor = new TableBuilderVisitor(table);
        visitor.visit(program);
        //TODO (assignment 4a): Initialize a symbol table with all predefined symbols and fill it with user-defined symbols
        //  throw new NotImplemented();
        return table;
    }

    class TableBuilderVisitor extends DoNothingVisitor {
        SymbolTable table;

        TableBuilderVisitor(SymbolTable s) {
            this.table = s;
        }

        public void visit(Program program) {
            for (GlobalDeclaration g : program.declarations) {
                g.accept(this);
            }
        }

        public void visit(VariableDeclaration variableDeclaration) {
            variableDeclaration.typeExpression.accept(this);
            table.enter(variableDeclaration.name,
                    new VariableEntry(variableDeclaration.typeExpression.dataType, false),
                    SplError.RedeclarationAsVariable(variableDeclaration.position, variableDeclaration.name));
        }

        public void visit(ArrayTypeExpression arrayTypeExpression) {
            arrayTypeExpression.baseType.accept(this);
            arrayTypeExpression.dataType = new ArrayType(
                    arrayTypeExpression.baseType.dataType, arrayTypeExpression.arraySize);
        }

        public void visit(TypeDeclaration typeDeclaration) {
            typeDeclaration.typeExpression.accept(this);
            table.enter(typeDeclaration.name,
                    new TypeEntry(typeDeclaration.typeExpression.dataType), SplError.RedeclarationAsType(typeDeclaration.position, typeDeclaration.name));
        }

        @Override
        public void visit(NamedTypeExpression namedTypeExpression) {
            Entry entry = table.lookup(namedTypeExpression.name, SplError.UndefinedType(namedTypeExpression.position, namedTypeExpression.name));
            if (!(entry instanceof TypeEntry)) {
                throw SplError.NotAType(namedTypeExpression.position, namedTypeExpression.name);
            }
            namedTypeExpression.dataType = ((TypeEntry) entry).type;
        }

        @Override
        public void visit(ParameterDeclaration parameterDeclaration) {
            SymbolTable symbolTable = table.getUpperLevel().orElseThrow();
            parameterDeclaration.typeExpression.accept(new TableBuilderVisitor(symbolTable));
            if (parameterDeclaration.typeExpression.dataType instanceof ArrayType && !parameterDeclaration.isReference) {
                throw SplError.MustBeAReferenceParameter(parameterDeclaration.position, parameterDeclaration.name);
            }
            table.enter(parameterDeclaration.name, new VariableEntry(parameterDeclaration.typeExpression.dataType, parameterDeclaration.isReference), SplError.RedeclarationAsParameter(parameterDeclaration.position, parameterDeclaration.name));
        }

        @Override
        public void visit(ProcedureDeclaration procedureDeclaration) {
            SymbolTable s = new SymbolTable(table);
            TableBuilderVisitor lokal_Visitor = new TableBuilderVisitor(s);
            List<ParameterType> p = new ArrayList<>();
            for (ParameterDeclaration procedureDeclaration1 : procedureDeclaration.parameters) {
                procedureDeclaration1.accept(lokal_Visitor);
                p.add(new ParameterType(procedureDeclaration1.typeExpression.dataType, procedureDeclaration1.isReference));
            }
            for (VariableDeclaration variableDeclaration : procedureDeclaration.variables) {
                variableDeclaration.accept(lokal_Visitor);
            }
            ProcedureEntry p_entry = new ProcedureEntry(s, p);
            table.enter(procedureDeclaration.name, p_entry,SplError.RedeclarationAsProcedure(procedureDeclaration.position, procedureDeclaration.name));
            if (showTables) {
                printSymbolTableAtEndOfProcedure(procedureDeclaration.name, p_entry);
            }
        }
    }


    /**
     * Prints the local symbol table of a procedure together with a heading-line
     * NOTE: You have to call this after completing the local table to support '--tables'.
     *
     * @param name  The name of the procedure
     * @param entry The entry of the procedure to print
     */
    private static void printSymbolTableAtEndOfProcedure(Identifier name, ProcedureEntry entry) {
        System.out.format("Symbol table at end of procedure '%s':\n", name);
        System.out.println(entry.localTable.toString());
    }
}
