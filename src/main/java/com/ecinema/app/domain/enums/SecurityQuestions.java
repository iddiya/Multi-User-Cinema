package com.ecinema.app.domain.enums;

import java.util.ArrayList;
import java.util.List;

/** The enumeration of security questions. */
public class SecurityQuestions {

    /** Security question 1 */
    public static final String SQ1 = "What is the name of the first pet you've ever had?";

    /** Security question 2 */
    public static final String SQ2 = "What city was your mother born in?";

    /** Security question 3 */
    public static final String SQ3 = "What was your nickname when you were a kid?";

    /** Security question 4 */
    public static final String SQ4 = "What is your favorite movie?";

    /** Security question 5 */
    public static final String SQ5 = "What is your favorite soft drink?";

    /**
     * Gets the list of security questions.
     *
     * @return the list of security questions
     */
    public static List<String> getList() {
        return new ArrayList<>() {{
            add(SQ1);
            add(SQ2);
            add(SQ3);
            add(SQ4);
            add(SQ5);
        }};
    }

}
