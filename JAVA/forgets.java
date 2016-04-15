

public class forgets extends ActionBarActivity {

    EditText etxtEmail;
    Button btnSendEmail;
    JSONParser jsonParser=new JSONParser();
    String newPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgets);
        etxtEmail=(EditText)findViewById(R.id.etxtEmailF);
        btnSendEmail=(Button)findViewById(R.id.btnSendEmail);
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Registers");
                query.whereEqualTo("email", etxtEmail.getText().toString());
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> scoreList, ParseException e) {
                        if (e == null) {
                            Random rn=new Random();

                            if(scoreList.size()>0)
                            {
                                ParseObject object=scoreList.get(0);
                                newPass=object.getString("password");
                                new newPassWord().execute();
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(forgets.this);
                                // Specify the list in the dialog using the array
                                builder.setTitle("Error").setMessage("This email is not register")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });

                                //create and show list dialog
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        } else {

                        }
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forgets, menu);
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
    class newPassWord extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Getting product details in background thread
         */
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        String comment = "Hello user,\n your password is: " + newPass + "\nplease try login for change new.";
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("email", etxtEmail.getText().toString()));
                        params.add(new BasicNameValuePair("subject", "diabetsLife-password recover"));
                        params.add(new BasicNameValuePair("comment", comment));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        System.out.println("params is:" + params);
                        JSONObject json = jsonParser.makeHttpRequest(
                                Constrants.url_send_email, "GET", params);

                        // check your log for json response
                        Log.d("Single Product Details", json.toString());

                        // json success tag
                        success = json.getInt("success");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            AlertDialog.Builder builder = new AlertDialog.Builder(forgets.this);
            // Specify the list in the dialog using the array
            builder.setTitle("success").setMessage("Your password has send to your email. Please check!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            //create and show list dialog
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }
}
