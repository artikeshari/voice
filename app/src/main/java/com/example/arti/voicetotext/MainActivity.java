package com.example.arti.voicetotext;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageButton btn;
    private ImageButton playBtn;
    private TextView textInput;
    private TextToSpeech mTts;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int MY_DATA_CHECK_CODE=150;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn=(ImageButton)findViewById(R.id.mic);
        playBtn=(ImageButton)findViewById(R.id.playButton);
        textInput=(TextView)findViewById(R.id.txtSpeechInput);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeechConverter();
                Log.v("Main","play button");
            }
        });
    }
    private void promptSpeechInput()
    {
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,R.string.speech_prompt);
        try
        {
            startActivityForResult(intent,REQ_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException a)
        {
            Toast.makeText(getApplicationContext(),
                    R.string.speech_not_supported,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQ_CODE_SPEECH_INPUT:
            {
                if(resultCode==RESULT_OK && data!=null)
                {
                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textInput.setText(result.get(0));
                }
            }
            break;
            case MY_DATA_CHECK_CODE: {
                if (resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    Intent installIntent = new Intent();
                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    Log.v("Main", "need intallation");
                    startActivity(installIntent);
                    // mTts=new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener());

                }
                mTts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            mTts.setLanguage(Locale.US);
                        }
                        String myText = textInput.getText().toString();
                        mTts.speak(myText, TextToSpeech.QUEUE_FLUSH, null);
                    }
                });
            }
        }
    }
    private void textToSpeechConverter()
    {
        Intent checkIntent=new Intent();
       checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent,MY_DATA_CHECK_CODE);
    }
}
