package com.abahoabbott.wordcoach.features.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.abahoabbott.wordcoach.R
import com.abahoabbott.wordcoach.ui.theme.WordCoachTheme

@Composable
fun SplashScreen(){

    Box {
        Column {
            //Logo
            //Image(
               // imageVector = ImageVector.vectorResource(R.drawable)
           // )
        }
    }

}

@PreviewLightDark
@Composable
private fun SplashScreenPreview(){

    WordCoachTheme {
        Surface {
            SplashScreen()
        }
    }

}