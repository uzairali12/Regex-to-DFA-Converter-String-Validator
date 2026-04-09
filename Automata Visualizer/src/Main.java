import java.util.*;

public class Main {

    // -------------------- NFA State --------------------
    static class NFAState {
        int id;
        Map<Character, List<NFAState>> transitions = new HashMap<>();
        List<NFAState> epsilon = new ArrayList<>();
        NFAState(int id) { this.id = id; }
    }

    // -------------------- NFA Fragment --------------------
    static class NFAFragment {
        NFAState start;
        NFAState end;
        NFAFragment(NFAState start, NFAState end) { this.start = start; this.end = end; }
    }

    static int stateId = 0;

    // -------------------- INFIX -> POSTFIX --------------------
    static String infixToPostfix(String regex) {
        StringBuilder output = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        Map<Character, Integer> precedence = new HashMap<>();
        precedence.put('|', 1);
        precedence.put('.', 2);
        precedence.put('*', 3);

        // Add explicit concatenation operator '.'
        StringBuilder modified = new StringBuilder();
        char[] arr = regex.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char c1 = arr[i];
            modified.append(c1);
            if (i + 1 < arr.length) {
                char c2 = arr[i + 1];
                if ((Character.isLetterOrDigit(c1) || c1 == '*' || c1 == ')') &&
                        (Character.isLetterOrDigit(c2) || c2 == '(')) {
                    modified.append('.');
                }
            }
        }

        char[] m = modified.toString().toCharArray();
        for (char c : m) {
            if (Character.isLetterOrDigit(c)) {
                output.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(')
                    output.append(stack.pop());
                stack.pop();
            } else {
                while (!stack.isEmpty() && stack.peek() != '(' &&
                        precedence.get(stack.peek()) >= precedence.get(c)) {
                    output.append(stack.pop());
                }
                stack.push(c);
            }
        }
        while (!stack.isEmpty()) output.append(stack.pop());
        return output.toString();
    }

    // -------------------- THOMPSON CONSTRUCTION --------------------
    static NFAFragment regexToNFA(String postfix) {
        Stack<NFAFragment> stack = new Stack<>();
        for (char c : postfix.toCharArray()) {
            if (c == '*') {
                NFAFragment frag = stack.pop();
                NFAState start = new NFAState(stateId++);
                NFAState end = new NFAState(stateId++);
                start.epsilon.add(frag.start);
                start.epsilon.add(end);
                frag.end.epsilon.add(frag.start);
                frag.end.epsilon.add(end);
                stack.push(new NFAFragment(start, end));
            } else if (c == '.') {
                NFAFragment frag2 = stack.pop();
                NFAFragment frag1 = stack.pop();
                frag1.end.epsilon.add(frag2.start);
                stack.push(new NFAFragment(frag1.start, frag2.end));
            } else if (c == '|') {
                NFAFragment frag2 = stack.pop();
                NFAFragment frag1 = stack.pop();
                NFAState start = new NFAState(stateId++);
                NFAState end = new NFAState(stateId++);
                start.epsilon.add(frag1.start);
                start.epsilon.add(frag2.start);
                frag1.end.epsilon.add(end);
                frag2.end.epsilon.add(end);
                stack.push(new NFAFragment(start, end));
            } else {
                NFAState s1 = new NFAState(stateId++);
                NFAState s2 = new NFAState(stateId++);
                s1.transitions.putIfAbsent(c, new ArrayList<>());
                s1.transitions.get(c).add(s2);
                stack.push(new NFAFragment(s1, s2));
            }
        }
        return stack.pop();
    }

    // -------------------- EPSILON CLOSURE --------------------
    static Set<NFAState> epsilonClosure(NFAState state) {
        Set<NFAState> set = new HashSet<>();
        Stack<NFAState> stack = new Stack<>();
        stack.push(state);
        while (!stack.isEmpty()) {
            NFAState s = stack.pop();
            if (!set.contains(s)) {
                set.add(s);
                stack.addAll(s.epsilon);
            }
        }
        return set;
    }

    static Set<NFAState> epsilonClosure(Set<NFAState> states) {
        Set<NFAState> result = new HashSet<>();
        for (NFAState s : states) result.addAll(epsilonClosure(s));
        return result;
    }

    // -------------------- DFA Construction --------------------
    static class DFAState {
        Set<NFAState> nfaStates;
        int id;
        boolean isAccept;
        Map<Character, DFAState> transitions = new HashMap<>();
        DFAState(Set<NFAState> set, int id, boolean isAccept) {
            this.nfaStates = set;
            this.id = id;
            this.isAccept = isAccept;
        }
    }

