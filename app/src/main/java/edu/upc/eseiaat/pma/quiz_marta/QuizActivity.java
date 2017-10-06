package edu.upc.eseiaat.pma.quiz_marta;

import android.content.DialogInterface;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import edu.upc.eseiaat.pma.quiz_marta.R;

public class QuizActivity extends AppCompatActivity {

    public static final String CORRECT_ANSWER = "correct_answer";
    public static final String CURRENT_QUESTION = "current_question";
    public static final String ANSWER_IS_CORRECT = "answer_is_correct";
    public static final String ANSWER = "answer";
    private int id_answers[]={
            R.id.answer1,
            R.id.answer2,
            R.id.answer3,
            R.id.answer4
    };

    private String[] all_questions;

    private TextView text_question;
    private RadioGroup group;
    private Button btn_next, btn_prev;


    // todos ellos los queremos guardar
    private int correct_answer;
    private int current_question;
    private boolean[] answer_is_correct;
    private int[] answer;

    @Override
    protected void onSaveInstanceState(Bundle outState) { // CUIDADO HEM D'AGAFAR EL QUE NO TE DOS PARAMETRES
        Log.i("lifecycle","onSaveInstanceState");

        super.onSaveInstanceState(outState); // objecto con 'all' lo que quieres guardar
        // Tot el que guardem a la motxilla amb el Put
        outState.putInt(CORRECT_ANSWER, correct_answer);
        outState.putInt(CURRENT_QUESTION, current_question);
        outState.putBooleanArray(ANSWER_IS_CORRECT, answer_is_correct);
        outState.putIntArray(ANSWER, answer);

    }

    @Override
    protected void onStop() {
        Log.i("lifecycle","onStop");
        super.onStop();
    }

    @Override
    protected void onStart() {
        Log.i("lifecycle","onStart");
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.i("lifecycle","onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("lifecycle","onCreate"); // ver por donde pasa l'aplicación
        super.onCreate(savedInstanceState); // lo que guardaste ( la primera vez sera nulo )
        setContentView(R.layout.activity_quiz);

        text_question = (TextView) findViewById(R.id.text_question);
        group = (RadioGroup) findViewById(R.id.Group_answers);
        btn_next = (Button) findViewById(R.id.btn_check);
        btn_prev = (Button) findViewById(R.id.btn_prev);

        all_questions = getResources().getStringArray(R.array.all_questions);

        if (savedInstanceState == null){ // si no hay nada guardado en la motxilla
            startOver(); // volver a empezar ( hemos creado un metodo porque se tiene que repetir varias veces cmand+alt+M

        } else {
            // Tot el que guardem a la motxilla amb el Get
            Bundle state = savedInstanceState;
            correct_answer = state.getInt(CORRECT_ANSWER);
            current_question = state.getInt(CURRENT_QUESTION);
            answer_is_correct= state.getBooleanArray(ANSWER_IS_CORRECT);
            answer = state.getIntArray(ANSWER);
            //mostra'm el que tenia
            showQuestion();

        }



        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
                if (current_question < all_questions.length-1) {
                    current_question++;
                    showQuestion();
                } else {
                    checkResults();
                }

            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
                if (current_question > 0) {
                    current_question--;
                    showQuestion();
                }
            }
        });
    }

    private void startOver() {
        answer_is_correct = new boolean[all_questions.length];
        answer = new int[all_questions.length];
        for (int i = 0; i < answer.length; i++) {
            answer[i] = -1;
        }
        current_question = 0;
        showQuestion();
    }

    private void checkResults() {
        int correctas = 0, incorrectas = 0, nocontestadas=0;
        for (int i=0; i< all_questions.length; i++) {

            if (answer_is_correct[i]) correctas++;
            else if (answer[i] == -1) nocontestadas++;
            else incorrectas++;
        }

        // TODO: Permitir traducción de este texto
        String message =

               String.format("Correctas: %d\nIncorrectas: %d\nNo contestadsas: %d\n",
                       correctas, incorrectas, nocontestadas);

        // Cuadro de dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // objeto builder
        builder.setTitle(R.string.results); // Título del mensaje
        builder.setMessage(message); // el contenido
        builder.setCancelable(false); // no se puede ir para atras
        builder.setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() { // Esta okei, acabar
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish(); // cierra la actividad
                // QuizActivity.this.finish() seria lo mismo
            }
        });



        builder.setNegativeButton(R.string.start_over, new DialogInterface.OnClickListener() { // no, quiero volver a hacer el quiz
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startOver();
                // borrar respuestas y volver al principio
            }
        }) ;

        builder.create().show(); // crearlo
    }

    private void checkAnswer() {
        int id = group.getCheckedRadioButtonId();
        int ans = -1;
        for (int i = 0; i < id_answers.length; i++) {
            if (id_answers[i] == id) {
                ans = i;
            }
        }
        answer_is_correct[current_question] = (ans == correct_answer);
        answer[current_question] = ans;
    }

    private void showQuestion() {
        String q = all_questions[current_question];
        String[] parts = q.split(";");

        group.clearCheck();

        text_question.setText(parts[0]);
        for (int i = 0; i < id_answers.length; i++) {
            RadioButton rb = (RadioButton) findViewById(id_answers[i]);
            String ans = parts[i+1];
            if (ans.charAt(0) == '*') {
                correct_answer = i;
                ans = ans.substring(1);
            }
            rb.setText(ans);
            if (answer[current_question] == i) {
                rb.setChecked(true);
            }
        }
        if (current_question == 0) {
            btn_prev.setVisibility(View.GONE);
        } else {
            btn_prev.setVisibility(View.VISIBLE);
        }
        if (current_question == all_questions.length-1) {
            btn_next.setText(R.string.finish);
        } else {
            btn_next.setText(R.string.next);
        }
    }
}

// MARTA BARRACHINA