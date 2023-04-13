package com.dummy.app

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import dagger.hilt.android.AndroidEntryPoint


/*
* Cloud Firestore
*
* customer {
*   id,
*   fName,
*   lName,
*   email,
*   phone,
*   city
* }
* */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            val context = this.baseContext
            RootComposeUI(context)
        }
    }
}


@Composable
fun RootComposeUI(context: Context){
    val dataViewModel = hiltViewModel<MainViewModel>()
    val cryptocurrency = dataViewModel.cryptoCurrency
    ShowScreen(cryptocurrency) {event ->
        when(event){
            is RandonUIEvent.RandomUI -> {
                dataViewModel.addNewCrypto()
                Toast.makeText( context,"woohoo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun ShowScreen(cryptocurrency: LiveData<List<Cryptocurrency>>, eventListener: (RandonUIEvent) -> Unit){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment  = Alignment.CenterHorizontally
    ) {
        val list = mutableListOf<Cryptocurrency>()
        val name = remember { mutableStateOf(list) }
        cryptocurrency.observeForever {
            name.value = it.toMutableList()
        }
        Text(
            text = name.value.size.toString(),
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily.Default,
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier
                .clickable {
                    eventListener(RandonUIEvent.RandomUI(true))
                },
            shape = RoundedCornerShape(12.dp),
            backgroundColor = Color.Cyan,
            contentColor = Color.White
        ) {
            Text(
                text = "increase count",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Default,
                    color = Color.Black
                ),
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}