package com.example.movie_ticketing.repository

import com.example.movie_ticketing.domain.Theater
import org.springframework.data.jpa.repository.JpaRepository

interface TheaterRepository :JpaRepository<Theater, Int> {

}