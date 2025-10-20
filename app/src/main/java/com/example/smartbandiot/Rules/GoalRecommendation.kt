package com.example.smartbandiot.Rules

import com.example.smartbandiot.model.User

class hrMaxCalculationAndBMI{
    fun calculateHRMax(user: User): Int {
        return 220 - user.age
    }

    fun calculateBMI(user: User): Double {
        return user.weight / ((user.height/100)* (user.height/100))
    }

    fun keepFitRecommendation(user: User, currentHR: Int): String {

        val maxHR = calculateHRMax(user)
        val bmi = calculateBMI(user)
        val easy = (maxHR * 0.75).toInt()
        val comfortable = (maxHR * 0.85).toInt()
        val uncomfortable = (maxHR * 0.92).toInt()
        val toofar = (maxHR * 1.0).toInt()

        // alert kalau HR keluar dari batas
        if (currentHR > comfortable) {
            return "⚠️ Detak jantungmu ${currentHR} bpm, dimana harusnya dibawah ($comfortable bpm) Turunkan intensitas dengan pelan pelan"
        }

        // kelompok usia
        val ageGroup = when {
            user.age < 18 -> "Childen and adoselent"
            user.age in 18..64 -> "Adult"
            else -> "Older adults"
        }

        // durasi per minggu berdasarkan usia
        val durationPerWeek = when (ageGroup) {
            "child" -> "≥ 1 jam × 3 hari/minggu"
            "adult" -> "2.5 jam (3–5 hari/minggu)"
            else -> "2.5 jam per minggu"
        }

        // kategori BMI
        val category = when {
            bmi < 18.5 -> "Underweight"
            bmi < 25 -> "Healthy"
            bmi < 30 -> "Overweight"
            else -> "Obese"
        }

        // rekomendasi berdasarkan BMI
        val activity = when (category) {
            "Underweight" -> "Jalan santai < 75 % HRmax selama 1 jam. Tidak ada peningkatan jarak mingguan."
            "Healthy" -> "Jogging 75–85 % HRmax selama 1 jam. Progres berdasarkan RPE."
            "Overweight" -> "Jogging < 75 % HRmax selama 1 jam."
            else -> "Jalan santai < 75 % HRmax selama 1 jam."
        }

        return """
        Goal: Keep Fit  
        HR zone: <75 % (${(maxHR * 0.75).toInt()} bpm) – 85 % (${(maxHR * 0.85).toInt()} bpm)  
        Usia: ${user.age} (${ageGroup}) → Durasi $durationPerWeek  
        BMI: %.1f ($category)  
        Rekomendasi: $activity
    """.trimIndent().format(bmi)
    }

}



//Underweight
//Less than 18.5

//Healthy Weight
//18.5 to less than 25

//Overweight
//25 to less than 30

//Obesity
//30 or greater

//Class 1 Obesity
//30 to less than 35

//Class 2 Obesity
//35 to less than 40

//Class 3 Obesity
//(Severe Obesity)
//40 or greater

// source =  https://www.cdc.gov/bmi/adult-calculator/bmi-categories.html



// gender : gender was found to be an important factor influencing endurance performance
//source : https://dl.acm.org/doi/10.1145/3732299.3732334#sec-5


//height & weight source : https://dl.acm.org/doi/10.1145/3732299.3732334#sec-5
//The influence of height, weight and BMI: Height and weight, as basic indicators of body shape,
//have a direct impact on endurance performance. Higher BMI is negatively correlated with endurance
//performance, indicating that overweight and obese individuals typically have poorer endurance
//performance. This highlights the importance of weight control in improving endurance performance.

//makin gede bmi kalo besarannya itu isinya lemak semua maka bakal cpt capek juga orgnya kalo lari
//        kalo gedenya karena muscle beda lagi mesti kuat kuat aja larinya
