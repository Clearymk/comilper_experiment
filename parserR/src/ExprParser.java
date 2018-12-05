import java.util.Scanner;

public class ExprParser {
    private char peek = ' ';
    private int index = 0;
    private static Scanner sc = new Scanner(System.in);
    private String expr;
    private Node root;

    private void E(Node E) throws Exception {
        Node T = new Node();
        Node E_ = new Node();

        E.leftNode = T;
        E.rightNode = E_;

        T(T);
        E_.inh = T.val;

        E_(E_);
        E.val = E_.syn;
    }

    private void T(Node T) throws Exception {
        Node F = new Node();
        Node T_ = new Node();

        T.leftNode = F;
        T.rightNode = T_;

        F(F);
        T_.inh = F.val;

        T_(T_);
        T.val = T_.syn;
    }

    private void F(Node F) throws Exception {
        if (peek == '(') {
            Node E = new Node();

            match('(');

            E(E);
            F.val = E.val;

            match(')', "缺少右括号");
        } else if (Character.isDigit(peek)) {
            F.val = readNum();
        } else {
            throw new Exception("非法的数字!");
        }
    }

    private void T_(Node T_) throws Exception {
        Node F = new Node();
        Node T_1 = new Node();

        if (peek == '*') {
            match('*');

            F(F);
            T_1.inh = T_.inh * F.val;

            T_(T_1);
            T_.syn = T_1.syn;
            return;
        } else if (peek == '/') {
            match('/');
            F(F);

            if (F.val == 0.0) {
                throw new Exception("被除数不能为0");
            } else {
                T_1.inh = T_.inh / F.val;
            }


            T_(T_1);
            T_.syn = T_1.syn;
            return;
        }

        T_.syn = T_.inh;
    }

    private void E_(Node E_) throws Exception {
        Node T = new Node();
        Node E_1 = new Node();

        if (peek == '+') {
            match('+');
            T(T);
            E_1.inh = E_.inh + T.val;

            E_(E_1);
            E_.syn = E_1.syn;
            return;
        } else if (peek == '-') {
            match('-');

            T(T);
            E_1.inh = E_.inh - T.val;

            E_(E_1);
            E_.syn = E_1.syn;
            return;
        }

        E_.syn = E_.inh;
    }

    private void init() throws Exception {
        getExpr();
        read();
        root = new Node();
    }

    private void getExpr() {
        expr = sc.nextLine();
        expr = expr.replaceAll(" ", "");
        index = 0;
    }

    //负责读一个字符
    private void read() throws Exception {
        try {
            peek = expr.charAt(index++);
        } catch (StringIndexOutOfBoundsException e) {
            throw new Exception("缺少分号");
        }

    }

    private float readNum() throws Exception {
        int i = 0;
        do {
            i = i * 10 + Character.digit(peek, 10);
            read();
        } while (Character.isDigit(peek));

        //判断浮点数
        float d = 10;
        float j = i;

        if (peek == '.') {
            do {
                j += (float) Character.digit(peek, 10) / d;
                d = d * 10;
            } while (Character.isDigit(peek));

            return j;
        }
        return i;
    }

    //匹配字符
    private void match(char c, String errorMessage) throws Exception {
        if (peek != c) {
            throw new Exception(errorMessage);
        }
        read();
    }

    private void match(char c) throws Exception {
        match(c, "error");
    }

    public static void main(String[] args) {
        ExprParser exprParser = new ExprParser();
        while (sc.hasNext()) {
            try {
                exprParser.init();
                exprParser.E(exprParser.root);
                if (exprParser.peek == ';') {
                    System.out.println("res: " + exprParser.root.val);
                    System.out.println("success");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
