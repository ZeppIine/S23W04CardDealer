package kr.ac.kumoh.ce.s20180147.s23w04carddealer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.ac.kumoh.ce.s20180147.s23w04carddealer.databinding.ActivityMainBinding
import kotlin.math.log
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var main: ActivityMainBinding
    private lateinit var iview: Array<ImageView?>
    private lateinit var model: CardDealerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        main = ActivityMainBinding.inflate(layoutInflater)
        setContentView(main.root)

        model = ViewModelProvider(this)[CardDealerViewModel::class.java]

        iview = arrayOf(main.card1, main.card2, main.card3, main.card4, main.card5)

        model.cards.observe(this, Observer {
            setCardNumber()
        })

        main.btn1.setOnClickListener{
            model.shuffle()
            setHandRankings()
        }
//        main.card1.setImageResource(R.drawable.c_ace_of_spades2)
//        val c = IntArray(5) {0}
//        for (i in 0..4) {
//            c[i] = Random.nextInt(52)
//            Log.i("Card!", "$c : ${getCardNumber(c)}")
//        }
    }

    private fun setCardNumber() {
        iview.forEachIndexed { index, imageview->
            imageview?.setImageResource(resources.getIdentifier(
                getCardNumber(model.cards.value!![index]),
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
        }   // 클, 하, 다, 스

        val number = when (c % 13){
            in 0..8 -> (c % 13 + 2).toString()
            9 -> "jack"
            10 -> "queen"
            11 -> "king"
            12 -> "ace"
            else -> "error"
        }   // 0~8: 2~10, 9~12: j/q/k/a

        val case = when (c % 13){
            in 0..8 -> ""
            in 9..11 -> "2"
            12 -> ""
            else -> "error"
        }   // j, q, k 검증용

        if (c == -1) return "c_red_joker"

        return "c_${number}_of_${shape}${case}"
    }

    private fun setHandRankings() {     // 족보 중 탑 카드 계산
        val numbers = model.cards.value!!.copyOf()
        var num = -1
        var shap = -1

        for (i in numbers) {
            if (num < i % 13){
                num = i % 13
                shap = i / 13
            }
        }
//        Log.i("Top!", "$num")


        val shape = when (shap){
            0 -> "클로버"
            1 -> "하트"
            2 -> "다이아"
            3 -> "스페이드"
            else -> "에러"
        }   // 클, 하, 다, 스

        val number = when (num){
            in 0..8 -> (num + 2).toString()
            9 -> "잭"
            10 -> "퀸"
            11 -> "킹"
            12 -> "에이스"
            else -> "에러"
        }   // 0~8: 2~10, 9~12: j/q/k/a
//        Log.i("Top Name!", "$number")

        main.txt1.text = "$shape $number 탑"
    }
}