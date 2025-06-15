package com.damian.whatsapp.chat.room;

import com.damian.whatsapp.chat.room.exception.RoomNotFoundException;
import com.damian.whatsapp.chat.room.http.RoomCreateRequest;
import com.damian.whatsapp.common.exception.Exceptions;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.customer.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        customerRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    void setUpContext(Customer customer) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customer);
    }

    @Test
    @DisplayName("Should get all rooms")
    void shouldGetAllRooms() {
        // given
        Room room1 = new Room("gaming", "room1");
        Room room2 = new Room("music", "room2");

        List<Room> roomList = List.of(
                room1, room2
        );

        // when
        when(roomRepository.findAll()).thenReturn(roomList);
        List<Room> result = roomService.getRooms();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get room")
    void shouldGetRoom() {
        // given
        Room room1 = new Room("gaming", "room1");
        room1.setId(1L);

        // when
        when(roomRepository.findById(room1.getId())).thenReturn(Optional.of(room1));
        Room result = roomService.getRoom(room1.getId());

        // then
        assertNotNull(result);
        verify(roomRepository, times(1)).findById(room1.getId());
    }

    @Test
    @DisplayName("Should not get room")
    void shouldNotGetRoomWhenNotFound() {
        // given
        Room room1 = new Room("gaming", "room1");
        room1.setId(1L);

        // when
        when(roomRepository.findById(room1.getId())).thenReturn(Optional.empty());
        RoomNotFoundException exception = assertThrows(
                RoomNotFoundException.class,
                () -> roomService.getRoom(room1.getId())
        );

        // then
        assertEquals(Exceptions.ROOM.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should create room")
    void shouldCreateRoom() {
        // given
        RoomCreateRequest request = new RoomCreateRequest(
                "Gaming",
                "Gaming room"
        );

        // when
        when(roomRepository.save(any(Room.class))).thenReturn(
                new Room(request.name(), request.description())
        );
        Room result = roomService.createRoom(request);

        // then
        assertNotNull(result);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    // TODO: shouldDeleteRoom
}
