import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class ExprParser {
    /*
     *ACTION表第一个数字表示操作类型
     * 1 移入 2 错误 3 规约 4 空操作 5 接受
     * 后两个数字表示操作数
     * 进行移入操作时表示进入的状态
     * 错误中，1、不匹配的右括号符，2、缺少运算对象，3、缺少运算符 4、缺少右括号符
     * 进行规约操作表示按照什么表达式规约
     */
    private final int[][] ACTION = {
            {105, 201, 202, 202, 202, 202, 104, 106, 202},
            {203, 201, 107, 108, 400, 400, 203, 203, 500},
            {203, 304, 304, 304, 109, 110, 203, 203, 304},
            {203, 307, 307, 307, 307, 307, 203, 203, 307},
            {203, 308, 308, 308, 308, 308, 203, 203, 308},
            {105, 202, 202, 202, 202, 202, 104, 106, 204},
            {400, 310, 310, 310, 310, 310, 203, 203, 310},
            {105, 201, 202, 202, 202, 202, 104, 106, 202},
            {105, 202, 202, 202, 202, 202, 104, 106, 202},
            {105, 202, 202, 202, 202, 202, 104, 106, 202},
            {105, 202, 202, 202, 202, 202, 104, 106, 202},
            {204, 116, 107, 108, 400, 400, 204, 204, 204},
            {203, 302, 302, 302, 109, 110, 104, 106, 302},
            {203, 303, 303, 303, 109, 110, 203, 203, 303},
            {203, 305, 305, 305, 305, 305, 203, 203, 305},
            {203, 306, 306, 306, 306, 306, 203, 203, 306},
            {203, 309, 309, 309, 309, 309, 203, 203, 309}};
    //分别对应每个状态遇到E T F后跳转到那个状态
    private final int[][] GOTO = {
            {1, 2, 3},
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0},
            {11, 2, 3},
            {0, 0, 0},
            {0, 12, 3},
            {0, 13, 3},
            {0, 0, 14},
            {0, 0, 15},
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}};
    //表达式
    private String[] EXPR = {"S->E", "E->E+T", "E->E-T", "E->T", "T->T*F", "T->T/F", "T->F", "F->i", "F->(E)", "F->n"};
    //映射表
    private Map<Character, Integer> map;
    //状态栈
    private Stack<Integer> stateStack;
    //符号栈
    private Stack<Character> symbolStack;
    //计算栈
    private Stack<Double> calStack;
    //输入串
    private String input;
    private static Scanner scanner = new Scanner(System.in);
    //当前状态
    private int state;
    private boolean flag;

    //得到输入串
    private void getInput() {
        input = scanner.nextLine().replaceAll(" ", "");
        input = input + "$";
    }

    //初始化资源
    private ExprParser() {
        stateStack = new Stack<>();
        symbolStack = new Stack<>();
        calStack = new Stack<>();
        map = new HashMap<>();

        state = 0;
        stateStack.push(0);
        symbolStack.push('$');
        map.put('(', 0);
        map.put(')', 1);
        map.put('+', 2);
        map.put('-', 3);
        map.put('*', 4);
        map.put('/', 5);
        map.put('i', 6);
        map.put('n', 7);
        map.put('$', 8);
        map.put('E', 0);
        map.put('T', 1);
        map.put('F', 2);
        getInput();
    }

    //移入操作
    private void shift(char c, int action) {
        symbolStack.push(c);
        stateStack.push(action % 100);
        state = stateStack.peek();

        if (Character.isDigit(c)) {
            calStack.push((double) Character.digit(c, 10));
        }
    }

    //规约操作
    private void reduce(int action) throws Exception {
        String[] subExpr = EXPR[action % 100 - 1].split("->");
        cal(action % 100 - 1);

        for (int i = 0; i < subExpr[1].toCharArray().length; i++) {
            symbolStack.pop();
            stateStack.pop();
        }

        for (char temp : subExpr[0].toCharArray()) {
            symbolStack.push(temp);
            stateStack.push(GOTO[stateStack.peek()][map.get(temp)]);
        }

        state = stateStack.peek();
    }

    private void cal(int index) throws Exception {
        switch (index) {
            case 1:
                calHelper('+');
                break;
            case 2:
                calHelper('-');
                break;
            case 4:
                calHelper('*');
                break;
            case 5:
                calHelper('/');
                break;
        }
    }

    private void calHelper(char c) throws Exception {
        double d1 = calStack.pop();
        double d2 = calStack.pop();
        switch (c) {
            case '+':
                calStack.push(d2 + d1);
                break;
            case '-':
                calStack.push(d2 - d1);
                break;
            case '*':
                calStack.push(d2 * d1);
                break;
            case '/':
                if(d1 == 0.0) {
                    throw  new Exception("被除数不能为0");
                } else {
                    calStack.push(d2 / d1);
                    break;
                }
        }
    }

    //分析完毕 接受
    private void acc() {
        symbolStack.pop();
        flag = true;
    }

    //报错
    private void parserException(int i) throws Exception {
        switch (i) {
            case 1:
                throw new Exception("不匹配的右括号符");
            case 2:
                throw new Exception("缺少运算对象");
            case 3:
                throw new Exception("缺少运算符");
            case 4:
                throw new Exception("缺少右括号符");
        }

    }

    //分析过程
    private void analysis() throws Exception {
        printHead();

        char[] chars = input.toCharArray();
        int i = 0, t = 0;

        while (true) {
            char c = chars[i];

            int action = Character.isDigit(c) ? ACTION[state][map.get('n')] : ACTION[state][map.get(c)];
            int j = action / 100;

            if (j == 1) {
                shift(c, action);
            } else if (j == 2) {
                parserException(action % 100);
            } else if (j == 3) {
                reduce(action);
                i--;
            } else if (j == 5) {
                acc();
            }

            if (flag) {
                printStep(++t, input, i, action);
                System.out.println("res: " + calStack.pop());
                break;
            } else {
                printStep(++t, input, ++i, action);
            }
        }
    }

    //打印栈中信息
    private static void printStack(Stack stack) {
        StringBuilder builder = new StringBuilder();
        for (Object b : stack) {
            builder.append(b);
        }

        System.out.printf("%-13s", builder.toString());
    }

    //打印每一步的分析过程
    private void printStep(int step, String s, int index, int action) {
        System.out.printf("%-12d", step);
        printStack(stateStack);
        printStack(symbolStack);
        printSubString(s, index);
        printAction(action);
        System.out.println();
    }

    //打印当前输入串
    private void printSubString(String s, int index) {
        System.out.printf("%-14s", s.substring(index));
    }

    //打印进行何种操作，如果是规约，打印GOTO的状态
    private void printAction(int action) {
        if (action / 100 == 1) {
            System.out.printf("%-12s", "s" + action % 100);
        } else if (action / 100 == 3) {
            System.out.printf("%-10s%d", "r" + action % 100, stateStack.peek());
        } else if (action / 100 == 5) {
            System.out.printf("%-12s", "acc");
        }
    }

    //打印头部信息
    private void printHead() {
        System.out.printf("%-10s%-10s%-10s%-10s%-10s%s\n", "步骤", "状态栈", "符号栈", "输入串", "ACTION", "GOTO");
    }

    public static void main(String[] args) {
        while (scanner.hasNext()) {
            try {
                ExprParser parser = new ExprParser();
                parser.analysis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
