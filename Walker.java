import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Stack;

public class Walker {
    public static class Evaluator extends CorundumBaseListener {
        ParseTreeProperty<Integer> int_values = new ParseTreeProperty<Integer>();
        ParseTreeProperty<Float> float_values = new ParseTreeProperty<Float>();
        ParseTreeProperty<String> string_values = new ParseTreeProperty<String>();
        ParseTreeProperty<String> which_value = new ParseTreeProperty<String>();

        public int SemanticErrorsNum = 0;
        public int NumStr = 1;
        java.util.LinkedList<String> definitions = new java.util.LinkedList<String>();

        public static boolean is_defined(java.util.LinkedList<String> definitions, String variable) {
            int index = definitions.indexOf(variable);
            if (index == -1) {
                return false;
            }
            return true;
        }

        public static String repeat(String s, int times) {
            if (times <= 0) return "";
            else return s + repeat(s, times-1);
        }

        // ======================================== Integer ========================================

        public void enterInt_assignment(CorundumParser.Int_assignmentContext ctx) {
            switch(ctx.op.getType()) {
                case CorundumParser.ASSIGN:
                    String var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        System.out.println(".local int " + ctx.var_id.getText());
                        definitions.add(ctx.var_id.getText());
                    }
                    break;
                default:
                    var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        System.out.println("line " + NumStr + " Error! Undefined variable " + var + "!");
                        SemanticErrorsNum++;
                    }
                    break;
            }
        }

        public void exitInt_assignment(CorundumParser.Int_assignmentContext ctx) {
            String str = ctx.var_id.getText() + " " + ctx.op.getText() + " " + int_values.get(ctx.getChild(2));
            System.out.println(str);
        }

        public void exitInt_result(CorundumParser.Int_resultContext ctx) {
            if ( ctx.getChildCount() == 3 && ctx.op != null ) { // operation node

                int left = int_values.get(ctx.getChild(0));
                int right = int_values.get(ctx.getChild(2));

                switch(ctx.op.getType()) {
                    case CorundumParser.MUL:
                        int_values.put(ctx, left * right);
                        which_value.put(ctx, "Integer");
                        break;
                    case CorundumParser.DIV:
                        int_values.put(ctx, left / right);
                        which_value.put(ctx, "Integer");
                        break;
                    case CorundumParser.MOD:
                        int_values.put(ctx, left % right);
                        which_value.put(ctx, "Integer");
                        break;
                    case CorundumParser.PLUS:
                        int_values.put(ctx, left + right);
                        which_value.put(ctx, "Integer");
                        break;
                    case CorundumParser.MINUS:
                        int_values.put(ctx, left - right);
                        which_value.put(ctx, "Integer");
                        break;
                }
            }
            else if ( ctx.getChildCount() == 1 ) { // near-terminal node
                int_values.put(ctx, int_values.get(ctx.getChild(0)));
                which_value.put(ctx, "Integer");
            }
            else if ( ctx.getChildCount() == 3 && ctx.op == null ) { // node with brackets
                int_values.put(ctx, int_values.get(ctx.getChild(1)));
                which_value.put(ctx, "Integer");
            }
        }

        public void exitInt_t(CorundumParser.Int_tContext ctx) {
            int_values.put(ctx, int_values.get(ctx.getChild(0)));
            which_value.put(ctx, which_value.get(ctx.getChild(0)));
        }

        // ======================================== Float ========================================

        public void enterFloat_assignment(CorundumParser.Float_assignmentContext ctx) {
            switch(ctx.op.getType()) {
                case CorundumParser.ASSIGN:
                    String var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        System.out.println(".local num " + ctx.var_id.getText());
                        definitions.add(ctx.var_id.getText());
                    }
                    break;
                default:
                    var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        System.out.println("line " + NumStr + " Error! Undefined variable " + var + "!");
                        SemanticErrorsNum++;
                    }
                    break;
            }
        }

        public void exitFloat_assignment(CorundumParser.Float_assignmentContext ctx) {
            String str = ctx.var_id.getText() + " " + ctx.op.getText() + " " + float_values.get(ctx.getChild(2));
            System.out.println(str);
        }

        public void exitFloat_result(CorundumParser.Float_resultContext ctx) {
            if ( ctx.getChildCount() == 3 && ctx.op != null ) { // operation node

                float left = 0;
                float right = 0;

                switch(which_value.get(ctx.getChild(0))) {
                    case "Integer":
                        left = (float) int_values.get(ctx.getChild(0));
                        break;
                    case "Float":
                        left = float_values.get(ctx.getChild(0));
                        break;
                }

                switch(which_value.get(ctx.getChild(2))) {
                    case "Integer":
                        right = (float) int_values.get(ctx.getChild(2));
                        break;
                    case "Float":
                        right = float_values.get(ctx.getChild(2));
                        break;
                }

                switch(ctx.op.getType()) {
                    case CorundumParser.MUL:
                        float_values.put(ctx, left * right);
                        which_value.put(ctx, "Float");
                        break;
                    case CorundumParser.DIV:
                        float_values.put(ctx, left / right);
                        which_value.put(ctx, "Float");
                        break;
                    case CorundumParser.MOD:
                        float_values.put(ctx, left % right);
                        which_value.put(ctx, "Float");
                        break;
                    case CorundumParser.PLUS:
                        float_values.put(ctx, left + right);
                        which_value.put(ctx, "Float");
                        break;
                    case CorundumParser.MINUS:
                        float_values.put(ctx, left - right);
                        which_value.put(ctx, "Float");
                        break;
                }
            }
            else if ( ctx.getChildCount() == 1 ) { // near-terminal node
                float_values.put(ctx, float_values.get(ctx.getChild(0)));
                which_value.put(ctx, "Float");
            }
            else if ( ctx.getChildCount() == 3 && ctx.op == null ) { // node with brackets
                float_values.put(ctx, float_values.get(ctx.getChild(1)));
                which_value.put(ctx, "Float");
            }
        }

        public void exitFloat_t(CorundumParser.Float_tContext ctx) {
            float_values.put(ctx, float_values.get(ctx.getChild(0)));
            which_value.put(ctx, which_value.get(ctx.getChild(0)));
        }

        // ======================================== String ========================================

        public void enterString_assignment(CorundumParser.String_assignmentContext ctx) {
            switch(ctx.op.getType()) {
                case CorundumParser.ASSIGN:
                    String var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        System.out.println(".local string " + ctx.var_id.getText());
                        definitions.add(ctx.var_id.getText());
                    }
                    break;
                default:
                    var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        System.out.println("line " + NumStr + " Error! Undefined variable " + var + "!");
                        SemanticErrorsNum++;
                    }
                    break;
            }
        }

        public void exitString_assignment(CorundumParser.String_assignmentContext ctx) {
            String str = ctx.var_id.getText() + " " + ctx.op.getText() + " \"" + string_values.get(ctx.getChild(2)) + "\"";
            System.out.println(str);
        }

        public void exitString_result(CorundumParser.String_resultContext ctx) {
            if ( ctx.getChildCount() == 3 && ctx.op != null ) { // operation node

                int times = 0;
                String left_s = "";
                String right_s = "";
                String str = "";

                switch(which_value.get(ctx.getChild(0))) {
                    case "Integer":
                        times = int_values.get(ctx.getChild(0));
                        break;
                    case "String":
                        left_s = string_values.get(ctx.getChild(0));
                        str = left_s;
                        break;
                }

                switch(which_value.get(ctx.getChild(2))) {
                    case "Integer":
                        times = int_values.get(ctx.getChild(2));
                        break;
                    case "String":
                        right_s = string_values.get(ctx.getChild(2));
                        str = right_s;
                        break;
                }

                switch(ctx.op.getType()) {
                    case CorundumParser.MUL:
                        string_values.put(ctx, (String) repeat(str, times));
                        which_value.put(ctx, "String");
                        break;
                    case CorundumParser.PLUS:
                        string_values.put(ctx, (String) left_s + right_s);
                        which_value.put(ctx, "String");
                        break;
                }
            }
            else if ( ctx.getChildCount() == 1 ) { // near-terminal node
                string_values.put(ctx, string_values.get(ctx.getChild(0)));
                which_value.put(ctx, "String");
            }
            else if ( ctx.getChildCount() == 3 && ctx.op == null ) { // node with brackets
                string_values.put(ctx, string_values.get(ctx.getChild(1)));
                which_value.put(ctx, "String");
            }
        }

        public void exitLiteral_t(CorundumParser.Literal_tContext ctx) {
            string_values.put(ctx, string_values.get(ctx.getChild(0)));
            which_value.put(ctx, which_value.get(ctx.getChild(0)));
        }

        // ======================================== Terminal node ========================================

        public void visitTerminal(TerminalNode node) {
            Token symbol = node.getSymbol();
            switch(symbol.getType()) {
                case CorundumParser.INT:
                    int_values.put(node, Integer.valueOf(symbol.getText()));
                    which_value.put(node, "Integer");
                    break;
                case CorundumParser.FLOAT:
                    float_values.put(node, Float.valueOf(symbol.getText()));
                    which_value.put(node, "Float");
                    break;
                case CorundumParser.LITERAL:
                    String str_terminal;
                    str_terminal = String.valueOf(symbol.getText());
                    str_terminal = str_terminal.replaceAll("\"", "");
                    str_terminal = str_terminal.replaceAll("\'", "");
                    string_values.put(node, str_terminal);
                    which_value.put(node, "String");
                    break;
            }
        }

        public void exitCrlf(CorundumParser.CrlfContext ctx) {
            NumStr++;
        }
    }

    public static void main(String[] args) throws Exception {
        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) {
            is = new FileInputStream(inputFile);
        }
        ANTLRInputStream input = new ANTLRInputStream(is);
        CorundumLexer lexer = new CorundumLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CorundumParser parser = new CorundumParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.prog();

        ParseTreeWalker walker = new ParseTreeWalker();

        Evaluator eval = new Evaluator();
        walker.walk(eval, tree);
    }
}