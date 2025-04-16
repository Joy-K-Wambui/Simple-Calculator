package com.per.SimpleCalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView result;
    private double lastAnswer = 0;
    private StringBuilder equationBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);
        setupListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupListeners() {
        View.OnClickListener numberClickListener = v -> {
            Button b = (Button) v;
            equationBuilder.append(b.getText().toString());
            result.setText(equationBuilder.toString());
        };

        // Number buttons
        for (int i = 0; i <= 9; i++) {
            int resID = getResources().getIdentifier("btn" + i, "id", getPackageName());
            findViewById(resID).setOnClickListener(numberClickListener);
        }
        findViewById(R.id.decimal).setOnClickListener(numberClickListener);

        // Operators
        findViewById(R.id.add).setOnClickListener(v -> setOperation("+"));
        findViewById(R.id.sub).setOnClickListener(v -> setOperation("-"));
        findViewById(R.id.mul).setOnClickListener(v -> setOperation("*"));
        findViewById(R.id.div).setOnClickListener(v -> setOperation("/"));
        findViewById(R.id.power).setOnClickListener(v -> setOperation("^"));

        findViewById(R.id.percent).setOnClickListener(v -> applyPercentage());

        // Pi button
        findViewById(R.id.pi).setOnClickListener(v -> appendPiValue());

        // Equal button to calculate the result
        findViewById(R.id.equal).setOnClickListener(v -> calculateResult());

        // Reset and backspace
        findViewById(R.id.reset).setOnClickListener(v -> resetCalculator());
        findViewById(R.id.backspace).setOnClickListener(v -> backspaceInput());
    }

    private void appendPiValue() {
        equationBuilder.append(Math.PI);result.setText(equationBuilder.toString());}

    // Get current input from equationBuilder
    private double getCurrentInput() {
        String currentInput = equationBuilder.toString();
        return currentInput.isEmpty() ? 0 : Double.parseDouble(currentInput);
    }

    private void displayResult(double output) {
        result.setText(String.valueOf(output));
        equationBuilder.setLength(0);
        lastAnswer = output;
    }

    // Set an operation when an operator button is clicked
    private void setOperation(String op) {
        String currentInput = equationBuilder.toString();

        if (currentInput.isEmpty() && op.equals("-")) {
            equationBuilder.append("-");
        } else if (isNumeric(currentInput.charAt(currentInput.length() - 1) + "")) {
            equationBuilder.append(" ").append(op).append(" ");
        } else if (op.equals("-") && (currentInput.endsWith(" ") || currentInput.isEmpty())) {
            equationBuilder.append("-");
        }
        result.setText(equationBuilder.toString());
    }


    // Apply percentage logic
    private void applyPercentage() {
        try {
            if (!equationBuilder.toString().isEmpty()) {
                double lastNumber = getCurrentInput();
                double percentageValue = lastNumber / 100;
                displayResult(percentageValue);
            }
        } catch (NumberFormatException e) {
            result.setText("Error: Invalid Input");
        }
    }

    // Calculate result for the expression
    @SuppressLint("SetTextI18n")
    private void calculateResult() {
        try {
            String equation = equationBuilder.toString();
            double resultValue = evaluateExpression(equation);

            result.setText(equation + "\n" + resultValue);
            equationBuilder.setLength(0);
            equationBuilder.append(resultValue);
            lastAnswer = resultValue;

        } catch (Exception e) {
            result.setText("Error: " + e.getMessage());
        }
    }

    private double evaluateExpression(String equation) {
        String[] tokens = equation.split(" "); // Split the input equation into tokens
        Stack<Double> values = new Stack<>(); // Stack to hold numeric values
        Stack<String> ops = new Stack<>(); // Stack to hold operators

        for (String token : tokens) {
            if (token.isEmpty()) continue; // Skip empty tokens

            if (isNumeric(token)) {
                // If the token is a number, push it onto the values stack
                values.push(Double.parseDouble(token));
            } else {
                // If the token is an operator
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(token)) {
                    // Apply the operation and push the result onto the values stack
                    values.push(applyOperation(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(token); // Push the current operator onto the ops stack
            }
        }

        // After reading all tokens, apply any remaining operations
        while (!ops.isEmpty()) {
            values.push(applyOperation(ops.pop(), values.pop(), values.pop())); // Apply remaining operations
        }

        return values.pop(); // The result of the expression is the last value left in the stack
    }

    // Check if a string is numeric
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Apply basic operations
    private double applyOperation(String op, double b, double a) {
        switch (op) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
            case "^":
                return Math.pow(a, b);
        }
        return 0;
    }

    // Define operator precedence
    private int precedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
        }
        return 0;
    }

    // Reset calculator
    private void resetCalculator() {
        equationBuilder.setLength(0);
        result.setText("");
        lastAnswer = 0;
    }

    // Backspace function
    private void backspaceInput() {
        if (equationBuilder.length() > 0) {
            equationBuilder.deleteCharAt(equationBuilder.length() - 1);
            result.setText(equationBuilder.toString());
        }
    }
}
