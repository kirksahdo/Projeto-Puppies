package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import android.view.*;
import android.text.style.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
	public void click (View view)
     {
		 if(view.getId()==R.id.btn_cadastar)
		 {
			 setContentView(R.layout.cadastro);
		 }
		 if(view.getId()==R.id.btn_logar)
		 {
			 
		 }
	 }
}
