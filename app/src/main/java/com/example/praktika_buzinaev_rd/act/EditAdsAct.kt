package com.example.praktika_buzinaev_rd.act

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import com.example.praktika_buzinaev_rd.R
import com.example.praktika_buzinaev_rd.adapters.ImageAdapter
import com.example.praktika_buzinaev_rd.databinding.ActivityEditAdsBinding
import com.example.praktika_buzinaev_rd.dialogs.DialogSpinnerHelper
import com.example.praktika_buzinaev_rd.frag.FragmentCloseInterface
import com.example.praktika_buzinaev_rd.frag.ImageListFrag
import com.example.praktika_buzinaev_rd.frag.SelectImageItem
import com.example.praktika_buzinaev_rd.utils.CityHelper
import com.example.praktika_buzinaev_rd.utils.ImagePicker
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil

class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    lateinit var rootElement: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    private var isImagesPermissionGranted = false
    private lateinit var imageAdapter : ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
        init()

        /*
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, CityHelper.getAllCountries(this))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rootElement.spCountry.adapter = adapter*/

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_GET_IMAGES) {
            if (data != null) {
                val returnValues: ArrayList<String> = data.getStringArrayListExtra(Pix.IMAGE_RESULTS) as ArrayList<String>
                rootElement.scrollViewMain.visibility = View.GONE
                val fm = supportFragmentManager.beginTransaction()
                fm.replace(R.id.place_holder, ImageListFrag(this, returnValues))
                fm.commit()

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.getImages(this, 3)
                } else {

                    Toast.makeText(this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun init(){
        imageAdapter = ImageAdapter()
        rootElement.vpimages.adapter = imageAdapter
    }

    fun onClickSelectCountry(view: View){
        val listCountry = CityHelper.getAllCountries(this)
        dialog.ShowSpinnerDialog(this, listCountry, rootElement.tvCountry)
        if (rootElement.tvCity.text.toString() != getString(R.string.select_city)){
            rootElement.tvCity.text = getString(R.string.select_city)
        }
    }

    fun onClickSelectCity(view: View){
        val selectedCountry = rootElement.tvCountry.text.toString()
        if (selectedCountry != getString(R.string.select_country)){
            val listCity = CityHelper.getAllCities(selectedCountry, this)
            dialog.ShowSpinnerDialog(this,listCity, rootElement.tvCity)
        }
        else{
            Toast.makeText(this, R.string.attention_country, Toast.LENGTH_LONG).show()
        }
    }

    fun onClickGetImages(view: View){
        ImagePicker.getImages(this, 3)

    }

    override fun onFragClose(list: ArrayList<SelectImageItem>) {
        rootElement.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
    }
}