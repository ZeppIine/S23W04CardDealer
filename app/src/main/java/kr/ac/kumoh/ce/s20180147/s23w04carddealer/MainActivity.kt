package kr.ac.kumoh.ce.s20180147.s23w04carddealer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.widget.ImageView
import kr.ac.kumoh.ce.s20180147.s23w04carddealer.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var main: ActivityMainBinding
    private lateinit var iview: Array<ImageView?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        main = ActivityMainBinding.inflate(layoutInflater)
        setContentView(main.root)

//        main.card1.setImageResource(R.drawable.c_ace_of_spades2)
        val c = IntArray(5) {0}
        for (i in 0..4) {
            c[i] = Random.nextInt(52)
//            Log.i("Card!", "$c : ${getCardNumber(c)}")
        }
        iview = arrayOf(main.card1, main.card2, main.card3, main.card4, main.card5)

        iview.forEachIndexed { index, imageview->
            imageview?.setImageResource(resources.getIdentifier(
                getCardNumber(c[index]),
                "drawable",
                packageName))
        }
    }


    private fun getCardNumber(c: Int): String {
        val shape = when (c / 13){
            0 -> "clubs"
            1 -> "hearts"
            2 -> "diamonds"
            3 -> "spades"
            else -> "error"
        }

        val number = when (c % 13){
            in 0..8 -> (c % 13 + 2).toString()
            9 -> "jack"
            10 -> "queen"
            11 -> "king"
            12 -> "ace"
            else -> "error"
        }

        val case = when (c % 13){
            in 0..8 -> ""
            in 9..11 -> "2"
            12 -> ""
            else -> "error"
        }//j, q, k 검증용

        return "c_${number}_of_${shape}${case}"
    }
}