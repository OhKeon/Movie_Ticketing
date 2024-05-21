package com.example.movie_ticketing.controller

import com.example.movie_ticketing.domain.*
import com.example.movie_ticketing.repository.*
import com.example.movie_ticketing.service.MovieService
import com.example.movie_ticketing.service.ReservationService
import com.example.movie_ticketing.service.SeatService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 예약 순서 : 영화 선택 -> 날짜 선택 -> 시간 선택 -> 좌석 선택
 */
@Controller
class ReservationController(
    private val theaterRepository: TheaterRepository,
    private val scheduleRepository: ScheduleRepository,
    private val ticketRepository: TicketRepository,
    private val memberRepository: MemberRepository,
    private val reservationRepository: ReservationRepository,
    private val seatRepository: SeatRepository
) {

    @Transactional
    @PostMapping("/user/reservation")
    fun selectReservation(@Valid form: ReservationForm, model: Model, @AuthenticationPrincipal user: User): String {
        println("Received form: $form")

        val email = user.username
        val member = memberRepository.findByEmail(email).orElseThrow()

        val reservation = Reservation().apply {
            this.member = member
            this.date = LocalDate.now().toString()
        }
        println(reservation)
        reservationRepository.save(reservation)

        val theater = theaterRepository.findByName(form.theaterName) //관 이름을 전송했기에 관id를 찾아서 저장
        val schedule = scheduleRepository.findByMovieIdAndDateAndStartAndTheaterId(form.movieId, form.date, form.time,theater.id)

        form.seatNumbers.forEach{number -> //선택된 좌석들을 받아옴 = 리스트형식이기에 각각의 정보들을 이용해 티켓에 저장
            val seat = seatRepository.findByTheaterAndSeatNumber(schedule.theater,number) //form태그에서 온 것은 seatNumber이기에 theaterid로 좌석을 구분
            val ticket = Ticket().apply {                               // why? seatNumber는 모두 1~49이기에
                this.schedule = schedule                // 구한 좌석을 통해 스케줄과 좌석을 맵핑해서 저장
                this.seat = seat
                this.reservation = reservation
            }
            println(ticket)
            ticketRepository.save(ticket)
        }




//        selectedSeats.forEach { seat ->
//            seat.isSelected = true
//            seatService.save(seat)
//        }


        return "redirect:/reservationComplete"
    }

    @PostMapping("/cancelReservation")
    fun cancelReservation(ticketId: Int, result: BindingResult): String {

        if (result.hasErrors()) {
            return "redirect:/reservation"
        }

        val ticket = ticketRepository.findById(ticketId)
            .orElseThrow { IllegalArgumentException("error") }

//        ticket.seat.isSelected = false

        ticketRepository.delete(ticket)

        return "redirect:/mypage2"
    }

    fun generateDateRange(): List<String> {
        return (0 until 10).map { LocalDate.now().plusDays(it.toLong()).format(DateTimeFormatter.ISO_DATE) }
    }

    fun generateTimeSlots(): List<String> {
        return listOf("10:00", "12:30", "15:00", "17:30", "20:00", "22:30")
    }
}