/**Created by Austin Patel*/

package austinpatel.handwrittenletterrecognition;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import austinpatel.handwrittenletterrecognition.data.Alphabet;
import austinpatel.handwrittenletterrecognition.data.Constants;
import austinpatel.handwrittenletterrecognition.data.FileManager;
import austinpatel.handwrittenletterrecognition.data.LetterData;
import austinpatel.handwrittenletterrecognition.neural_network.BackpropagationAlgorithm;
import austinpatel.handwrittenletterrecognition.neural_network.NeuralNetwork;
import austinpatel.handwrittenletterrecognition.neural_network.Trainer;

/**Activity for the drawing interface that takes a drawn image and classifies
 * it using the neural network.*/
public class MainActivity extends AppCompatActivity {

    private static final int BRUSH_WIDTH = 20;

    private TextView resultTextView;
    private NeuralNetwork neuralNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileManager.context = this;

        // Get the layout
        RelativeLayout coordinatorLayout = (RelativeLayout) findViewById(R.id.mainLayout);

        // Initialize the drawing interface
        final Paint drawingPaint = new Paint() {{
            setAntiAlias(true);
            setDither(true);
            setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
            setStyle(Paint.Style.STROKE);
            setStrokeJoin(Paint.Join.ROUND);
            setStrokeCap(Paint.Cap.ROUND);
            setStrokeWidth(MainActivity.BRUSH_WIDTH);
        }};

        final DrawingView drawingView = new DrawingView(this, drawingPaint, this);

        coordinatorLayout.addView(drawingView);

        // Set up the neural network
        neuralNetwork = new NeuralNetwork(new BackpropagationAlgorithm(0.1), Constants.GRID_WIDTH * Constants.GRID_HEIGHT, Alphabet.getLength());
        Trainer trainer = new Trainer(neuralNetwork);
        trainer.trainNetwork();

        // Set up result TextView
        resultTextView = (TextView) findViewById(R.id.resultTextView);

        // Set up clear Button
        Button clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.clear();
                resultTextView.setText("");
            }
        });

        // Set up the mode button
        final Button modeButton = (Button) findViewById(R.id.modeButton);
        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.changeMode();

                if (modeButton.getText().toString().equals(getResources().getString(R.string.mode_button_text_simple)))
                    modeButton.setText(getResources().getString(R.string.mode_button_text_advanced));
                else
                    modeButton.setText(getResources().getString(R.string.mode_button_text_simple));
            }
        });
    }

    /**Takes in the drawn boxes and uses the neural network to predict
     * that letter and updates the prediction text on the layout.*/
    public void updateNeuralNetworkOutput(int[][] fill) {
        int[] colors1D = LetterData.data2DTo1D(fill);

        double[] result = neuralNetwork.getOutput(colors1D);

        int highestIndex = 0;

        for (int i = 0; i < result.length; i++)
            if (result[highestIndex] < result[i]) {
                highestIndex = i;
            }

        boolean diff = false;

        for (int i : colors1D)
            if (i != 0)
                diff = true;

        String resultText = String.valueOf(Alphabet.getCharacter(highestIndex));

        if (diff)
            resultTextView.setText(resultText);
    }

}
