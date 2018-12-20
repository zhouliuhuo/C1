package com.example.administrator.c1;

import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ObjectStreamException;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private Button[] btns;
    private Button btnClear,btnDelect,btnEqual;
    private TextView tvDisplay;
    private Stack<Double> numberStack;
    private Stack<Character> symbolStack;
    private String strRecord="";
    private String strCurrent="";
    private Spannable spannable;
    private long firstClickTime= System.currentTimeMillis();
    private long secondClickTime;
    private boolean tvExtent=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvDisplay=(TextView) findViewById(R.id.tvDisplay);//获取TextView的id
        tvDisplay.setMovementMethod(ScrollingMovementMethod.getInstance());//设置滚动监听
        tvDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondClickTime=System.currentTimeMillis();
                if(secondClickTime-firstClickTime<500)
                {
                    ViewGroup.LayoutParams layoutParams=tvDisplay.getLayoutParams();
                    if(tvExtent)
                    {
                        Log.d("MainActivity",tvDisplay.getHeight()+"");
                        layoutParams.height=((LinearLayout)findViewById(R.id.activity_main)).getHeight();
                        tvExtent=false;
                    }else
                    {
                        layoutParams.height=300;
                        tvExtent=true;
                    }
                    tvDisplay.setLayoutParams(layoutParams);
                }
                firstClickTime=secondClickTime;
            }
        });
        btns=new Button[19];
        getId(btns);
        for(int i=0;i<btns.length;i++)
        {
            btns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strCurrent=tvDisplay.getText()+((Button)v).getText().toString();
                    spannable=new SpannableString(strCurrent);
                    spannable.setSpan(new AbsoluteSizeSpan(15,true),0,strRecord.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    tvDisplay.setText(spannable);
                    int tvDisplayHeight=tvDisplay.getHeight();
                    int heightDiff=tvDisplay.getLayout().getLineTop(tvDisplay.getLineCount())-tvDisplayHeight;
                    if(heightDiff>0)
                    {
                        tvDisplay.scrollTo(0,heightDiff);
                    }else{
                        tvDisplay.scrollTo(0,0);
                    }
                }
            });

            btnClear=(Button)findViewById(R.id.btnClear);
            btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvDisplay.setText("");
                    tvDisplay.scrollTo(0,0);
                    strRecord="";
                }
            });

            btnDelect=(Button)findViewById(R.id.btnDelect);
            btnDelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strCurrent=tvDisplay.getText().toString();
                    if(strCurrent.length()>strRecord.length())
                    {
                        strCurrent=strCurrent.substring(0,strCurrent.length()-1);
                        spannable=new SpannableString(strCurrent);
                        spannable.setSpan(new AbsoluteSizeSpan(15,true),0,strRecord.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                    tvDisplay.setText(spannable);
                    int tvDisplayHeight=tvDisplay.getHeight();
                    int heightDiff=tvDisplay.getLayout().getLineTop(tvDisplay.getLineCount())-tvDisplayHeight;
                    if(heightDiff>0)
                    {
                        tvDisplay.scrollTo(0,heightDiff);
                    }else{
                        tvDisplay.scrollTo(0,0);
                    }
                }
            });

            btnEqual=(Button)findViewById(R.id.btnEqual);
            btnEqual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str=tvDisplay.getText().toString();
                    if(str.length()==strRecord.length())
                        return;
                    str+="=";
                    double result=0;
                    try{
                        result= getResult(str);
                        if(result%1==0){
                            strRecord=str+(int)result+"\n";
                        }
                        else {
                            strRecord=str+result+"\n";
                        }
                        spannable=new SpannableString(strRecord);
                        spannable.setSpan(new AbsoluteSizeSpan(15,true),0,str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        tvDisplay.setText(spannable);   
                        int tvDisplayHeight=tvDisplay.getHeight();
                        int heightDiff=tvDisplay.getLayout().getLineTop(tvDisplay.getLineCount())-tvDisplayHeight;
                        if(heightDiff>0)
                        {
                            tvDisplay.scrollTo(0,heightDiff);
                        }else{
                            tvDisplay.scrollTo(0,0);
                        }
                    }catch(Exception e){
                        Toast.makeText(MainActivity.this,"表达式有误",Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
    }
    //辅助方法
    private void getId(Button[] btns)
    {
        btns[0]=(Button)findViewById(R.id.btn0);
        btns[1]=(Button)findViewById(R.id.btn1);
        btns[2]=(Button)findViewById(R.id.btn2);
        btns[3]=(Button)findViewById(R.id.btn3);
        btns[4]=(Button)findViewById(R.id.btn4);
        btns[5]=(Button)findViewById(R.id.btn5);
        btns[6]=(Button)findViewById(R.id.btn6);
        btns[7]=(Button)findViewById(R.id.btn7);
        btns[8]=(Button)findViewById(R.id.btn8);
        btns[9]=(Button)findViewById(R.id.btn9);
        btns[10]=(Button)findViewById(R.id.btnAdd);
        btns[11]=(Button)findViewById(R.id.btnReduce);
        btns[12]=(Button)findViewById(R.id.btnMultiply);
        btns[13]=(Button)findViewById(R.id.btnDivide);
        btns[14]=(Button)findViewById(R.id.btnSquare);
        btns[15]=(Button)findViewById(R.id.btnRemainder);
        btns[16]=(Button)findViewById(R.id.btnPoint);
        btns[17]=(Button)findViewById(R.id.btnLiftParenthesis);
        btns[18]=(Button)findViewById(R.id.btnRightParenthesis);
    }


    //核心代码
    private Double getResult  (String str)throws Exception
    {

        str=str.substring(strRecord.length(),str.length());
        str.trim();
        Log.d("MainActivity","str:"+str+"\nstrRecord:"+strRecord);
        Double result=0.0;
        numberStack=new Stack<Double>();
        symbolStack=new Stack<Character>();
        StringBuffer number=new StringBuffer();
        for(int i=0;i<str.length();i++)
        {
            char ch=str.charAt(i);
            if((ch>='0'&&ch<='9')||ch=='.')
            {
                number.append(ch);
            }else{
                if(number.length()!=0)
                {
                    numberStack.push(Double.parseDouble(number.toString()));
                    number.setLength(0);
                }
                //数字入栈，处理是否运算
                while(compare(ch))
                {
                    Double b=numberStack.pop();
                    Double a=numberStack.pop();
                    switch ((char)symbolStack.pop())
                    {
                        case '+':
                            numberStack.push(a+b);
                            break;

                        case '-':
                            numberStack.push(a-b);
                            break;
                        case 'x':
                            numberStack.push(a*b);
                            break;
                        case '÷':
                            numberStack.push(a/b);
                            break;
                        case'%':
                            numberStack.push(a%b);
                            break;
                        case '^':
                            numberStack.push(Math.pow(a,b));
                            break;
                    }
                }
                //运算结束，处理运算符ch
                if(ch=='='&&!numberStack.isEmpty())
                    result=numberStack.pop();
                else {
                    if(ch==')')
                    {
                        symbolStack.pop();
                    }else {
                        symbolStack.push(ch);
                    }
                }
            }
        }

        return result;
    }

    private boolean compare(char symbol){

        if(symbolStack.isEmpty())
        {
            return false;
        }else{
            char pop=symbolStack.peek();
            switch (pop)
            {
                case '^':
                    if (symbol!='(')
                        return true;
                case 'x':
                case '÷':
                case '%':
                    if(symbol!='^'&&symbol!='(')
                        return true;
                case '+':
                case '-':
                    if(symbol=='+'||symbol=='-'||symbol==')'||symbol=='=')
                        return true;
                default://pop=='('
                    return false;
            }
        }
    }
}

