package com.example.excessbaggagecalculator

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var etBaggageWeight: EditText
    private lateinit var etRatePerKg: EditText
    private lateinit var spTicketClass: Spinner
    private lateinit var tvExcessWeight: TextView
    private lateinit var tvExcessCharge: TextView
    private lateinit var tvTotalPayable: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        etBaggageWeight = findViewById(R.id.etBaggageWeight)
        etRatePerKg = findViewById(R.id.etRatePerKg)
        spTicketClass = findViewById(R.id.spTicketClass)
        tvExcessWeight = findViewById(R.id.tvExcessWeight)
        tvExcessCharge = findViewById(R.id.tvExcessCharge)
        tvTotalPayable = findViewById(R.id.tvTotalPayable)

        val btnCalculate = findViewById<Button>(R.id.btnCalculate)
        val btnClear = findViewById<Button>(R.id.btnClear)

        btnCalculate.setOnClickListener { calculateFee() }
        btnClear.setOnClickListener { clearFields() }
    }

    private fun calculateFee() {
        val weightStr = etBaggageWeight.text.toString().trim()
        val rateStr = etRatePerKg.text.toString().trim()

        var isValid = true
        var baggageWeight = 0.0
        var ratePerKg = 0.0

        // Reset previous outputs first on every new calculation attempt
        resetOutputs()

        // Validate Weight Field
        if (weightStr.isEmpty()) {
            etBaggageWeight.error = "Please enter baggage weight"
            isValid = false
        } else {
            try {
                baggageWeight = weightStr.toDouble()
                if (baggageWeight < 0.0) {
                    etBaggageWeight.error = "Weight cannot be negative"
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                etBaggageWeight.error = "Please enter a valid numeric weight"
                isValid = false
            }
        }

        // Validate Rate Field
        if (rateStr.isEmpty()) {
            etRatePerKg.error = "Please enter rate per kg"
            isValid = false
        } else {
            try {
                ratePerKg = rateStr.toDouble()
                if (ratePerKg < 0.0) {
                    etRatePerKg.error = "Rate cannot be negative"
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                etRatePerKg.error = "Please enter a valid numeric rate"
                isValid = false
            }
        }

        if (!isValid) return

        // Compute outputs
        val freeLimit = if (spTicketClass.selectedItemPosition == 0) 20 else 40
        val excessWeight = maxOf(0.0, baggageWeight - freeLimit)
        val excessCharge = excessWeight * ratePerKg
        val totalPayable = excessCharge + (0.08 * excessCharge)

        tvExcessWeight.text = String.format(Locale.US, getString(R.string.excess_weight_format), excessWeight)
        tvExcessCharge.text = String.format(Locale.US, getString(R.string.excess_charge_format), excessCharge)
        tvTotalPayable.text = String.format(Locale.US, getString(R.string.total_payable_format), totalPayable)
    }

    private fun resetOutputs() {
        tvExcessWeight.text = "Excess Weight: 0.00 kg"
        tvExcessCharge.text = "Excess Charge: RM 0.00"
        tvTotalPayable.text = "Total Amount Payable (incl. 8% SST): RM 0.00"
    }

    private fun clearFields() {
        etBaggageWeight.text.clear()
        etRatePerKg.text.clear()
        etBaggageWeight.error = null
        etRatePerKg.error = null
        spTicketClass.setSelection(0)
        resetOutputs()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Excess Baggage Fee Calculator")
                    putExtra(Intent.EXTRA_TEXT, "Check out the Excess Baggage Calculator App: " + getString(R.string.github_url))
                }
                startActivity(Intent.createChooser(shareIntent, "Share via"))
                return true
            }
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}