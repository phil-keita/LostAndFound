package com.example.lostandfound.data

import com.example.lostandfound.model.FindPost
import com.example.lostandfound.model.FoundPost

class DummyPosts (

){
    fun getDummyFindPosts(): List<FindPost>{
        return listOf<FindPost>(
            FindPost("Earbuds", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "SAC", "10:00AM-12:00PM"),
            FindPost("Blue jacket", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "STEM", "12:00PM-5:00PM"),
            FindPost("Earbuds", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "SAC", "10:00AM-12:00PM"),
            FindPost("Blue jacket", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "STEM", "12:00PM-5:00PM"),
            FindPost("Earbuds", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "SAC", "10:00AM-12:00PM"),
            FindPost("Blue jacket", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "STEM", "12:00PM-5:00PM"),
            FindPost("Earbuds", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "SAC", "10:00AM-12:00PM"),
            FindPost("Blue jacket", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "STEM", "12:00PM-5:00PM"),
            )
    }

    fun getDummyFoundPosts(): List<FoundPost>{
        return listOf(
            FoundPost("Earbuds", 0, "HBL Library"),
            FoundPost("Earbuds", 0, "HBL Library"),
            FoundPost("Earbuds", 0, "HBL Library"),
            FoundPost("Earbuds", 0, "HBL Library"),
            FoundPost("Earbuds", 0, "HBL Library"),
            FoundPost("Earbuds", 0, "HBL Library"),
            FoundPost("Earbuds", 0, "HBL Library"),
            FoundPost("Earbuds", 0, "HBL Library")
        )
    }
}