    static DFAState buildDFA(NFAFragment nfa, List<DFAState> dfaStatesList) {
        Map<Set<NFAState>, DFAState> map = new HashMap<>();
        Queue<Set<NFAState>> queue = new LinkedList<>();

        Set<NFAState> startSet = epsilonClosure(nfa.start);
        DFAState startDFA = new DFAState(startSet, 0, startSet.contains(nfa.end));
        map.put(startSet, startDFA);
        queue.add(startSet);
        dfaStatesList.add(startDFA);
        int idCounter = 1;

        System.out.println("\n===== DFA TRANSITIONS =====");
        while (!queue.isEmpty()) {
            Set<NFAState> currentSet = queue.poll();
            DFAState currentDFA = map.get(currentSet);

            for (char a = 'a'; a <= 'z'; a++) {
                Set<NFAState> moveSet = new HashSet<>();
                for (NFAState s : currentSet) {
                    if (s.transitions.containsKey(a)) moveSet.addAll(s.transitions.get(a));
                }
                moveSet = epsilonClosure(moveSet);
                if (!moveSet.isEmpty()) {
                    DFAState nextDFA = map.get(moveSet);
                    if (nextDFA == null) {
                        nextDFA = new DFAState(moveSet, idCounter++, moveSet.contains(nfa.end));
                        map.put(moveSet, nextDFA);
                        queue.add(moveSet);
                        dfaStatesList.add(nextDFA);
                    }
                    currentDFA.transitions.put(a, nextDFA);
                    System.out.println("DFA " + currentDFA.id + " --" + a + "--> DFA " + nextDFA.id);
                }
            }
        }
        return startDFA;
    }

    // -------------------- TEST STRING --------------------
    static void testString(DFAState start, String testString) {
        DFAState current = start;
        for (char c : testString.toCharArray()) {
            if (current.transitions.containsKey(c)) {
                current = current.transitions.get(c);
            } else {
                System.out.println("REJECTED ❌ (no transition for '" + c + "')");
                return;
            }
        }
        System.out.println(current.isAccept ? "ACCEPTED ✅" : "REJECTED ❌");
    }

    // -------------------- MENU / HELP --------------------
    static void showExamples() {
        System.out.println("\n===== EXAMPLES =====");
        System.out.println("1. ab.      -> a concatenated with b");
        System.out.println("2. ab|      -> a OR b");
        System.out.println("3. a*       -> zero or more a");
        System.out.println("4. a|bc|da* -> complex example");
    }

    static void showHelp() {
        System.out.println("\n===== HELP =====");
        System.out.println("• Use lowercase letters (a-z)");
        System.out.println("• Operators: . (concat), | (or), * (Kleene star)");
        System.out.println("• Regex can be typed in infix; program converts to postfix automatically");
    }

    // -------------------- MAIN --------------------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input;
        while (true) {
            System.out.println("\n======= AUTOMATA VISUALIZER MENU ========");
            System.out.println("1. Build Regex → NFA → DFA & Test Strings");
            System.out.println("2. Show Example Regex");
            System.out.println("3. Help / How Regex Format Works");
            System.out.println("4. Exit");
            System.out.print("Enter choice or type regex directly: ");
            input = sc.nextLine();

            if (input.equals("1")) {
                System.out.print("Enter REGEX (infix): ");
                input = sc.nextLine();
            } else if (input.equals("2")) {
                showExamples();
                continue;
            } else if (input.equals("3")) {
                showHelp();
                continue;
            } else if (input.equals("4")) {
                System.out.println("Exiting program...");
                break;
            }

            // Convert infix to postfix
            String postfix = infixToPostfix(input);
            System.out.println("Postfix regex: " + postfix);

            stateId = 0;
            NFAFragment nfa = regexToNFA(postfix);
            List<DFAState> dfaStatesList = new ArrayList<>();
            DFAState dfaStart = buildDFA(nfa, dfaStatesList);

            // Loop for multiple string tests
            String testStr;
            do {
                System.out.print("\nEnter string to test (or type 'exit' to return to menu): ");
                testStr = sc.nextLine();
                if (!testStr.equalsIgnoreCase("exit")) {
                    testString(dfaStart, testStr);
                }
            } while (!testStr.equalsIgnoreCase("exit"));
        }
    }
}
