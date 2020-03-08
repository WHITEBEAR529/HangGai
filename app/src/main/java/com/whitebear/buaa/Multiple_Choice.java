package com.whitebear.buaa;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Multiple_Choice extends Fragment {
    TextView question;
    TextView answer;
    ImageView imageView;
    CheckBox A;
    CheckBox B;
    CheckBox C;
    CheckBox D;
    Button button;
    String[] questionlist;
    int[] position;
    String answerState="下一题",subject;
    Context context;

    public Multiple_Choice(Context context,String subject,String[] questionlist,int[] position) {
        // Required empty public constructor
        this.questionlist=questionlist;
        this.position=position;
        this.subject=subject;
        this.context=context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_multiple__choice, container, false);
        question=view.findViewById(R.id.mutiple_question);
        answer=view.findViewById(R.id.answer);
        button=view.findViewById(R.id.next_page);
        imageView=view.findViewById(R.id.image_multiple);
        A=view.findViewById(R.id.mutiple_A);
        B=view.findViewById(R.id.mutiple_B);
        C=view.findViewById(R.id.mutiple_C);
        D=view.findViewById(R.id.mutiple_D);
        question.setText(questionlist[7]+"."+questionlist[0]);
        A.setText(questionlist[1]);
        B.setText(questionlist[2]);
        C.setText(questionlist[3]);
        D.setText(questionlist[4]);
        button.setText(answerState);
        if(answerState.equals("下一题")|answerState.equals("点这个就做完啦")){
            button.setEnabled(true);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("nextpage");
                    if (answerState.equals("点这个就做完啦")) {
                        intent.putExtra("final", true);
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    } else if(answerState.equals("下一题")){
                        intent.putExtra("final", false);
                        if (check()) {
                            if (position[0] + 1 == position[1]) {
                                answerState = "点这个就做完啦";
                                button.setText("点这个就做完啦");
                                answer.setText("正确答案是：" + questionlist[5]);
                            } else {
                                answerState = "回答正确";
                                button.setText("回答正确");
                            }

                            DBOpenHelper myDbHelper = new DBOpenHelper(context);
                            String sql = "update " + subject + "_questions set wrong=0 where id=" + questionlist[7];
                            myDbHelper.openDataBase();
                            myDbHelper.getMyDataBase().execSQL(sql);
                            myDbHelper.getMyDataBase().close();

                            intent.putExtra("answer", "right");
                            intent.putExtra("score", 2);
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                        } else {
                            if (position[0] + 1 == position[1]) {
                                answerState = "点这个就做完啦";
                                button.setText("点这个就做完啦");
                            } else {
                                answerState = "回答错误";
                                button.setText("回答错误");
                            }

                            DBOpenHelper myDbHelper = new DBOpenHelper(context);
                            String sql = "update " + subject + "_questions set WRONG=1 where id=" + questionlist[7];
                            myDbHelper.openDataBase();
                            myDbHelper.getMyDataBase().execSQL(sql);
                            myDbHelper.getMyDataBase().close();

                            answer.setText("正确答案是：" + questionlist[5]);
                            intent.putExtra("answer", "wrong");
                            intent.putExtra("score", 0);
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                        }
                    }else{
                            Intent intent1 = new Intent("onlynext");
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent1);
                    }
                }
            });
        }else if(answerState.equals("回答正确")){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("onlynext");
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                }
            });
        }else{
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("onlynext");
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                }
            });
            answer.setText("正确答案是："+questionlist[5]);
        }
        // 判断是否有图片，有则显示
        if(questionlist[6].length()>0){
            String[] string=questionlist[6].split("\\.");
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append("n");
            stringBuilder.append(string[0]);
            int paramInt = getResources().getIdentifier(stringBuilder.toString(), "drawable", "com.whitebear.buaa");
            imageView.setImageDrawable(getResources().getDrawable(paramInt));
        }

        return view;
    }

    public Boolean check(){
        StringBuilder answer=new StringBuilder();
        if(A.isChecked()){
            answer.append("A-");
        }
        if(B.isChecked()){
            answer.append("B-");
        }
        if(C.isChecked()){
            answer.append("C-");
        }
        if(D.isChecked()){
            answer.append("D-");
        }
        if(answer.toString().length()!=0) {
            answer.deleteCharAt(answer.length() - 1);
        }
        if (questionlist[5].equals(answer.toString())){
            return true;
        }else return false;
    }
}
