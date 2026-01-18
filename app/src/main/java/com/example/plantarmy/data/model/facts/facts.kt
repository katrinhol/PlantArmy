package com.example.plantarmy.data.model.facts

object PlantFacts {

    private val facts = listOf(

        // 💧 Watering
        "Overwatering is one of the most common reasons houseplants die.",
        "Most plants prefer deep watering rather than frequent small sips.",
        "Always check the soil before watering — dry on top doesn't mean dry below.",
        "Plants usually need less water in winter due to slower growth.",

        // 🌞 Light
        "Too much direct sunlight can burn plant leaves.",
        "Most indoor plants thrive in bright, indirect light.",
        "Plants grow toward light — rotating them helps even growth.",
        "Low light slows plant growth and reduces water needs.",

        // 🌿 General care
        "Dusty leaves can reduce a plant’s ability to photosynthesize.",
        "Cleaning plant leaves helps them absorb more light.",
        "Many plants benefit from occasional misting to increase humidity.",
        "Yellow leaves can indicate overwatering, underwatering, or low light.",

        // 🌱 Growth & biology
        "Plants can sense gravity — roots grow downward, stems grow upward.",
        "New growth usually appears lighter green than older leaves.",
        "Healthy roots are usually white or light-colored.",
        "Repotting is best done during the growing season (spring).",

        // 🪴 Indoor plant tips
        "Most houseplants come from tropical regions with high humidity.",
        "Grouping plants together can increase humidity naturally.",
        "Drainage holes are essential to prevent root rot.",
        "Standing water at the bottom of a pot can suffocate roots.",

        // ✨ Fun facts
        "Plants can communicate stress through chemical signals.",
        "Some plants close their leaves at night to conserve energy.",
        "Talking to plants doesn’t harm them — vibration can stimulate growth.",
        "Plants release oxygen during the day, improving indoor air quality."
    )

    fun randomFact(): String = facts.random()
}