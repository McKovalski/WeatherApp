package com.example.weatherapp.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.weatherapp.R
import com.example.weatherapp.activities.AboutActivity
import com.example.weatherapp.activities.MainActivity
import com.example.weatherapp.databinding.ClearAlertDialogViewBinding
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.example.weatherapp.helpers.LanguageHelper
import com.example.weatherapp.helpers.MeasurementUnitsHelper
import com.example.weatherapp.viewmodels.MainViewModel

class SettingsFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.aboutCard.moreInfoButton.setOnClickListener {
            val intent = Intent(requireContext(), AboutActivity::class.java)
            requireContext().startActivity(intent)
        }

        val languages = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            languages
        )
        binding.chooseLanguageAutocomplete.setAdapter(adapter)
        val language = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0)
        } else {
            resources.configuration.locale
        }
        binding.chooseLanguageAutocomplete.setText(language.displayLanguage, false)

        binding.chooseLanguageAutocomplete.setOnItemClickListener { adapterView, _, position, _ ->
            val languageName = adapterView?.getItemAtPosition(position) as String
            val languageCode = getLanguageCode(languageName)
            if (languageCode != language.language) {
                LanguageHelper(requireContext()).changeLanguage(languageCode)
                val intent = Intent(requireContext(), MainActivity::class.java)
                activity?.finish()
                startActivity(intent)
            }
        }

        val currentUnits = MeasurementUnitsHelper((requireContext())).getUnits()
        if (currentUnits == "km") {
            binding.chooseUnitsCard.radioButtonMetric.isChecked = true
        } else {
            binding.chooseUnitsCard.radioButtonImperial.isChecked = true
        }

        binding.chooseUnitsCard.radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            val checkedRadioButton: RadioButton = radioGroup.findViewById(checkedId)
            if (checkedRadioButton.text == getString(R.string.metric)) {
                MeasurementUnitsHelper(requireContext()).saveUnits("km")
            } else {
                MeasurementUnitsHelper(requireContext()).saveUnits("mi")
            }
        }

        binding.clearRecentButton.setOnClickListener {
            val alertBinding = ClearAlertDialogViewBinding.inflate(inflater)
            val clearRecentAlertDialog = AlertDialog.Builder(requireContext())
                .setView(alertBinding.root)
                .show()

            alertBinding.title.text = getString(R.string.clear_recent_search_list_question)
            alertBinding.textBody.text = getString(R.string.clear_recent_search_list_body)
            alertBinding.buttonClear.setOnClickListener {
                mainViewModel.deleteAllRecent(requireContext())
                clearRecentAlertDialog.dismiss()
            }
            alertBinding.buttonCancel.setOnClickListener {
                clearRecentAlertDialog.dismiss()
            }
        }

        binding.clearMyCitiesButton.setOnClickListener {
            val alertBinding = ClearAlertDialogViewBinding.inflate(inflater)
            val clearFavouritesAlertDialog = AlertDialog.Builder(requireContext())
                .setView(alertBinding.root)
                .show()

            alertBinding.title.text = getString(R.string.clear_my_cities_list_question)
            alertBinding.textBody.text = getString(R.string.clear_my_cities_list_body)
            alertBinding.buttonClear.setOnClickListener {
                mainViewModel.deleteAllFavourites(requireContext())
                clearFavouritesAlertDialog.dismiss()
            }
            alertBinding.buttonCancel.setOnClickListener {
                clearFavouritesAlertDialog.dismiss()
            }
        }

        return binding.root
    }

    override fun onResume() {
        val favouriteCitiesNames = mutableListOf<String>()
        mainViewModel.favoriteLocationDetails.observe(viewLifecycleOwner) {
            it.forEach { favourite ->
                favouriteCitiesNames.add(favourite.title)
            }
        }
        binding.chooseCityAutocomplete.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                favouriteCitiesNames
            )
        )
        super.onResume()
    }

    private fun getLanguageCode(languageName: String): String {
        return when (languageName) {
            getString(R.string.english) -> getString(R.string.english_code)
            else -> getString(R.string.croatian_code)
        }
    }
}