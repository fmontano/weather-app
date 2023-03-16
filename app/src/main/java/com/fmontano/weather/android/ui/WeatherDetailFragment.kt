package com.fmontano.weather.android.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import coil.request.CachePolicy.ENABLED
import com.fmontano.weather.android.R
import com.fmontano.weather.android.databinding.FragmentWeatherDetailsBinding
import com.fmontano.weather.android.ui.model.WeatherUIModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class WeatherDetailFragment: Fragment() {

    private val viewModel by viewModels<WeatherViewModel>()
    private var _binding: FragmentWeatherDetailsBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("FOO", "Permission JUST granted")
                viewModel.getWeatherForCurrentLocation()
            } else {
                // TODO The user has explicitly declined to grant the location permission.
                //   At this point, we can continue with location search, but we could try
                //   to prompt the user for the permission again in the future.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::handleUiStateChange)
            }
        }

        checkLocationPermissions(
            onPermissionGranted = {
                viewModel.getWeatherForCurrentLocation()
            },
            onPermissionDenied = {
                viewModel.fetchWeatherFromSavedSearch()
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherDetailsBinding.inflate(inflater, container, false)

        binding.forecastRecyclerView.apply {
            adapter = ForecastAdapter()
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }

        binding.searchResultRecyclerView.adapter = LocationSearchAdapter {
            viewModel.onLocationClicked(it)
            binding.searchView.hide()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkLocationPermissions(
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit,
    ) {
        when {
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                showContextUI()
            }
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                onPermissionGranted()
            }
            else -> {
                onPermissionDenied()
            }
        }
    }

    private var permissionRequestCode = 1
    // Showing users why we need the permissions the first time we ask them to share their location
    private fun showContextUI() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.location_permission_rationale_title))
            .setMessage(getString(R.string.location_permission_rationale_content))
            .setPositiveButton(
                getString(android.R.string.ok)
            ) { _, _ ->
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    permissionRequestCode
                )
            }
            .setNegativeButton(
                getString(android.R.string.cancel)
            ) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    // Showing a dialog to let the user know they must enable the permissions via settings
    private fun showPermissionNeededDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.location_permission_rationale_title)
            .setMessage(getString(R.string.permission_forced_request_message))
            .setPositiveButton(
                getString(android.R.string.ok)
            ) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(getString(android.R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun handleUiStateChange(uiState: WeatherDetailsUiState) {
        showLoadingWeatherProgressView(uiState.showLoadingWeatherProgressView)
        showEmptyWeatherPlaceholderView(uiState.showEmptyState)
        showErrorView(uiState.showError)
        uiState.weather?.let(::showWeather)
        showEmptySearchPlaceHolder(uiState.showSearchEmptyView)
        searchingLocationProgressView(uiState.showSearchProgressView)
        binding.searchBar.text = uiState.weather?.location?.name.orEmpty()
        (binding.searchResultRecyclerView.adapter as LocationSearchAdapter).submitList(uiState.searchResults)

        setupMenu(uiState)
    }

    private fun showErrorView(show: Boolean) {
        binding.errorView.isVisible = show
    }

    private fun showLoadingWeatherProgressView(show: Boolean) {
        binding.loadingWeatherProgressIndicator.isVisible = show
    }

    private fun showEmptyWeatherPlaceholderView(show: Boolean) {
        binding.emptyWeatherPlaceHolder.isVisible = show
    }

    private fun showEmptySearchPlaceHolder(show: Boolean) {
        binding.emptySearchPlaceHolder.isVisible = show
    }

    private fun searchingLocationProgressView(show: Boolean) {
        binding.searchingLocationProgressIndicator.isVisible = show
    }

    private fun showWeather(weather: WeatherUIModel) {

        binding.iconImageView.load(weather.location.iconUrl) { diskCachePolicy(ENABLED) }
        binding.weatherConditionsTextView.text = weather.location.currentConditions.orEmpty()
        binding.currentTempTextView.text = weather.location.currentTemp
        binding.feelsListTextView.text = weather.location.feelsLike

        with (binding.includeCurrentConditions) {
            conditionsTemperatureTextView.text = weather.location.currentTemp
            conditionsMinTemperatureTextView.text = weather.location.minTemp
            conditionsMaxTemperatureTextView.text = weather.location.maxTemp
            conditionsWindSpeedTextView.text = weather.location.windSpeed
            conditionsHumidityTextView.text = weather.location.humidity
        }

        (binding.forecastRecyclerView.adapter as ForecastAdapter).submitList(weather.forecast)
    }

    private fun setupMenu(uiState: WeatherDetailsUiState) {
        binding.searchBar.menu.clear()
        MenuCompat.setGroupDividerEnabled(binding.searchBar.menu, true)
        binding.searchBar.inflateMenu(R.menu.menu_weather_details)
        binding.searchBar.menu.findItem(R.id.menu_use_imperial_system).isChecked =
            uiState.unitMeasurementSystem == UnitMeasurementSystem.IMPERIAL
        binding.searchBar.menu.findItem(R.id.menu_use_metric_system).isChecked =
            uiState.unitMeasurementSystem == UnitMeasurementSystem.METRIC
        binding.searchBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_use_imperial_system -> {
                    onUnitMeasurementChanged(UnitMeasurementSystem.IMPERIAL)
                }
                R.id.menu_use_metric_system -> {
                    onUnitMeasurementChanged(UnitMeasurementSystem.METRIC)
                }
                R.id.share_location -> {
                    onLocateUserClicked()
                }
                else -> false
            }
        }

        binding.searchView.editText
            .setOnEditorActionListener { _, actionId, _ ->
                if (actionId == IME_ACTION_SEARCH) {
                    binding.searchBar.text = binding.searchView.text
                    viewModel.searchLocation(binding.searchView.editText.text.toString())
                }
                false
            }
    }

    private fun onUnitMeasurementChanged(unitMeasurement: UnitMeasurementSystem): Boolean {
        viewModel.setMeasurementUnits(unitMeasurement)
        return true
    }

    private fun onLocateUserClicked(): Boolean {
        checkLocationPermissions(
            onPermissionGranted = {
                viewModel.getWeatherForCurrentLocation()
            },
            onPermissionDenied = {
                showPermissionNeededDialog()
            }
        )

        return true
    }

}
