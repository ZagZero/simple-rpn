# simple-rpn

This is a simple calculator for Android devices that has an input style that mimics the style of older HP calculators (Reverse Polish Notation/RPN).

ToDo:
  First Priority
    - Fix 'tan' button actually performing cosine operation (check atan)
    - Make 'Swap' button exit stack mode when pressed
    - Fix 'too few arguments' error deleting the item
    - Stop 'Drop' from deleting stack item if a number is being entered while in stack mode
  
  Second Priority
    - Make 'Stack' button swap when shift isn't pressed (relabel accordingly)
    - Prevent landscape mode
    - Swap squareroot and xth root actions in regards to shift
    - Make y^x the shift action and 1/x the non-shift action for the F5 button
    
  Third Priority
    - Track stackbox to cursor in stack mode
    - Fix 'standard formatting' (sometimes changes how it appears for seemingly no reason)
    - Add options menu
    - Add new formatting options
    - Add alternative mode options for the F-keys (or at least system that allows for expansion)
    
  Maybe Someday
    - Overhaul stack storage and display system to support non-scalar values
    - Add Vector support/mode
    - Add Matrix support/mode
    - Add value storage support
