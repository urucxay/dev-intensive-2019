package ru.skillbranch.devintensive.models

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    fun askQuestion(): String = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {

        return when {
            question == Question.NAME && (answer.isEmpty() || answer.first().isLowerCase() || answer.first().isDigit()) ->
                "Имя должно начинаться с заглавной буквы\n${question.question}" to status.color
            question == Question.PROFESSION && (answer.isEmpty() || answer.first().isUpperCase() || answer.first().isDigit()) ->
                "Профессия должна начинаться со строчной буквы\n${question.question}" to status.color
            question == Question.MATERIAL && answer.contains(Regex("[0-9]")) ->
                "Материал не должен содержать цифр\n${question.question}" to status.color
            question == Question.BDAY && answer.contains(Regex("\\D")) ->
                "Год моего рождения должен содержать только цифры\n${question.question}" to status.color
            question == Question.SERIAL && (answer.contains(Regex("\\D")) || answer.length != 7) ->
                "Серийный номер содержит только цифры, и их 7\n${question.question}" to status.color
            question == Question.IDLE -> "На этом все, вопросов больше нет" to status.color
            else -> if (question.answers.contains(answer.toLowerCase())) {
                question = question.nextQuestion()
                "Отлично - ты справился\n${question.question}" to status.color
            } else {
                status = status.nextStatus()
                if (status == Status.NORMAL) {
                    question = Question.NAME
                    "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
                } else {
                    "Это неправильный ответ\n${question.question}" to status.color
                }
            }
        }
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status {
            return if (this.ordinal < values().lastIndex) {
                values()[this.ordinal + 1]
            } else {
                values()[0]
            }
        }
    }

    enum class Question(val question: String, val answers: List<String>) {
        NAME("Как меня зовут?", listOf("бендер", "bender")) {
            override fun nextQuestion(): Question = PROFESSION
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")) {
            override fun nextQuestion(): Question = MATERIAL
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood")) {
            override fun nextQuestion(): Question = BDAY
        },
        BDAY("Когда меня создали?", listOf("2993")) {
            override fun nextQuestion(): Question = SERIAL
        },
        SERIAL("Мой серийный номер?", listOf("2716057")) {
            override fun nextQuestion(): Question = IDLE
        },
        IDLE("На этом все, вопросов больше нет", listOf()) {
            override fun nextQuestion(): Question = IDLE
        };

        abstract fun nextQuestion(): Question
    }
}