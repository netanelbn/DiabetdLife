

public class register extends ActionBarActivity {

    EditText etxtName,etxtEmail,etxtPassword,etxtAddress,etxtBreakTime,etxtLunch,etxtDinner;
    Button btnRegister;
    TextView txtLink;

    
    static final int TIME_DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        etxtName=(EditText)findViewById(R.id.etxtName);
        etxtEmail=(EditText)findViewById(R.id.etxtEmail);
        etxtPassword=(EditText)findViewById(R.id.etxtPassword);
        etxtAddress=(EditText)findViewById(R.id.etxtAddress);
        etxtBreakTime=(EditText)findViewById(R.id.etxtBreakTime);
        SetTime setBreakfast=new SetTime(etxtBreakTime,register.this);
        etxtLunch=(EditText)findViewById(R.id.etxtLunch);
        SetTime setLunch=new SetTime(etxtLunch,register.this);
        etxtDinner=(EditText)findViewById(R.id.etxtDinner);
        SetTime setDinner=new SetTime(etxtDinner,register.this);
        btnRegister=(Button)findViewById(R.id.btnRRegister);
        btnRegister.setOnClickListener(btnRegisterClick);
        txtLink=(TextView)findViewById(R.id.link_to_login);
        txtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }
    android.view.View.OnClickListener btnRegisterClick=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ParseObject registers = new ParseObject("Registers");
            registers.put("username", etxtName.getText().toString());
            registers.put("email", etxtEmail.getText().toString());
            registers.put("password", etxtPassword.getText().toString());
            registers.put("address", etxtAddress.getText().toString());
            registers.put("breakfasttime", etxtBreakTime.getText().toString());

            registers.put("lunchtime",  etxtLunch.getText().toString());

            registers.put("dinnertime",  etxtDinner.getText().toString());

            registers.saveInBackground();
            AlertDialog.Builder builder = new AlertDialog.Builder(register.this);
            // Specify the list in the dialog using the array
            builder.setTitle("Done").setMessage("register is successfull")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            //create and show list dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
