package zag.zero.testcalc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import java.lang.Math;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    // DECLARATIONS
    boolean altMode = false;
    boolean stackMode = false;
    boolean numEntry = false;
    boolean exponentEntry = false;
    boolean clearArmed = false;
    final int maxStack = 127;
    String enteredNumPrep = "";
    int exponentNum = 0;
    double stackNums[] = new double[maxStack+1];
    int stackIndex = 0;
    int cursorPos = 0;
    int markPos = 0;

    TextView inputView;
    TextView stackView;

    Button fn1;
    Button fn2;
    Button fn3;
    Button fn4;
    Button fn5;
    Button signBtn;
    Button stackBtn;
    Button divBtn;
    Button multBtn;
    Button subBtn;
    Button addBtn;
    Button altBtn;

    interface iBtnAction {
        void action(View v);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        inputView = (TextView) this.findViewById(R.id.inputView);
        stackView = (TextView) this.findViewById(R.id.stackView);
        stackView.setMovementMethod(new ScrollingMovementMethod());

        functionPressed fnAction = new functionPressed();
        miscButtonPressed miscAction = new miscButtonPressed();
        operatorPressed operatorAction = new operatorPressed();
        enterPressed enterAction = new enterPressed();
        numButtonPressed numAction = new numButtonPressed();


        fn1 = initButton(R.id.btn_fn_1, HapticFeedbackConstants.KEYBOARD_TAP, fnAction);
        fn2 = initButton(R.id.btn_fn_2, HapticFeedbackConstants.KEYBOARD_TAP, fnAction);
        fn3 = initButton(R.id.btn_fn_3, HapticFeedbackConstants.KEYBOARD_TAP, fnAction);
        fn4 = initButton(R.id.btn_fn_4, HapticFeedbackConstants.KEYBOARD_TAP, fnAction);
        fn5 = initButton(R.id.btn_fn_5, HapticFeedbackConstants.KEYBOARD_TAP, fnAction);
        signBtn  = initButton(R.id.btn_misc_sign, HapticFeedbackConstants.KEYBOARD_TAP, miscAction);
        stackBtn = initButton(R.id.btn_misc_stack, HapticFeedbackConstants.KEYBOARD_TAP, miscAction);
        divBtn   = initButton(R.id.btn_op_div, HapticFeedbackConstants.KEYBOARD_TAP, operatorAction);
        multBtn  = initButton(R.id.btn_op_mult, HapticFeedbackConstants.KEYBOARD_TAP, operatorAction);
        subBtn   = initButton(R.id.btn_op_sub, HapticFeedbackConstants.KEYBOARD_TAP, operatorAction);
        addBtn   = initButton(R.id.btn_op_add, HapticFeedbackConstants.KEYBOARD_TAP, operatorAction);
        altBtn   = initButton(R.id.btn_misc_shift, HapticFeedbackConstants.KEYBOARD_TAP, miscAction);

        initButton(R.id.btn_enter, HapticFeedbackConstants.LONG_PRESS, enterAction);
        initButton(R.id.btn_misc_back, HapticFeedbackConstants.KEYBOARD_TAP, miscAction);
        initButton(R.id.btn_misc_dec, HapticFeedbackConstants.KEYBOARD_TAP, miscAction);
        initButton(R.id.btn_num_0, HapticFeedbackConstants.KEYBOARD_TAP, numAction);
        initButton(R.id.btn_num_1, HapticFeedbackConstants.KEYBOARD_TAP, numAction);
        initButton(R.id.btn_num_2, HapticFeedbackConstants.KEYBOARD_TAP, numAction);
        initButton(R.id.btn_num_3, HapticFeedbackConstants.KEYBOARD_TAP, numAction);
        initButton(R.id.btn_num_4, HapticFeedbackConstants.KEYBOARD_TAP, numAction);
        initButton(R.id.btn_num_5, HapticFeedbackConstants.KEYBOARD_TAP, numAction);
        initButton(R.id.btn_num_6, HapticFeedbackConstants.KEYBOARD_TAP, numAction);
        initButton(R.id.btn_num_7, HapticFeedbackConstants.KEYBOARD_TAP, numAction);
        initButton(R.id.btn_num_8, HapticFeedbackConstants.KEYBOARD_TAP, numAction);
        initButton(R.id.btn_num_9, HapticFeedbackConstants.KEYBOARD_TAP, numAction);

        modeHandle(false, false);


    }

    private Button initButton(int buttonID, int hapticTypeID, iBtnAction action) {
        Button newButton = this.findViewById(buttonID);
        newButton.setOnTouchListener(new HapticTouchListener(hapticTypeID, action));
        return newButton;
    }

    // Class to handle touch events and respond with haptic feedback
    private class HapticTouchListener implements View.OnTouchListener {

        private final int feedbackType;
        private final iBtnAction btnAction;

        public HapticTouchListener(int type, iBtnAction action) {
            feedbackType = type;
            btnAction = action;
        }
        public int feedbackType() { return feedbackType; }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // only perform feedback when the user touches the view, as opposed
            // to lifting a finger off the view
            if( event.getAction() == MotionEvent.ACTION_DOWN ){
                // perform the feedback
                v.performHapticFeedback( feedbackType() );
                // do what it was supposed to do
                btnAction.action(v);
                // allow it to change to the pressed icon (false)
                return false;
            }

            return false;
        }
    }

    private class numButtonPressed implements iBtnAction {
        @Override
        public void action(View v) {
            String numStr = v.getTag().toString();
            numStr = numStr.substring(0, 1);
            int numPressed = Integer.parseInt(numStr);
            if (exponentEntry) {
                if (Math.abs(exponentNum) * 10 < 100)
                    exponentNum = exponentNum * 10 + (numPressed * ((exponentNum >= 0)?1:-1));
            }
            else
            if (numDigits(enteredNumPrep) < 16)
                enteredNumPrep = enteredNumPrep + numStr;
            numEntry = true;
            formatEntry();
        }
    }

    private class miscButtonPressed implements iBtnAction {
        @Override
        public void action(View v) {
            String indexStr = v.getTag().toString();
            indexStr = indexStr.substring(0, 1);
            switch (indexStr){
                case "1": //Sign Button - EEX in alt
                    if (altMode) {
                        if (!numEntry) {
                            numEntry = true;
                            enteredNumPrep = "1";
                        }
                        exponentEntry = true;
                        modeHandle(false, false);
                    } else {
                        if (numEntry) {
                            if (exponentEntry) {
                                exponentNum *= -1;
                            } else {
                                //enteredNum *= -1;
                                if (enteredNumPrep.contains("-"))
                                    enteredNumPrep = enteredNumPrep.replace("-", "");
                                else
                                    enteredNumPrep = "-" + enteredNumPrep;
                            }
                        } else {
                            if (stackIndex > 0) {
                                stackNums[stackIndex - 1] *= -1;
                                //Redraw last in stack
                                updateStackBox(true, true);
                            }
                        }
                    }
                    break;
                case "2": //Stack Button
                    if (!altMode && !stackMode) {
                        stackFnHandler("fn5");
                    } else {
                        modeHandle(false, !stackMode);
                    }
                    break;
                case "3": //Back Button
                    if (numEntry) {
                        // Remove digit from entry field, from the exponent input active,
                        //   otherwise from the actual decimal number
                        if (exponentEntry) {
                            // Set exponentEntry to false if it = 0
                            exponentEntry = exponentNum != 0;
                            // Divide exponent by 10 to truncate last digit, works since exponentNum
                            //   is an integer - By putting this after the exponentEntry check,
                            //   reducing exponentNum to zero the first time keeps exponentEntry active,
                            //   the second time reverts back to decimal entry
                            exponentNum /= 10;
                        } else {
                            // Get the length of the entered number
                            int entryLength = enteredNumPrep.length();
                            String nextDelChar = "";

                            // Backspace if there is more than one character, otherwise just clear the
                            //   entry field
                            if (entryLength > 1) {
                                // Set nextDelChar to the character before the one about to be deleted
                                nextDelChar = enteredNumPrep.substring(entryLength - 2,
                                        entryLength - 1);

                                // Clear the entry if there is only the negative sign and one digit,
                                //   otherwise remove the last character
                                if (nextDelChar.equals("-"))
                                    entryClear();
                                else
                                    enteredNumPrep = enteredNumPrep.substring(0, entryLength - 1);
                            } else {
                                entryClear();
                            }
                        }
                    } else {
                        // Drop from stack
                        stackFnHandler("drop");
                    }
                    break;
                case "4": //Decimal Button
                    // Check if there is any existing number entry
                    if (numEntry) {
                        if (!exponentEntry) {
                            if (!enteredNumPrep.contains(".") && numDigits(enteredNumPrep) < 16)
                                enteredNumPrep = enteredNumPrep + ".";
                        }
                    } else {
                        // Start with 0. if there
                        enteredNumPrep = "0.";
                        numEntry = true;
                    }

                /*
                if (!decimalEntry) {
                    decimalEntry = true;

                }*/
                    break;
                case "5": //Shift Button
                    modeHandle(!altMode, false);
            }

            formatEntry();
        }
    }

    private class enterPressed implements iBtnAction {
        @Override
        public void action(View v) {
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

            if (stackMode)
                modeHandle(false, false);
            if (numEntry) {
                stackEntry(true);
            } else {
                if (stackIndex < maxStack) {
                    if (stackIndex > 0) {
                        stackNums[stackIndex] = stackNums[stackIndex - 1];
                        stackIndex++;
                        updateStackBox(false, false);
                    }
                } else {
                    errorToast("stack full", Toast.LENGTH_SHORT);
                }
            }
        }
    }


    private class operatorPressed implements iBtnAction{
        @Override
        public void action(View v) {
            String indexStr = v.getTag().toString();
            indexStr = indexStr.substring(0, 1);

            if (!stackMode) {
                double opX;
                double opY;
                boolean failure = false;
                boolean wasNumEntry = false;

                if (numEntry) {
                    stackEntry(false);
                    wasNumEntry = true;
                }

                if (stackIndex >= 2) {
                    opY = stackNums[stackIndex - 2];
                    opX = stackNums[stackIndex - 1];
                } else {
                    errorToast("too few arguments", Toast.LENGTH_SHORT);
                    if (wasNumEntry)
                        updateStackBox(true, false);
                    return;
                }
                try {
                    switch (indexStr) {
                        case "4": //Plus
                            stackNums[stackIndex - 2] = opY + opX;
                            break;
                        case "3": //Minus
                            stackNums[stackIndex - 2] = opY - opX;
                            break;
                        case "2": //Multiply
                            stackNums[stackIndex - 2] = opY * opX;
                            break;
                        case "1": //Divide
                            if (opX != 0) {
                                stackNums[stackIndex - 2] = opY / opX;
                            } else {
                                errorToast("divide by zero", Toast.LENGTH_SHORT);
                            }
                    }
                } catch (Exception e) {
                    errorToast(e.getMessage(), Toast.LENGTH_LONG);
                    failure = true;
                } finally {
                    if (!failure) {
                        stackIndex--;
                        updateStackBox(false, false);
                    }
                }
            } else {
                stackFnHandler("op"+indexStr);
            }
        }
    }

    private class functionPressed implements iBtnAction {
        @Override
        public void action(View v) {
            String numStr = v.getTag().toString();
            numStr = numStr.substring(0, 1);
            int fnNum = Integer.parseInt(numStr);

            if (!stackMode) {

                if (altMode) {
                    fnNum += 10;
                    modeHandle(false, false);
                }

                double opX;
                double opY;
                int numOperands = 1;
                boolean failure = false;
                boolean wasNumEntry = false;

                if (numEntry) {
                    stackEntry(false);
                    wasNumEntry = true;
                }


                try {
                    switch (fnNum) {
                        case 1: //sin
                            numOperands = 1;
                            if (stackIndex >= numOperands) {
                                opX = stackNums[stackIndex - 1];
                            } else {
                                errorToast("too few arguments", Toast.LENGTH_SHORT);
                                if (wasNumEntry)
                                    updateStackBox(true, false);
                                failure = true;
                                return;
                            }
                            //Degree option
                            stackNums[stackIndex - numOperands] = Math.sin(opX);
                            break;
                        case 2: //cos
                            numOperands = 1;
                            if (stackIndex >= numOperands) {
                                opX = stackNums[stackIndex - 1];
                            } else {
                                errorToast("too few arguments", Toast.LENGTH_SHORT);
                                if (wasNumEntry)
                                    updateStackBox(true, false);
                                failure = true;
                                return;
                            }
                            //Degree option
                            stackNums[stackIndex - numOperands] = Math.cos(opX);
                            break;
                        case 3: //tan
                            numOperands = 1;
                            if (stackIndex >= numOperands) {
                                opX = stackNums[stackIndex - 1];
                            } else {
                                errorToast("too few arguments", Toast.LENGTH_SHORT);
                                if (wasNumEntry)
                                    updateStackBox(true, false);
                                failure = true;
                                return;
                            }
                            //Degree option
                            stackNums[stackIndex - numOperands] = Math.tan(opX);
                            break;
                        case 4: //square root                        numOperands = 1;
                            if (stackIndex >= numOperands) {
                                opX = stackNums[stackIndex - 1];
                            } else {
                                errorToast("too few arguments", Toast.LENGTH_SHORT);
                                if (wasNumEntry)
                                    updateStackBox(true, false);
                                failure = true;
                                return;
                            }
                            if (opX < 0) {
                                errorToast("complex numbers not supported", Toast.LENGTH_SHORT);
                                if (wasNumEntry)
                                    updateStackBox(true, false);
                                return;
                            } else {
                                stackNums[stackIndex - numOperands] = Math.sqrt(opX);
                            }
                            break;
                        case 5: //y to the power of x
                            numOperands = 2;
                            if (stackIndex >= numOperands) {
                                opY = stackNums[stackIndex - 2];
                                opX = stackNums[stackIndex - 1];
                            } else {
                                errorToast("too few arguments", Toast.LENGTH_SHORT);
                                if (wasNumEntry)
                                    updateStackBox(true, false);
                                failure = true;
                                return;
                            }
                            stackNums[stackIndex - numOperands] = Math.pow(opY, opX);
                            break;
                        case 11: //asin
                            if (stackIndex >= numOperands) {
                                opX = stackNums[stackIndex - 1];
                            } else {
                                errorToast("too few arguments", Toast.LENGTH_SHORT);
                                if (wasNumEntry)
                                    updateStackBox(true, false);
                                failure = true;
                                return;
                            }
                            stackNums[stackIndex - numOperands] = Math.asin(opX);
                            //Degree option
                            break;
                        case 12: //acos
                            numOperands = 1;
                            if (stackIndex >= numOperands) {
                                opX = stackNums[stackIndex - 1];
                            } else {
                                errorToast("too few arguments", Toast.LENGTH_SHORT);
                                if (wasNumEntry)
                                    updateStackBox(true, false);
                                failure = true;
                                return;
                            }
                            stackNums[stackIndex - numOperands] = Math.acos(opX);
                            //Degree option
                            break;
                        case 13: //atan
                            numOperands = 1;
                            if (stackIndex >= numOperands) {
                                opX = stackNums[stackIndex - 1];
                            } else {
                                errorToast("too few arguments", Toast.LENGTH_SHORT);
                                if (wasNumEntry)
                                    updateStackBox(true, false);
                                failure = true;
                                return;
                            }
                            //Degree option
                            stackNums[stackIndex - numOperands] = Math.atan(opX);
                            break;
                        case 14: //xth root of y
                            numOperands = 2;
                            if (stackIndex >= numOperands) {
                                opY = stackNums[stackIndex - 2];
                                opX = stackNums[stackIndex - 1];
                            } else {
                                errorToast("too few arguments", Toast.LENGTH_SHORT);
                                if (wasNumEntry)
                                    updateStackBox(true, false);
                                failure = true;
                                return;
                            }
                            if (opY < 0) {
                                errorToast("complex numbers not supported", Toast.LENGTH_SHORT);
                                if (wasNumEntry)
                                    updateStackBox(true, false);
                                return;
                            } else {
                                stackNums[stackIndex - numOperands] = Math.pow(opY, 1 / opX);
                            }
                            break;
                        case 15: // 1/x
                            numOperands = 1;
                            if (stackIndex >= numOperands) {
                                opX = stackNums[stackIndex - 1];
                            } else {
                                errorToast("too few arguments", Toast.LENGTH_SHORT);
                                if (wasNumEntry)
                                    updateStackBox(true, false);
                                failure = true;
                                return;
                            }
                            stackNums[stackIndex - numOperands] = 1 / opX;

                    }
                } catch (Exception e) {
                    errorToast(e.getMessage(), Toast.LENGTH_LONG);
                    failure = true;
                } finally {
                    //Seems like a pretty strange use of finally... I have no idea what I am doing
                    if (!failure) {
                        stackIndex -= numOperands - 1;
                        updateStackBox(false, false);
                    }
                }
            } else {
                stackFnHandler("fn"+numStr);
            }
        }
    }


    void stackEntry(boolean refreshStack) {
        if (stackIndex < maxStack) {
            String fullNum = enteredNumPrep +
                    ((exponentNum != 0) ? "e" + String.format("%d", exponentNum) : "");
            stackNums[stackIndex] = Double.parseDouble(fullNum);
            stackIndex++;
            entryClear();
            if (refreshStack) {
                updateStackBox(false, false);
            }
        } else {
            errorToast("stack full", Toast.LENGTH_SHORT);
        }
    }

    void formatEntry() {
        if (numEntry) {
            String enteredString = enteredNumPrep;

            /*
            if (enteredNum == (long) enteredNum)
                enteredString = String.format("%d", (long) enteredNum);
            else
                enteredString = String.format("%s", enteredNum);
            */

            if (exponentEntry) {
                enteredString = enteredString + "E";
                if (exponentNum != 0)
                    enteredString = enteredString + String.format("%d", (int) exponentNum);
            }

            inputView.setText(enteredString);
        } else {
            inputView.setText("");
        }
    }

    void entryClear() {
        enteredNumPrep = "";
        numEntry = false;
        exponentEntry = false;
        exponentNum = 0;
        inputView.setText("");
    }

    //Updates the Stack textView
    //  - If onlyLast is true, will add stackNum[stackIndex] to the end of the textView
    //  - If delLast is true, will remove the last number from the textView
    //  - Using both onlyLast and delLast effectively updates the last value
    //  - NOTE: If stackIndex has been changed since last full update, textView will not be synced
    //        with array if onlyLast or delLast is used
    void updateStackBox(boolean onlyLast, boolean delLast) {
        StringBuilder fullStackText = new StringBuilder();
        // Only do anything if there is an item to display
        if (stackIndex > 0) {
            if (delLast || onlyLast) {
                // Both deleting last item and only appending last item need the current stack
                //   text to work with
                fullStackText.append(stackView.getText().toString());

                if (delLast) {
                    // Get the last newline index of the stack
                    int lastNewLine = fullStackText.lastIndexOf("\n");

                    if (lastNewLine > 0) {
                        // Not sure why I append... Works somehow, but I think I should just
                        //   set it to the substring #FIXME ?
                        fullStackText.append(fullStackText.substring(0, lastNewLine));
                    } else {
                        fullStackText.setLength(0); //should effectively set it to nothing: AKA ""
                    }
                }

                if (onlyLast) {
                    fullStackText.append(fullStackText).append("\n").append(
                            formatDouble(stackNums[stackIndex - 1]));
                }
            } else {
                //redo whole stack
                String numStr = "";
                for (int i = 0; i < stackIndex; i++) {
                    numStr = formatDouble(stackNums[i]);

                    // Add the stack cursor and marker if needed
                    if (i == cursorPos - 1 && cursorPos == markPos) {
                        fullStackText.append("\n").append("►■  ").append(numStr);
                    } else if (i == cursorPos - 1) {
                        fullStackText.append("\n").append("►   ").append(numStr);
                    } else  if(i == markPos - 1) {
                        fullStackText.append("\n").append("■  ").append(numStr);
                    } else {
                        fullStackText.append("\n").append(numStr);
                    }
                }
            }
        }

        // Set the stack textbox to have a number of lines equal to the number of stack items
        //   (to ensure scrolling is sized correctly)
        stackView.setMaxLines(stackIndex);
        stackView.setText(fullStackText.toString());

        //stackView.scrollTo(0, 200);
        /*int cursorDelta = (cursorPos == 0) ? 0 : stackIndex - cursorPos;
        if (cursorDelta > 2) {
            //stackView.scrollTo(0, (cursorDelta-2)*stackView.getLineHeight());
            stackView.scrollTo(0, stackView.getBottom());
        } else {
            stackView.scrollTo(0, stackView.getBottom());
        }*/
    }

    String formatDouble(double number) {
        String formattedNumber = "";
        //set format string based on settings; placeholders for now
        String formatMode = "standard";  // #FIXME - USE ENUM!
        String numForm = "%1.4g"; // Scientific format, will need to better handle this
                                  //   in the future

        // Temporary until proper format options are implemented
        //   Utilizes standard float/integer formatting if the number isn't too large (> 1e6)
        //   or too small
        if (formatMode.equals("standard") && (Math.abs(number) < 1e6 && Math.abs(number) > 1e-6)) {
            // If the number is the same as itself converted to integer (long) form,
            //   just format as integer
            if (number == (long) number)
                formattedNumber = String.format(Locale.getDefault(), "%d", (long) number);
            else
                formattedNumber = String.format(Locale.getDefault(),"%s", number);
        } else {
            // Use the scientific format string
            formattedNumber = String.format(Locale.getDefault(), numForm, number);
        }
        return formattedNumber;
    }

    int numDigits(String number) {
        number = number.replace("-", "");
        number = number.replace(".", "");
        return number.length();
    }

    void modeHandle(boolean enableAlt, boolean enableStack) {
        if((enableAlt && altMode) || (enableStack && stackMode)){
            return;
        } else {
            if (stackMode && !enableStack ) {
                clearArmed = false;
                cursorPos = 0;
                markPos = 0;
                updateStackBox(false, false);
            }
            if (enableAlt) {

                fn1.setText(R.string.fn1_a);
                fn2.setText(R.string.fn2_a);
                fn3.setText(R.string.fn3_a);
                fn4.setText(R.string.fn4_a);
                fn5.setText(R.string.fn5_a);
                signBtn.setText(R.string.btn_sign_a);
                divBtn.setText(R.string.btn_div_d);
                multBtn.setText(R.string.btn_mult_d);
                subBtn.setText(R.string.btn_sub_d);
                addBtn.setText(R.string.btn_add_d);
                stackBtn.setText(R.string.btn_stack_a);
                altBtn.setBackgroundResource(R.drawable.key_pressed);
                stackBtn.setBackgroundResource(R.drawable.main_key);

            } else if (enableStack){

                fn1.setText(R.string.fn1_s);
                fn2.setText(R.string.fn2_s);
                fn3.setText(R.string.fn3_s);
                fn4.setText(R.string.fn4_s);
                fn5.setText(R.string.fn5_s);
                signBtn.setText(R.string.btn_sign_d);
                divBtn.setText(R.string.btn_div_s);
                multBtn.setText(R.string.btn_mult_s);
                subBtn.setText(R.string.btn_sub_s);
                addBtn.setText(R.string.btn_add_s);
                stackBtn.setText(R.string.btn_stack_a);
                altBtn.setBackgroundResource(R.drawable.main_key);
                stackBtn.setBackgroundResource(R.drawable.key_pressed);

            } else {

                fn1.setText(R.string.fn1_d);
                fn2.setText(R.string.fn2_d);
                fn3.setText(R.string.fn3_d);
                fn4.setText(R.string.fn4_d);
                fn5.setText(R.string.fn5_d);
                signBtn.setText(R.string.btn_sign_d);
                divBtn.setText(R.string.btn_div_d);
                multBtn.setText(R.string.btn_mult_d);
                subBtn.setText(R.string.btn_sub_d);
                addBtn.setText(R.string.btn_add_d);
                stackBtn.setText(R.string.btn_stack_d);
                altBtn.setBackgroundResource(R.drawable.main_key);
                stackBtn.setBackgroundResource(R.drawable.main_key);

            }
        }
        altMode = enableAlt;
        stackMode = enableStack;
    }


    void stackFnHandler(String stackComm) {

        int point1 = cursorPos - 1;
        int point2 = markPos - 1;
        switch (stackComm) {  // #FIXME - USE ENUM!
            case "fn1": //Roll Up
                if (stackIndex >= 2) {
                    if (point2 < 0)
                        point2 = stackIndex - 1;
                    if (point1 == point2)
                        break;
                    if (point1 < 0)
                        point1 = 0;
                    if (point2 < point1) {
                        int hodl = point2;
                        point2 = point1;
                        point1 = hodl;
                    }

                    stackNums[maxStack] = stackNums[point1];
                    for (int i = point1; i < point2; i++) {
                        stackNums[i] = stackNums[i + 1];
                    }
                    stackNums[point2] = stackNums[maxStack];

                } else {
                    errorToast("too few arguments", Toast.LENGTH_SHORT);
                }
                break;
            case "fn2": //Roll Down
                if (stackIndex >= 2) {
                    if (point2 < 0)
                        point2 = stackIndex - 1;
                    if (point1 == point2)
                        break;
                    if (point1 < 0)
                        point1 = 0;
                    if (point2 < point1) {
                        int hodl = point2;
                        point2 = point1;
                        point1 = hodl;
                    }

                    stackNums[maxStack] = stackNums[point2];
                    for (int i = point2; i > point1; i--) {
                        stackNums[i] = stackNums[i-1];
                    }
                    stackNums[point1] = stackNums[maxStack];
                } else {
                    errorToast("too few arguments", Toast.LENGTH_SHORT);
                }
                break;
            case "fn3": //Mark
                if (markPos == 0) {
                    markPos = cursorPos;
                } else {
                    markPos = 0;
                }
                break;
            case "fn4": //Copy
                if (stackIndex < maxStack) {
                    if (stackIndex >= 1) {
                        if (point1 < 0) point1 = stackIndex - 1;
                        if (point2 < 0) {
                            stackNums[stackIndex] = stackNums[point1];
                            stackIndex++;
                        } else {
                            stackNums[maxStack] = stackNums[point1];
                            for (int i = stackIndex; i > point2; i--) {
                                stackNums[i] = stackNums[i-1];
                            }
                            stackNums[point2] = stackNums[maxStack];
                            stackIndex++;
                            if (cursorPos >= markPos)
                                cursorPos++;
                            markPos = (markPos < stackIndex) ? markPos + 1 : 0;
                        }

                    }
                } else {
                    errorToast("stack full", Toast.LENGTH_SHORT);
                }
                break;
            case "fn5": //Swap
                if (stackIndex >= 2) {
                    if (point1 < 0) point1 = stackIndex - 2;
                    if (point2 < 0) point2 = stackIndex - 1;
                    if (point1 == point2)
                        break;
                    stackNums[maxStack] = stackNums[point1];
                    stackNums[point1] = stackNums[point2];
                    stackNums[point2] = stackNums[maxStack];
                    if (point1 == (stackIndex - 2) && point2 == (stackIndex - 1)) {
                        modeHandle(false, false);
                    }
                } else {
                    errorToast("too few arguments", Toast.LENGTH_SHORT);
                }
                break;
            case "op1": //Cursor up (actually cursorPos-=1 since top is 0)
                // Set cursor to the bottom of the stack (the highest stack index) if it isn't
                //   currently active (indicated by being 0)
                if (cursorPos <= 0)
                    cursorPos = stackIndex;

                // Only move cursor up (decrement) if it isn't at the top (= 1) and wasn't just
                //   activated - Prevents advancing up past the top entry
                else if (cursorPos != 1)
                    cursorPos--;
                break;
            case "op2": //Cursor down (actually cursorPos+=1 since top is 0)
                // Only move down (increment) if the cursor is active (> 0)
                if (cursorPos > 0) {
                    cursorPos++;
                    // Deactivate the cursor if it was moved past the bottom of the stack
                    //   (> stackIndex) by setting it to 0
                    if (cursorPos > stackIndex)
                        cursorPos = 0;
                }
                break;
            case "op4": //Clear stack
                if (clearArmed) {
                    stackIndex = 0;
                } else {
                    clearArmed = true;
                    CharSequence text = "Press 'Clear' again to clear stack";
                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case "drop": //Drop
                if (stackIndex > 0) {
                    if (point1 < 0) {
                        stackIndex--;
                    } else {
                        for (int i = point1; i < stackIndex-1; i++) {
                            stackNums[i] = stackNums[i+1];
                        }
                        stackIndex--;
                    }
                }
        }

        if (cursorPos > stackIndex)
            cursorPos = 0;
        if (markPos > stackIndex)
            markPos = 0;
        updateStackBox(false, false);
    }




    void errorToast(String errorText, int duration) {
        CharSequence text = "ERROR: " + errorText;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }
}
