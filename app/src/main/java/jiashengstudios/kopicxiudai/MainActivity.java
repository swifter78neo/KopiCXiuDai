package jiashengstudios.kopicxiudai;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//  This app displays an order form to order coffee.
public class MainActivity extends AppCompatActivity {

    int quantity = 1;
    String whippedCreamString;
    String chocolateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //  This method is called when the plus button is clicked.
    public void increment(View view) {
        if (quantity == 20) {
            quantity = 20;
            // Show an error message as a toast
            Toast.makeText(this, "Sorry, we cannot handle more than 20 coffees", Toast.LENGTH_SHORT).show();
        } else {
            quantity += 1;
            refreshOrder();
        }
    }

    //  This method is called when the minus button is clicked.
    public void decrement(View view) {
        if (quantity == 1) {
            quantity = 1;
            // Show an error message as a toast
            Toast.makeText(this, "Sorry, you cannot order less than 1 coffee", Toast.LENGTH_SHORT).show();
        } else {
            quantity -= 1;
            refreshOrder();
        }
    }

    //  This method resets the entire order form.
    public void Reset(View view) {
        name(1);
        quantity = 1;
        isWhippedCreamChecked(1);
        isChocolateChecked(1);
        refreshOrder();
    }

    //  Calculates the total price of drinks.
    private int calculatePrice(boolean addWhippedCream, boolean addChocolate) {
        int basePrice = 4;

        if (addWhippedCream) {basePrice += 1;}
        if (addChocolate) {basePrice += 2;}

        return quantity * basePrice;
    }

    //  Returns order summary, which includes quantity, price, name of user and choice of toppings.
    String returnsOrderSummary () {
        return "Name: " + name(0) + "\nQuantity: " + quantity + whippedCreamString +
        chocolateString + "\nTotal: $" + calculatePrice(isWhippedCreamChecked(0), isChocolateChecked(0));
    }

    //  Gets name of user for submission into the order summary later.
    private String name (int reset){
        EditText userName = (EditText) findViewById(R.id.name);
        if (reset == 1) {
            //  Resets name to blank.
            userName.setText("");
            return "";
        }
        else {
            return userName.getText().toString();
        }
    }

    //  Identifies the Whipped Cream checkbox and its current state.
    private boolean isWhippedCreamChecked (int reset) {
        CheckBox whippedCreamCheckBox = (CheckBox) findViewById(R.id.WhippedCreamCheckBox);
        if (reset == 1) {
            whippedCreamCheckBox.setChecked(false);
            return whippedCreamCheckBox.isChecked();
        }
        else {
            return whippedCreamCheckBox.isChecked();
        }
    }

    //  Identifies the Chocolate checkbox and its current state.
    private boolean isChocolateChecked (int reset) {
        CheckBox chocolateCheckBox = (CheckBox) findViewById(R.id.ChocolateCheckBox);
        if (reset == 1) {
            chocolateCheckBox.setChecked(false);
            return chocolateCheckBox.isChecked();
        }
        else {
            return chocolateCheckBox.isChecked();
        }
    }

    //  Refreshes the quantity and price onscreen. This method is called every time the quantity is
    //  changed or the order button is pressed.
    private void refreshOrder() {

        //  Updates quantity onscreen.
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText("" + quantity);

        //  Determines the correct string(s) to be displayed, based on choice of toppings.
        if (isWhippedCreamChecked(0) && isChocolateChecked(0)) {
            whippedCreamString = " with whipped cream";
            chocolateString = " and chocolate!";
        } else if ((isWhippedCreamChecked(0) == true) && (isChocolateChecked(0) == false)) {
            whippedCreamString = " with whipped cream";
            chocolateString = "";
        } else if ((isWhippedCreamChecked(0) == false) && (isChocolateChecked(0) == true)) {
            whippedCreamString = "";
            chocolateString = " with chocolate";
        } else {
            whippedCreamString = "";
            chocolateString = "";
        }

        //  Updates price onscreen.
        TextView priceTextView = (TextView) findViewById(R.id.price_text_view);
        priceTextView.setText(returnsOrderSummary());
    }

    //  User presses Order button. Order is refreshed to account for any change in inputs. Order is
    //  displayed in an AlertDialog and user chooses to accept it or cancel.
    public void submitOrder(View view){
        refreshOrder();

        //  Code to set up AlertDialog
        AlertDialog.Builder confirmOrderDialog = new AlertDialog.Builder(this);
        confirmOrderDialog.setTitle("Confirm Order");
        confirmOrderDialog.setMessage(returnsOrderSummary() + "\nThank you!")
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Set-up email
                        composeEmail ("", "Kopi Order");
                        Toast.makeText(getApplicationContext(), "Thank you!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = confirmOrderDialog.create();
        alert.show();
    }

    public void composeEmail(String address, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:jiashengyee@gmail.com"));
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, returnsOrderSummary() + "\nThank you!");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
