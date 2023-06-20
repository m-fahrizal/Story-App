package com.example.submissionstory.data.Rules

import com.example.submissionstory.data.response.ListStory

object FakeData {
    fun mainFake(): List<ListStory> {
        val data: MutableList<ListStory> = arrayListOf()

        for (i in 0..200) {
            val fill = ListStory(
                "1",
                "fahrizal",
                "ini deskripsi",
                "urlPhoto",
                "8-6-2023",
                14.87663456,
                -45.34566543
            )
            data.add(fill)
        }
        return data
    }

    val mainNullPoint = emptyList<ListStory>()
}


