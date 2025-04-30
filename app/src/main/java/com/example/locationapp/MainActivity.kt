package com.example.locationapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationapp.ui.theme.LocationAppTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Makes app layout edge to edge(full-screen look)
        enableEdgeToEdge()
        //Sets the UI using Jetpack Compose
        setContent {

            val viewModel : LocationViewModel = viewModel()
            //Applies your custom theme
            LocationAppTheme {
                //Base container using the theme's background
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    //Calls our main app UI
                    MyApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp(viewModel: LocationViewModel){
    //Get the current context(like Activity)
    val context = LocalContext.current
    //Creates an instance of our helper class (assumes locationUnits is defined elsewhere )
    val locationUtils = LocationUtils(context)
    // Calls UI function that handles location display logic
    onLocationDisplay(context=context,viewModel, locationUtils=locationUtils)
}


@Composable
@SuppressLint("MissingPermission")
fun onLocationDisplay(context: Context,
                      viewModel: LocationViewModel,
                      locationUtils: LocationUtils){

    //Checks whether the spinner should be shown or not
    var isLoading by remember { mutableStateOf(false) } //Spinner control

    val location = viewModel.location.value
    // State to store location text

    val address = location?.let{
        locationUtils.reverseGeoCodeLocation(location)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // Check if both coarse and fine location are granted
            if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            &&  permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                ){
                //I have access to location
                isLoading = true
                locationUtils.requestLocationUpdates(viewModel = viewModel)
            }
        else {
            // Ask for permission...
            // Check if we should show a permission rationale
            val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                context as MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )  ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                // Show an explanation to the user
             val message = if(rationaleRequired){
                 "Location permission is required for this feature to work."
             }
                else{
                    "Location permission is required. Please enable it in the ANDROID settings."
                 }
                Toast.makeText(context, message,Toast.LENGTH_LONG).show()
            }
        })

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        if (location!=null){
            Text("Address : ${location.lattitue} ${location.longitude} \n $address")
        } else {
            Text("Location not available") // Show current text or placeholder
        }
        Spacer(modifier = Modifier.height(20.dp))
        // Button that triggers permission request
        // Show spinner while location is loading
        if (isLoading) {
            CircularProgressIndicator()
        }

        Button( onClick = {
            if(locationUtils.hasLocationAccess(context)){
                //Permission already granted update the location
                // If permission is already granted
              locationUtils.requestLocationUpdates(viewModel)
                }
            else {
                //Request location permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text("Get location") // Button label
        }

    }
}