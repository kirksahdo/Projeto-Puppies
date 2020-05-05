
	package com.mycompany.myapp;

	import android.app.*;
	import android.os.*;
	import android.view.*;
	import android.text.style.*;

	public class Cadastro extends Activity 
	{
		@Override
		protected void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.cadastro);
		}
		public void click (View view)
		{
			if(view.getId()==R.id.btn_cadastar)
			{
				setContentView(R.layout.main);
			}
			if(view.getId()==R.id.btn_logar)
			{
				setContentView(R.layout.main);
			}
		}
	}
