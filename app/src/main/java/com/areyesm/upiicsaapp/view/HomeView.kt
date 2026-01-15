package com.areyesm.upiicsaapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.areyesm.upiicsaapp.components.ActionBar
import com.areyesm.upiicsaapp.components.FooterBar
import com.areyesm.upiicsaapp.components.MainContent
import com.areyesm.upiicsaapp.model.HomeModel
import com.areyesm.upiicsaapp.ui.theme.ColorBackground
import com.areyesm.upiicsaapp.viewModel.CampusViewModel
import com.areyesm.upiicsaapp.viewModel.LocationViewModel
import com.areyesm.upiicsaapp.viewModel.LoginViewModel

@Composable
fun HomeView(navController: NavHostController, loginVM: LoginViewModel, campusVM: CampusViewModel, locationVM: LocationViewModel ) {

    //Define la sección que será mostrada
    var currentSection by rememberSaveable {
        mutableStateOf(HomeModel.HOME)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ColorBackground
    ) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
                .padding(horizontal = 24.dp, vertical = 10.dp)
        ) {
            ActionBar(navController, loginVM, campusVM)
            Spacer(
                modifier = Modifier.height(15.dp)
            )
            MainContent(
                modifier = Modifier.weight(1f),
                section = currentSection,
                campusVM = campusVM,
                locationVM = locationVM
            )
            Spacer(
                modifier = Modifier.height(24.dp)
            )
            FooterBar(
                modifier = Modifier.fillMaxWidth(),
                currentSection = currentSection,
                onSectionSelected = { section ->
                    currentSection = section
                }
            )
        }
    }
}