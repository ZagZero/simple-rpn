package zag.zero.testcalc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import java.lang.Math;



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
    String stackStrings[] = new String[maxStack];
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        inputView = (TextView) this.findViewById(R.id.inputView);
        stackView = (TextView) this.findViewById(R.id.stackView);
        stackView.setMovementMethod(new ScrollingMovementMethod());

        fn1 = (Button) this.findViewById(R.id.btn_fn_1);
        fn2 = (Button) this.findViewById(R.id.btn_fn_2);
        fn3 = (Button) this.findViewById(R.id.btn_fn_3);
        fn4 = (Button) this.findViewById(R.id.btn_fn_4);
        fn5 = (Button) this.findViewById(R.id.btn_fn_5);
        //enterBtn = (Button) this.findViewById(R.id.btn_enter);
        signBtn = (Button) this.findViewById(R.id.btn_misc_sign);
        stackBtn = (Button) this.findViewById(R.id.btn_misc_stack);
        //backBtn = (Button) this.findViewById(R.id.btn_misc_back);
        divBtn = (Button) this.findViewById(R.id.btn_op_div);
        multBtn = (Button) this.findViewById(R.id.btn_op_mult);
        subBtn = (Button) this.findViewById(R.id.btn_op_sub);
        addBtn = (Button) this.findViewById(R.id.btn_op_add);
        altBtn = (Button) this.findViewById(R.id.btn_misc_shift);

    }

    public void numButtonPressed(View v) {
        String numStr = v.getTag().toString();
        numStr = numStr.substring(0, 1);
        int numPressed = Integer.parseInt(numStr);
        if (exponentEntry) {
            if (Math.abs(exponentNum) * 10 < 100)
                exponentNum = exponentNum * 10 + (numPressed * ((exponentNum >= 0)?1:-1));
        }// else if (decimalEntry);
        else
            //enteredNum = enteredNum * 10 + numPressed
            if (numDigits(enteredNumPrep) < 16)
                enteredNumPrep = enteredNumPrep + numStr;
        numEntry = true;
        formatEntry();
    }

    public void miscButtonPressed(View v) {
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
                modeHandle(false, !stackMode);
                break;
            case "3": //Back Button
                if (!stackMode) {
                    if (numEntry) {
                        if (exponentEntry) {
                            exponentEntry = exponentNum != 0;
                            exponentNum /= 10;

                            //} else if (decimalEntry) {
                        } else {
                            //enteredNum = (long) enteredNum / 10;
                            int entryLength = enteredNumPrep.length();
                            String nextDelChar = "";
                            if (entryLength > 1) {
                                nextDelChar = enteredNumPrep.substring(entryLength - 2,
                                        entryLength - 1);
                                if (nextDelChar.equals("-"))
                                    entryClear();
                                else
                                    enteredNumPrep = enteredNumPrep.substring(0, entryLength - 1);
                            } else {
                                entryClear();
                            }
                        }
                    } else {
                        //drop last stack val
                        if (stackIndex > 0) {
                            stackIndex--;
                            updateStackBox(false, true);
                        }
                    }
                } else {
                    stackFnHandler("drop");
                }
                break;
            case "4": //Decimal Button
                if (numEntry) {
                    if (!exponentEntry) {
                        if (!enteredNumPrep.contains(".") && numDigits(enteredNumPrep) < 16)
                            enteredNumPrep = enteredNumPrep + ".";
                    }
                } else {
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

    public void enterPressed(View v){
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

    public void operatorPressed(View v){
        String indexStr = v.getTag().toString();
        indexStr = indexStr.substring(0, 1);

        if (!stackMode) {
            double opX;
            double opY;
            boolean failure = false;

            if (numEntry)
                stackEntry(false);

            if (stackIndex >= 2) {
                opY = stackNums[stackIndex - 2];
                opX = stackNums[stackIndex - 1];
            } else {
                errorToast("too few arguments", Toast.LENGTH_SHORT);
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

    public void functionPressed(View v) {
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

            if (numEntry)
                stackEntry(false);


            try {
                switch (fnNum) {
                    case 1: //sin
                        numOperands = 1;
                        if (stackIndex >= numOperands) {
                            opX = stackNums[stackIndex - 1];
                        } else {
                            errorToast("too few arguments", Toast.LENGTH_SHORT);
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
                            failure = true;
                            return;
                        }
                        //Degree option
                        stackNums[stackIndex - numOperands] = Math.cos(opX);
                        break;
                    case 4: //Square root
                        numOperands = 1;
                        if (stackIndex >= numOperands) {
                            opX = stackNums[stackIndex - 1];
                        } else {
                            errorToast("too few arguments", Toast.LENGTH_SHORT);
                            return;
                        }
                        if (opX < 0) {
                            errorToast("complex numbers not supported", Toast.LENGTH_SHORT);
                            failure = true;
                        } else {
                            stackNums[stackIndex - numOperands] = Math.sqrt(opX);
                        }
                        break;
                    case 5: //Squared
                        numOperands = 1;
                        if (stackIndex >= numOperands) {
                            opX = stackNums[stackIndex - 1];
                        } else {
                            errorToast("too few arguments", Toast.LENGTH_SHORT);
                            failure = true;
                            return;
                        }
                        stackNums[stackIndex - numOperands] = Math.pow(opX, 2);
                        break;
                    case 11: //asin
                        if (stackIndex >= numOperands) {
                            opX = stackNums[stackIndex - 1];
                        } else {
                            errorToast("too few arguments", Toast.LENGTH_SHORT);
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
                            failure = true;
                            return;
                        }
                        //Degree option
                        stackNums[stackIndex - numOperands] = Math.cos(opX);
                        break;
                    case 14: //xth root of y
                        numOperands = 2;
                        if (stackIndex >= numOperands) {
                            opY = stackNums[stackIndex - 2];
                            opX = stackNums[stackIndex - 1];
                        } else {
                            errorToast("too few arguments", Toast.LENGTH_SHORT);
                            failure = true;
                            return;
                        }
                        if (opY < 0) {
                            errorToast("complex numbers not supported", Toast.LENGTH_SHORT);
                            failure = true;
                        } else {
                            stackNums[stackIndex - numOperands] = Math.pow(opY, 1 / opX);
                        }
                        break;
                    case 15: //y to the power of x
                        numOperands = 2;
                        if (stackIndex >= numOperands) {
                            opY = stackNums[stackIndex - 2];
                            opX = stackNums[stackIndex - 1];
                        } else {
                            errorToast("too few arguments", Toast.LENGTH_SHORT);
                            failure = true;
                            return;
                        }
                        stackNums[stackIndex - numOperands] = Math.pow(opY, opX);

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
        String fullStackText = "";
        //set format string based on settings
        String numForm = "%g";
        if (stackIndex > 0) {
            if (delLast || onlyLast) {
                fullStackText = stackView.getText().toString();

                if (delLast) {
                    int lastNewLine = fullStackText.lastIndexOf("\n");

                    if (lastNewLine > 0) {
                        fullStackText = fullStackText.substring(0, lastNewLine);
                    } else {
                        fullStackText = "";
                    }
                }

                if (onlyLast) {
                    stackStrings[stackIndex - 1] = String.format(numForm, stackNums[stackIndex - 1]);
                    fullStackText = fullStackText + "\n" + stackStrings[stackIndex - 1];
                }
            } else {
                //redo whole stack, draw cursor at appropriate pos
                for (int i = 0; i < stackIndex; i++) {
                    stackStrings[i] = String.format(numForm, stackNums[i]);

                    if (i == cursorPos - 1 && cursorPos == markPos) {
                        fullStackText = fullStackText + "\n" + "►■  " + stackStrings[i];
                    } else if (i == cursorPos - 1) {
                        fullStackText = fullStackText + "\n" + "►   " + stackStrings[i];
                    } else  if(i == markPos - 1) {
                        fullStackText = fullStackText + "\n" + "■  " + stackStrings[i];
                    } else {
                        fullStackText = fullStackText + "\n" + stackStrings[i];
                    }
                }
            }
        }
        stackView.setMaxLines(stackIndex);
        stackView.getScrollY();
        stackView.setText(fullStackText);
        stackView.scrollTo(0, 200);
        /*int cursorDelta = (cursorPos == 0) ? 0 : stackIndex - cursorPos;
        if (cursorDelta > 2) {
            //stackView.scrollTo(0, (cursorDelta-2)*stackView.getLineHeight());
            stackView.scrollTo(0, stackView.getBottom());
        } else {
            stackView.scrollTo(0, stackView.getBottom());
        }*/
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
        switch (stackComm) {
            case "fn1": //Roll Up
                if (stackIndex >= 2) {
                    if (point2 < 0)
                        point2 = stackIndex - 1;
                    if (point1 == point2)
                        break;
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
                } else {
                    errorToast("too few arguments", Toast.LENGTH_SHORT);
                }
                break;
            case "op1": //Cursor up (actually cursorPos-=1 since top is 0)
                if (cursorPos != 1)
                    cursorPos--;
                if (cursorPos < 0)
                    cursorPos = stackIndex;
                break;
            case "op2": //Cursor down (actually cursorPos+=1 since top is 0)
                if (cursorPos != 0) {
                    cursorPos++;
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
