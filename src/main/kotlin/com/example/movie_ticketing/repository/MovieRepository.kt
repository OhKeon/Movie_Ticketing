package com.example.movie_ticketing.repository

import com.example.movie_ticketing.domain.Movie
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface MovieRepository : JpaRepository<Movie?, Int?> {

    fun findByTitleContaining(searchKeyword: String?, pageable: Pageable?): Page<Movie?>?

    @Query("SELECT m.title FROM Movie m")
    fun findTitles() : List<Movie>
    fun save(movieTitle: Optional<Movie?>)

    fun findByTitle(title : String)

}