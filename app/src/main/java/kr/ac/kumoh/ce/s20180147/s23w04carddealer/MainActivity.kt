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
            setHandRankings()
        })

        main.btn1.setOnClickListener{
            model.shuffle()
            setHandRankings()
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

    private fun setHandRankings() {     // 족보 중 탑 카드 계산
        when{
            checkRoyalStraightFlush() -> main.txt1.text = "로얄 스트레이트 플러쉬"
            checkBackStraightFlush() -> main.txt1.text = "백스트레이트 플러쉬"
            checkStraightFlush() -> main.txt1.text = "스트레이트 플러쉬"
            checkFourCards() -> main.txt1.text = "포 카드"
            checkFullHouse() -> main.txt1.text = "풀 하우스"
            checkFlush() -> main.txt1.text = "플러시"
            checkMountain() -> main.txt1.text = "마운틴"
            checkBackStraight() -> main.txt1.text = "백 스트레이트"
            checkStraight() -> main.txt1.text = "스트레이트"
            checkTriple() -> main.txt1.text = "트리플"
            checkTwoPair() -> main.txt1.text = "투 페어"
            checkOnePair() -> main.txt1.text = "원 페어"
            else -> main.txt1.text = checkTopcard()
        }
    }

    private fun checkRoyalStraightFlush(): Boolean {
        return checkMountain() && checkFlush()
    }

    private fun checkBackStraightFlush(): Boolean {
        return checkFlush() && checkStraight()
    }

    private fun checkStraightFlush(): Boolean {
        return checkStraight() && checkFlush()
    }

    private fun checkFullHouse(): Boolean {
        if (checkTriple()) {
            val numbers = model.cards.value!!.copyOf().map { it % 13 }.toList().sorted()
            if (numbers.toSet().size == 2) {
                return true
            }
        }
        return false
    }

    private fun checkStraight(): Boolean {
        val numbers = model.cards.value!!.copyOf().map { it % 13 }.toList().sorted()
        for (i in 0 until 4) {
            if (numbers[i] + 1 != numbers[i + 1]) return false
        }
        return true
    }

    private fun checkMountain(): Boolean {
        val numbers = model.cards.value!!.copyOf().map { it % 13 }.toList().sorted()
        return numbers == intArrayOf(9, 10, 11, 12, 13).toList()
    }

    private fun checkFlush(): Boolean {
        val numbers = model.cards.value!!.copyOf()
        if (numbers[0] == -1) return false
        numbers.map { it / 13 }.toList().sorted()
        return numbers.all { it == numbers[0] }
    }

    private fun checkBackStraight(): Boolean {
        val numbers = model.cards.value!!.copyOf().map { it % 13 }.toList().sorted()
        return numbers == intArrayOf(0, 1, 2, 3, 13).toList()
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

    private fun checkTwoPair(): Boolean {
        val numbers = model.cards.value!!.copyOf().map { it % 13 }.toList().sorted()
        return numbers.toSet().size == 3
    }

    private fun checkOnePair(): Boolean {
        val numbers = model.cards.value!!.copyOf().map { it % 13 }.toList().sorted()
        return numbers.toSet().size == 4
    }

    private fun checkTopcard(): String {
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

// MaiT's help
// 변경 예정 알고리즘
// 인자로 넘버를 받아온 후
// if else문을 반복하여 판별 함
// 플러시는 기존과 같이 판별 함수를 작성하여 판별
// 탑카드는 기존과 같이 else시에 checkTopCard() 이용하여 판별