package kr.ac.kumoh.ce.s20180147.s23w04carddealer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.ac.kumoh.ce.s20180147.s23w04carddealer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var main: ActivityMainBinding
    private lateinit var iview: Array<ImageView?>
    private lateinit var model: CardDealerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        main = ActivityMainBinding.inflate(layoutInflater)
        setContentView(main.root)

        model = ViewModelProvider(this)[CardDealerViewModel::class.java]

        iview = arrayOf(main.card1, main.card2, main.card3, main.card4, main.card5)

        model.cards.observe(this, Observer {
            setCardNumber()
            main.txt1.text = setHandRankings()
        })

        main.btn1.setOnClickListener{
            model.shuffle()
            main.txt1.text = setHandRankings()
        }
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

    private fun setHandRankings(): String {     // 족보 계산
        // 모양 판정
        val shapes = model.cards.value!!.copyOf().map { it / 13 }.toList().sorted()
        // 숫자 판정
        val numbers = model.cards.value!!.copyOf().map { it % 13 }.toList().sorted()

        if(shapes.all { it == shapes[0] }){   // 플러쉬 족보
            if(model.cards.value!!.copyOf().toList()[0] == -1){
                return "포커 게임"
            } else if(numbers == intArrayOf(8, 9, 10, 11, 12).toList()) {
                return "로얄 스트레이트 플러쉬"
            } else if(numbers == intArrayOf(0, 1, 2, 3, 12).toList()) {
                return "백 스트레이트 플러쉬"
            } else if(checkStraight()) {
                return "스트레이트 플러쉬"
            } else {
                return "플러쉬"
            }
        }
        else{   // 플러쉬가 아닌 족보
            if(checkFourCards()) {
                return "포 카드"
            } else if(checkTriple()) {
                if (numbers.toSet().size == 2) {
                    return "풀 하우스"
                } else {
                    return "트리플"
                }
            } else if(numbers == intArrayOf(8, 9, 10, 11, 12).toList()) {
                return "마운틴"
            } else if(numbers == intArrayOf(0, 1, 2, 3, 12).toList()) {
                return "백 스트레이트"
            } else if(checkStraight()) {
                return "스트레이트"
            } else if(numbers.toSet().size == 3) {
                return "투 페어"
            } else if(numbers.toSet().size == 4) {
                return "원 페어"
            } else {
                return checkTopCard()
            }
        }
    }

    private fun checkStraight(): Boolean {
        val numbers = model.cards.value!!.copyOf().map { it % 13 }.toList().sorted()
        for (i in 0 until 4) {
            if (numbers[i] + 1 != numbers[i + 1]) return false
        }
        return true
    }

    private fun checkFourCards(): Boolean {
        val numbers = model.cards.value!!.copyOf().map { it % 13 }.toList().sorted()
        for (i in numbers) {
            if (numbers.count { it == i } == 4) return true
        }
        return false
    }

    private fun checkTriple(): Boolean {
        val numbers = model.cards.value!!.copyOf().map { it % 13 }.toList().sorted()
        for (i in numbers) {
            if (numbers.count { it == i } == 3) return true
        }
        return false
    }

    private fun checkTopCard(): String {
        val numbers = model.cards.value!!.copyOf()
        var num = -1
        var shap = -1

        for (i in numbers) {
            if (num < i % 13){
                num = i % 13
                shap = i / 13
            }
        }

        val shape = when (shap){
            0 -> "클로버"
            1 -> "하트"
            2 -> "다이아"
            3 -> "스페이드"
            else -> "error"
        }   // 클, 하, 다, 스

        val number = when (num){
            in 0..8 -> (num + 2).toString()
            9 -> "잭"
            10 -> "퀸"
            11 -> "킹"
            12 -> "에이스"
            else -> "error"
        }   // 0~8: 2~10, 9~12: j/q/k/a

        return if (shape == number) "포커 게임" else "$shape $number 탑"
    }
}