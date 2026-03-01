package org.miun.se.backend.rest;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.miun.se.backend.DTO.BookingDto;
import org.miun.se.backend.DTO.BookingAddDto;
import org.miun.se.backend.model.Booking;
import org.miun.se.backend.model.Customer;
import org.miun.se.backend.model.DiningTable;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON )
@Produces(MediaType.APPLICATION_JSON)



public class BookingResource {

    @PersistenceContext
    private EntityManager em;


    @GET
    public List<BookingDto> getBookings(){

        List<Booking> bookings = em.createQuery(
                "SELECT booking FROM Booking booking " + "JOIN FETCH booking.customer " +
                "LEFT JOIN FETCH booking.diningTable ", Booking.class).getResultList();

        List<BookingDto> bookingsDto = new ArrayList<>();
        for(Booking booking : bookings){
            Customer customer = booking.getCustomer();
            DiningTable table = booking.getDiningTable();

            Integer tableId, tableNumber;
            if(table != null){
                tableId = table.getTableId();
                tableNumber = table.getTableNumber();
            } else {
                tableId = null;
                tableNumber = null;
            }
            bookingsDto.add(new BookingDto(booking.getBookingId(), customer.getFirstName(), customer.getLastName(),
                    customer.getPhoneNumber(), booking.getGuestCount(), booking.getBookingTime(), tableId, tableNumber));
        }
        return bookingsDto;
    }

    @POST
    @Transactional
    public Response addBooking(BookingAddDto newBooking){
        Customer customer;
        try {
            customer = em.createQuery("SELECT customer FROM Customer customer WHERE customer.phoneNumber = :phoneNumber", Customer.class)
                    .setParameter("phoneNumber", newBooking.phoneNumber())
                    .getSingleResult();
        } catch (Exception e){
            customer = new Customer(newBooking.firstName(), newBooking.lastName(), newBooking.phoneNumber());
            em.persist(customer);
        }


        Booking booking = new Booking(customer, null, newBooking.date(), newBooking.guestCount());

        em.persist(booking);

        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/{bookingId}")
    @Transactional
    public Response deleteBooking(@PathParam("bookingId") Integer bookingId){

        Booking booking;
        try {
            booking = em.createQuery(
                    "SELECT booking FROM Booking booking WHERE booking.bookingId = :bookingId", Booking.class)
                    .setParameter("bookingId", bookingId).getSingleResult();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        em.remove(booking);
        return Response.ok().build();
    }

    @PUT
    @Path("/{bookingId}")
    @Transactional
    public Response editBooking(@PathParam("bookingId") Integer bookingId, BookingAddDto bookingDto){
        Booking booking;
        try {
            booking = em.createQuery(
                    "SELECT booking FROM Booking booking WHERE booking.bookingId = :bookingId", Booking.class)
                    .setParameter("bookingId", bookingId).getSingleResult();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Customer customer = booking.getCustomer();
        customer.setFirstName(bookingDto.firstName());
        customer.setLastName(bookingDto.lastName());
        customer.setPhoneNumber(bookingDto.phoneNumber());

        DiningTable table = null;

        if(bookingDto.tableId() != null){
            try {
                table = em.createQuery("SELECT table FROM DiningTable table WHERE table.tableId = :tableId", DiningTable.class)
                        .setParameter("tableId", bookingDto.tableId())
                        .getSingleResult();
            } catch (Exception e){
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }

        booking.setCustomer(customer);
        booking.setDiningTable(table);
        booking.setBookingTime(bookingDto.date());
        booking.setGuestCount(bookingDto.guestCount());


        return Response.ok().build();
    }
}
