package com.example.shellexecutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    private String executeCommand(String command, String commandType) {
        Process process = null;
        try {
            if (commandType.equals("ROOT"))
                process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            else
                process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            BufferedReader errorBufferedReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));

            String line;
            StringBuilder output = new StringBuilder("===== [START] =====\n");
            while ((line = bufferedReader.readLine()) != null) {
                output.append(line).append("\n");
            }
            while ((line = errorBufferedReader.readLine()) != null) {
                output.append("ERROR: ").append(line).append("\n");
            }
            output.append("===== [END] =====\n");
            return output.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);

        button.setOnClickListener(v -> executeAndDisplay("NON ROOT"));
        button2.setOnClickListener(v -> executeAndDisplay("ROOT"));
        button3.setOnClickListener(v -> editText.append(" 2> /dev/null"));
        button4.setOnClickListener(v -> editText.setText(""));
    }

    private void executeAndDisplay(String commandType) {
        textView.setText("");
        String textToFind = editText.getText().toString();
        executorService.execute(() -> {
            String result = executeCommand(textToFind, commandType);
            handler.post(() -> textView.setText(result));
        });
    }
}
